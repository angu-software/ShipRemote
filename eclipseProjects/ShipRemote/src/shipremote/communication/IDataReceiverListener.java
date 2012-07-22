package shipremote.communication;

/**
 * Interface for a listener that is informed when the TCPClient received data.
 * 
 * @author Andreas Günter
 * 
 */
public interface IDataReceiverListener {
  /**
   * Called when data is received..
   * @param receivedData The received data.
   */
  public void dataReceived(ShipData receivedData);
}
