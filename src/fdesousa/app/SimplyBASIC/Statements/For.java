/*
 * S_FOR.java - Implement a FOR Statement.
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

import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.Tokenizer;
import fdesousa.app.SimplyBASIC.framework.Expression;
import fdesousa.app.SimplyBASIC.framework.Statement;
import fdesousa.app.SimplyBASIC.framework.TextIO;
import fdesousa.app.SimplyBASIC.framework.Variable;

/**
 * <h1>S_FOR.java</h1>
 * Handles the FOR Statement. It takes an instance of a named Variable,<br>
 * and the named Expressions for Assigning, checking the Limit, and<br>
 * setting the Step of a FOR Statement for every iteration.
 * @author Filipe De Sousa
 * @version 0.1
 */
public class For extends Statement {
	/*
	 * FOR statement syntax is:
	 * 	FOR v = assign TO limit
	 * or
	 * 	FOR v = assign TO limit STEP count
	 */
	private String regexNOSTEP = "^\\s*(\\w{1}\\d?)\\s*[=]{1}(.+)[T]{1}[O]{1}(.+)$";
	private String regexSTEP = "^\\s*(\\w{1}\\d?)\\s*[=]{1}(.+)[T]{1}[O]{1}(.+)[S]{1}[T]{1}[E]{1}[P]{1}(.+)$";
	/*
	 * The variable and the three expressions associated with FOR:
	 * 	v is the named variable to increment/decrement with FOR .. NEXT
	 * 	assign is the expression to assign v with first
	 * 	limit is the expression v is limited to
	 * 	count if the expression v is (in/de)cremented by
	 */
	private Variable v;
	private Expression assgn, limit, count;
	/*
	 * As the value of the expressions is all we care about, these
	 * store them.
	 *	first = assign.eval
	 *	last = limit.eval
	 *	step = count.eval
	 */
	private double first, last, step;
	// Store the code list to return to
	private Set<Entry<Integer, String>> codeList;

	Tokenizer tokenizer;
	BASICProgram program;
	TextIO textIO;

	public For(Terminal terminal) {
		super(terminal);
		tokenizer = terminal.getTokenizer();
		program = terminal.getBasicProgram();
		textIO = terminal.getTextIO();
	}

	@Override
	/**
	 * Do the statement first-time, when run by Statement
	 */
	public void doSt() {
		// Get the FOR expressions, and calculate them
		getFORExp();
		if (v.getValue() < last) {
			codeList = program.getlNs();
			program.newFor(getName(), this);			
		} else {
			textIO.writeLine("ERROR ASSIGNING FOR - LINE NUMBER " + program.getCurrentLine());
			program.stopExec();
		}
	}

	/**
	 * Do the statement a subsequent time, when run by NEXT
	 */
	public void doStNext() {
		if (v.getValue() < last) {
			v.setValue(v.getValue() + step);
			program.setlNs(codeList);
		}
	}

	/**
	 * Get the FOR expressions, for either type of FOR statement.<br>
	 * Once these calculations are done, set the variables to measure against
	 */
	private void getFORExp() {
		String line = tokenizer.getRestOfLine();
		Pattern pT;
		Matcher m;
		Tokenizer expTok = new Tokenizer();

		if (Pattern.matches(regexSTEP, line)) {
			pT = Pattern.compile(regexSTEP);
			m = pT.matcher(line);
			/*
			 * Upon using .group we get:
			 * 0	The whole matched portion
			 * 1	Named Variable
			 * 2	Assignment Expression
			 * 3	Limit Expression
			 * 4	Step Expression
			 */
			v = Variable.getVariable(program, m.group(1));
			expTok.reset(m.group(2));
			assgn = Expression.getExp(terminal, expTok);
			first = assgn.eval();
			v.setValue(first);

			expTok.reset(m.group(3));
			limit = Expression.getExp(terminal, expTok);
			last = limit.eval();

			expTok.reset(m.group(4));
			count = Expression.getExp(terminal, expTok);
			step = count.eval();
			
		} else if (Pattern.matches(regexNOSTEP, line)) {
			pT = Pattern.compile(regexNOSTEP);
			m = pT.matcher(line);
			/*
			 * Upon using .group we get:
			 * 0	The whole line
			 * 1	Named Variable
			 * 2	Assignment Expression
			 * 3	Limit Expression
			 */
			v = Variable.getVariable(program, m.group(1));
			expTok.reset(m.group(2));
			assgn = Expression.getExp(terminal, expTok);
			first = assgn.eval();
			v.setValue(first);

			expTok.reset(m.group(3));
			limit = Expression.getExp(terminal, expTok);
			last = limit.eval();

			step = 1.0;
			
		} else {
			textIO.writeLine("INVALID FOR - LINE NUMBER " + program.getCurrentLine());
			program.stopExec();
		}
	}

	public static For getFOR(BASICProgram p, String vName) {
		return p.getFor(vName);
	}

	public String getName() {
		return v.getName();
	}
}
