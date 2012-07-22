package shipremote.control;

import shipremote.ai.ShipAIFactory;
import shipremote.communication.INetworkClient;
import shipremote.communication.TCPClient;
import shipremote.res.R;
import shipremote.ui.GesturesView;
import shipremote.ui.TextViewOutput;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Main application ShipRemote.
 * 
 * @author Andreas Günther
 * 
 */
public class Main extends Activity {

  static final String SHIP_REMOTE_TAG = "ShipRemote";
  private static boolean DEBUG = true;
  //private static String HOST = "192.168.1.123";
  private static String HOST = "141.45.205.75"; //host for simulator!
  private static int PORT = 2000;

  private static Main _instance = null;
  private GesturesView gestureView = null;
  private INetworkClient networkClient = null;
  private ShipController controller = null;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.main);
    Main._instance = this;
    this.gestureView = (GesturesView) this.findViewById(R.id.GesturesView);
    TextViewOutput.setTextView(this.gestureView);
    this.networkClient = new TCPClient();
    this.networkClient.setConnectionTimeout(10000);
    this.controller = new ShipController(this.gestureView, this.networkClient,
        ShipAIFactory.getShipAI());

  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(SHIP_REMOTE_TAG, "Start activity");
    try {
      Main.showToast(this.getString(R.string.tryToConnect));
      Log.i(SHIP_REMOTE_TAG, getString(R.string.tryToConnect));
      this.networkClient.connect(HOST, PORT);
      Main.showToast(this.getString(R.string.connected));
      Log.i(SHIP_REMOTE_TAG, getString(R.string.connected));
      this.controller.start();
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(SHIP_REMOTE_TAG, getString(R.string.connectionFaild), e);
      Main.showErrorMsg(this.getString(R.string.connectionFaild), !DEBUG);
    }
  }

  /**
   * Shows an error dialog.
   * 
   * @param msg
   *          Message for the dialog.
   * @param quitApplication
   *          Sets if the application should quit after commiting the dialog.
   *          True for quit, else false.
   */
  public static void showErrorMsg(String msg, boolean quitApplication) {
    AlertDialog.Builder alertDlgBuilder = new AlertDialog.Builder(
        Main._instance);
    alertDlgBuilder.setMessage(msg);
    alertDlgBuilder.setCancelable(false);
    alertDlgBuilder.setTitle(R.string.errorDlgTitle);
    alertDlgBuilder.setNeutralButton(R.string.btnOK,
        Main._instance.new AlertDialogClickListener(quitApplication));
    AlertDialog alertDlg = alertDlgBuilder.create();
    alertDlg.show();
  }

  /**
   * Shows a Toast.
   * @param msg Message to display on Toast.
   */
  public static void showToast(String msg) {
    Toast connectMsg = Toast.makeText(Main._instance, msg, Toast.LENGTH_SHORT);
    connectMsg.show();
  }

  /**
   * Writes message to the log.
   * @param text Text to log.
   */
  public static void log(String text) {

    Log.d(SHIP_REMOTE_TAG, text);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.i(SHIP_REMOTE_TAG, "Stop activity.");
    try {
      this.controller.stop();
      this.networkClient.disconnect();
    } catch (Exception e) {
      Main.showErrorMsg(e.getMessage(), false);
    } finally {
      Main.this.finish();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.i(SHIP_REMOTE_TAG, "Restarting activity");
    this.onStart();
  }

  private class AlertDialogClickListener implements OnClickListener {

    boolean quitApplication = false;

    public AlertDialogClickListener(boolean quitApplication) {
      super();
      this.quitApplication = quitApplication;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
      if (this.quitApplication) {
        Main.this.finish();
      }
    }

  }

}