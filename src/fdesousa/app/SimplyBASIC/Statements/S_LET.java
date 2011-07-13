/*
 * S_LET.java - Implement a LET Statement.
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

import java.util.LinkedList;
import java.util.Queue;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Expression;
import fdesousa.app.SimplyBASIC.Statement;
import fdesousa.app.SimplyBASIC.Tokenizer;
import fdesousa.app.SimplyBASIC.Variable;

import android.widget.EditText;

/**
 * <h1>S_LET.java</h1>
 * Handles the LET Statement, by instantiating a new Variable or<br>
 * retrieving an instantiated one from BASIC Program's storage and<br>
 * assigning it the given value from the evaluated expression.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class S_LET extends Statement {

	public S_LET(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		String vName = t.nextToken();
		// Let Variable sort itself out, and return a Variable to work with
		Variable v = Variable.getVariable(p, vName);
		
		String token = t.nextToken();
		// If token is an equals sign, the expression begins next
		if (token.equals("=")){
			Queue<String> expression = new LinkedList<String>();
			// Very simple for the moment, will only handle numbers and symbols, hoping to mend that asap
			while (t.hasMoreTokens()){
				token = t.nextToken();
				if (! token.equals("\n")){
					expression.offer(token);
				}
				else{
					break;
				}
			}
			doAssign(expression, v, vName);
			return;
		}
		else {
			errLineNumber("INCORRECT FORMAT");
			return;
		}
	}
	
	private void doAssign(Queue<String> expression, Variable v, String vName){
		Expression e = new Expression(expression, p, et);
		// Once the expression has been resolved, have to put it somewhere, ideally in the named variable
		v.setValue(vName, e.eval(p, et));
		et.append(String.valueOf(v.getValue(vName)) + "\n");
	}
	
	private void errLineNumber(String type){
		et.append(type + " - LINE NUMBER " + p.getCurrentLine());
		p.stopExec();
	}
}
