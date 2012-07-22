package shipremote.ui;

import android.widget.TextView;

/**
 * Provides methods to simply access the a TextView for output text.
 * @author Andreas Günther
 *
 */
public class TextViewOutput {

	private static TextView tv = null;

	/**
	 * Sets a TextView
	 * @param tv The TextView
	 */
	public static void setTextView(TextView tv) {
		TextViewOutput.tv = tv;
	}

	/**
	 * Sets text to display.
	 * @param text Text to display.
	 */
	public static void setText(String text) {
		TextViewOutput.tv.setText(text);
	}

	/**
	 * Appends a line to the displayed text.
	 * @param lineText Text to append.
	 */
	public static void appendLine(String lineText) {
		TextViewOutput.tv.append("\n" + lineText);
	}

	/**
	 * Appends text to the displayed text.
	 * @param text Text to append.
	 */
	public static void append(String text) {
		TextViewOutput.tv.append(text);
	}

}
