package shipremote.ai;

import shipremote.ai.base.IShipAIDecision;
import shipremote.ai.base.ShipState;

/**
 * A danger level. Base of the DangerLevelAI.
 * @author Andreas Günther
 *
 */
public abstract class DangerLevel {
  private int level = 0;
  private Range levelRange;

  /**
   * Constructor.
   * @param level The level of this DangerLevel.
   * @param minRange Minimum value of the Range.
   * @param maxRange Maximum value of the Range.
   */
  public DangerLevel(int level, int minRange, int maxRange) {
    this.level = level;
    this.levelRange = new Range(minRange, maxRange);
  }

  /**
   * Gets the level of this DangerLevel.
   * @return The level.
   */
  public int getLevel() {
    return level;
  }

  /**
   * Gets the Range.
   * @return The Range.
   */
  public Range getLevelRange() {
    return levelRange;
  }

  /**
   * Sets the Range.
   * @param levelRange Teh Range.
   */
  public void setLevelRange(Range levelRange) {
    this.levelRange = levelRange;
  }
  
  /**
   * Gets the IShipAIDecision for this DangerLevel.
   * @param shipState The ShipState. Basis for the decision.
   * @return An IShipAIDecision
   */
  public abstract IShipAIDecision getDecision(ShipState shipState);

  /**
   * Gets the default decision for the DangerLevel.
   * @param data A ShipData object as base for the decision.
   * @return An IShipAIDecision.
   */
  public static IShipAIDecision getDefaultDecision(ShipState data) {
    DangerLevelAIDecision defDecision = new DangerLevelAIDecision();
    defDecision.setSpeed(100);
    defDecision.setSpeedBackward(false);
    defDecision.setDirection(90);
    return defDecision;
  }
  
  @Override
  public String toString() {
    
    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append(DangerLevel.class.getSimpleName());
    strBuilder.append(": ");
    strBuilder.append(Integer.toString(this.level));
    strBuilder.append(" | ");
    strBuilder.append(this.getLevelRange().toString());
    return strBuilder.toString();
  }
  
}
