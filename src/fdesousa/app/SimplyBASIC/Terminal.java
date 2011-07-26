package fdesousa.app.SimplyBASIC;

import java.io.IOException;

import android.widget.EditText;
import fdesousa.app.SimplyBASIC.Commands.New;
import fdesousa.app.SimplyBASIC.Commands.Old;
import fdesousa.app.SimplyBASIC.Commands.Save;
import fdesousa.app.SimplyBASIC.framework.Command;
import fdesousa.app.SimplyBASIC.framework.Expression;
import fdesousa.app.SimplyBASIC.framework.FileIO;
import fdesousa.app.SimplyBASIC.framework.TextIO;

/**	
 *	
 */
public class Terminal implements Runnable {
	// Array of commands for the system, to make matching easier:
	public final static String[] COMMANDS = {
		"HELLO", "NEW", "OLD", "STOP", 
		"LIST", "SAVE", "UNSAVE", "CATALOG",
		"SCRATCH", "RENAME", "RUN", "BYE" };

	// Makes it easier to find the right command in the above array:
	public final static int HELLO	=  0;	// Start BASIC Interpreter
	public final static int NEW		=  1;	// Make new program, erasing current
	public final static int OLD		=  2;	// Open a previously saved program
	public final static int STOP	=  3;	// Stop execution of the current program
	public final static int LIST	=  4;	// List the entered commands in the program
	public final static int SAVE	=  5;	// Save the current program to storage
	public final static int UNSAVE	=  6;	// Delete the currently running program from storage
	public final static int CATALOG	=  7;	// Display all previously saved programs
	public final static int SCRATCH	=  8;	// Empty program listing, but keep name
	public final static int RENAME	=  9;	// Rename the program without removing program listing
	public final static int RUN		= 10;	// Run the BASIC program as per the listing 
	public final static int BYE		= 11;	// Exit BASIC Interpreter

	private SimplyBASIC simplyBasic;	//	Instance of the calling class, for using the BYE command
	private TextIO textIO;				//	We use this for readLine() and writeLine()
	private FileIO fileIO;				//	CATALOG, OLD, SAVE and UNSAVE commands won't work without file access
	private Tokenizer tokenizer;		//	A Tokenizer for checking out what we've entered
	private BASICProgram basicProgram;	//	Instance of BASICProgram we want to add to, run, whatever
	//	Replacing CommandInterpreter with Terminal, so say bye-bye to the line below!
	//private CommandInterpreter comInt;	//	Instance of our CommandInterpreter to run system commands
	volatile boolean running = false;	//	Volatile to help keep loop structure and order in JITC
	Thread terminalThread = null;
	Thread programThread = null;

	Command command;
	String token = null;	//	We'll hold a token one at a time here when tokenizing

	public Terminal(SimplyBASIC simplyBasic, EditText editText) {
		this.simplyBasic = simplyBasic;
		textIO = new TextIO(editText);
		fileIO = new FileIO("SimplyBASIC");
		tokenizer = new Tokenizer();
		//	BASIC Program will be instantiated later
		//basicProgram = new BASICProgram(this);
		//comInt = new CommandInterpreter(this);
	}	//	End of class constructor

	public void resume() {
		//	This class handles its own thread, so resume within itself
		running = true;
		terminalThread = new Thread(this);
		terminalThread.start();
		//	TextIO will be later put into a thread so we can wait for a
		//+	line to read if necessary, so we'll be adding a call to TextIO.resume()
	}

	@Override
	public void run() {
		while (running) {
			//	We'll be relying upon the tokenizer here to separate out the individual tokens
			tokenizer.reset(textIO.readLine());
			if (tokenizer.hasNext())
				token = tokenizer.next();
			else
				continue;

			//	As we've got a token to process, figure it out and run it
			if (token.equals(COMMANDS[HELLO])) {
				//	We forego performance hit of new class instance, and just decide which to run
				String temp;
				//	Until the user types NEW or OLD, force them to try again
				while (true) {
					textIO.write("NEW OR OLD-- ");
					temp = textIO.readLine();
					//	We don't need to keep the instances after they've run once
					if (temp.equals("NEW")) {
						new New(this).run();
						break;
					} else if (temp.equals("OLD")) {
						new Old(this).run();
						break;
					} else {
						textIO.writeLine("MUST WRITE ONLY 'NEW' OR 'OLD'");
					}
				}

			} else if (token.equals(COMMANDS[CATALOG])) {
				//	Simply use calls FileIO.() to list all the files in the directory
				try {
					textIO.writeLine(fileIO.folderListing());
				} catch (IOException e) {
					textIO.writeLine("DIRECTORY IS NOT READABLE");
				}

			//	If we have an instance of BASICProgram, we have more available input methods
			} else if (basicProgram instanceof BASICProgram) {
				if (Expression.isNumber(token))	{
					//	First token is a number, assume it's a BASIC line and parse it
					basicProgram.addLine(Integer.valueOf(token.trim()).intValue(), tokenizer.getRestOfLine());

				} else if (token.equals(COMMANDS[NEW])) {
					//	Instantiate and run the New command
					command = new New(this);

				} else if (token.equals(COMMANDS[OLD])) {
					//	Instantiate and run the Old command
					command = new Old(this);

				} else if (token.equals(COMMANDS[STOP])) {
					//	Check if there's an instance of Thread in programThread
					if (programThread instanceof Thread) {
						//	Alright then, all clear! Stop the program running
						basicProgram.stop();
						//	Then attempt to block the thread
						while (true) {
							try {
								programThread.join();
								break;
							} catch (InterruptedException e) {
								//	Naughty, naughty! We retry this, so silently ignore
							}
						}
					}
					//	If there was no instance of Thread, then we had yet to receive 'RUN'

				} else if (token.equals(COMMANDS[LIST])) {
					//	Write returned String from BASICProgram.list() with TextIO.writeLine()
					textIO.writeLine(basicProgram.list());

				} else if (token.equals(COMMANDS[SAVE])) {
					//	Instantiate and run the Save command
					command = new Save(this);

				} else if (token.equals(COMMANDS[UNSAVE])) {
					//	Just try to use FileIO.deleteFile(), surrounded by try/catch for safety
					try {
						fileIO.deleteFile(basicProgram.getProgName());
					} catch (IOException e) {
						//	Assume the file couldn't be deleted
						textIO.writeLine("FILE COULD NOT BE UNSAVED");
					}

				} else if (token.equals(COMMANDS[SCRATCH])) {
					//	No need for a new class instance, so just do the method call
					basicProgram.scratch();

				} else if (token.equals(COMMANDS[RENAME])) {
					//	Just ask for the new name of the program and parse
					//+	the entered value to BASICProgram.setProgName()
					textIO.write("NEW NAME FOR PROGRAM-- ");
					basicProgram.setProgName(textIO.readLine());

				} else if (token.equals(COMMANDS[RUN])) {
					//	Didn't make sense to have RUN in a new class considering it handles
					//+	a thread containing the BASICProgram being executed
					programThread = new Thread(basicProgram);
					programThread.start();

				} else if (token.equals(COMMANDS[BYE])) {
					//	Doesn't make sense to have a dedicated class, so just execute the method
					end();

				} else if (token.equals("") || token == null) {
					//	We don't mind empty inputs, but catch them as not being errors
					//+	Continue the loop on the next iteration because we don't want
					//+	to execute the previously run command by accident
					continue;

				} else {
					//	If it's none of the above, the input is wrong/unknown. Continue loop
					textIO.writeLine("UNKNOWN INPUT");
					continue;
				}
				//	If not in a BASICProgram instance, tell the user to try typing 'HELLO' first
			} else {
				//	If there's no instance of BASICProgram, then you've got to go through HELLO
				textIO.writeLine("MUST TYPE 'HELLO' FIRST");
				continue;
			}
			//	Common run for all the commands here, for simplicity's sake
			command.run();
		}	//	End of while(running) loop
	}

	public void pause() {
		//	We attempt to stop and block the BASICProgram thread before blocking our own
		//	Check it's actually in an instance of BASICProgram first
		if (basicProgram instanceof BASICProgram) {
			//	Then check if there's an instance of Thread in programThread
			if (programThread instanceof Thread) {
				//	Alright then, all clear! Stop the program running
				basicProgram.stop();
				//	Then attempt to block the thread
				while (true) {
					try {
						programThread.join();
						break;
					} catch (InterruptedException e) {
						//	Naughty, naughty! We retry this, so silently ignore
					}
				}
			}
		}
		//	As we handle our own thread, we stop the thread in here
		running = false;
		//	Keep trying to block the thread until it actually blocks
		while (true) {
			try {
				terminalThread.join();
				break;
			} catch (InterruptedException e) {
				//	Naughty, naughty! We retry this, so silently ignore
			}
		}
	}

	/**
	 * Convenience method: End this activity/application
	 */
	public void end() {
		simplyBasic.end();
	}	//	End of end method

	/**	
	 *	Method for getting an instance of TextIO for text input/output
	 *	@return instance of TextIO held in Terminal instance
	 */	
	public TextIO getTextIO() {
		return textIO;
	}	//	End of getTextIO method

	/**	
	 *	Method for getting an instance of FileIO for file input/output
	 *	@return instance of FileIO held in Terminal instance
	 */	
	public FileIO getFileIO() {
		return fileIO;
	}	//	End of getFileIO method

	/**	
	 *	Method for getting an instance of Tokenizer for tokenizing the text input
	 *	@return instance of Tokenizer held in Terminal instance
	 */	
	public Tokenizer getTokenizer() {
		return tokenizer;
	}	//	End of getTokenizer method

	/**	
	 *	Method for getting an instance of BASICProgram for running/editing
	 *+	the BASIC code we're holding, depending upon input we receive
	 *	@return instance of BASICProgram held in Terminal instance
	 */	
	public BASICProgram getBasicProgram() {
		return basicProgram;
	}	//	End of getBasicProgram method

	/**
	 *	Method for setting the instance of BASICProgram for running/editing
	 *+	the BASIC code we're holding, depending upon input we receive
	 *	@param basicProgram
	 */
	public void setBasicProgram(BASICProgram basicProgram) {
		this.basicProgram = basicProgram;
	}	//	End of setBasicProgram method
}	//	End of Terminal class