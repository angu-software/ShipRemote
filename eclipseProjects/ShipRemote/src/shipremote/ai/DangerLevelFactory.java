package shipremote.ai;

import shipremote.ai.base.IShipAIDecision;
import shipremote.ai.base.ShipState;

/**
 * Factory class for creating DangerLevel.
 * @author Andreas Günther
 *
 */
public class DangerLevelFactory {

  /**
   * Defines the number of available DangerLevel.
   */
  public static final int MAX_DANGERLEVEL = 4;

  /**
   * Gets a DangerLevel by a level value.
   * @param level The level of danger.
   * @return A DangerLevel specified by the level of danger.
   */
  public static DangerLevel getDangerLevel(int level) {
    switch (level) {
      case 1:
        return getDangerLevelOne();
      case 2:
        return getDangerLevelTwo();
      case 3:
        return getDangerLevelThree();
      case 4:
        return getDangerLevelFour();
      default:
        return getDangerLevelFour();
    }
  }

  private static DangerLevel getDangerLevelFour() {
    return new DangerLevel(4, 0, 100) {
      @Override
      public IShipAIDecision getDecision(ShipState shipState) {
        // reset the oar and set the speed to full backward.
        DangerLevelAIDecision decision = new DangerLevelAIDecision();
        decision.setSpeed(100);
        decision.setSpeedBackward(true);
        decision.setDirection(90);
        return decision;
      }
    };
  }

  private static DangerLevel getDangerLevelThree() {
    return new DangerLevel(3, 100, 150) {
      @Override
      public IShipAIDecision getDecision(ShipState shipState) {
        // steer more left and reduce the speed.
        DangerLevelAIDecision decision = new DangerLevelAIDecision();
        decision.setSpeed(60);
        decision.setSpeedBackward(false);
        decision.setDirection(45);
        return decision;
      }
    };
  }

  private static DangerLevel getDangerLevelTwo() {
    return new DangerLevel(2, 150, 200) {
      @Override
      public IShipAIDecision getDecision(ShipState shipState) {
        // steer left but leave the speed.
        DangerLevelAIDecision decision = new DangerLevelAIDecision();
        decision.setSpeed(100);
        decision.setSpeedBackward(false);
        decision.setDirection(60);
        return decision;
      }
    };
  }

  private static DangerLevel getDangerLevelOne() {
    return new DangerLevel(1, 200, 250) {
      @Override
      public IShipAIDecision getDecision(ShipState shipState) {
        // leave the speed and angle as set.
        return DangerLevel.getDefaultDecision(shipState);
      }
    };
  }
}
