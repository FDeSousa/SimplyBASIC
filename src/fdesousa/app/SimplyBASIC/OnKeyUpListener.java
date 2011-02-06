package fdesousa.app.SimplyBASIC;

import android.view.View;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.EditText;

public class OnKeyUpListener extends SimplyBASIC implements OnKeyListener{

	final EditText etCW = (EditText)findViewById(R.id.etMain);
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		// getText, parse to interpreter, add "> "
		if(event.getAction()==KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			// Splits the string into tokens, split by Carriage Return
			// and the characters "> " that show user input area
			String[] tokens = etCW.getText().toString().split("\n> ");
			String token = tokens[tokens.length - 1];
			
			//String tempS = CommInt.ExecCommand(token);
			//etCW.append(tempS);
			
			// Append "> " and set cursor to last position on screen
			etCW.append("> ");
			etCW.setSelection(etCW.getText().length());
			return true;
		}
		// Ignore ACTION_DOWN of keyboard input
		if(event.getAction()==KeyEvent.ACTION_DOWN) {
			return false;
		}
		return false;
	}

}

/*
		etCW.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
	    	// getText, parse to interpreter, add "> "
				if(event.getAction()==KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					// Splits the string into tokens, split by Carriage Return
					// and the characters "> " that show user input area
					String[] tokens = etCW.getText().toString().split("\n> ");
					String token = tokens[tokens.length - 1];
					String tempS = CommInt.ExecCommand(token);
					etCW.append(tempS);

					// Append "> " and set cursor to last position on screen
					etCW.append("> ");
					etCW.setSelection(etCW.getText().length());
					return true;
				}
				// Ignore ACTION_DOWN of keyboard input
				if(event.getAction()==KeyEvent.ACTION_DOWN) {
					return false;
				}
				return false;
			}
        });
 */
