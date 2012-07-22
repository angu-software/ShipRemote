package shipremote.control;

import java.util.TimerTask;

import android.os.Handler;

class MessageTimerTask extends TimerTask {

	private Handler handler = null;
	private int messageID = 0;
	
	
	
	public MessageTimerTask(Handler handler, int messageID) {
		super();
		this.handler = handler;
		this.messageID = messageID;
	}

	@Override
	public void run() {
		handler.sendEmptyMessage(this.messageID);
	}

}
