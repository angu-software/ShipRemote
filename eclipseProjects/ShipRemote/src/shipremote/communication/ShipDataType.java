package shipremote.communication;

/**
 * An enumeration that defines serveral datatypes for a transmision data frame
 * between ship and remote control.
 * 
 * @author Andreas Günther
 * 
 */
public enum ShipDataType {

  /**
   * Invalid data frame.
   */
  INVALID((byte) 0),
  /**
   * Alive data frame.
   */
  ALIVE(ShipCommandFactory.DATA_TYPE_ALIVE),
  /**
   * Type to switch on ranging.
   */
  INIT_RANGING(ShipCommandFactory.DATA_TYPE_INIT_RANGING),
  /**
   * Type to switch off ranging.
   */
  STOP_RANGING(ShipCommandFactory.DATA_TYPE_STOP_RANGING),
  /**
   * Data frame contains sonar data that is provided by the ranging.
   */
  SONAR(ShipCommandFactory.DATA_TYPE_SONAR),
  /**
   * Data frame contains a speed value for moving forward.
   */
  SPEED_FORWARD(ShipCommandFactory.DATA_TYPE_CONTROL_SPEED_FORWARD),
  /**
   * Data frame contains a speed value for moving backward.
   */
  SPEED_BACKWARD(ShipCommandFactory.DATA_TYPE_CONTROL_SPEED_BACKWARD),
  /**
   * Data frame that contains a direction value.
   */
  DIRECTION(ShipCommandFactory.DATA_TYPE_CONTROL_DIRECTION), ;

  // ///////////////////////////////
  // fields
  // ///////////////////////////////
  private byte value = (byte) 0;

  // ///////////////////////////////
  // constructor
  // ///////////////////////////////
  ShipDataType(byte value) {
    this.value = value;
  }

  // ///////////////////////////////
  // public methods
  // ///////////////////////////////
  /**
   * Gets the byte value of the enumeration type.
   * 
   * @return The byte value of the enumeration type.
   */
  public byte getValue() {
    return this.value;
  }

  /**
   * Gets a enumeration type for a specific byte value.
   * @param byteValue A byte value.
   * @return The corresponding enumeration type.
   */
  public static ShipDataType valueOf(byte byteValue) {
    if (byteValue == SPEED_FORWARD.getValue()) {
      return SPEED_FORWARD;
    } else if (byteValue == SPEED_BACKWARD.getValue()) {
      return SPEED_BACKWARD;
    } else if (byteValue == DIRECTION.getValue()) {
      return DIRECTION;
    } else if (byteValue == SONAR.getValue()) {
      return SONAR;
    } else if (byteValue == INIT_RANGING.getValue()) {
      return INIT_RANGING;
    } else if (byteValue == STOP_RANGING.getValue()) {
      return STOP_RANGING;
    } else if (byteValue == ALIVE.getValue()) {
      return ALIVE;
    }
    return INVALID;
  }
}
