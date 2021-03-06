/*
 * S_DEF.java - Implement a DEF Statement.
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

import java.util.PriorityQueue;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.Tokenizer;
import fdesousa.app.SimplyBASIC.framework.Expression;
import fdesousa.app.SimplyBASIC.framework.Function;
import fdesousa.app.SimplyBASIC.framework.Statement;
import fdesousa.app.SimplyBASIC.framework.Variable;

/**
 * <h1>S_DEF.java</h1>
 * Handles a DEF Statement, by instantiating a new User Function,<br>
 * which is then stored in an instance of BASIC Program.
 * @version 0.2
 * @author Filipe De Sousa
 */
public class Def extends Statement {
	Tokenizer tokenizer;
	BASICProgram program;
	
	public Def(Terminal terminal) {
		super(terminal);
		tokenizer = terminal.getTokenizer();
		program = terminal.getBasicProgram();
	}

	@Override
	public void doSt() {
		// Next token after "DEF" will be the function call name
		String fnName = tokenizer.next();
		// Get the argument from within fnName, this is the variable to look for
		String fnVarName = Function.getArg(fnName);
		// Create a new Variable for use with this user-defined Function
		Variable fnVar = new Variable(fnVarName);
		// Put this new variable into BASIC Program
		program.putVar(fnVar);
		
		PriorityQueue<String> expression = new PriorityQueue<String>();
		
		// Since we have the basic stuff sorted, get the expression associated with this new Function
		String token = tokenizer.next();
		// If token is an equals sign, the expression begins next
		if (token.equals("=")) {
			// Very simple for the moment, will only handle numbers and symbols, hoping to mend that asap
			while (tokenizer.hasNext()) {
				if (! token.equals("\n")) {
					token = tokenizer.next();
					expression.offer(token);					
				} else if (token.equals("\n")) {
					putFn(fnName, expression, fnVar);
					return;
				} else {
					errFormat();
					return;
				}
			}
		} else {
			// If the token isn't a number/comma/EOL, it's in the wrong place
			errFormat();
			return;
		}
		
	}
	
	private void putFn(String fnName, PriorityQueue<String> expression, Variable fnVar) {
		Expression fnExp = new Expression(expression);
		Function fn = new Function(terminal, fnName, fnExp, fnVar);
		program.putFunction(fn);
	}
	
	private void errFormat() {
		terminal.getTextIO().writeLine("INCORRECT FORMAT - LINE NUMBER " + program.getCurrentLine());
		program.stop();
	}
}