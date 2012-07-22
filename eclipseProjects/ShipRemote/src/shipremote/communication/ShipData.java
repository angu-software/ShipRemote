package shipremote.communication;

/**
 * Provides data that defines the state of the ship.
 * @author Andreas Günther
 *
 */
public class ShipData {

	private int dataValue = 0;
	private ShipDataType dataType = ShipDataType.INVALID;

	/**
	 * Constructor.
	 * @param dataType Type of data.
	 * @param dataValue The value of the data.
	 */
	public ShipData(ShipDataType dataType, int dataValue) {
		super();
		this.dataValue = dataValue;
		this.dataType = dataType;
	}

	/**
	 * Gets the data.
	 * @return The data.
	 */
	public int getDataValue() {
		return dataValue;
	}

	/**
	 * Sets the data.
	 * @param dataValue The data to set.
	 */
	public void setDataValue(int dataValue) {
		this.dataValue = dataValue;
	}

	/**
	 * Gets the type of the data.
	 * @return The type.
	 */
	public ShipDataType getDataType() {
		return dataType;
	}

	/**
	 * Sets the type.
	 * @param dataType The type.
	 */
	public void setDataType(ShipDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		String str = this.getClass().getSimpleName().toString() + ": ";
		str += this.dataType.name();
		str += "(" + Integer.toString(this.dataValue) + ")";
		return str;
	}
	
}
