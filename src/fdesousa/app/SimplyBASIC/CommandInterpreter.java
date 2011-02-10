package fdesousa.app.SimplyBASIC;

import java.util.regex.*;
import java.io.*;

import android.os.Environment;
//import android.util.Log; // Unused for now, may use for logging activity
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class CommandInterpreter {

	// Set directory to read from/write to
	private File sdRoot = Environment.getExternalStorageDirectory();
	private File dir = new File (sdRoot.getAbsolutePath() + "/SimplyBASIC");
	//private String line;	// not used, no need for specific line, using inputToken
	private String token;	// token to work on. May remove, to replace with inputToken
	private String[] tokens = null; // whole line, divided into tokens
	//private String output;	// class now writes straight to etCW
	private String[][] listing = null;
	private String[] progDetails = new String[3];
	//private boolean runBASIC = false;
	private int C_HELLO_Step = 0;
	private int C_NEW_Step = 0; 
	private int C_OLD_Step = 0;
	private static BASICProgram BP;
	//private static BASICInterpreter BI;
	private EditText etCW;
	//private InputStream is;
	//private OutputStream os;

	final static String[] commands = {
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

	private static final String nL = "\n> ";

	public CommandInterpreter(EditText edtextCW) {
		super();
		this.etCW = edtextCW;

		if (dir.exists() == false 
				&& dir.isDirectory() == false){
			dir.mkdir();
		}

		// Only initialises runBASIC with parsed value for now
		// If true, the CI is being executed to run BASIC commands
		this.etCW.setOnKeyListener(new OnKeyListener() {
			boolean runBASIC;
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_UP 
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					if (C_HELLO_Step >= 1){
						tokens = etCW.getText().toString().split("-- ");
						C_HELLO(tokens[tokens.length - 1].trim());
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
							etCW.append("ERROR WITH SYSTEM COMMAND." + nL);
							break;
							
						case -1:
						case 0:
							etCW.append(nL);
							break;
							
						case 1:
							// Do nothing for now, no action needed
							break;
							
						default:
							// Just in case an odd error value is sent
							etCW.append("ERROR WITH RETURN VALUE." + nL);
							break;
						}
					}					

					return true;
				}
				if(this.runBASIC == true 
						&& event.getAction()==KeyEvent.ACTION_DOWN 
						&& event.getKeyCode() == KeyEvent.KEYCODE_S) {
					// TODO Add code to stop execution of program
					// Used when running a BASIC application, to stop operation
					return false;
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

		if (token.equals(commands[C_HELLO])){
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
				BP.addLine(tokens);
				return 0;
				// TODO Add code to add the tokens to Program Listing
			}

			if (token.equals(commands[C_NEW])){
				etCW.append("NEW PROGRAM NAME-- ");
				C_NEW_Step = 1;
				return 1;
			} // end NEW command

			else if (token.equals(commands[C_OLD])){
				etCW.append("OLD PROGRAM NAME-- ");
				C_OLD_Step = 1;
				return 1;
			} // end OLD command

			else if (token.equals(commands[C_LIST])){
				if(tokens[3] == null){
					BP.C_LIST();
				}
				else if(isNumber(tokens[3])){
					int n = Integer.parseInt(tokens[3].trim());
					BP.C_LIST(n);
				}
			} // end LIST command

			else if (token.equals(commands[C_SAVE])){
				if (C_SAVE(BP.getProgName() + ".bas")){
					return 0;
				}
				else {
					return -1;
				}
			} // end SAVE command

			else if (token.equals(commands[C_UNSAVE])){
				if (C_UNSAVE(BP.getProgName() + ".bas")){
					return 0;
				}
				else {
					return -1;
				}
			} // end UNSAVE command
			
			else if (token.equals(commands[C_CATALOG])){
				File[] dirList = dir.listFiles();
				
				if (dirList != null){
					etCW.append("There are " + dirList.length + " files in program directory\n");
					for (int i = 0; i < dirList.length; i++){
						etCW.append("\t" + dirList[i].getName() + "\n");
					}
				}
				return 0;
			} // end CATALOG command
			
			else if (token.equals(commands[C_SCRATCH])){
				;
			} // end SCRATCH command
			
			else if (token.equals(commands[C_RENAME])){
				;
			} // end RENAME command
			
			else if (token.equals(commands[C_RUN])){
				;
			} // end RUN command
			
			else if (token.equals(commands[C_BYE])){
				;
			} // end BYE command
			
			else if (token.equals("")){
				etCW.append(nL);
				return 1;
			}
			else{
				etCW.append("'" + tokens[0] + "'" + " : " + "'" + token + "'\n");
				etCW.append("ERROR WITH SYSTEM COMMAND" + nL);
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
			etCW.append("READY." + nL);
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
					// and reader it line-by-line
					in.close();
					C_OLD_Step = 0;
					etCW.append("PROGRAM OPENED SUCCESSFULLY.");
				}
			}
			catch (IOException e) {
				etCW.append("COULD NOT READ FILE " + e.getMessage().toUpperCase());
			}
			
			BP = new BASICProgram(progDetails[1], progDetails[2], listing);
			etCW.append("READY." + nL);
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
				BP.C_OLD(token, listing);
				C_OLD_Step = 0;
				etCW.append("PROGRAM OPENED SUCCESSFULLY." + nL);
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