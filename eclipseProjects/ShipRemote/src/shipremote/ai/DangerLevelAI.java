package shipremote.ai;

import java.util.ArrayList;
import java.util.List;

import shipremote.ai.base.IShipAI;
import shipremote.ai.base.IShipAIDecision;
import shipremote.ai.base.ShipState;

/**
 * Implementation of a the danger level ai.
 * @author Andreas Günther
 *
 */
public class DangerLevelAI implements IShipAI {
  private List<DangerLevel> dangerLevels = null;

  /**
   * Constructor.
   */
  public DangerLevelAI() {
    this.createDangerLevels();
  }

  private void createDangerLevels() {
    this.dangerLevels = new ArrayList<DangerLevel>();
    // create all available danger levels from the factory
    for (int i = 1; i <= DangerLevelFactory.MAX_DANGERLEVEL; i++) {
      this.dangerLevels.add(DangerLevelFactory.getDangerLevel(i));
    }
  }

  @Override
  public IShipAIDecision getDecision(ShipState data) {
    for (DangerLevel dangerLevel : this.dangerLevels) {
      if (dangerLevel.getLevelRange().isInRange(data.getObjectDistance())) {
        return dangerLevel.getDecision(data);
      }
    }
    return DangerLevel.getDefaultDecision(data);
  }

}
