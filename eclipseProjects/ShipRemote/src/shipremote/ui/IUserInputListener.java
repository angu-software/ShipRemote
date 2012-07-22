package shipremote.ui;

/**
 * Listener interface for user input.
 * @author Andreas Günther
 *
 */
public interface IUserInputListener {

  /**
   * User has performed a slide.
   * @param direction Direction of the slide.
   * @param value Slide value.
   */
	void onSlide(SlideDirection direction, int value);
	
	/**
	 * Slide starts.
	 */
	void onSlideStart();
	
	/**
	 * Slide ends.
	 */
	void onSlideEnd();
	
	/**
	 * Gesture aboard.
	 */
	void onSlideGestureAbord();
	
	/**
	 * User performs double tab.
	 */
	void onDoubleTab();
	
	/**
	 * User performs long press.
	 */
	void onLongPress();

}
