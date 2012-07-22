package shipremote.ai.base;

/**
 * an instance of this class represents the current state of the ship.
 * 
 * @author Andreas Günther
 * 
 */
public class ShipState implements IShipAIDecision {

  // /////////////////////////////////
  // fields
  // /////////////////////////////////
  private int objectDistance = 0;
  private int direction = 90;
  private int speed = 0;
  private boolean isSpeedBackward = false;

  // /////////////////////////////////
  // constructor
  // /////////////////////////////////
  /**
   * Constructor.
   * 
   * @param objectDistance
   *          Distance to an object.
   * @param direction
   *          Current ship direction.
   * @param speed
   *          Current ship speed
   * @param isSpeedBackward
   *          Indicates if the speed value is meant for backward (true) or
   *          forward (false) direction.
   */
  public ShipState(int objectDistance, int direction, int speed,
      boolean isSpeedBackward) {
    super();
    this.objectDistance = objectDistance;
    this.direction = direction;
    this.speed = speed;
    this.isSpeedBackward = isSpeedBackward;
  }

  // /////////////////////////////////
  // public properties
  // /////////////////////////////////

  /**
   * Gets the distance to an object.
   * @return Distance value.
   */
  public int getObjectDistance() {
    return objectDistance;
  }

  /**
   * Sets the distance to an object.
   * @param objectDistance Distance value.
   */
  public void setObjectDistance(int objectDistance) {
    this.objectDistance = objectDistance;
  }

  /**
   * Gets the direction angle.
   */
  public int getDirection() {
    return direction;
  }

  /**
   * 
   * @param direction
   */
  public void setDirection(int direction) {
    this.direction = direction;
  }
 /**
  * Gets the speed.
  */
  public int getSpeed() {
    return speed;
  }

  /**
   * Sets the speed.
   * @param speed
   */
  public void setSpeed(int speed) {
    this.speed = speed;
  }

  /**
   * Gets a value that indicates whether the speed value is meant for backward
   * (true) or forward (false) direction.
   */
  public boolean isSpeedBackward() {
    return isSpeedBackward;
  }

  /**
   * Sets a value that indicates whether the speed value is meant for backward
   * (true) or forward (false) direction.
   * @param isSpeedBackward True for backward. False for forward.
   */
  public void setSpeedBackward(boolean isSpeedBackward) {
    this.isSpeedBackward = isSpeedBackward;
  }

}
