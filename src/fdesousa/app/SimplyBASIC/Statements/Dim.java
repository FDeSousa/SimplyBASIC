/*
 * S_DIM.java - Implement a DIM Statement.
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

package fdesousa.app.SimplyBASIC.Statements;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.Tokenizer;
import fdesousa.app.SimplyBASIC.framework.Statement;
import fdesousa.app.SimplyBASIC.framework.TextIO;
import fdesousa.app.SimplyBASIC.framework.Variable;

/**
 * <h1>S_DIM.java</h1>
 * Handles a DIM Statement, by instantiating one of more new Number Array(s)<br>
 * (either One- or Two-Dimensional), without elements.
 * @version 0.2
 * @author Filipe De Sousa
 */
public class Dim extends Statement {
	Tokenizer tokenizer;
	BASICProgram program;
	TextIO textIO;
	
	public Dim(Terminal terminal){
		super(terminal);
		tokenizer = terminal.getTokenizer();
		program = terminal.getBasicProgram();
		textIO = terminal.getTextIO();
	}

	@Override
	public void doSt(){
		while (tokenizer.hasMoreTokens()){
			String vName = tokenizer.nextToken();
			// Check if it's a Variable
			if (Variable.isVariable(vName)){
				Variable v = new Variable(vName);
				// Since putting all of the constructors of Variable into one
				// unified constructor, that figures out, splits, and then assigns
				// initialises itself, it's much easier here
				program.putVar(v);
			}
			else if (tokenizer.equals(",")){
				;	// Don't do anything with it, just acknowledge its existence
			}
			else if (tokenizer.equals("\n")){
				return;	// Not an error condition, but an exit condition
			}
			else {
				errVariable();
				return;
			}
		}
	}
	
	private void errVariable(){
		textIO.writeLine("ILLEGAL VARIABLE - LINE NUMBER " + program.getCurrentLine());
		program.stopExec();
	}
}
