package shipremote.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * A TCPClient that sends and receives data via socket.
 * 
 * @author Andreas Günther
 * 
 */
public class TCPClient implements Runnable, INetworkClient {

  // /////////////////////////////
  // fields
  // /////////////////////////////

  private static final int FRAME_LENGTH = 4;
  private String host = "";
  private int port = 0;
  private int connectionTimeout = 10000;
  private Socket socket = null;
  private InputStream inStream = null;
  private OutputStream outStream = null;
  private List<IDataReceiverListener> receiverListener = null;
  private Thread listenerTread = null;
  private boolean isListening = false;
  private boolean isConnected = false;

  // ///////////////////////////
  // constructor
  // ///////////////////////////
  /**
   * Constructor.
   */
  public TCPClient() {
    this.receiverListener = new ArrayList<IDataReceiverListener>();
  }

  // ///////////////////////////
  // private methods
  // ///////////////////////////

  private ShipData bytesToShipData(final Byte[] receivedData) {
    if (receivedData[0] == ShipCommandFactory.DATA_START) {
      final int value = (int) receivedData[2] & 0xFF;
      return new ShipData(ShipDataType.valueOf(receivedData[1]), value);
    }
    return null;
  }

  private byte[] shipDataToBytes(final ShipData shipCommand) {
    byte[] data = null;
    switch (shipCommand.getDataType()) {
      case INIT_RANGING:
        data = ShipCommandFactory.getInitRangingFrame();
        break;
      case STOP_RANGING:
        data = ShipCommandFactory.getStopRangingFrame();
        break;
      case SPEED_FORWARD:
        data = ShipCommandFactory.getForwardSpeedFrame(shipCommand
            .getDataValue());
        break;
      case SPEED_BACKWARD:
        data = ShipCommandFactory.getBackwardSpeedFrame(shipCommand
            .getDataValue());
        break;
      case DIRECTION:
        data = ShipCommandFactory.getDirectionFrame(shipCommand.getDataValue());
        break;
      default:
        data = ShipCommandFactory.getAliveFrame();
        break;
    }
    return data;
  }

  // ////////////////////////////////
  // INetworkClient implementation
  // ////////////////////////////////
  /*
   * (non-Javadoc)
   * 
   * @see shipremote.communication.NetworkClient#isConnected()
   */
  public boolean isConnected() {
    return this.isConnected;
  }

  /*
   * (non-Javadoc)
   * 
   * @see shipremote.communication.NetworkClient#getConnectionTimeout()
   */
  public int getConnectionTimeout() {
    return this.connectionTimeout;
  }

  /*
   * (non-Javadoc)
   * 
   * @see shipremote.communication.NetworkClient#setConnectionTimeout(int)
   */
  public void setConnectionTimeout(final int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  /*
   * (non-Javadoc)
   * 
   * @see shipremote.communication.NetworkClient#connect(java.lang.String, int)
   */
  public void connect(final String host, final int port)
      throws UnknownHostException, IOException {
    this.host = host;
    this.port = port;

    final InetSocketAddress remoteAddr = new InetSocketAddress(this.host,
        this.port);
    this.socket = new Socket();
    this.socket.connect(remoteAddr, this.connectionTimeout);
    this.inStream = this.socket.getInputStream();
    this.outStream = this.socket.getOutputStream();
    this.isConnected = true;

  }

  /*
   * (non-Javadoc)
   * 
   * @see shipremote.communication.NetworkClient#disconnect()
   */
  public void disconnect() throws IOException, InterruptedException {

    this.isListening = false;
    if (this.listenerTread != null) {
      this.listenerTread.join();
    }
    if (this.socket != null) {
      try {
        this.socket.close();
      } catch (final IOException e) {
        throw e;
      } finally {
        this.resetListenerFields();
      }
    }
  }

  private void resetListenerFields() {
    this.socket = null;
    this.inStream = null;
    this.outStream = null;
    this.host = "";
    this.port = 0;
    this.isConnected = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * shipremote.communication.NetworkClient#sendData(shipremote.communication
   * .ShipData)
   */
  public boolean sendData(final ShipData shipCommand) throws IOException {
    final byte[] data = this.shipDataToBytes(shipCommand);
    if (this.streamAndDataAvailable(data)) {
      this.outStream.write(data);
      return true;
    }
    return false;
  }

  private boolean streamAndDataAvailable(final byte[] data) {
    return this.outStream != null && data != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see shipremote.communication.NetworkClient#readData()
   */
  public byte[] readData() throws IOException {
    byte[] receivedData = null;
    if (this.inStream != null) {
      int byteCount;
      byteCount = this.inStream.available();
      if (byteCount >= 4) {
        receivedData = new byte[byteCount];
        this.inStream.read(receivedData);
      }
    }
    return receivedData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * shipremote.communication.NetworkClient#addDataReceiverListener(shipremote
   * .communication.IDataReceiverListener)
   */
  public void addDataReceiverListener(final IDataReceiverListener listener) {
    if (listener != null) {
      this.receiverListener.add(listener);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see shipremote.communication.NetworkClient#listen()
   */
  public void listen() {
    this.listenerTread = new Thread(this);
    this.isListening = true;
    this.listenerTread.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see shipremote.communication.NetworkClient#stopListening()
   */
  public void stopListening() {
    this.isListening = false;
  }

  @Override
  /**
   * Runs the listening logic.
   */
  public void run() {
    while (this.isListening) {
      byte[] receivedData;
      try {
        receivedData = this.readData();
        if (receivedData != null) {
          // get the frame bytes
          final Byte[] shipDataBytes = this.getFrameBytes(receivedData);
          // convert to ShipData
          final ShipData shipData = this.bytesToShipData(shipDataBytes);
          if (shipData != null) {
            // inform the listener
            this.informListenerAboutDataReceived(shipData);
          }
        }
      } catch (final IOException e) {
        this.handleException(e);
        // stops the thread
        this.isListening = false;
      }
    }
  }

  private Byte[] getFrameBytes(final byte[] receivedData) {
    final Byte[] shipDataBytes = new Byte[FRAME_LENGTH];
    final int len = shipDataBytes.length;
    for (int i = 0; i < len; i++) {
      shipDataBytes[i] = new Byte(receivedData[receivedData.length - (len - i)]);
    }
    return shipDataBytes;
  }

  private void handleException(final IOException e) {
    System.out.println(e.toString());
    System.out.println("Listening canceled!");
    e.printStackTrace();
  }

  private void informListenerAboutDataReceived(final ShipData shipData) {
    for (final IDataReceiverListener listener : this.receiverListener) {
      listener.dataReceived(shipData);
    }
  }

}
