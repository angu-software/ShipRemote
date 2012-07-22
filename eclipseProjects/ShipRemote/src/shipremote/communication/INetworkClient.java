package shipremote.communication;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * An interface that provides methods for a Network client such as a TCP or UDP
 * client.
 * 
 * @author Andreas Günther
 */
public interface INetworkClient {

  // ///////////////////////////
  // getter
  // ///////////////////////////
  /**
   * Gets the connection state of the TCPClient.
   * 
   * @return True if TCPClient is connected, else false.
   */
  public abstract boolean isConnected();

  /**
   * Gets the Connection Timeout.
   * 
   * @return The timeout value in milliseconds.
   */
  public abstract int getConnectionTimeout();

  /**
   * Sets the connection timeout.
   * 
   * @param connectionTimeout
   *          The timeout value in milliseconds.
   */
  public abstract void setConnectionTimeout(int connectionTimeout);

  /**
   * Connects the client to a host.
   * 
   * @param host
   *          The IP address of the host.
   * @param port
   *          The port the host is listening.
   * @throws UnknownHostException
   * @throws IOException
   */
  public abstract void connect(String host, int port)
      throws UnknownHostException, IOException;

  /**
   * Disconnects the client from the host.
   * 
   * @throws IOException
   * @throws InterruptedException
   */
  public abstract void disconnect() throws IOException, InterruptedException;

  /**
   * Sends data to the host.
   * 
   * @param shipCommand
   *          A ShipData containing the data to send.
   * @return true if the data was send successfully, false otherwise.
   * @throws IOException
   */
  public abstract boolean sendData(ShipData shipCommand) throws IOException;

  /**
   * Reads data from the host, if available.
   * 
   * @return A byte array with the received data. If no data was received
   *         returns null.
   * @throws IOException
   */
  public abstract byte[] readData() throws IOException;

  /**
   * Adds a IDataReceiverListener.
   * 
   * @param listener
   *          An IDataReceiverListener.
   */
  public abstract void addDataReceiverListener(IDataReceiverListener listener);

  /**
   * Starts the listening for data.
   */
  public abstract void listen();

  /**
   * Stops listening for data.
   */
  public abstract void stopListening();

}