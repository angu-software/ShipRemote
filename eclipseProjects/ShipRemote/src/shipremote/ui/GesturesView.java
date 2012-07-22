package shipremote.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.widget.TextView;

/**
 * A view that detects simple gestures and displays text.
 * 
 * @author Andreas Günther
 */
public class GesturesView extends TextView implements OnGestureListener,
    OnDoubleTapListener {

  // /////////////////////////
  // fields
  // /////////////////////////

  private int THRESHOLD = 30;
  private Point gestureStart = null;
  private List<IUserInputListener> slideListener = new ArrayList<IUserInputListener>();
  private SlideDirection slideDirection = SlideDirection.NONE;
  private boolean isGestureAbord = false;
  private GestureDetector gestureDecoder = null;
  private int lastValue;
  private int currentSlideValue;

  // /////////////////////////
  // constructor
  // /////////////////////////

  /**
   * Constructor.
   * 
   * @param context
   *          The Context the view is running in, through which it can access
   *          the current theme, resources, etc.
   * @see TextView
   */
  public GesturesView( Context context) {
    super(context);
    this.initialize();
  }

  /**
   * Constructor.
   * 
   * @param context
   *          The Context the view is running in, through which it can access
   *          the current theme, resources, etc.
   * @param attrs
   *          The attributes of the XML tag that is inflating the view.
   * @see TextView
   */
  public GesturesView( Context context,  AttributeSet attrs) {
    super(context, attrs);
    this.initialize();
  }

  /**
   * Constructor.
   * 
   * @param context
   *          The Context the view is running in, through which it can access
   *          the current theme, resources, etc.
   * @param attrs
   *          The attributes of the XML tag that is inflating the view.
   * @param defStyle
   *          The default style to apply to this view. If 0, no style will be
   *          applied (beyond what is included in the theme). This may either be
   *          an attribute resource, whose value will be retrieved from the
   *          current theme, or an explicit style resource.
   * @see TextView
   */
  public GesturesView( Context context,  AttributeSet attrs,
       int defStyle) {
    super(context, attrs, defStyle);
    this.initialize();
  }

  // /////////////////////////
  // public methods
  // /////////////////////////

  /**
   * Adds a IUserInputListener that is informed when a slide was performed by
   * the user.
   * 
   * @param l
   *          An IUserInputListener
   */
  public void addSlideListener(IUserInputListener l) {
    this.slideListener.add(l);
    this.initialize();
  }

  // /////////////////////////
  // private methods
  // /////////////////////////

  private void initialize() {

    this.gestureDecoder = new GestureDetector(this.getContext(), this);
    this.gestureDecoder.setOnDoubleTapListener(this);
  }

  private void onActionDown( MotionEvent event) {

    // set start point of gesture
    this.gestureStart = this.getScreenPoint(event);
    this.isGestureAbord = false;

    // inform observer
    this.informObserverAboutActionDown();
  }

  private void informObserverAboutActionDown() {
    for ( IUserInputListener listener : this.slideListener) {
      listener.onSlideStart();
    }
  }

  private void onActionMove( MotionEvent event) {

    if (this.gestureStart == null) {
      return;
    }

    // get current point
     Point currentPoint = this.getScreenPoint(event);
    // calculate difference between start point and current point
     Point diffPoint = this.getDifferencePoint(this.gestureStart,
        currentPoint);
    // define moving areas
     Bounds bounds = this.getSlideBounds();

    if (this.pointMatchesThresold(diffPoint)) {
      this.currentSlideValue = 0;
      // detect the slide direction and value
      this.slideDirection = this.getSlideDirectionWithinBounds(currentPoint,
          diffPoint, bounds);
      if (this.slideDirection == SlideDirection.NONE) {
        this.onGestureAbord();
        return;
      }
      this.currentSlideValue = this.getValueForSlideDirection(diffPoint);
      // inform the observer if value changed
      this.informSlideListener();
      this.lastValue = this.currentSlideValue;
    }
  }

  private void informSlideListener() {
    if (!this.isGestureAbord && this.lastValue != this.currentSlideValue) {
      this.onSlide(this.slideDirection, this.currentSlideValue);
    }
  }

  private int getValueForSlideDirection( Point point) {
    return this.slideDirection == SlideDirection.UP
        || this.slideDirection == SlideDirection.DOWN ? Math.abs(point.y)
        : Math.abs(point.x);
  }

  private boolean pointMatchesThresold( Point diffPoint) {
    return Math.abs(diffPoint.x) > this.THRESHOLD
        || Math.abs(diffPoint.y) > this.THRESHOLD;
  }

  private SlideDirection getSlideDirectionWithinBounds(
       Point currentPoint,  Point diffPoint,  Bounds bounds) {
    if (this.pointInVerticalBounds(currentPoint, bounds)) {
      if (diffPoint.y < 0) {
        return SlideDirection.DOWN;
      } else {
        return SlideDirection.UP;
      }
    } else if (this.pointInHorizontalBounds(currentPoint, bounds)) {
      if (diffPoint.x < 0) {
        return SlideDirection.LEFT;
      } else {
        return SlideDirection.RIGHT;
      }
    } else {
      return SlideDirection.NONE;
    }
  }

  private boolean pointInHorizontalBounds( Point currentPoint,
       Bounds bounds) {
    return currentPoint.y >= bounds.left && currentPoint.y <= bounds.right;
  }

  private boolean pointInVerticalBounds( Point currentPoint,
       Bounds bounds) {
    return currentPoint.x >= bounds.top && currentPoint.x <= bounds.bottom;
  }

  private Point getScreenPoint( MotionEvent event) {
     Point currentPoint = new Point();
    currentPoint.x = (int) event.getX();
    currentPoint.y = (int) event.getY();
    return currentPoint;
  }

  private Bounds getSlideBounds() {
     int xLBound = this.gestureStart.x - Math.abs(this.THRESHOLD);
     int xRBound = this.gestureStart.x + Math.abs(this.THRESHOLD);
     int yLBound = this.gestureStart.y - Math.abs(this.THRESHOLD);
     int yRBound = this.gestureStart.y + Math.abs(this.THRESHOLD);
     Bounds b = new Bounds();
    b.top = xLBound;
    b.bottom = xRBound;
    b.left = yLBound;
    b.right = yRBound;
    return b;
  }

  private Point getDifferencePoint( Point p1,  Point p2) {
     int xDiff = p1.x - p2.x;
     int yDiff = p1.y - p2.y;
    return new Point(xDiff, yDiff);
  }

  private void onActionUp( MotionEvent event) {

    // reset the gesture detection
    this.reset();

    // inform the observer
    for ( IUserInputListener listener : this.slideListener) {
      listener.onSlideEnd();
    }
  }

  private void onSlide( SlideDirection direction,  int value) {
    for ( IUserInputListener listener : this.slideListener) {
      listener.onSlide(direction, value);
    }
  }

  private void onGestureAbord() {
    for ( IUserInputListener listener : this.slideListener) {
      listener.onSlideGestureAbord();
    }
    this.isGestureAbord = true;
    this.reset();
  }

  private void reset() {
    this.gestureStart = null;
    this.slideDirection = SlideDirection.NONE;
  }

  // ///////////////////////////
  // overridden methods
  // ///////////////////////////

  @Override
  public boolean onTouchEvent( MotionEvent event) {

    if (this.gestureDecoder.onTouchEvent(event)) {
      return true;
    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
      this.onActionMove(event);
      return true;
    } else if (event.getAction() == MotionEvent.ACTION_UP) {
      this.onActionUp(event);
      return true;
    } else {
      this.onGestureAbord();
    }
    return false;
  }

  // ////////////////////////////////////
  // OnGestureListener implementation
  // ////////////////////////////////////

  @Override
  public boolean onDown( MotionEvent e) {
    this.onActionDown(e);
    return true;
  }

  @Override
  public boolean onFling( MotionEvent e1,  MotionEvent e2,
       float velocityX,  float velocityY) {
    return false;
  }

  @Override
  public void onLongPress( MotionEvent e) {
    for (IUserInputListener listener : this.slideListener) {
      listener.onLongPress();
    }
  }

  @Override
  public boolean onScroll( MotionEvent e1,  MotionEvent e2,
       float distanceX,  float distanceY) {
    return false;
  }

  @Override
  public void onShowPress( MotionEvent e) {
  }

  @Override
  public boolean onSingleTapUp( MotionEvent e) {
    return false;
  }

  // ////////////////////////////////////////
  // OnDoubleTapListener implementation
  // ////////////////////////////////////////

  @Override
  public boolean onDoubleTap(MotionEvent e) {
    for ( IUserInputListener inputListener : this.slideListener) {
      inputListener.onDoubleTab();
    }
    return true;
  }

  @Override
  public boolean onDoubleTapEvent(MotionEvent e) {
    return false;
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent e) {
    return false;
  }

}
