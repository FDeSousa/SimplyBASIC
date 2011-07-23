package fdesousa.app.SimplyBASIC;

import android.widget.EditText;
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
	public final static int C_HELLO		=  0;	// Start BASIC Interpreter
	public final static int C_NEW		=  1;	// Make new program, erasing current
	public final static int C_OLD		=  2;	// Open a previously saved program
	public final static int C_STOP		=  3;	// Stop execution of the current program
	public final static int C_LIST		=  4;	// List the entered commands in the program
	public final static int C_SAVE		=  5;	// Save the current program to storage
	public final static int C_UNSAVE	=  6;	// Delete the currently running program from storage
	public final static int C_CATALOG	=  7;	// Display all previously saved programs
	public final static int C_SCRATCH	=  8;	// Empty program listing, but keep name
	public final static int C_RENAME	=  9;	// Rename the program without removing program listing
	public final static int C_RUN		= 10;	// Run the BASIC program as per the listing 
	public final static int C_BYE		= 11;	// Exit BASIC Interpreter

	private SimplyBASIC simplyBasic;	//	Instance of the calling class, for using the BYE command
	private TextIO textIO;				//	We use this for readLine() and writeLine()
	private FileIO fileIO;				//	CATALOG, OLD, SAVE and UNSAVE commands won't work without file access
	private Tokenizer tokenizer;		//	A Tokenizer for checking out what we've entered
	private BASICProgram basicProgram;	//	Instance of BASICProgram we want to add to, run, whatever
	//	Replacing CommandInterpreter with Terminal, so say bye-bye to the line below!
	//private CommandInterpreter comInt;	//	Instance of our CommandInterpreter to run system commands
	volatile boolean running = false;	//	Volatile to help keep loop structure and order in JITC
	Thread terminalThread = null;

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

			
		}
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