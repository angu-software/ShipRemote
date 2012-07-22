package shipremote.ai.base;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

/**
 * A processor class to process ShipAIs in an separate thread.
 * 
 * @author Andreas Günther
 * 
 */
public class ShipAIProcessor extends Thread {

  /**
   * Message identifier. A Message with this id is send when the AI processing
   * was finished.
   */
  public static final int DATA_PROCESSED_BY_AI = 0x102;

  private IShipAI ai;
  private IShipAIDecision aiDecision;
  private ShipState currentShipData;
  private List<Handler> listener;
  private boolean enabled = false;

  /**
   * Constructor.
   * 
   * @param ai
   */
  public ShipAIProcessor(IShipAI ai) {
    this.listener = new ArrayList<Handler>();
    this.ai = ai;
  }

  /**
   * Gets a value that indicates whether the processor is enabled.
   * 
   * @return True if enabled, else false.
   */
  public boolean isEnabled() {
    if (this.ai != null) {
      return enabled;
    }
    return false;
  }

  /**
   * Sets whether the processor is enabled or disabled.
   * 
   * @param enabled
   *          True to enable processor, else false.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Gets the currend ship data to process with the AI.
   * 
   * @return The data as ShipState
   */
  public ShipState getCurrentShipData() {
    return currentShipData;
  }

  /**
   * Sets the ship data to process with the AI.
   * 
   * @param currentShipData
   *          The ship data.
   */
  public void setCurrentShipData(ShipState currentShipData) {
    this.currentShipData = currentShipData;
  }

  /**
   * Gets the decision of the AI after processing is finished.
   * 
   * @return A IShipAIDecision.
   */
  public IShipAIDecision getAiDecision() {
    return aiDecision;
  }

  /**
   * Adds a listener to the ShipAIProcessor that is informed when the AI
   * processing is finished.
   * 
   * @param listener The listener.
   */
  public void addDataProcessedListener(Handler listener) {
    if (listener != null) {
      this.listener.add(listener);
    }
  }

  @Override
  public void run() {
    if (this.isEnabled()) {
      this.processAI();
      this.informListener();
    }
  }

  private synchronized void processAI() {
    this.aiDecision = this.ai.getDecision(this.currentShipData);

  }

  private void informListener() {
    for (Handler listener : this.listener) {
      listener.sendEmptyMessage(DATA_PROCESSED_BY_AI);
    }
  }
}
