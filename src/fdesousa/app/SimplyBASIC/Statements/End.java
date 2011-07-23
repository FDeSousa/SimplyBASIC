/*
 * S_END.java - Implement the END Statement.
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

import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.framework.Statement;

/**
 * <h1>S_END.java</h1>
 * Handles an END Statement, by ending the execution and<br>
 * printing the time it took to execute.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class End extends Statement {

	public End(Terminal terminal) {
		super(terminal);
	}

	@Override
	public void doSt() {
		(terminal.getTextIO()).writeLine("TIME TO FINISH: " + 
				String.valueOf((terminal.getBasicProgram()).getTimeToExecute() / 10.0) + 
				" SECONDS.");	//	Display the time it took to execute and finish
		(terminal.getBasicProgram()).stop();
		return;
	}
}
