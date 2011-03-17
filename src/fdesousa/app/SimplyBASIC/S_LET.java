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

package fdesousa.app.SimplyBASIC;

import java.util.PriorityQueue;
import android.widget.EditText;

public class S_LET extends Statement {

	public S_LET(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		double result = 0.0;
		String vName = t.nextToken();
		// Let Variable sort itself out, and return a Variable to work with
		Variable v = Variable.getVariable(p, vName);
		
		String token = t.nextToken();
		// If token is an equals sign, the expression begins next
		if (token.equals("=")){
			PriorityQueue<String> expression = new PriorityQueue<String>();
			// Very simple for the moment, will only handle numbers and symbols, hoping to mend that asap
			while (t.hasMoreTokens()){
				token = t.nextToken();
				expression.offer(token);
			}
			Expression e = new Expression(expression, p, et);
			result = e.eval(p, et);	// Expression we want to evaluate was parsed while instantiating 'e'
		}
		
		// Once the expression has been resolved, have to put it somewhere, ideally in the named variable
		v.setValue(vName, result);
		et.append("\n" + String.valueOf(result));
	}
}
