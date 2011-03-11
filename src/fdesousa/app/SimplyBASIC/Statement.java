package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class Statement {
	final static String[] commands = {
		"IF", "THEN", "FOR", "TO", "STEP", 
		"NEXT", "LET", "READ", "DATA", 
		"PRINT", "GOTO", "GOSUB", "RETURN", 
		"DIM", "DEF", "FN", "END", "REM"};

	final int S_IF	 	=  0;	// Start of IF...THEN statement
	final int S_THEN	=  1;	// Continues of IF...THEN statement
	final int S_FOR		=  2;	// Start of FOR...TO...STEP statement
	final int S_TO		=  3;	// Defines limit of FOR...TO...STEP statement
	final int S_STEP	=  4;	// The number to (in/de)crement by in FOR
	final int S_NEXT	=  5;	// (in/de)crements variable defined by FOR with variable 
	final int S_LET		=  6;	// Assignment statement
	final int S_READ	=  7;	// Reads the next Data value (FIFO ordering)
	final int S_DATA	=  8;	// Provides data values for the program
	final int S_PRINT	=  9;	// Print something to screen
	final int S_GOTO	= 10;	// Unconditional transferal of program execution to a different line
	final int S_GOSUB	= 11;	// As GOTO, but can be used to define a sub-routine that is returnable
	final int S_RETURN	= 12;	// Returns execution to where GOSUB left off
	final int S_DIM		= 13;	// Used to define one- or two-dimensional arrays
	final int S_DEF		= 14;	// Used to define a function
	final int S_FN		= 15;	// Beginning two letters of a user-defined function
	final int S_END		= 16;	// Ends the program on that line, no matter what
	final int S_REM		= 17;	// Signifies the line is a comment, and should be ignored by interpreter

	private String command = null;
	
	public Statement(){}		// Poor empty constructor...
	
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW) {
		Statement s;
		
		if (t.hasMoreTokens())
			command = t.nextToken();
		
		// Because a user could potentially type in a line without spaces between the
		// command and the rest of the line, we're checking here to see if the first n
		// letters of the command string match up to the command
		// This makes for messy code, and possibly messy results, so it should be noted
		// to the user that input should really, ideally, have spaces between tokens
		if (command.substring(0, 1).equals(commands[S_IF])){
			s = new S_IF();
			s.doSt(p, t, etCW);
		}
		else if (command.substring(0, 2).equals(commands[S_FOR])){
			s = new S_FOR();
			s.doSt(p, t, etCW);
		}
		else if (command.equals(commands[S_NEXT])){
			
		}
		else if (command.substring(0, 2).equals(commands[S_LET])){
			
		}
		else if (command.substring(0, 3).equals(commands[S_READ])){
			
		}
		else if (command.substring(0, 3).equals(commands[S_DATA])){
			return;
		}
		else if (command.substring(0, 4).equals(commands[S_PRINT])){
			
		}
		else if (command.substring(0, 3).equals(commands[S_GOTO])){
			
		}
		else if (command.substring(0, 4).equals(commands[S_GOSUB])){
			
		}
		else if (command.substring(0, 5).equals(commands[S_RETURN])){
			
		}
		else if (command.substring(0, 2).equals(commands[S_DIM])){
			
		}
		else if (command.substring(0, 2).equals(commands[S_DEF])){
			
		}
		else if (command.substring(0, 1).equals(commands[S_FN])){
			
		}
		else if (command.equals(commands[S_END])){
			
		}
		else if (command.substring(0, 2).equals(commands[S_REM])){
			return;
		}

	}

}
