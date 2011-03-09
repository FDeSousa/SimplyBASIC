package fdesousa.app.SimplyBASIC;

public class Statement {
	final static String[] commands = {
		"IF", "THEN", "FOR", "TO", 
		"STEP", "NEXT", "LET", "READ",
		"DATA", "PRINT", "GOTO", "GOSUB",
		"RETURN", "DIM", "DEF", "FN", 
		"STOP", "END", "REM"};
	
	final int S_IF	 	=  0;	// Start of IF...THEN statement
	final int S_THEN	=  1;	// Continues of IF...THEN statement
	final int S_FOR		=  2;	// Start of FOR...TO...STEP statement
	final int S_TO		=  3;	// Defines limit of FOR...TO...STEP statement
	final int S_STEP	=  4;	// The number to (in/de)crement by in FOR
	final int S_NEXT	=  5;	// 
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
	final int S_STOP	= 16;	// Stops execution of the program in its tracks
	final int S_END		= 17;	// Ends the program on that line, no matter what
	final int S_REM		= 18;	// Signifies the line is a comment, and should be ignored by interpreter
	
	public Statement(){
		
	}
	
	public void doStatement(Tokenizer tokenizer){
		
	}
	
}
