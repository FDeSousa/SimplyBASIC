/*
 * S_IF.java - Implement an IF Statement.
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

import java.util.PriorityQueue;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.Tokenizer;
import fdesousa.app.SimplyBASIC.framework.Expression;
import fdesousa.app.SimplyBASIC.framework.Statement;
import fdesousa.app.SimplyBASIC.framework.TextIO;

/**
 * <h1>S_IF.java</h1>
 * Handles the IF Statement, by evaluating two given expressions and<br>
 * checking their results against each other, by the given relation.<br>
 * If the relation is evaluated as true, the program then moves to execute<br>
 * the named line number.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class If extends Statement {
	Tokenizer t;
	BASICProgram p;
	TextIO textIO;
	
	public If(Terminal terminal) {
		super(terminal);
		t = terminal.getTokenizer();
		p = terminal.getBasicProgram();
		textIO = terminal.getTextIO();
	}

	@Override
	public void doSt() {
		// Syntax of IF:
		//	IF <expression> <relation> <expression> THEN <linenumber>
		String token = new String();
		String relation = new String();

		// Get the first expression
		Expression e1 = getIFExpr(false);

		// Get the relational operator
		relation = token;

		// Get the second expression
		Expression e2 = getIFExpr(true);

		// Do the operation before considering the THEN
		if (t.hasNext() & doIFRel(e1, e2, relation)){
			// Token already contains THEN, and it's been checked if it exists too
			// So we can pass over it, and move to the next token, line number
			if (Expression.isNumber(token)){
				Statement gotoLN = new Goto(terminal);
				// gotoLN gets the next token, we already have THEN
				// gotoLN then sets the point of execution in p to the new line number 
				gotoLN.doSt();
			}
		}

	}

	/**
	 * This function returns a new Expression
	 * @param endOnTHEN - true to end expression with "THEN", false if with relation
	 * @return Expression e, new expression based on the written Expression
	 */
	private Expression getIFExpr(boolean endOnTHEN) {
		PriorityQueue<String> expr = new PriorityQueue<String>();
		String token = t.next();

		if (endOnTHEN){	// This gets an expression that ends with THEN
			while (t.hasNext() & 
					! token.equals("THEN")){	// Keep that THEN away!
				// If t has more tokens, and the token isn't THEN, we're still in an expression
				expr.offer(token);
				token = t.next();
			}
		}
		else{			// This gets an expression that ends with relational operator
			// Get the first expression
			while (t.hasNext() & !isRelation(token)) {		// We don't want no stinkin' relations here!
				// If t has more tokens, and the token isn't a relational operator, we're still in an expression
				expr.offer(token);
				token = t.next();
			}
		}
		Expression e = new Expression(expr, terminal);
		return e;
	}

	private boolean doIFRel(Expression e1, Expression e2, String relation) {
		if (relation.equals("=")) {
			if (e1.eval() == e2.eval())
				return true;	// The expressions are equal in value
		} else if (relation.equals("<=")) {
			if (e1.eval() <= e2.eval())
				return true;	// Expression 1 is less than or equal to expression 2
		} else if (relation.equals(">=")) {
			if (e1.eval() >= e2.eval())
				return true;	// Expression 1 is greater than or equal to expression 2
		} else if (relation.equals("<")) {
			if (e1.eval() < e2.eval())
				return true;	// Expression 1 is less than expression 2
		} else if (relation.equals(">")) {
			if (e1.eval() > e2.eval())
				return true;	// Expression 1 is greater than expression 2
		} else if (relation.equals("<>")) {
			if (e1.eval() != e2.eval())
				return true;	// Expression 1 is not equal to expression 2
		}
		return false;			// If any of the above fail, return false
	}

	public static boolean isRelation(String token) {
		return  (token.equals("=")	|| 	// Equal to
				 token.equals("<=")	|| 	// Less than or equal to
				 token.equals(">=")	|| 	// Greater than or equal to
				 token.equals("<")	|| 	// Less than
				 token.equals(">")	|| 	// Greater than
				 token.equals("<>"));	// Not equal to
		// Should return true if any of these is the case
	}
}
