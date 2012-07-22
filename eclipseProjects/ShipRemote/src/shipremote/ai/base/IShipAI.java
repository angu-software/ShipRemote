package shipremote.ai.base;

/**
 * An interface for the ai of the system.
 * @author Andreas Günther
 *
 */
public interface IShipAI {

  /**
   * Processes the ai logic.
   * @param data The ShipState as base for the ai processing.
   * @return A IShipAIDecision with the decision the ai.
   */
	public IShipAIDecision getDecision(ShipState data);

}
