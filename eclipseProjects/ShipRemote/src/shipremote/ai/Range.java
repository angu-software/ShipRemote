package shipremote.ai;

/**
 * Defines the range from a minimum value to a maximum value.
 * @author Andreas Günther
 *
 */
public class Range {
  private int min = 0;
  private int max = 0;

  /**
   * Constructor.
   * @param min Minimum of range.
   * @param max Maximum of range.
   */
  public Range(int min, int max) {
    this.min = min;
    this.max = max;
  }

  /**
   * Gets the minimum value.
   * @return The minimum value.
   */
  public int getMin() {
    return min;
  }

  /**
   * Sets the minimum value.
   * @param min The minimum value.
   */
  public void setMin(int min) {
    this.min = min;
  }

  /**
   * Gets the maximum value.
   * @return The maximum value.
   */
  public int getMax() {
    return max;
  }

  /**
   * Sets the maximum value.
   * @param max The maximum value.
   */
  public void setMax(int max) {
    this.max = max;
  }

  /**
   * Checks whether a value is in the range of this object.
   * @param value Value to check.
   * @return True if the value is inside of the range, else false.
   */
  public boolean isInRange(int value) {
    if (value > this.min && value <= this.max) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append(Range.class.getSimpleName());
    strBuilder.append("(" + this.min + " - " + this.max);
    return strBuilder.toString();
  }

}
