package fdesousa.app.SimplyBASIC;

//Java imports
import java.io.*;

// Android imports
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class SimplyBASIC extends Activity {
	// Called when the activity is first created.
	
	InputStream is;
	OutputStream os;
	
	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Declare and initialise variables used class-wide
		final String lines[] = new String[255];	
		final String token = lines[0];
		final String output = lines[0];
		final CommandInterpreter CommInt = new CommandInterpreter(false, is, os);
		final EditText etCW = (EditText)findViewById(R.id.etMain);
		
		// Initialise etCW, display Welcome message to user, set cursor position
		etCW.setText("WELCOME TO SIMPLYBASIC\n"
				+ "TYPE 'HELP' FOR A LIST OF COMMANDS\n"
				+ "> ");
		etCW.setSelection(etCW.getText().length());

		// TODO Add TextChangedListener, get it working properly
		// TextChangedListener to stop users changed text before the new line they're given
		// TextChangedListener tcl = new TextChangedListener();
		// etCW.addTextChangedListener(tcl);

		// OnKeyUpListener okl = new OnKeyUpListener();
		// etCW.setOnKeyListener(okl);

		// OnKeyListener to handle users entering data into command window
		// TODO Must move the OnKeyListener to somewhere more out of the way
		etCW.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					// Splits the string into tokens, split by Carriage Return
					// and the characters "> " that show user input area
					// last token is parsed to CommInt(Command Interpreter), 
					// and executed if needed. Inelegant, but works.
					
					// As they are used in an inner-class, these Strings have to be declared again,
					// but they use the same values as the variables of the same name outside
					String tokens[] = etCW.getText().toString().split("\n> ");
					String token = tokens[tokens.length - 1];
					String output = CommInt.procCommand(token);
					
					// Only write out information if 'output' > 0 length
					// should do something more elegant for output if there is none
					// like adding a bunny rabbit ASCII picture or something
					if (output.length() > 1){
						etCW.append(output + "\n> ");
					}
					else
					{
						etCW.append("> ");
					}

					// Set cursor to new position
					etCW.setSelection(etCW.getText().length());
					// Return true, action was handled.
					return true;
				}
				// Ignore ACTION_DOWN of keyboard input
				//				if(event.getAction()==KeyEvent.ACTION_DOWN) {
				//					return false;
				//				}
				return false;
			}
		});// end onKeyListener



		/*
        private EditText getEditText() {

            if (inputBox == null) {

                inputBox = (EditText) findViewById(R.id.inputEditText);

                inputBox.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        // XXX do something
                    }

                    public void beforeTextChanged(CharSequence s, int start,
                            int count, int after) {
                        // XXX do something
                    }

                    public void onTextChanged(CharSequence s, int start,
                            int before, int count) {
                        makePreview();
                    }
                });

            }

            return inputBox;
        }*/
	}    
}

/*        
Meant to test InToPostfix.java, but kept making an exception, so ignored for now.
    try {
		String tempString = "(1+2*3)/3.14159";
		String outputString = "Failure";

		Queue<String> Pfix = new LinkedList<String>();

		tempString.trim();

		InToPostfix itP = new InToPostfix();

		Pfix = itP.InfixToPostfix(tempString.trim());
		outputString = Pfix.poll();

		while (Pfix.isEmpty() != true){
			outputString += Pfix.poll() + " ";
		}
		outputString += "\n";
		etCW.append(outputString);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} //end try and catch
 */