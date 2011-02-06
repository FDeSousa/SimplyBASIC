package fdesousa.app.SimplyBASIC;

import java.util.regex.*;
import java.io.*;

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
	private String tokens[] = new String[255];
	private String output;
	private boolean runBASIC;
	
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
	public CommandInterpreter(boolean runBASIC, InputStream is, OutputStream os) {
		super();
		this.runBASIC = runBASIC;
		// Only initialises runBASIC with parsed value for now
		// If true, the CI is being executed to run BASIC commands
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

	public String procCommand(String token){		
		// For now, split string by spaces. Each space signals new token.
		// Not pretty, and not compatible with original syntax, but it should
		// work well enough for now, to worry about more later.
		inputToken = token;
		tokens = inputToken.split(" ");
		output = "";
		
		if (runBASIC == true){
			// Must mean someone has executed RUN command, and
			// this execCommand is a new instance for the interpreter
			BASICInterpreter BI = new BASICInterpreter(tokens);
			// TODO rest of runBASIC section
		}
		else{
			if (isBASIC(tokens[0]) == true){
				// Must insert call to BASICProgram, to add line to program
				// possibly parsing inputToken, and tokens[] for storage
				// and execution of commands later
				//output = "BASIC command " + tokens[0];
				// TODO Add code to add the tokens to Program Listing
			}
			else{
				//output = inputToken;
				// TODO Add code to create, initialise and execute System command
				//SysInterpreter SI = new SysInterpreter(tokens);
				//output = SI.execCommand();
				
				if (tokens[0] == commands[C_HELLO]){
					// For now, syntax of HELLO command should include:
					//		Arg1: --NEW | --OLD
					//		Arg2: program name
					//		Arg3: user name
					// This is temporary, I want to get this manipulating
					// EditText or the input/output streams asap
					;
				} // HELLO command
				else if (tokens[0] == commands[C_NEW]){
					;
				} // NEW command
				else if (tokens[0] == commands[C_OLD]){
					;
				} // OLD command
				else if (tokens[0] == commands[C_STOP]){
					;
				} // STOP command
				else if (tokens[0] == commands[C_LIST]){
					String[][] tempListing;
				} // LIST command
				else if (tokens[0] == commands[C_SAVE]){
					;
				} // SAVE command
				else if (tokens[0] == commands[C_UNSAVE]){
					;
				} // UNSAVE command
				else if (tokens[0] == commands[C_CATALOG]){
					;
				} // CATALOG command
				else if (tokens[0] == commands[C_SCRATCH]){
					;
				} // SCRATCH command
				else if (tokens[0] == commands[C_RENAME]){
					;
				} // RENAME command
				else if (tokens[0] == commands[C_RUN]){
					;
				} // RUN command
				else if (tokens[0] == commands[C_BYE]){
					;
				} // BYE command
				else{
					output = "ERROR WITH SYSTEM COMMAND";
				}
			}
		}
		
		return output;
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

	private boolean isBASIC(String chkToken){

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