package fdesousa.app.SimplyBASIC;

import java.util.regex.*;
import java.io.*;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class CommandInterpreter {

	private String line;
	private String token;
	private String[] tokens = null;
	//private String output;
	private String[][] listing = null;
	private String[] progDetails = new String[3];
	private boolean runBASIC = false;
	private int C_HELLO_Step = 0;
	private static BASICProgram BP;
	private static BASICInterpreter BI;
	private static EditText etCW;
	private InputStream is;
	private OutputStream os;

	final static String[] commands = {
		"HELLO", "NEW", "OLD", "STOP", 
		"LIST", "SAVE", "UNSAVE", "CATALOG",
		"SCRATCH", "RENAME", "RUN", "BYE"
	};

	private static final String nL = "\n> ";

	// Start BASIC Interpreter
	final int C_HELLO = 0;
	// Make new program, erasing current
	final int C_NEW = 1;
	// Open a previously saved program
	final int C_OLD = 2;
	// Stop execution of the current program
	final int C_STOP = 3;
	// List the entered commands in the program
	final int C_LIST = 4;
	// Save the current program to storage
	final int C_SAVE = 5;
	// Delete the currently running program from storage
	final int C_UNSAVE = 6;
	// Display all previously saved programs
	final int C_CATALOG = 7;
	// Empty program listing, but keep name
	final int C_SCRATCH = 8;
	// Rename the program without removing program listing
	final int C_RENAME = 9;
	// Run the BASIC program as per the listing
	final int C_RUN = 10; 
	// Exit BASIC Interpreter
	final int C_BYE = 11;

	/**
	 * @param inputToken
	 */
	public CommandInterpreter(boolean runBASIC, InputStream is, OutputStream os, EditText edtextCW) {
		super();
		this.runBASIC = runBASIC;
		this.etCW = edtextCW;
		this.is = is;
		this.os = os;

		// Only initialises runBASIC with parsed value for now
		// If true, the CI is being executed to run BASIC commands
		this.etCW.setOnKeyListener(new OnKeyListener() {
			boolean runBASIC;
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_UP 
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					// Splits the string into tokens, split by Carriage Return
					// and the characters "> " that show user input area
					// last token is parsed to CommInt(Command Interpreter), 
					// and executed if needed. Inelegant, but works.

					// As they are used in an inner-class, these variables have to be declared again,
					// but they use the same values as the variables of the same name outside
					//String tokens[] = etCW.getText().toString().split("\n> ");
					//String token = tokens[tokens.length - 1];

					if (C_HELLO_Step > 0){
						tokens = etCW.getText().toString().split("-- ");
						C_HELLO(tokens[tokens.length - 1].trim());
					}
					else {
						tokens = etCW.getText().toString().split("\n> ");
						procCommand(tokens[tokens.length - 1].trim());
					}
					// String output = CommInt.procCommand(token);
					/*
					// If procCommand returns that it didn't process the file,
					// then it probably didn't output either. If it did, add a new line,
					// and add the > sign, to signify user input area
					if (procCommand(token)){
						etCW.append("\n> ");
					}
					else
					{
						etCW.append("> ");
					}

					// Set cursor to new position
					etCW.setSelection(etCW.getText().length());
					// Return true, action was handled.
					 */
					return true;
				}
				if(this.runBASIC == true 
						&& event.getAction()==KeyEvent.ACTION_DOWN 
						&& event.getKeyCode() == KeyEvent.KEYCODE_S) {
					// Used when running a BASIC application, to stop operation
					return false;
				}
				return false;
			}
		});// end onKeyListener
	}

	/*
	public boolean determineCommand(String token){
		// For now, split string by spaces. Each space signals new token.
		// Not pretty, and not compatible with original syntax, but it should
		// work well enough for now, to worry about more later.
		inputToken = token;
		tokens = inputToken.split(" ");
		output = "";

		if (IsBASIC(inputToken.trim()) == true){
			;
		}
		else
		{
			output = inputToken;
		}

		return false;
	}
	// Does a job that can be handled elsewhere with ease.
	 */

	public void procCommand(String line){		
		// For now, split string by spaces. Each space signals new token.
		// Not pretty, and not compatible with original syntax, but it should
		// work well enough for now, to worry about more later.
		this.line = line;
		tokens = this.line.split(" ");
		token = tokens[0].trim();
		//output = "";

		if (runBASIC == true){
			// Must mean someone has executed RUN command, and
			// this execCommand is a new instance for the interpreter
			BI = new BASICInterpreter(tokens);
			// TODO rest of runBASIC section
		}
		else{
			if (isNumber(token) == true){
				// Must insert call to BASICProgram, to add line to program
				// possibly parsing inputToken, and tokens[] for storage
				// and execution of commands later
				etCW.append("BASIC command" + nL);
				
				// TODO Add code to add the tokens to Program Listing
			}
			// TODO Add code to create, initialise and execute System command

			if (token.compareTo(commands[C_HELLO]) >= 0){
				
				etCW.append("NEW OR OLD-- ");
				C_HELLO_Step++;

			} // end HELLO command
			else if (token.compareTo(commands[C_NEW]) >= 0){
				BP.C_NEW(tokens[2], tokens[3]);
			} // end NEW command
			else if (token.compareTo(commands[C_OLD]) >= 0){
				BP.C_OLD(tokens[2], tokens[3], listing);
			} // end OLD command
			//else if (token == commands[C_STOP]){
				// TODO Add code to stop execution of the BASIC program
			//	;
			//} // end STOP command
			else if (token.compareTo(commands[C_LIST]) >= 0){
				if(tokens[3] == null){
					BP.C_LIST();
				}
				else if(isNumber(tokens[3])){
					int n = Integer.parseInt(tokens[3].trim());
					BP.C_LIST(n);
				}
			} // end LIST command
			else if (token.compareTo(commands[C_SAVE]) >= 0){
				;
			} // end SAVE command
			else if (token.compareTo(commands[C_UNSAVE]) >= 0){
				;
			} // end UNSAVE command
			else if (token.compareTo(commands[C_CATALOG]) >= 0){
				;
			} // end CATALOG command
			else if (token.compareTo(commands[C_SCRATCH]) >= 0){
				;
			} // end SCRATCH command
			else if (token.compareTo(commands[C_RENAME]) >= 0){
				;
			} // end RENAME command
			else if (token.compareTo(commands[C_RUN]) >= 0){
				;
			} // end RUN command
			else if (token.compareTo(commands[C_BYE]) >= 0){
				;
			} // end BYE command
			else{
				etCW.append("'" + tokens[0] + "'" + " : " + "'" + token + "'" + nL);
				etCW.append("ERROR WITH SYSTEM COMMAND" + nL);
				//output = "ERROR WITH SYSTEM COMMAND";
			}
		}

		//return true;
	}

	private void C_HELLO(String token){
		this.token = token.trim();

		// Steps of HELLO command and inputs
		switch (C_HELLO_Step) {

		// Step 1: Ask if new or old
		case 1:		// Check if NEW or OLD
			if (token.compareTo(commands[C_NEW]) >= 0){
				C_HELLO_Step++;
			}
			else if (token.compareTo(commands[C_OLD]) >= 0){
				C_HELLO_Step = 3;
			}
			else{
				etCW.append("ONLY TYPE 'NEW' OR 'OLD'-- ");
				break;
			}
			progDetails[0] = this.token;
			etCW.append("PROGRAM NAME-- ");
			break;

			// Step 2: Get program name
		case 2:		// If NEW, get input program name
			progDetails[1] = this.token;
			C_HELLO_Step = 4;
			etCW.append("USER NAME-- ");
			break;

		case 3:		// If OLD, get input program name
			progDetails[1] = this.token;
			// Load program listing from file
			C_HELLO_Step++;
			etCW.append("USER NAME-- ");
			break;

			// Step 3: Ask for user name
		case 4:		// Get input user name
			progDetails[2] = this.token;
			C_HELLO_Step++;
			etCW.append("WELCOME TO BASIC" + nL);
			break;

		case 5:		// 
			;
			break;

		default:
			break;
		}

		//return false;
	}

	/*private String System(String inputToken){
		this.inputToken = inputToken;

		// Figures out what to do with a System command

		return output;
	}

	private String BASIC(String inputToken){
		this.inputToken = inputToken;

		// Figures out what to do with a BASIC command

		return output;
	}*/

	private boolean isNumber(String chkToken){

		try {
			// If first token is number, assume it's a BASIC command
			// and return true, to state it's to be added to queue.

			//Integer.parseInt(chkToken);
			//Pattern integerPattern = Pattern.compile("^-?\\d+$");
			//Matcher matchesInteger = integerPattern.matcher(chkToken);
			//boolean isInteger = matchesInteger.matches();

			boolean isInteger = Pattern.matches("^-?\\d+$", chkToken);
			return isInteger;

			//if (isInteger == true){
			//	return true;
			//}
		}
		catch (NumberFormatException ex){
			// output = ex.toString();
			// As first token isn't number, return false,
			// command is system command, and is to be 
			// executed immediately by the interpreter.
			return false;
		}
	}
}