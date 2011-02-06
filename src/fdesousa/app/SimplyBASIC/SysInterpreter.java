/**
 * 
 */
package fdesousa.app.SimplyBASIC;

/**
 * @author Fil
 *
 */
public class SysInterpreter {

	/**
	 * 
	 */
	
	private String tokens[] = new String[255];
	private String output = "";
	
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
	
	public SysInterpreter(String[] tokens) {
		// TODO Auto-generated constructor stub
		
		this.tokens = tokens;
		
	}
	
	public String execCommand(){
		
		if (tokens[0] == commands[C_HELLO]){
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
			;
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
		return output;
	}

}
