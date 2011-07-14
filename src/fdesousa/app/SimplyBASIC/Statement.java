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

import fdesousa.app.SimplyBASIC.Statements.Def;
import fdesousa.app.SimplyBASIC.Statements.Dim;
import fdesousa.app.SimplyBASIC.Statements.End;
import fdesousa.app.SimplyBASIC.Statements.For;
import fdesousa.app.SimplyBASIC.Statements.GoSub;
import fdesousa.app.SimplyBASIC.Statements.Goto;
import fdesousa.app.SimplyBASIC.Statements.If;
import fdesousa.app.SimplyBASIC.Statements.Let;
import fdesousa.app.SimplyBASIC.Statements.Next;
import fdesousa.app.SimplyBASIC.Statements.Print;
import fdesousa.app.SimplyBASIC.Statements.Read;
import fdesousa.app.SimplyBASIC.Statements.Return;
import android.widget.EditText;

/**
 * <h1>Statement.java</h1>
 * Determines what to do with a BASIC Statement command from the current line.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class Statement {
	final static String[] STATEMENTS = {
		"IF", "THEN", "FOR", "TO", "STEP", 
		"NEXT", "LET", "READ", "DATA", 
		"PRINT", "GOTO", "GOSUB", "RETURN", 
		"DIM", "DEF", "FN", "END", "REM"};

	final static int IF	 		=  0;	// Start of IF...THEN statement
	final static int THEN		=  1;	// Continues of IF...THEN statement
	final static int FOR		=  2;	// Start of FOR...TO...STEP statement
	final static int TO			=  3;	// Defines limit of FOR...TO...STEP statement
	final static int STEP		=  4;	// The number to (in/de)crement by in FOR
	final static int NEXT		=  5;	// (in/de)crements variable defined by FOR with variable 
	final static int LET		=  6;	// Assignment statement
	final static int READ		=  7;	// Reads the next Data value (FIFO ordering)
	final static int DATA		=  8;	// Provides data values for the program
	final static int PRINT		=  9;	// Print something to screen
	final static int GOTO		= 10;	// Unconditional transferal of program execution to a different line
	final static int GOSUB		= 11;	// As GOTO, but can be used to define a sub-routine that is returnable
	final static int RETURN		= 12;	// Returns execution to where GOSUB left off
	final static int DIM		= 13;	// Used to define one- or two-dimensional arrays
	final static int DEF		= 14;	// Used to define a function
	final static int FN			= 15;	// Beginning two letters of a user-defined function
	final static int END		= 16;	// Ends the program on that line, no matter what
	final static int REM		= 17;	// Signifies the line is a comment, and should be ignored by interpreter

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

		if (command.equals(STATEMENTS[IF])){
			s = new If(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[FOR])){
			s = new For(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[NEXT])){
			s = new Next(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[LET])){
			s = new Let(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[READ])){
			s = new Read(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[DATA])){
			return;	// As we have a first-run to get DATA, it's safer to
					// acknowledge, but ignore it in Statement.java
		}
		else if (command.equals(STATEMENTS[PRINT])){
			s = new Print(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[GOTO])){
			s = new Goto(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[GOSUB])){
			s = new GoSub(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[RETURN])){
			s = new Return(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[DIM])){
			s = new Dim(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[DEF])){
			s = new Def(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[END])){
			s = new End(p, t, et);
			s.doSt();
		}
		else if (command.equals(STATEMENTS[REM])){
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
