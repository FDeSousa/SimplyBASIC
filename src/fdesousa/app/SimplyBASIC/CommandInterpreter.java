package fdesousa.app.SimplyBASIC;

import java.util.Hashtable;
import java.util.regex.*;
import java.io.*;

import android.os.Environment;
//import android.util.Log; // Unused for now, may use for logging activity
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class CommandInterpreter {
	// Get SD card root, then set working directory:
	private File sdRoot = Environment.getExternalStorageDirectory();
	private File dir = new File (sdRoot.getAbsolutePath() + "/SimplyBASIC");
	// Used for storing the code listing during certain operations:
	private Hashtable<String, String[]> codeList = new Hashtable<String, String[]>(2011, 0.75f);
	private String token = "";	// token to work on. May remove, to replace with inputToken
	private String[] tokens = null; // whole line, divided into tokens
	private String lineNumber = "";
	private static BASICProgram BP;
	// To take control of etCW, once SimplyBASIC parses it:
	private EditText etCW;
	private String[] lines = null;
	private char[] line = null;
	Tokenizer tokenizer = new Tokenizer();
	// Array of commands for the system, to make matching easier:
	final static String[] commands = {
		"HELLO", "NEW", "OLD", "STOP", 
		"LIST", "SAVE", "UNSAVE", "CATALOG",
		"SCRATCH", "RENAME", "RUN", "BYE" };
	// Makes it easier to find the right command in the above array:
	final int C_HELLO	 =  0;	// Start BASIC Interpreter
	final int C_NEW		 =  1;	// Make new program, erasing current
	final int C_OLD		 =  2;	// Open a previously saved program
	final int C_STOP	 =  3;	// Stop execution of the current program
	final int C_LIST	 =  4;	// List the entered commands in the program
	final int C_SAVE	 =  5;	// Save the current program to storage
	final int C_UNSAVE	 =  6;	// Delete the currently running program from storage
	final int C_CATALOG	 =  7;	// Display all previously saved programs
	final int C_SCRATCH	 =  8;	// Empty program listing, but keep name
	final int C_RENAME	 =  9;	// Rename the program without removing program listing
	final int C_RUN		 = 10;	// Run the BASIC program as per the listing 
	final int C_BYE		 = 11;	// Exit BASIC Interpreter
	// Used as counters for the named operations:
	private int C_HELLO_Step = 0, C_NEW_Step = 0, C_OLD_Step = 0;
	private String[] progDetails = new String[3];
	// uL = user line. Users input new commands where they see "> "
	private static final String uL = "\n> ";

	public CommandInterpreter(EditText editText) {
		super();
		this.etCW = editText;
		// Check if the working directory actually exists, if not, make it
		if (dir.exists() == false 
				&& dir.isDirectory() == false){
			dir.mkdir();
		}

		this.etCW.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_UP 
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					// Could have saved some processor time here, by splitting
					// the string by '-- ' and '\n> ' at the same time, and 
					// storing into new variables each, and done less if .. else if
					// statements, but it's a trade-off for now at least
					
					lines = etCW.getText().toString().split("\n");
					line = lines[lines.length - 1].toCharArray();
					
					if (C_HELLO_Step >= 1){
						tokens = etCW.getText().toString().split("-- ");// to delete after tokenizer implemented fully
						C_HELLO(tokens[tokens.length - 1].trim()); //t.nextToken to be used
					}
					else if (C_NEW_Step >= 1){
						tokens = etCW.getText().toString().split("-- ");
						C_NEW(tokens[tokens.length - 1].trim());
					}
					else if (C_OLD_Step >= 1){
						tokens = etCW.getText().toString().split("-- ");
						C_OLD(tokens[tokens.length - 1].trim());
					}
					else {
						tokens = etCW.getText().toString().split("\n> ");

						/**
						 * Numbers returned upon ending execution:
						 * 	--	PROBLEMS WITH SYSTEM COMMANDS
						 * -2:	Problem during execution, need nL and generic error
						 * -1:	Problem during execution, need nL, error already shown
						 * 	0:	No problems, needs nL
						 *  1:	No problems, doesn't need nL
						 */
						
						switch(procCommand(tokens[tokens.length - 1].trim())){							
						case -2:
							etCW.append("ERROR WITH SYSTEM COMMAND." + uL);
							break;
							
						case -1:
							// Should send something log, but does nothing at the moment,
							// other than append a new line with nL
						case 0:
							etCW.append(uL);
							break;
							
						case 1:
							// Do nothing for now, no action needed
							break;
							
						default:
							// Just in case an odd error value is sent
							etCW.append("ERROR WITH RETURN VALUE." + uL);
							break;
						}
					}					

					return true;
				}
				return false;
			}
		});// end onKeyListener
	}

	public int procCommand(String line){

		// For now, split string by spaces. Each space signals new token.
		// Not pretty, and not compatible with original syntax, but it should
		// work well enough for now, to worry about more later.
		tokens = line.split(" ");
		token = tokens[0].trim();

		// TODO Add code to create, initialise and execute System command

		if (token.equals(commands[C_HELLO])){				// Start BASIC system
			// Ask for user name, get ready for step 1 of HELLO
			etCW.append("USER NAME-- ");
			C_HELLO_Step = 1;
			return 1;
		} // end HELLO command

		// Only execute these commands if BP is instantiated
		else if (BP instanceof BASICProgram){
			// Separated from the other if .. else, this handles BASIC commands
			// only if and when BP had been instantiated
			
			if (isNumber(token) == true){
				// Must insert call to BASICProgram, to add line to program
				// possibly parsing inputToken, and tokens[] for storage
				// and execution of commands later
				String[] inputTokens = null;
				if (tokens.length > 1){
					for (int i = 1; i < tokens.length; i++){
						inputTokens[i - 1] = tokens[i];
					}
				}
				String resultAddLine = BP.addLine(tokens[0], inputTokens);
				if (resultAddLine != null){
					etCW.append(resultAddLine + "\n");
				}
				etCW.append("> ");
				return 1;
				// TODO Add code to add the tokens to Program Listing
			}

			if (token.equals(commands[C_NEW])){				// Create new program, with new name
				etCW.append("NEW PROGRAM NAME-- ");
				C_NEW_Step = 1;
				return 1;
			} // end NEW command

			else if (token.equals(commands[C_OLD])){		// Load old program from file
				etCW.append("OLD PROGRAM NAME-- ");
				C_OLD_Step = 1;
				return 1;
			} // end OLD command

			else if (token.equals(commands[C_LIST])){		// Display current program's code
//				if(tokens[3] == null){
					BP.C_LIST(etCW, 0);
//					return 0;
//				}
//				else{
//					String chkToken = tokens[3].replace("--", "");
//					if (isNumber(chkToken)){
//						int n = Integer.parseInt(tokens[3].trim());
//						BP.C_LIST(etCW, n);
//						return 0;
//					}
//					else{
//						etCW.append("INVALID LINE NUMBER GIVEN");
//						return -1;
//					}
//				}
//				else{
//					etCW.append("EMPTY PROGRAM LISTING");
//				}
			} // end LIST command

			else if (token.equals(commands[C_SAVE])){		// Save loaded program's to file
				if (C_SAVE(BP.getProgName() + ".bas")){
					return 0;
				}
				else {
					return -1;
				}
			} // end SAVE command

			else if (token.equals(commands[C_UNSAVE])){		// Delete loaded program's file
				if (C_UNSAVE(BP.getProgName() + ".bas")){
					return 0;
				}
				else {
					return -1;
				}
			} // end UNSAVE command
			
			else if (token.equals(commands[C_CATALOG])){	// Display all previously saved programs
				File[] dirList = dir.listFiles();
				
				if (dirList != null){
					etCW.append("There are " + dirList.length + " files in program directory\n");
					for (int i = 0; i < dirList.length; i++){
						etCW.append("\t" + dirList[i].getName() + "\n");
					}
				}
				return 0;
			} // end CATALOG command
			
			else if (token.equals(commands[C_SCRATCH])){	// Create new program with same name
				BP.C_SCRATCH();
				return 0;
			} // end SCRATCH command
			
			else if (token.equals(commands[C_RENAME])){		// Rename current program
				BP.setProgName(tokens[1]);
			} // end RENAME command
			
			else if (token.equals(commands[C_RUN])){		// Run BASIC program in Interpreter
				// As BP.run() does not return a value, must assume it will keep its own log,
				// if something goes wrong, and will display an appropriate error message
				BP.run();
				return 0;
			} // end RUN command
			
			else if (token.equals(commands[C_BYE])){		// Exit BASIC System
				;
			} // end BYE command
			
			else if (token.equals("") || token == null){
				etCW.append(uL);
				return 1;
			}
			else{
				etCW.append("'" + tokens[0] + "'" + " : " + "'" + token + "'\n");
				etCW.append("ERROR WITH SYSTEM COMMAND" + uL);
			}
		}
		else{
		}
		return -1;
	}

	private void C_HELLO(String inputToken){
		/**
		 * switch .. case steps:
		 * 	0	Not used; first step is in HELLO, ask for userName
		 * 	1	Get userName, ask for NEW or OLD
		 * 	2	If NEW, go to:	3
		 * 		If OLD, go to:	4
		 * 			Ask for progName
		 * 	3	Get progName, create new BP w/empty listing
		 * 	4	Get progName, create new BP w/loaded program listing
		 */

		token = inputToken.trim();

		switch (C_HELLO_Step) {

		case 1:		// Get userName, ask progName
			progDetails[1] = token;
			etCW.append("NEW OR OLD-- ");
			C_HELLO_Step++;
			break;

		case 2:
			if (token.equals(commands[C_NEW])){
				C_HELLO_Step++;
			}
			else if (token.equals(commands[C_OLD])){
				C_HELLO_Step = 4;
			}
			else{
				etCW.append("ONLY TYPE 'NEW' OR 'OLD'-- ");
				break;
			}

			etCW.append(token + " PROGRAM NAME-- ");
			break;

		case 3:
			progDetails[2] = token;
			C_HELLO_Step = 0;
			BP = new BASICProgram(progDetails[1], progDetails[2]);
			etCW.append("READY." + uL);
			break;

		case 4:
			progDetails[2] = token;
			C_HELLO_Step = 0;
			// Get listing
			try {
				if (dir.canRead()){
					File basFile = new File(dir, token + ".bas");
					FileReader basReader = new FileReader(basFile);
					BufferedReader in = new BufferedReader(basReader);
					// For now, does nothing with 'in', but should loop
					// and read in line-by-line
					in.close();
					C_OLD_Step = 0;
					etCW.append("PROGRAM OPENED SUCCESSFULLY." + uL);
				}
			}
			catch (IOException e) {
				etCW.append("COULD NOT READ FILE " + e.getMessage().toUpperCase() + uL);
			}
			
			BP = new BASICProgram(progDetails[1], progDetails[2], codeList);
			etCW.append("READY." + uL);
			break;

		default:
			break;
		}
	}

	private void C_NEW(String inputToken){

		token = inputToken.trim();
		BP.C_NEW(token);
		C_NEW_Step = 0;
	}

	private void C_OLD(String inputToken){

		token = inputToken.trim();
		// Get listing for given progName

		try {
			if (dir.canRead()){
				File basFile = new File(dir, token + ".bas");
				FileReader basReader = new FileReader(basFile);
				BufferedReader in = new BufferedReader(basReader);
				// For now, does nothing with 'in', but should loop
				// and reader it line-by-line
				in.close();
				BP.C_OLD(token, codeList);
				C_OLD_Step = 0;
				etCW.append("PROGRAM OPENED SUCCESSFULLY." + uL);
			}
		}
		catch (IOException e) {
			etCW.append("COULD NOT READ FILE " + e.getMessage().toUpperCase());
		}

	}

	private boolean C_SAVE(String fileName){
		try {
			if (dir.canWrite()){
				File basFile = new File(dir, fileName);
				FileWriter basWriter = new FileWriter(basFile);
				BufferedWriter out = new BufferedWriter(basWriter);
				// Temporary write command. To replace with loop, that
				// writes out the code as plain text
				out.write("Hello, world!");
				out.close();
				etCW.append("PROGRAM SAVED SUCCESSFULLY.");
				return true;
			}
			else{
				etCW.append("COULD NOT WRITE TO FOLDER.");
				return false;
			}
		}
		catch (IOException e) {
			etCW.append("COULD NOT WRITE TO FILE " + e.getMessage().toUpperCase());
			return false;
		}
	}
	
	private boolean C_UNSAVE(String fileName){
		if (dir.canWrite()){
			File basFileToDelete = new File(dir, fileName);
			if (basFileToDelete.delete()){
				etCW.append("PROGRAM UNSAVED SUCCESSFULLY.");
				return true;
			}
			else{
				etCW.append("COULD NOT UNSAVE FROM FOLDER.");
				return false;
			}
		}
		else{
			etCW.append("FILE CANNOT BE UNSAVED - NOT ENOUGH PERMISSIONS");
			return false;
		}
	}
	
	private boolean isNumber(String chkToken){

		try {
			// If first token is number, assume it's a BASIC command
			// and return true, to state it's to be added to queue.

			boolean isInteger = Pattern.matches("^-?\\d+$", chkToken);
			return isInteger;

		}
		catch (NumberFormatException ex){
			// As first token isn't number, return false,
			// command is system command, and is to be 
			// executed immediately by the interpreter.
			return false;
		}
	}
}