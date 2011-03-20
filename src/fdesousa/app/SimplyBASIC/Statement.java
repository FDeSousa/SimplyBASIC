/*
 * Statement.java - Implement a generic Statement.
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

import android.widget.EditText;

/**
 * <h1>Statement.java</h1>
 * Determines what to do with a BASIC Statement command from the current line.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class Statement {
	final static String[] statements = {
		"IF", "THEN", "FOR", "TO", "STEP", 
		"NEXT", "LET", "READ", "DATA", 
		"PRINT", "GOTO", "GOSUB", "RETURN", 
		"DIM", "DEF", "FN", "END", "REM"};

	final static int S_IF	 	=  0;	// Start of IF...THEN statement
	final static int S_THEN		=  1;	// Continues of IF...THEN statement
	final static int S_FOR		=  2;	// Start of FOR...TO...STEP statement
	final static int S_TO		=  3;	// Defines limit of FOR...TO...STEP statement
	final static int S_STEP		=  4;	// The number to (in/de)crement by in FOR
	final static int S_NEXT		=  5;	// (in/de)crements variable defined by FOR with variable 
	final static int S_LET		=  6;	// Assignment statement
	final static int S_READ		=  7;	// Reads the next Data value (FIFO ordering)
	final static int S_DATA		=  8;	// Provides data values for the program
	final static int S_PRINT	=  9;	// Print something to screen
	final static int S_GOTO		= 10;	// Unconditional transferal of program execution to a different line
	final static int S_GOSUB	= 11;	// As GOTO, but can be used to define a sub-routine that is returnable
	final static int S_RETURN	= 12;	// Returns execution to where GOSUB left off
	final static int S_DIM		= 13;	// Used to define one- or two-dimensional arrays
	final static int S_DEF		= 14;	// Used to define a function
	final static int S_FN		= 15;	// Beginning two letters of a user-defined function
	final static int S_END		= 16;	// Ends the program on that line, no matter what
	final static int S_REM		= 17;	// Signifies the line is a comment, and should be ignored by interpreter

	private String command = new String();
	
	protected BASICProgram p;
	protected Tokenizer t;
	protected EditText et;
	
	public Statement(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		// Adding these here instead of stating them elsewhere
		p = pgm;
		t = tok;
		et = edtxt;
	}
	
	public void doSt() {
		Statement s;

		if (t.hasMoreTokens()){
			command = t.nextToken();
		}
		
		// Because a user could potentially type in a line without spaces between the
		// command and the rest of the line, we're checking here to see if the first n
		// letters of the command string match up to the command
		// This makes for messy code, and possibly messy results, so it should be noted
		// to the user that input should really, ideally, have spaces between tokens
		if (command.equals(statements[S_IF])){
			s = new S_IF(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_FOR])){
			s = new S_FOR(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_NEXT])){
			s = new S_NEXT(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_LET])){
			s = new S_LET(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_READ])){
			s = new S_READ(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_DATA])){
			return;	// As we have a first-run to get DATA, it's safer to
					// acknowledge, but ignore it in Statement.java
		}
		else if (command.equals(statements[S_PRINT])){
			s = new S_PRINT(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_GOTO])){
			s = new S_GOTO(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_GOSUB])){
			s = new S_GOSUB(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_RETURN])){
			s = new S_RETURN(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_DIM])){
			s = new S_DIM(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_DEF])){
			s = new S_DEF(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_END])){
			s = new S_END(p, t, et);
			s.doSt();
		}
		else if (command.equals(statements[S_REM])){
			return;	// When encountering a REM statement, the line is ignored, so for
					// safety, acknowledge but ignore the statement here by using return
		}
		else{
			et.append("ILLEGAL INSTRUCTION - LINE NUMBER " + String.valueOf(p.getCurrentLine()) + "\n");
			p.stopExec();
			return;
		}
	}

}
