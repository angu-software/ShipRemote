package shipremote.communication;

/**
 * Factory class for ship commands. Each command is represented by a data frame
 * as byte array.
 * 
 * @author Andreas Günther
 * 
 */
public class ShipCommandFactory {

  final static byte DATA_START = (byte) 255;
  final static byte DATA_EMPTY = (byte) 0;
  final static byte DATA_END = (byte) 0;
  final static byte DATA_TYPE_ALIVE = (byte) 1;
  final static byte DATA_TYPE_SONAR = (byte) 2;
  final static byte DATA_TYPE_CONTROL_SPEED_FORWARD = (byte) 3;
  final static byte DATA_TYPE_CONTROL_SPEED_BACKWARD = (byte) 4;
  final static byte DATA_TYPE_CONTROL_DIRECTION = (byte) 5;
  final static byte DATA_TYPE_INIT_RANGING = (byte) 6;
  final static byte DATA_TYPE_STOP_RANGING = (byte) 7;

  /**
   * Gets a alive data frame.
   * 
   * @return A alive data frame.
   */
  public static byte[] getAliveFrame() {
    byte[] command = new byte[] { DATA_START, DATA_TYPE_ALIVE, DATA_EMPTY,
        DATA_END };
    return command;
  }

  /**
   * Gets a forward speed frame.
   * 
   * @param speed
   *          The speed value.
   * @return A forward speed frame.
   */
  public static byte[] getForwardSpeedFrame(int speed) {
    byte[] command = new byte[] { DATA_START, DATA_TYPE_CONTROL_SPEED_FORWARD,
        (byte) speed, DATA_END };
    return command;
  }

  /**
   * Gets a backward speed frame.
   * @param speed The speed value.
   * @return A backward speed frame.
   */
  public static byte[] getBackwardSpeedFrame(int speed) {
    byte[] command = new byte[] { DATA_START, DATA_TYPE_CONTROL_SPEED_BACKWARD,
        (byte) speed, DATA_END };
    return command;
  }

  /**
   * Gets a direction frame.
   * @param angle The angle value for the direction.
   * @return A direction frame.
   */
  public static byte[] getDirectionFrame(int angle) {
    byte[] command = new byte[] { DATA_START, DATA_TYPE_CONTROL_DIRECTION,
        (byte) angle, DATA_END };
    return command;
  }

  /**
   * Gets a initializing-ranging frame.
   * @return A initializing-ranging frame.
   */
  public static byte[] getInitRangingFrame() {
    byte[] command = new byte[] { DATA_START, DATA_TYPE_INIT_RANGING,
        DATA_EMPTY, DATA_END };
    return command;
  }

  /**
   * Gets a stop-ranging frame.
   * @return A stop-ranging frame.
   */
  public static byte[] getStopRangingFrame() {
    byte[] command = new byte[] { DATA_START, DATA_TYPE_STOP_RANGING,
        DATA_EMPTY, DATA_END };
    return command;
  }

  /**
   * Gets a sonar frame.
   * Just for simulation purposes.
   * @param range A range value.
   * @return A sonar frame.
   */
  public static byte[] getSonarFrame(int range) {
    byte[] command = new byte[] { DATA_START, DATA_TYPE_SONAR, (byte) range,
        DATA_END };
    return command;
  }

}
