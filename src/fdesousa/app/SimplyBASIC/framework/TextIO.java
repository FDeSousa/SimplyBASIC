package fdesousa.app.SimplyBASIC.framework;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class TextIO implements OnKeyListener, TextWatcher{
	boolean hasNewLine;
	String nextLine;
	EditText editText;

	public TextIO(EditText editText) {
		this.editText = editText;
		hasNewLine = false;
		nextLine = new String();
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		return false;
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	/**
	 * 
	 * @return
	 */
	public String readLine() {
		while(!hasNewLine){
			//	Should put this into a new thread if I'm going to be
			//+	in a nearly endless loop until a user presses CR
			try {
				wait();
			} catch (InterruptedException e) {
				// Naughty, naughty! Silently ignoring this
			}
		}
		return nextLine;
	}
	
	/**
	 * Convenience method: Append the parsed line to our editText with a new line added
	 * @param line what we want to write out
	 */
	public void writeLine(String line) {
		editText.append(line + "\n");
	}
	
	/**
	 * Convenience method: Append the parsed line to our editText
	 * @param line what we want to write out
	 */
	public void write(String line) {
		editText.append(line);
	}
}