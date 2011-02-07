package fdesousa.app.SimplyBASIC;

import java.util.regex.*;
import java.io.*;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class CommandInterpreter {
	/*
	@SuppressWarnings("unused")
	private DataInputStream	inStream;
	@SuppressWarnings("unused")
	private PrintStream 	outStream;

	public CommandInterpreter(InputStream in, OutputStream out) {
		if (in instanceof DataInputStream)
			inStream = (DataInputStream) in;
		else
			inStream = new DataInputStream(in);
		if (out instanceof PrintStream)
			outStream = (PrintStream) out;
		else
			outStream = new PrintStream(out);
	}
	 */

	private String inputToken;
	private String[] tokens = new String[255];
	private String output;
	private String[][] listing = new String[65535][255];
	private boolean runBASIC;
	private static BASICProgram BP;
	private static BASICInterpreter BI;
	private static EditText etCW;
	private InputStream is;
	private OutputStream os;
	
	final static String commands[] = {
		"HELLO", "NEW", "OLD", "STOP", 
		"LIST", "SAVE", "UNSAVE", "CATALOG",
		"SCRATCH", "RENAME", "RUN", "BYE"
	};
	
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
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_UP 
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					// Splits the string into tokens, split by Carriage Return
					// and the characters "> " that show user input area
					// last token is parsed to CommInt(Command Interpreter), 
					// and executed if needed. Inelegant, but works.
					
					// As they are used in an inner-class, these Strings have to be declared again,
					// but they use the same values as the variables of the same name outside
					String tokens[] = etCW.getText().toString().split("\n> ");
					String token = tokens[tokens.length - 1];
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
				// Ignore ACTION_DOWN of keyboard input
				//				if(event.getAction()==KeyEvent.ACTION_DOWN) {
				//					return false;
				//				}
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

	public boolean procCommand(String token){		
		// For now, split string by spaces. Each space signals new token.
		// Not pretty, and not compatible with original syntax, but it should
		// work well enough for now, to worry about more later.
		inputToken = token;
		tokens = inputToken.split(" ");
		output = "";
		
		if (runBASIC == true){
			// Must mean someone has executed RUN command, and
			// this execCommand is a new instance for the interpreter
			BI = new BASICInterpreter(tokens);
			// TODO rest of runBASIC section
		}
		else{
			if (isNumber(tokens[0]) == true){
				// Must insert call to BASICProgram, to add line to program
				// possibly parsing inputToken, and tokens[] for storage
				// and execution of commands later

				// TODO Add code to add the tokens to Program Listing
			}
			else{
				// TODO Add code to create, initialise and execute System command
				
				if (tokens[0] == commands[C_HELLO]){
					// For now, syntax of HELLO command should include:
					//		Arg1: --NEW | --OLD
					//		Arg2: program name
					//		Arg3: user name
					// This is temporary, I want to get this manipulating
					// EditText or the input/output streams asap
					if (tokens[1] == "--NEW"){
						BP = new BASICProgram(tokens[2], tokens[3]);
					}
					else if (tokens[2] == "--OLD"){
						BP = new BASICProgram(tokens[2], tokens[3], listing);
					}
					else{
						// TODO Add a final conditional statement
						;
					}
				} // end HELLO command
				else if (tokens[0] == commands[C_NEW]){
					BP.C_NEW(tokens[2], tokens[3]);
				} // end NEW command
				else if (tokens[0] == commands[C_OLD]){
					BP.C_OLD(tokens[2], tokens[3], listing);
				} // end OLD command
				else if (tokens[0] == commands[C_STOP]){
					// TODO Add code to stop execution of the BASIC program
					;
				} // end STOP command
				else if (tokens[0] == commands[C_LIST]){
					if(tokens[3] == null){
						BP.C_LIST();
					}
					else if(isNumber(tokens[3])){
						int n = Integer.parseInt(tokens[3].trim());
						BP.C_LIST(n);
					}
				} // end LIST command
				else if (tokens[0] == commands[C_SAVE]){
					;
				} // end SAVE command
				else if (tokens[0] == commands[C_UNSAVE]){
					;
				} // end UNSAVE command
				else if (tokens[0] == commands[C_CATALOG]){
					;
				} // end CATALOG command
				else if (tokens[0] == commands[C_SCRATCH]){
					;
				} // end SCRATCH command
				else if (tokens[0] == commands[C_RENAME]){
					;
				} // end RENAME command
				else if (tokens[0] == commands[C_RUN]){
					;
				} // end RUN command
				else if (tokens[0] == commands[C_BYE]){
					;
				} // end BYE command
				else{
					output = "ERROR WITH SYSTEM COMMAND";
				}
			}
		}
		
		return true;
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