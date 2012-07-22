import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import shipremote.communication.ShipCommandFactory;

public class SimulatorMain {

  private static final int LEVELVALUE_ONE = 230;
  private static final int LEVELVALUE_TWO = 175;
  private static final int LEVELVALUE_THREE = 125;
  private static final int LEVELVALUE_FOUR = 80;
  private static final int SLEEPTIME = 1000;
  private static final int PORT = 2000;
  private int dangerLevel = 1;
  //private InputStream inputStream = null;
  private OutputStream outputStream = null;

  /**
   * @param args
   */
  public static void main(String[] args) {
    new SimulatorMain().start();
  }

  private void start() {
    try {
      ServerSocketFactory serverSocketFactory = ServerSocketFactory
          .getDefault();
      ServerSocket serverSocket = serverSocketFactory.createServerSocket(PORT);

      System.out.println("Waiting for client to connect...");
      Socket clientSocket = serverSocket.accept();
      System.out.println("Client to connected.");
      
      //this.inputStream = clientSocket.getInputStream();
      this.outputStream = clientSocket.getOutputStream();

      while (true) {

        this.sendSensorData();

        if (this.dangerLevel == 4) {
          this.dangerLevel = 1;
        } else {
          this.dangerLevel++;
        }
        Thread.sleep(SLEEPTIME);
      }

    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (InterruptedException interuptEx) {
      interuptEx.printStackTrace();
    }

  }

  private void sendSensorData() throws IOException {
    if (this.outputStream != null) {
      byte[] command = this.getSonarCommand();
      this.outputStream.write(command);
      System.out.println((this.getSonarRange()) & 0xFF);
    }
  }

  private byte[] getSonarCommand() {
    return ShipCommandFactory.getSonarFrame(this.getSonarRange());
  }

  private int getSonarRange() {
    switch (this.dangerLevel) {
      case 4:
        return LEVELVALUE_FOUR;
      case 3:
        return LEVELVALUE_THREE;
      case 2:
        return LEVELVALUE_TWO;
      case 1:
        return LEVELVALUE_ONE;
      default:
        return LEVELVALUE_ONE;
    }
  }
}
