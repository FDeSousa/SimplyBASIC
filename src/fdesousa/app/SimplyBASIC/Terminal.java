package fdesousa.app.SimplyBASIC;

import android.widget.EditText;
import fdesousa.app.SimplyBASIC.Commands.*;
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

	Command command;
	String token = null;	//	We'll hold a token one at a time here when tokenizing

	public Terminal(SimplyBASIC simplyBasic, EditText editText) {
		this.simplyBasic = simplyBasic;
		textIO = new TextIO(editText);
		fileIO = new FileIO("SimplyBASIC");
		tokenizer = new Tokenizer();
		basicProgram = new BASICProgram(this);
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
				//	Instantiate and run the Hello command
				command = new Hello(this);
			} else if (token.equals(COMMANDS[CATALOG])) {
				//	Instantiate and run the Catalog command
				command = new Catalog(this);
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
					//	Instantiate and run the Stop command
					command = new Stop(this);
				} else if (token.equals(COMMANDS[LIST])) {
					//	Instantiate and run the List command
					command = new List(this);
				} else if (token.equals(COMMANDS[SAVE])) {
					//	Instantiate and run the Save command
					command = new Save(this);
				} else if (token.equals(COMMANDS[UNSAVE])) {
					//	Instantiate and run the Unsave command
					command = new Unsave(this);
				} else if (token.equals(COMMANDS[SCRATCH])) {
					//	Instantiate and run the Scratch command
					command = new Scratch(this);
				} else if (token.equals(COMMANDS[RENAME])) {
					//	Instantiate and run the Rename command
					command = new Rename(this);
				} else if (token.equals(COMMANDS[RUN])) {
					//	Instantiate and run the Run command
					command = new Run(this);
				} else if (token.equals(COMMANDS[BYE])) {
					//	Instantiate and run the Bye command
					command = new Bye(this);
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
		//	As we handle our own thread, we the thread in here
		running = false;
		//	Keep trying to block the thread until it actually blocks
		while (true) {
			try {
				terminalThread.join();
				break;
			} catch (InterruptedException e) {
				//	Naughty, naughty! We retry this
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