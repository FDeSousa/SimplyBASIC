package fdesousa.app.SimplyBASIC;

import android.widget.EditText;
import fdesousa.app.SimplyBASIC.framework.FileIO;
import fdesousa.app.SimplyBASIC.framework.TextIO;

/**	
 *	
 *	
 *	
 *	
 */	
public class Terminal {
	private SimplyBASIC simplyBasic;	//	Instance of the calling class, for using the BYE command
	private TextIO textIO;				//	We use this for readLine() and writeLine()
	private FileIO fileIO;				//	CATALOG, OLD, SAVE and UNSAVE commands won't work without file access
	private Tokenizer tokenizer;		//	A Tokenizer for checking out what we've entered
	private BASICProgram basicProgram;	//	Instance of BASICProgram we want to add to, run, whatever
	private CommandInterpreter comInt;	//	Instance of our CommandInterpreter to run system commands

	
	public Terminal(SimplyBASIC simplyBasic, EditText editText) {
		this.simplyBasic = simplyBasic;
		textIO = new TextIO(editText);
		fileIO = new FileIO("SimplyBASIC");
		tokenizer = new Tokenizer();
		basicProgram = new BASICProgram(this);
		comInt = new CommandInterpreter(this);
	}	//	End of class constructor

	public void run() {
		//	Object constructed, start the command interpreter! Terminal is
		//+	just mostly for convenience
		comInt.start();
		//	Will be replacing CommandInterpreter with Terminal at some point
		//+	for executing system commands, so the above will disappear
	}
	
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
	
	/**
	 * Convenience method: End this activity/application
	 */
	public void end() {
		simplyBasic.end();
	}	//	End of end method
}	//	End of Terminal class
