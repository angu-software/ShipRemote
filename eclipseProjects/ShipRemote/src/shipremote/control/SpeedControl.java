package shipremote.control;

/**
 * Controller for speed.
 * 
 * @author Andreas Günther
 * 
 */
public class SpeedControl {

  private int currentSpeed = 0;
  private int maxSpeed = 100;
  private int minSpeedPublic = 0;
  private int minSpeedInternal = -100;

  /**
   * Gets the maximum speed.
   * 
   * @return The maximum speed.
   */
  public int getMaxSpeed() {
    return maxSpeed;
  }

  /**
   * Sets the maximum speed.
   * 
   * @param maxSpeed
   *          The maximum speed.
   */
  public void setMaxSpeed(int maxSpeed) {
    this.maxSpeed = maxSpeed;
    this.minSpeedInternal = -maxSpeed;
  }

  /**
   * Gets the minimum speed.
   * 
   * @return The minimum speed.
   */
  public int getMinSpeed() {
    return minSpeedPublic;
  }

  /**
   * Sets the minimum speed.
   * 
   * @param minSpeed
   *          The minimum speed.
   */
  public void setMinSpeed(int minSpeed) {
    this.minSpeedPublic = minSpeed;
  }

  /**
   * Sets the current speed.
   * 
   * @param currentSpeed
   *          The current speed. If value is less than zero the speed is set to
   *          backward.
   */
  public void setCurrentSpeed(int currentSpeed) {
    this.currentSpeed = currentSpeed;
  }

  /**
   * Gets the current speed.
   * 
   * @return The current speed.
   */
  public int getCurrentSpeed() {
    return Math.abs(currentSpeed);
  }

  /**
   * Gets a value that indicates whether the speed value is meant for backward
   * direction.
   * 
   * @return True if backward, else false.
   */
  public boolean isBackward() {
    return this.currentSpeed < 0;
  }

  /**
   * Resets the speed to minimum speed.
   */
  public void resetSpeed() {
    this.currentSpeed = this.minSpeedPublic;
  }

  /**
   * Increases the current speed.
   * @param value The value to increase the speed.
   */
  public void increaseSpeed(int value) {
    this.currentSpeed += value;
    if (this.currentSpeed > this.maxSpeed) {
      this.currentSpeed = this.maxSpeed;
    }
  }

  /**
   * Decreases the current speed.
   * @param value The value to decrease the speed.
   */
  public void decreaseSpeed(int value) {
    this.currentSpeed -= value;
    if (this.currentSpeed < this.minSpeedInternal) {
      this.currentSpeed = this.minSpeedInternal;
    }
  }

}
