///////////////////////////////
// includes
///////////////////////////////
#include <Servo.h>

///////////////////////////////
// constants
///////////////////////////////
// Frame definitions
#include "WProgram.h"
void setup();
void loop();
int getSensorData(int pin);
void sendSensorData(int data);
boolean getRemoteData();
void processRemoteData();
void stopRangeDetection();
void setSteeringAngle(int value);
void setShipSpeed(int value, boolean isBackward);
void stopShip();
const int FRAME_LENGTH = 4;
const int POS_START_BYTE = 0;
const int POS_TYPE_BYTE = 1;
const int POS_DATA_BYTE = 2;
const int POS_STOP_BYTE = 3;
// Data Types
const byte DATA_START = (byte)255;
const byte DATA_EMPTY = (byte)0;
const byte DATA_END = (byte)0;
const byte DATA_TYPE_ALIVE = (byte)1;
const byte DATA_TYPE_SENSOR = (byte)2;
const byte DATA_TYPE_CONTROL_SPEED_FORWARD = (byte)3;
const byte DATA_TYPE_CONTROL_SPEED_BACKWARD = (byte)4;
const byte DATA_TYPE_CONTROL_DIRECTION = (byte)5;
const byte DATA_TYPE_INIT_RANGING = (byte)6;
const byte DATA_TYPE_STOP_RANGING = (byte)7;
const int MAX_RANGE = 250;
const int STRAIGHT = 90;

///////////////////////////////
// time dependent constants
//////////////////////////////

// set to 1500 millis to get an delay of 15 seconds,
// because it depends on PROCESS_DELAY
const int TIMEOUT_NO_DATA_RECEIVED = 1500; 
// set to 100 millis to get an delay of 1 seconds,
// because it depends on PROCESS_DELAY
const int SONAR_SENDING_DELAY = 100; 
// base time for operation timers
const int PROCESS_DELAY = 10; 

///////////////////////////////
// pins for sonar
///////////////////////////////
const int sonarInPin = 2;
const int sonarActivePin = 3;
const int sonarObjectDetectedOutput = 4;
///////////////////////////////
// pins for steering
///////////////////////////////
const int steeringPin = 9;
///////////////////////////////
// pins for speed
///////////////////////////////
const int motorDirectionPin = 13;
const int motorSpeedPin = 11;
///////////////////////////////
// control variables
///////////////////////////////
int sonarTimerCounter = 0;
boolean sonarActive = false;
int aliveCounter = 0;
Servo steeringServo;
byte receivedData[FRAME_LENGTH];
int steeringAngle;
int shipSpeed;
boolean isBackward;
int sensorData;

///////////////////////////////
// main functions
///////////////////////////////

void setup(){
  pinMode(sonarInPin, INPUT);
  pinMode(sonarActivePin, OUTPUT);
  pinMode(sonarObjectDetectedOutput, OUTPUT);
  pinMode(motorSpeedPin, OUTPUT);
  pinMode(motorDirectionPin, OUTPUT);
  //bugfix for motor
  pinMode(12,OUTPUT);
  digitalWrite(12, HIGH);
  
  steeringServo.attach(steeringPin);
  setSteeringAngle(STRAIGHT);
  
  setShipSpeed(0, false);
  
  sonarActive = false;
  sensorData = 0;
  
  Serial.begin(9600);
}

/*
 * main loop.
 */
void loop(){
  
  // sonar component
  if(sonarActive == true){
    // counter is nessesary to send the data only every second.
    if(sonarTimerCounter == SONAR_SENDING_DELAY){
      sensorData = getSensorData(sonarInPin);
      sendSensorData(sensorData);
      sonarTimerCounter = 0;
    }
    sonarTimerCounter += 1;
  }
  
  // communication component
  if(getRemoteData() == true){
    processRemoteData();
    // reset because data was received from the remote.
    aliveCounter = 0;
  }else{
    aliveCounter++;
    // stop ship if connection lost
    // means no data is received in the 
    // TIMEOUT_NO_DATA_RECEIVED
    if(aliveCounter >= TIMEOUT_NO_DATA_RECEIVED){
      stopShip();
    }
  } 
  delay(PROCESS_DELAY);
}

///////////////////////////////
// functions
///////////////////////////////

/*
 * Gets the data from the sonar sensor
 * @param pin the pin to read from
 * @returns the distance to an object in cm
 */
int getSensorData(int pin){
  int pulse = pulseIn(pin, HIGH);
  //147uS per inch
  int inches = pulse/147;
  //change inches to centimetres
  int cm = inches * 2.54;
  //limit the distance to 250 cm
  if(cm > MAX_RANGE){
    cm = MAX_RANGE;
  }
  return cm;
}

void sendSensorData(int data){
  // define the frame
  byte frame[FRAME_LENGTH];
  frame[POS_START_BYTE] = (byte)DATA_START;
  frame[POS_TYPE_BYTE] = (byte)DATA_TYPE_SENSOR;
  frame[POS_DATA_BYTE] = (byte)data;
  frame[POS_STOP_BYTE] = (byte)DATA_END;
  
  Serial.write(frame, FRAME_LENGTH);
}

/*
 * Gets remote data bytes (the next 3) and write
 * them to the recivedBytes array
 * @return returns true if data is received else false
 */
boolean getRemoteData(){
  int availableBytes = Serial.available();
  if(availableBytes >= FRAME_LENGTH){

    byte start = (byte)Serial.read();
    if(start == DATA_START){
      receivedData[POS_START_BYTE] = start;
      receivedData[POS_TYPE_BYTE] = (byte)Serial.read();
      receivedData[POS_DATA_BYTE] = (byte)Serial.read();
      receivedData[POS_STOP_BYTE] = (byte)Serial.read();
    
      Serial.write(receivedData, FRAME_LENGTH);
      
      return true;
    }
  }
  return false;
}

void processRemoteData(){
  if(receivedData[0] == DATA_START){
    byte type = receivedData[POS_TYPE_BYTE];
    // get value an convert to unsigned byte
    int value = receivedData[POS_DATA_BYTE] & 0xFF;
    switch(type){
      case DATA_TYPE_ALIVE:
        // do nothing. Just to be sure
        // the connection to the remote is not lost.
        // see aliveCounter and TIMEOUT_NO_DATA_RECEIVED
        break;
      case DATA_TYPE_CONTROL_SPEED_FORWARD:
        if(shipSpeed != value){
          setShipSpeed(value, false);
          shipSpeed = value;
        }
        break;
      case DATA_TYPE_CONTROL_SPEED_BACKWARD:
      
        if(shipSpeed != value){
          setShipSpeed(value, true);
          shipSpeed = value;
        }
        break;
      case DATA_TYPE_CONTROL_DIRECTION:
        if(steeringAngle != value){
          setSteeringAngle(value);
          steeringAngle = value;
        }
        break;
      case DATA_TYPE_INIT_RANGING:
        // start range detection
        digitalWrite(sonarActivePin, HIGH);
        delay(20);
        sonarActive = true;
        break;
      case DATA_TYPE_STOP_RANGING:
        // stop range detection
        stopRangeDetection();
        break;
    }   
  }
}

void stopRangeDetection(){
  digitalWrite(sonarActivePin, LOW);
  sonarActive = false;
}

void setSteeringAngle(int value){
  steeringServo.write(value);
}

void setShipSpeed(int value, boolean isBackward){
  
  int d = HIGH; // direction
  if(isBackward){
    d = LOW;
  }
  digitalWrite(motorDirectionPin, d);
  analogWrite(motorSpeedPin, value);
}

void stopShip(){
  setShipSpeed(0, false);
  setSteeringAngle(STRAIGHT);
  stopRangeDetection();
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

