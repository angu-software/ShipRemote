package shipremote.control;

import java.io.IOException;
import java.util.Timer;

import shipremote.ai.base.IShipAI;
import shipremote.ai.base.IShipAIDecision;
import shipremote.ai.base.ShipAIProcessor;
import shipremote.ai.base.ShipState;
import shipremote.communication.IDataReceiverListener;
import shipremote.communication.INetworkClient;
import shipremote.communication.ShipData;
import shipremote.communication.ShipDataType;
import shipremote.res.R;
import shipremote.ui.GesturesView;
import shipremote.ui.IUserInputListener;
import shipremote.ui.SlideDirection;
import shipremote.ui.TextViewOutput;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * The main controller of the ShipRemote application. Controls the visual
 * elements, the data transfer and the processing of the Ship AI.
 * 
 * @author Andreas Günther
 * 
 */
public class ShipController extends Handler implements IDataReceiverListener,
    IUserInputListener {

  // /////////////////////////////
  // constants
  // /////////////////////////////

  private static final int SHIPDATARECEIVED = 0x213;
  private static final int IAMALIVE = 0x214;
  private static final int ALIVETIMEOUT = 10000;
  private static final int STEERINGSTEP = 1;
  private static final int SPEED_STEP = 1;

  // /////////////////////////////
  // private member
  // /////////////////////////////
  private GesturesView gestureView = null;
  private INetworkClient tcpClient = null;
  private boolean isUserInputAvailable = false;
  private SpeedControl speedControl = null;
  private SteeringControl steeringControl = null;
  private int objectDistance = 0;
  private ShipData receivedShipData = null;
  private Timer aliveTimer = null;
  private ShipDataType lastSlideCommandType = ShipDataType.INVALID;
  private ShipAIProcessor aiProcessor = null;
  private IShipAI shipAI = null;
  private boolean kiEnabled = false;

  /**
   * Constructor.
   * 
   * @param gestureView
   *          A GesturesView.
   * @param tcpClient
   *          A INetworkClient.
   * @param shipAI
   *          A IShipAI
   */
  public ShipController(final GesturesView gestureView,
      final INetworkClient tcpClient, final IShipAI shipAI) {
    this.gestureView = gestureView;
    this.gestureView.addSlideListener(this);
    this.tcpClient = tcpClient;
    this.tcpClient.addDataReceiverListener(this);
    this.shipAI = shipAI;
    this.speedControl = new SpeedControl();
    this.steeringControl = new SteeringControl();
    this.aliveTimer = new Timer();
  }

  // /////////////////////////////
  // public methods
  // /////////////////////////////

  /**
   * Starts the controller.
   */
  public void start() {
    this.startTCPListening();
    this.startAliveTimer();
    this.startRanging();
    this.updateUI();
  }

  /**
   * Stops the controller.
   */
  public void stop() {
    this.stopRanging();
    this.stopAliveTimer();
    this.stopTCPListening();
  }

  // /////////////////////////////
  // overridden methods
  // /////////////////////////////

  @Override
  public synchronized void dataReceived(final ShipData shipData) {

    if (!this.isUserInputAvailable) {
      this.receivedShipData = shipData;

      // send message to UI-Thread to process data
      this.sendEmptyMessage(ShipController.SHIPDATARECEIVED);
    }
  }

  @Override
  public void handleMessage(final Message msg) {

    switch (msg.what) {
      case SHIPDATARECEIVED:
        // process received sensor data
        this.computeReceivedData();
        Main.log("==> " + this.receivedShipData.toString());
        break;
      case IAMALIVE:
        // tell the ship i'm still there
        this.sendCommand(new ShipData(ShipDataType.ALIVE, 0));
        break;
      case ShipAIProcessor.DATA_PROCESSED_BY_AI:
        // the ship AI finished the processing
        if (this.updateSpeedAndSteeringAngleWithAIDecisionData()) {
          this.sendCommandsFromAIToShip();
        }
        break;
    }
    this.updateUI();
    super.handleMessage(msg);
  }

  @Override
  public void onDoubleTab() {

    // stop the ship
    this.stopShip();
    this.printStatus();
    Main.log(this.getStringFromResource(R.string.shipResetInfo));
  }

  @Override
  public void onSlide(final SlideDirection direction, final int value) {

    if (this.verticalSlide(direction)) {
      // set the speed for the ship
      this.setSpeedByDirection(direction);
      this.lastSlideCommandType = this.getCurrentSpeedDirectionType();
    } else if (this.horizontalSlide(direction)) {
      // set the steering angle for the ship
      this.setSteeringAngleByDirection(direction);
      this.lastSlideCommandType = ShipDataType.DIRECTION;
    } else {
      // not a valid gesture
      this.onSlideGestureAbord();
      return;
    }
    // update the UI
    this.updateUI();
  }

  private void setSteeringAngleByDirection(final SlideDirection direction) {
    if (direction == SlideDirection.LEFT) {
      this.steeringControl.steerLeft(STEERINGSTEP);
    } else if (direction == SlideDirection.RIGHT) {
      this.steeringControl.steerRight(STEERINGSTEP);
    }
  }

  private void setSpeedByDirection(final SlideDirection direction) {
    if (direction == SlideDirection.UP) {
      this.speedControl.increaseSpeed(SPEED_STEP);
    } else if (direction == SlideDirection.DOWN) {
      this.speedControl.decreaseSpeed(SPEED_STEP);
    }
  }

  private ShipDataType getCurrentSpeedDirectionType() {
    return this.speedControl.isBackward() ? ShipDataType.SPEED_BACKWARD
        : ShipDataType.SPEED_FORWARD;
  }

  private boolean horizontalSlide(final SlideDirection direction) {
    return direction == SlideDirection.LEFT
        || direction == SlideDirection.RIGHT;
  }

  private boolean verticalSlide(final SlideDirection direction) {
    return direction == SlideDirection.UP || direction == SlideDirection.DOWN;
  }

  @Override
  public void onSlideStart() {
    this.isUserInputAvailable = true;
  }

  @Override
  public void onSlideEnd() {
    if (this.lastSlideCommandType != ShipDataType.INVALID) {
      // get the command value
      int value = this.getShipDataValueByLastComandType();
      ShipData shipCommand = new ShipData(this.lastSlideCommandType, value);
      // send the data
      this.sendCommand(shipCommand);
      this.lastSlideCommandType = ShipDataType.INVALID;
    }
    this.isUserInputAvailable = false;
  }

  private int getShipDataValueByLastComandType() {
    final int value = this.lastSlideCommandType == ShipDataType.DIRECTION ? this.steeringControl
        .getCurrentAngle()
        : this.speedControl.getCurrentSpeed();
    return value;
  }

  @Override
  public void onSlideGestureAbord() {
    Main.showToast(this.getStringFromResource(R.string.gestureAbord));
  }

  @Override
  public void onLongPress() {
    this.kiEnabled = !this.kiEnabled;
    String msg = "KI " + (this.kiEnabled ? "aktivert." : "deaktiviert.");
    Main.showToast(msg);
    Main.log(msg);
  }

  // /////////////////////////////
  // private methods
  // /////////////////////////////

  private void computeReceivedData() {

    if (this.receivedShipData == null) {
      return;
    }

    if (this.receivedShipData.getDataType() == ShipDataType.SONAR) {
      // update object distance
      this.objectDistance = this.receivedShipData.getDataValue();

      // process the received data with the AI
      // controller gets message if processor finished.
      this.processDataWithAI();
    }
  }

  private void processDataWithAI() {
    // get the IShipAIDecision
    final ShipState state = this.getShipState();
    // provide the data to the ai processor and start processing
    if (this.aiProcessor == null || !this.aiProcessor.isAlive()) {
      try {
        if (this.aiProcessor != null) {
          this.aiProcessor = null;
        }
        this.aiProcessor = new ShipAIProcessor(this.shipAI);
        this.aiProcessor.addDataProcessedListener(this);
        this.aiProcessor.setCurrentShipData(state);
        this.aiProcessor.setEnabled(this.kiEnabled);
        this.aiProcessor.start();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private boolean updateSpeedAndSteeringAngleWithAIDecisionData() {
    boolean dataChanged = false;
    // get the decision from the AI
    final IShipAIDecision decision = this.aiProcessor.getAiDecision();
    // update state data for UI
    final int speed = decision.getSpeed();
    if (speedChanged(decision)) {
      this.speedControl.setCurrentSpeed(decision.isSpeedBackward() ? -speed
          : speed);
      dataChanged = true;
    }
    if (steeringAngleChanged(decision)) {
      this.steeringControl.setCurrentAngle(decision.getDirection());
      dataChanged = true;
    }
    return dataChanged;
  }

  private boolean speedChanged(final IShipAIDecision decision) {
    return decision.getSpeed() != this.speedControl.getCurrentSpeed()
        || decision.isSpeedBackward() != this.speedControl.isBackward();
  }

  private boolean steeringAngleChanged(final IShipAIDecision decision) {
    return decision.getDirection() != this.steeringControl.getCurrentAngle();
  }

  private void sendCommandsFromAIToShip() {
    // get the commands
    final ShipData shipSpeedCommand = this.getSpeedCommand();
    final ShipData shipDirectionCommand = this.getDirectionCommand();

    // send commands
    this.sendCommands(shipSpeedCommand, shipDirectionCommand);
  }

  private void sendCommands(final ShipData... commands) {
    for (final ShipData shipCommand : commands) {
      this.sendCommand(shipCommand);
    }

  }

  private ShipState getShipState() {
    final ShipState state = new ShipState(this.objectDistance,
        this.steeringControl.getCurrentAngle(), this.speedControl
            .getCurrentSpeed(), this.speedControl.isBackward());
    return state;
  }

  private ShipData getDirectionCommand() {
    final ShipData shipDirectionCommand = new ShipData(ShipDataType.DIRECTION,
        this.steeringControl.getCurrentAngle());
    return shipDirectionCommand;
  }

  private ShipData getSpeedCommand() {
    final ShipDataType speedDirectionType = this.speedControl.isBackward() ? ShipDataType.SPEED_BACKWARD
        : ShipDataType.SPEED_FORWARD;
    final ShipData shipSpeedCommand = new ShipData(speedDirectionType,
        this.speedControl.getCurrentSpeed());
    return shipSpeedCommand;
  }

  private boolean isTcpNotNullAndConnected() {
    return this.tcpClient != null && this.tcpClient.isConnected();
  }

  private void printStatus() {
    final int angle = this.steeringControl.getCurrentAngle();
    final int distance = this.objectDistance;
    final int speed = this.speedControl.getCurrentSpeed();
    final boolean back = this.speedControl.isBackward();

    final StringBuilder outputText = new StringBuilder();
    outputText.append("Ship state");
    outputText.append("\n==========");
    outputText.append("\nCurrent Speed: " + (back ? "-" : "") + speed);
    outputText.append("\nDirection: " + this.getDirection(angle) + " (" + angle
        + "°)");
    outputText.append("\nObject distance: " + distance + "cm");
    final String s = outputText.toString();
    TextViewOutput.setText(s);
  }

  private String getDirection(final int angle) {
    if (angle >= 0 && angle < 90) {
      return "left";
    } else if (angle > 90 && angle <= 180) {
      return "right";
    }
    return "straight";
  }

  private void sendCommand(final ShipData command) {
    if (this.isTcpNotNullAndConnected() && command != null) {
      try {
        this.tcpClient.sendData(command);
        Main.log("<== " + command.toString());
      } catch (final IOException e) {
        e.printStackTrace();
        String errMsg = this.getStringFromResource(R.string.sendDataError)
            + "\n";
        errMsg += e.getMessage();
        Log.e(Main.SHIP_REMOTE_TAG, errMsg, e);
        Main.showToast(errMsg);
      }
    }
  }

  private String getStringFromResource(final int resId) {
    return this.gestureView.getContext().getString(resId);
  }

  private void startAliveTimer() {
    this.aliveTimer.schedule(new MessageTimerTask(this, IAMALIVE),
        ALIVETIMEOUT, ALIVETIMEOUT);
    Main.log("AliveTimer gestartet");
  }

  private void startRanging() {
    if (this.isTcpNotNullAndConnected()) {
      this.sendCommand(new ShipData(ShipDataType.INIT_RANGING, 0));
    }
  }

  private void startTCPListening() {
    if (this.isTcpNotNullAndConnected()) {
      this.tcpClient.listen();
    } else {
      final String errMsg = this
          .getStringFromResource(R.string.tcpListeningError);
      Log.e(Main.SHIP_REMOTE_TAG, errMsg);
      Main.showToast(errMsg);
    }
  }

  private void stopAliveTimer() {
    this.aliveTimer.cancel();
  }

  private void stopRanging() {
    if (this.isTcpNotNullAndConnected()) {
      this.sendCommand(new ShipData(ShipDataType.STOP_RANGING, 0));
    }
  }

  private void stopShip() {
    this.speedControl.resetSpeed();
    this.steeringControl.reset();
    this.sendCommand(new ShipData(ShipDataType.DIRECTION, this.steeringControl
        .getCurrentAngle()));
    this.sendCommand(new ShipData(ShipDataType.SPEED_FORWARD, this.speedControl
        .getCurrentSpeed()));
  }

  private void stopTCPListening() {
    if (this.isTcpNotNullAndConnected()) {
      this.tcpClient.stopListening();
    }
  }

  private void updateUI() {
    this.printStatus();
  }

}
