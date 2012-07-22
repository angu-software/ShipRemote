package shipremote.control;

/**
 * Controller for steering the oar of the ship.
 * @author Andreas Günther
 *
 */
public class SteeringControl {
	
	private final int ANGLEFULLLEFT = 180;
	private final int ANGLEFULLRIGHT = 0;
	private final int ANGLEMIDDLE = 90;
	private int currentAngle = ANGLEMIDDLE;
	
	/**
	 * Gets the current angle of the oar.
	 * @return the current angle of the oar.
	 */
	public int getCurrentAngle() {
		return currentAngle;
	}
	
	/**
	 * Sets the current angle of the oar.
	 * @param currentAngle the current angle of the oar.
	 */
	public void setCurrentAngle(int currentAngle) {
		this.currentAngle = currentAngle;
	}
	
	/**
	 * Steers left. Increases the current angle.
	 * @param value The value for steering left.
	 */
	public void steerLeft(int value){
		this.currentAngle += value;
		if(this.currentAngle > ANGLEFULLLEFT){
			this.currentAngle = ANGLEFULLLEFT;
		}
	}
	
	/**
   * Steers right. Decreases the current angle.
   * @param value The value for steering right.
   */
	public void steerRight(int value){
		this.currentAngle -= value;
		if(this.currentAngle < ANGLEFULLRIGHT){
			this.currentAngle = ANGLEFULLRIGHT;
		}
	}
	
	/**
	 * Resets the angle for the oar.
	 */
	public void reset(){
		this.currentAngle = ANGLEMIDDLE;
	}
	
}
