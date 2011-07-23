/*
 * S_DATA.java - Implement a DATA Statement.
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
import fdesousa.app.SimplyBASIC.framework.Expression;
import fdesousa.app.SimplyBASIC.framework.Statement;
import fdesousa.app.SimplyBASIC.framework.TextIO;

/**
 * <h1>S_DATA.java</h1>
 * Handles a DATA Statement, by adding each piece of data to a<br>
 * stack in the BASIC Program instance.
 * @version 0.2
 * @author Filipe De Sousa
 */
public class Data extends Statement {
	Tokenizer tokenizer;
	BASICProgram program;
	TextIO textIO;

	public Data(Terminal terminal){
		super(terminal);
		tokenizer = terminal.getTokenizer();
		textIO = terminal.getTextIO();
	}

	@Override
	public void doSt(){
		String s = new String();

		while (tokenizer.hasNext()){
			s = tokenizer.next();
			if (Expression.isNumber(s)){	//	Add numbers to program's data list
				program.addData(Double.valueOf(s.trim()).doubleValue());
			} else if (s.equals(",")){		// Ignore and continue past commas
				continue;
			} else if (s.equals("\n")){		// Break from this Statement if the token is EOL
				return;						// ("\n") as there's nothing else to do
			} else {						// If the token isn't a number/comma/EOL, it's in the wrong place
				errConstant(s);
				return;
			}
		}
	}

	private void errConstant(String s){
		textIO.writeLine("ILLEGAL CONSTANT: " + s + " LINE NUMBER " + program.getCurrentLine() +".\n");
		program.stop();
	}
}