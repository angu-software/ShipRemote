package shipremote.ai.base;

/**
 * Interface for an ai decision object
 * 
 * @author Andreas Günther
 * 
 */
public interface IShipAIDecision {

  /**
   * Gets a value that indicates whether the contains speed value is meant for
   * backward (true) or forward (false) direction.
   * 
   * @return True for backward or false for forward speed direction.
   */
  boolean isSpeedBackward();

  /**
   * Gets the speed the ai has set.
   * @return The speed as int.
   */
  int getSpeed();

  /**
   * Gets the angle for the oar.
   * @return The angle for the oar in degrees.
   */
  int getDirection();

}
