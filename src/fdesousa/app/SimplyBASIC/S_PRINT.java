/*
 * S_PRINT.java - Implement a PRINT Statement.
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

/**
 * <h1>S_PRINT.java</h1>
 * Handles the PRINT Statement. It can print a string to the screen<br>
 * consisting of the value of a named Variable, the result of a named<br>
 * evaluated expression, or a String literal given in double-quotes.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class S_PRINT extends Statement {

	public S_PRINT(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		String token = new String();
		String outLine = new String();
		// while t has more tokens, keep them coming, and evaluate whatever needs evaluation on-the-spot, before printing
		// start/end of printable string: '"'
		// separator of sections: ','
		while (t.hasMoreTokens()){
			token = t.nextToken();
			
			if (token.contains("\"")){
				// If the token has a double-quotation mark, it's a literal, print it
				outLine += Tokenizer.removeQuotes(token);
			}
			else if (token.equals(",")){
				// Acknowledge commas as new line indicator
				outLine += "\t";
			}
			else if (token.equals("\n")){
				// Acknowledge this but ignore it, we default to adding "\n" later
				break;
			}
			else {
				// Assume it's an expression, figure that out first
				outLine += exp(token);
			}
		}
		et.append(outLine + "\n");
	}
	
	private String exp(String token){
		String append = new String();
		PriorityQueue<String> ex = new PriorityQueue<String>();
		Expression e;
		ex.offer(token);
		
		while (t.hasMoreTokens()){
			token = t.nextToken();
			if (token.equals(",") || token.equals("\n")){
				break;
			}
			else{
				ex.offer(token);
			}
		}
		e = new Expression(ex, p, et);
		append += String.valueOf(e.eval(p, et)) + "\t";
		return append;
	}
}
