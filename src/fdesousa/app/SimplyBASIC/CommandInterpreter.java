/*
 * CommandInterpreter.java - Implement the main Command Interpreter.
 *
 * Copyright (c) 2011 Filipe De Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package fdesousa.app.SimplyBASIC;

import java.util.Map;
import java.util.TreeMap;
import java.io.*;

import fdesousa.app.SimplyBASIC.framework.Expression;
import fdesousa.app.SimplyBASIC.framework.FileIO;
import fdesousa.app.SimplyBASIC.framework.TextIO;
import android.os.Environment;

/**
 * <h1>CommandInterpreter.java</h1>
 * The main execution class. Determines what to do with the user's input.<br>
 * Input from the user is typed, through an EditText control, and can be a<br>
 * system command, or a BASIC command, which is parsed into a code store.<br>
 * @version 0.1
 * @author Filipe De Sousa
 */
public class CommandInterpreter extends Thread {
	//	Out of convenience, we keep the below here for now, until this class
	//+	is replaced by Terminal in functionality
	private Terminal terminal;
	private TextIO textIO;
	private FileIO fileIO;
	private Tokenizer tokenizer;
	private BASICProgram program;

	// Get SD card root, then set working directory:
	private File sdRoot = Environment.getExternalStorageDirectory();
	private File dir = new File (sdRoot.getAbsolutePath() + "/SimplyBASIC");

	// The current token to work on. Could be one of:
	// -	Command
	// -	Line Number
	// -	File name
	// etc depending upon the context
	private String token = new String();

	// If token is identified to be a number, it's stored in lineNumber
	private int lineNumber = 0;
	private String[] lines = null;

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

	public CommandInterpreter(Terminal terminal) {
		this.terminal = terminal;
		this.textIO = terminal.getTextIO();
		this.fileIO = terminal.getFileIO();
		this.tokenizer = terminal.getTokenizer();
		this.program = terminal.getBasicProgram();
		/*
		this.et.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_UP
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					**
					 * PRELIMINARY NOTE!
					 * For now, file names with numbers or symbols attached WILL NOT WORK!
					 * The tokenizer takes it to mean that a word has only letters, a variable
					 * can have ONE digit, nothing else.
					 * THEREFORE, to clarify for now:
					 * A word will have more than one letter, without numbers or spaces
					 * A command will have more than one letter, without numbers or spaces
					 * A variable will have one letter, and may have one number, without spaces
					 * ========================================================================
					 * I can work around this, and will have to for the PRINT command, 
					 * but for now, this is how it will be, and there's no other choice.
					 
					if (C_HELLO_Step >= 1 || C_NEW_Step >= 1 || C_OLD_Step >= 1) {
						// We're only interested in the last entered item, which is after '-- '
						// So split the text from edtxt into "lines" by '-- '
						//	Used to have a line of code here, now doesn't. Deal with it!
						
						// Give the tokenizer the last line, which is what we're interested in
						tokenizer.reset(textIO.readLine());
						// Tell tokenizer to pass us the next token, store is token
						token = tokenizer.nextToken();

						if (C_HELLO_Step >= 1) {
							C_HELLO();
						} else if (C_NEW_Step >= 1) {
							C_NEW();
						} else if (C_OLD_Step >= 1) {
							C_OLD();
						}
					} else {
						tokenizer.reset(textIO.readLine());
						token = tokenizer.nextToken();

						
						 * Numbers returned upon ending execution:
						 * 	--	PROBLEMS WITH SYSTEM COMMANDS
						 * -2:	Problem during execution, need nL and generic error
						 * -1:	Problem during execution, need nL, error already shown
						 * 	0:	No problems, needs nL
						 *  1:	No problems, doesn't need nL
						 
						switch(procCommand(token)) {
						case -2:
							textIO.writeLine("ERROR WITH SYSTEM COMMAND." + uL);
							break;
						case -1:	// Should send something log, but does nothing at the moment,
						case 0:		// other than append a new line with nL
							textIO.writeLine(uL);
							break;
						case 1:		// Do nothing for now, no action needed
							break;
						default:	// Just in case an odd error value is sent
							textIO.writeLine("ERROR WITH RETURN VALUE." + uL);
							break;
						}
					}
					return true;
				}
				return false;
			}
		});// end onKeyListener	*/
		
		/*
		 * We don't need no stinkin' onKeyListener in these here parts!
		 * Moved to somewhere more useful, our TextIO class
		 */
	}

	public int procCommand(String input){

		if (input.equals(commands[C_HELLO])){			// Start BASIC system, initialise BP
			// Ask for user name, get ready for step 1 of HELLO
			textIO.writeLine("USER NAME-- ");
			C_HELLO_Step = 1;
			return 1;
		} // end HELLO command

		else if (input.equals(commands[C_CATALOG])){	// Display all previously saved programs
			File[] dirList = dir.listFiles();

			if (dirList != null){
				textIO.writeLine("There are " + dirList.length + " files in program directory");
				for (int i = 0; i < dirList.length; i++){
					textIO.writeLine("\t" + dirList[i].getName());
				}
			}
			return 0;
		} // end CATALOG command

		// Only execute these commands if BP is instantiated
		else if (program instanceof BASICProgram){
			// Separated from the other if .. else, this handles BASIC commands
			// only if and when BP had been instantiated

			if (Expression.isNumber(input) == true){
				lineNumber = Integer.valueOf(input.trim()).intValue();
				program.addLine(Integer.valueOf(input.trim()).intValue(), tokenizer.getRestOfLine());
				textIO.writeLine("> ");
				return 1;
			}

			if (input.equals(commands[C_NEW])){				// Create new program, with new name
				textIO.writeLine("NEW PROGRAM NAME-- ");
				C_NEW_Step = 1;
				return 1;
			} // end NEW command

			else if (input.equals(commands[C_OLD])){		// Load old program from file
				textIO.writeLine("OLD PROGRAM NAME-- ");
				C_OLD_Step = 1;
				return 1;
			} // end OLD command

			else if (input.equals(commands[C_LIST])){		// Display current program's code
				program.C_LIST();
				return 0;
			} // end LIST command

			else if (input.equals(commands[C_SAVE])){		// Save loaded program's to file
				if (C_SAVE(program.getProgName() + ".bas")){
					return 0;
				}
				else {
					return -1;
				}
			} // end SAVE command

			else if (input.equals(commands[C_UNSAVE])){		// Delete loaded program's file
				if (C_UNSAVE(program.getProgName() + ".bas")){
					return 0;
				}
				else {
					return -1;
				}
			} // end UNSAVE command

			else if (input.equals(commands[C_SCRATCH])){	// Create new program with same name
				program.C_SCRATCH();
				return 0;
			} // end SCRATCH command

			else if (input.equals(commands[C_RENAME])){		// Rename current program
				program.setProgName(tokenizer.next());
			} // end RENAME command

			else if (input.equals(commands[C_RUN])){		// Run BASIC program in Interpreter
				// As BP.run() does not return a value, must assume it will keep its own log,
				// if something goes wrong, and will display an appropriate error message
				program.run();
				return 0;
			} // end RUN command

			else if (input.equals(commands[C_BYE])){		// Exit BASIC System
				// Needs something here. Like, exit the system, not sure all-in-all
				terminal.end();
			} // end BYE command

			else if (input.equals("") || input == null){
				return 0;
			}
			else{
				textIO.writeLine("ERROR WITH SYSTEM COMMAND");
				return -1;
			}
		}
		return -1;
	}

	public TextIO getTextIO() {
		return textIO;
	}

	private void C_HELLO(){
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
		switch (C_HELLO_Step) {

		case 1:		// Get userName, ask NEW or OLD program
			progDetails[1] = token;
			textIO.writeLine("NEW OR OLD-- ");
			C_HELLO_Step++;
			break;

		case 2:		// Ask for Program name
			if (token.equals(commands[C_NEW])){
				C_HELLO_Step++;
			}
			else if (token.equals(commands[C_OLD])){
				C_HELLO_Step = 4;
			}
			else{
				textIO.writeLine("ONLY TYPE 'NEW' OR 'OLD'-- ");
				break;
			}

			textIO.writeLine(token + " PROGRAM NAME-- ");
			break;

		case 3:		// NEW, so instantiate BP
			progDetails[2] = token;
			C_HELLO_Step = 0;
			program = new BASICProgram(progDetails[1], progDetails[2]);
			textIO.writeLine("READY." + uL);
			break;

		case 4:		// OLD, so read file, instantiate BP
			progDetails[2] = token;
			C_HELLO_Step = 0;
			// Get listing
			C_OLD();
			break;

		default:
			break;
		}
	}

	// Because of the fact there's an onKeyListener, waiting for Enter key,
	// NEW, OLD, SAVE, UNSAVE, are all controlled within CI instance
	private void C_NEW(){
		program.C_NEW(token);
		C_NEW_Step = 0;
		textIO.writeLine("READY." + uL);
	}

	private void C_OLD(){
		// Get listing for given progName
		try {
			if (dir.canRead()){
				File basFile = new File(dir, token + ".bas");
				FileReader basReader = new FileReader(basFile);
				BufferedReader in = new BufferedReader(basReader);
				Map <Integer, String> oldCodeList = new TreeMap<Integer, String>();
				String userName = in.readLine();
				String progName = in.readLine();
				String line = "";
				// For now, does nothing with 'in', but should loop
				// and read it line-by-line, then parse to BP
				while ((line = in.readLine()) != null){
					tokenizer.reset(line);
					oldCodeList.put(Integer.valueOf(tokenizer.next()).intValue()
							, tokenizer.getRestOfLine());
				}
				in.close();
				program = BASICProgram.C_OLD(progName, userName, oldCodeList);
				C_OLD_Step = 0;
				textIO.writeLine("PROGRAM OPENED SUCCESSFULLY.\nREADY." + uL);
			}
		}
		catch (IOException e) {
			textIO.writeLine("COULD NOT READ FILE: " + e.getMessage().toUpperCase() + uL);
		}

	}

	private boolean C_SAVE(String filename){
		try {
			if (dir.canWrite()) {
				File basFile = new File(dir, filename);
				FileWriter basWriter = new FileWriter(basFile);
				BufferedWriter out = new BufferedWriter(basWriter);
				// Temporary write command. To replace with loop, that
				// writes out the code as plain text
				out.write(program.C_SAVE());
				out.close();
				textIO.writeLine("PROGRAM SAVED SUCCESSFULLY.");
				return true;
			} else {
				textIO.writeLine("COULD NOT WRITE TO FOLDER.");
				return false;
			}
		} catch (IOException e) {
			textIO.writeLine("COULD NOT WRITE TO FILE " + e.getMessage().toUpperCase());
			return false;
		}
	}

	private boolean C_UNSAVE(String filename) {
		try {
			fileIO.deleteFile(filename);
			return true;
		} catch (IOException e) {
			textIO.writeLine("COULD NOT DELETE FILE " + e.getMessage().toUpperCase());
			return false;
		}
	}

}