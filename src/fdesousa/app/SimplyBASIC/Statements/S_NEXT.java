/*
 * S_NEXT.java - Implement a NEXT Statement.
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
import fdesousa.app.SimplyBASIC.Statement;
import fdesousa.app.SimplyBASIC.Tokenizer;
import fdesousa.app.SimplyBASIC.Variable;
import android.widget.EditText;

/**
 * <h1>S_NEXT.java</h1>
 * Handles the NEXT Statement by retrieving the matching FOR Statement<br>
 * instance from BASIC Program, if it exists, and executes it.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class S_NEXT extends Statement {

	public S_NEXT(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		String vName;
		S_FOR forNext;
		
		if (t.hasMoreTokens()){
			vName = t.nextToken();
			if (Variable.isVariable(vName) & 
					Variable.checkVariableType(vName) == Variable.NUM){
				forNext = p.getFor(vName);
				forNext.doStNext();
			}
			else {
				errNEXT("INVALID VARIABLE");
				return;
			}
		}
		else {
			errNEXT("NEXT WITHOUT VARIABLE");
			return;
		}
	}
	
	private void errNEXT(String type){
		et.append(type + " - LINE NUMBER " + p.getCurrentLine() + "\n");
		p.stopExec();
	}
}
