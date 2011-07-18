/*
 * S_RETURN.java - Implement a RETURN Statement.
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
import fdesousa.app.SimplyBASIC.framework.Statement;

/**
 * <h1>S_RETURN.java</h1>
 * Handles RETURN Statement by returning line execution to the<br>
 * point of the last called GOSUB statement.
 * @version 0.2
 * @author Filipe De Sousa
 */
public class Return extends Statement {
	BASICProgram p;
	
	public Return(Terminal terminal) {
		super(terminal);
		p = terminal.getBasicProgram();
	}

	@Override
	public void doSt() {
		if (!p.getRETURNKeySetisEmpty()) {
			p.setlNs(p.getRETURNKeySet());
		} else {
			(terminal.getTextIO()).writeLine("ILLEGAL RETURN - LINE NUMBER " + p.getCurrentLine());
			p.stopExec();
			return;
		}
	}
}
