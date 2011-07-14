/*
 * S_GOSUB.java - Implement a GOSUB Statement.
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
import fdesousa.app.SimplyBASIC.Expression;
import fdesousa.app.SimplyBASIC.Tokenizer;
import android.widget.EditText;

/**
 * <h1>S_GOSUB.java</h1>
 * Handles the GOSUB Statement, by setting a RETURN point and<br>
 * going to the named line number.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class GoSub extends Statement {

	public GoSub(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		String token;
		if (t.hasMoreTokens()) {
			token = t.nextToken();
			if (Expression.isNumber(token)) {
				int lN = Integer.valueOf(token.trim()).intValue();
				p.putRETURNKeySet(p.getlNs());
				p.setlNs(p.getTailSet(lN));
			}
			else {
				errLineNumber("ILLEGAL");
				return;
			}
		}
		else {
			errLineNumber("MISSING");
			return;
		}
	}
	
	public void errLineNumber(String type){
		et.append(type + " LINE NUMBER - LINE " + p.getCurrentLine());
		p.stopExec();
	}
}
