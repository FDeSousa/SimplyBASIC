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

package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_DIM extends Statement {

	public S_DIM(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		while (t.hasMoreTokens()){
			String vName = t.nextToken();
			// Check if it's a Variable
			if (Variable.isVariable(vName)){
				@SuppressWarnings("unused")
				Variable v = new Variable(vName);
				// Since putting all of the constructors of Variable into one
				// unified constructor, that figures out, splits, and then assigns
				// initialises itself, it's much easier here
			}
			else if (t.equals(",")){
				;	// Don't do anything with it, just acknowledge its existence
			}
			else {
				et.append("ILLEGAL VARIABLE - LINE " + p.getCurrentLine() + "\n");
			}
		}
	}
}
