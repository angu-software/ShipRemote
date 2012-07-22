package shipremote.ai;

import shipremote.ai.base.IShipAI;

/**
 * Factory class for creating ShipAIs
 * @author Andreas Günther
 *
 */
public class ShipAIFactory {

  /**
   * Gets a IShipAI.
   * @return A IShipAI.
   */
  public static IShipAI getShipAI(){
    return new DangerLevelAI();
  }
}
