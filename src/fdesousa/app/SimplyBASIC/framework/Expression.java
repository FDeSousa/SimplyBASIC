/*
 * Expression.java - Implement an Expression.
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

package fdesousa.app.SimplyBASIC.framework;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.Tokenizer;

/**
 * <h1>Expression.java</h1>
 * This class handles an expression, consisting of one or more:
 * <ul>
 * 	<li>Number</li>
 * 	<li>Variable</li>
 * 	<li>Function</li>
 * 	<li>Operator</li>
 * </ul>
 * It follows the execution order defined by BODMAS, using
 * stacks and queues.
 * @version 0.2
 * @author Filipe De Sousa
 */
public class Expression{

	public final static String regexNumber = "^([+-]?\\d*\\.?\\d+)[E]?([+-]?\\d*)$";
	public final static String regexExponent = "^([+-]?\\d*\\.?\\d+)[E]{1}([+-]?\\d+)$";
	public static final String[] operators = { "^", "*", "/", "+", "-" };
	public static final String[] relations = { "=", "<", ">", "<=", ">=", "<>" };

	boolean FN_Expression = false;

	// A queue of infix values parsed in as a new expression
	Queue<String> in;
	// The same expression, but rearranged into postfix notation
	Queue<String> post = new LinkedList<String>();
	// A stack of operators. Temporary use while converting
	Stack<String> ops = new Stack<String>();

	Terminal terminal;
	Tokenizer t;
	BASICProgram p;
	TextIO et;

	/**
	 * This empty constructor is used by DEF statement, which then resets the queue later
	 */
	public Expression () {
		FN_Expression = true;
	}

	/**
	 * This constructor initialises the expr queue, and convert input queue to postfix immediately
	 * @param expr - the queue holding the expr to calculate
	 * @param program - an instance of BASICProgram, used with inToPost()
	 * @param textIO - an instance of EditText, used with inToPost()
	 */
	public Expression (Queue<String> expr, Terminal terminal) {
		in = expr;
		inToPost();
	}

	/**
	 * This constructor initialises the expr queue, but does nothing else
	 * @param expr - the queue holding the expr to calculate
	 */
	public Expression (Queue<String> expr) {
		in = expr;
	}

	public void inToPost() {
		String token = new String();

		if (! in.isEmpty() & in.size() < 2){
			post.offer(in.poll());
			return;
		}

		while (! in.isEmpty()) {
			token = in.poll();
			// This used to be simpler, it was originally just isNumber(token)
			// but as a number can also have the letter 'E', to mark the exponent, 
			// things have been changed around a little bit to match the possibility
			if (isNumber(token)) {
				if (hasExponent(token)) {
					// If there's an exponent, calculate it first
					String n = String.valueOf(calculateExponent(token));
					token = n;
				}
				// Add the number to the Queue
				post.offer(token);
			} else if (Variable.isVariable(token)) {
				// If it's a variable value, get the value of that variable first
				Variable v = Variable.getVariable(p, token);
				post.offer(String.valueOf(v.getValue(token)));
			} else if (Function.isFunction(token)) {
				// If it's a function call, calculate it before adding it
				if (Function.isUserFunction(token)) {
					p.getFunction(token);
				} else {
					Function.doFn(terminal, token);
				}
			} else if (isOperator(token)) {
				if (ops.isEmpty()) {
					ops.push(token);
				} else {	// While ops isn't empty OR precedence current token <= precedence ops.top
					while (! ops.isEmpty() && (precedence(token) <= precedence(ops.peek()))) {
						post.offer(ops.pop());
					}
					ops.push(token);
				}
			} else if (token.equals("(")) {
				ops.push(token);
			} else if (token.equals(")")) {
				while (! ops.isEmpty() && ! ops.peek().equals("(")) {
					post.offer(ops.pop());
				}
				ops.pop();
			}
		}
		while (! ops.isEmpty()) {
			post.offer(ops.pop());
		}
	}

	public double eval() {
		try {
			// The stack with the values to be operated on, in their order
			Stack<Double> runningTotal = new Stack<Double>();
			// The two values to be operated on
			double val1, val2;
			// The operator to use on the two values
			String op;

			if (! post.isEmpty() & post.size() < 2)
				return Double.valueOf(post.poll().trim()).doubleValue();

			// so only parse in one item, and receive nothing.
			while (! post.isEmpty()) {
				if (isNumber(post.peek())) {
					runningTotal.push(Double.valueOf(post.poll().trim()).doubleValue());
				} else if (isOperator(post.peek())) {
					val2 = runningTotal.pop();
					val1 = runningTotal.pop();
					op = post.poll();
					if (op.equals("^")){
						runningTotal.push(Math.pow(val1, val2));
					} else if (op.equals("*")) {
						runningTotal.push(val1 * val2);
					} else if (op.equals("/")) {
						runningTotal.push(val1 / val2);
					} else if (op.equals("+")) {
						runningTotal.push(val1 + val2);
					} else if (op.equals("-")) {
						runningTotal.push(val1 - val2);
					}
				}
			}	
			return runningTotal.pop();
		} catch (Exception e) {
			et.writeLine("ILLEGAL FORMULA - LINE NUMBER " + String.valueOf(p.getCurrentLine()));
			et.writeLine(e.toString());
			p.stop();
			return Double.MIN_VALUE;
		}
	}

	private int precedence(String token) {
		if (token.equals("(") || token.equals(")"))
			return 3;
		else if (token.equals("^"))
			return 2;
		else if (token.equals("*") || token.equals("/"))
			return 1;
		else if (token.equals("+") || token.equals("-"))
			return 0;
		else
			return -1;
	}

	/**
	 * Get the expression Queue, and create and parse a new Expression instance
	 * @param program - an instance of BASICProgram
	 * @param textIO - an instance of EditText for output
	 * @param expTok - a Tokenizer, for tokenizing the expression
	 * @return A new Expression, with data, and convert to postfix
	 */
	public static Expression getExp(Terminal terminal, Tokenizer expressionTokenizer) {
		Tokenizer tokenizer = expressionTokenizer;
		Queue<String> expr = new LinkedList<String>();

		String token = tokenizer.next();

		while (tokenizer.hasNext()) {
			expr.offer(token);
			token = tokenizer.next();
			if (token.equals("\n")) {
				break;
			}
		}

		Expression e = new Expression(expr);
		return e;
	}

	public static boolean isOperator(String token) {
		for (int i = 0; i < operators.length; i++) {
			if (token.equals(operators[i]))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the input String is a number in the format:
	 * �.123/�.123E456/�123.456/�123.456E789
	 * (� refers to positive/negative number, not the symbol itself)
	 * as compared by regex pattern match
	 * @param input String
	 * @return true if it matches / false if it doesn't
	 */
	public static boolean isNumber(String input) {
		try {
			// It's a public static just to make it easier to use in BP, which does a check every-so-often.
			Pattern p = Pattern.compile(regexNumber);
			Matcher m = p.matcher(input);
			return m.find();
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean hasExponent(String input) {
		try {
			// It's a public static just to make it easier to use in BP, which does a check every-so-often.
			Pattern p = Pattern.compile(regexExponent);
			Matcher m = p.matcher(input);
			return m.find();
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static double[] separateNumber(String input) {
		try {
			Pattern p = Pattern.compile(regexExponent);
			Matcher m = p.matcher(input);
			double[] numbers = new double[2];
			if (m.matches()) {
				if (m.find()) {
					numbers[0] = Double.valueOf(m.group(1).trim()).doubleValue();
					numbers[1] = Double.valueOf(m.group(2).trim()).doubleValue();
					return numbers;
				}
				//	Not bothering with an else as we return null otherwise
			}
		} catch (IllegalStateException e) {
			//	Ignore, since we'll be returning null either way
		}
		return null;
	}

	public static double calculateExponent(String input) {
		double[] numbers = separateNumber(input);
		return Math.pow(numbers[0], numbers[1]);
	}
}
