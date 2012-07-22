package shipremote.ai;

import shipremote.ai.base.IShipAIDecision;

/**
 * Implementation of a IShipAIDecision for the DangerLevelAI.
 * Keeps the values of the decision of the DangerLevelAI.
 * @author Andreas Günther
 *
 */
public class DangerLevelAIDecision implements IShipAIDecision {

  private static final int MIN_DIRECTION = 0;
  private static final int MAX_DIRECTION = 180;
  private static final int MIN_SPEED = 0;
  private static final int MAX_SPEED = 100;
  private int speed = 0;
  private boolean isBackwardSpeed = false;
  private int direction = 90;
  
  @Override
  public int getDirection() {
    return this.direction;
  }

  /**
   * Sets the direction.
   * @param direction The direction of the ship.
   */
  public void setDirection(int direction){
    if(direction < MIN_DIRECTION){
      direction = MIN_DIRECTION;
    }
    if(direction > MAX_DIRECTION){
      direction = MAX_DIRECTION;
    }
    this.direction = direction;
  }
  
  @Override
  public int getSpeed() {
    return this.speed;
  }

  /**
   * Sets the speed.
   * @param speed The speed.
   */
  public void setSpeed(int speed){
    if(speed < MIN_SPEED){
      speed = MIN_SPEED;
    }
    if(speed > MAX_SPEED){
      speed = MAX_SPEED;
    }
    this.speed = speed;
  }
  
  @Override
  public boolean isSpeedBackward() {
    return this.isBackwardSpeed;
  }
  
  /**
   * Sets whether the speed means backward (true) or forward (false).
   * @param backward True for backward, false for forward.
   */
  public void setSpeedBackward(boolean backward){
    this.isBackwardSpeed = backward;
  }

}
