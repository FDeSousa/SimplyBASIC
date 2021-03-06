/*
 * S_READ.java - Implement a READ Statement.
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
import fdesousa.app.SimplyBASIC.framework.Variable;

/**
 * <h1>S_READ.java</h1>
 * Handles a READ Statement, by retrieving a value from the DATA stack<br>
 * and assign it to the named Variable.
 * @version 0.2
 * @author Filipe De Sousa
 */
public class Read extends Statement {
	Tokenizer t;
	BASICProgram p;
	
	public Read(Terminal terminal) {
		super(terminal);
		t = terminal.getTokenizer();
		p = terminal.getBasicProgram();
	}

	@Override
	public void doSt() {
		do {
			String token = t.next();
			if (! token.equals(",")) {
				if (Variable.isVariable(token)) {
					if (p.hasData()) {
						Variable v = Variable.getVariable(p, token);
						v.setValue(token, p.getData());
					} else {
						errREAD("NO DATA");
						return;
					}
				} else {
					errREAD("ILLEGAL VARIABLE");
					return;
				}
			} else if (token.equals("\n")) {
				// Just acknowledge and leave, on EOL
				return;
			}
		} while (t.hasNext());
	}
	
	private void errREAD(String type) {
		terminal.getTextIO().writeLine(type + " - LINE NUMBER " + p.getCurrentLine());
		p.stop();
	}
}
