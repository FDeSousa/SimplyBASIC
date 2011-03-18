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

package fdesousa.app.SimplyBASIC;

import java.util.PriorityQueue;
import java.util.Stack;
import java.util.regex.Pattern;
import android.widget.EditText;

public class Expression {

	static final String[] operators = { "^", "*", "/", "+", "-" };

	boolean FN_Expression = false;

	// A queue of infix values parsed in as a new expression
	PriorityQueue<String> in = new PriorityQueue<String>();
	// The same expression, but rearranged into postfix notation
	PriorityQueue<String> post = new PriorityQueue<String>();
	// A stack of operators. Temporary use while converting
	Stack<String> ops = new Stack<String>();

	public final static String regexNumber = "^([+-]?\\d*\\.?\\d+)[E]?([+-]?\\d+)$";
	public final static String regexExponent = "^([+-]?\\d*\\.?\\d+)[E]{1}([+-]?\\d+)$";

	/**
	 * This empty constructor is used by DEF statement, which then resets the queue later
	 */
	public Expression (){
		FN_Expression = true;
	}

	/**
	 * This constructor initialises the expr queue, and convert input queue to postfix immediately
	 * @param expr - the queue holding the expr to calculate
	 * @param p - an instance of BASICProgram, used with inToPost()
	 * @param et - an instance of EditText, used with inToPost()
	 */
	public Expression (PriorityQueue<String> expr, BASICProgram p, EditText et){
		in = expr;
		inToPost(p, et);
	}

	/**
	 * This constructor initialises the expr queue, but does nothing else
	 * @param expr - the queue holding the expr to calculate
	 */
	public Expression (PriorityQueue<String> expr){
		in = expr;
	}

	public void inToPost(BASICProgram p, EditText et){
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
			if (isNumber(token)){
				if (hasExponent(token)){
					// If there's an exponent, calculate it first
					String n = String.valueOf(calculateExponent(token));
					token = n;
				}
				// Add the number to the Queue
				post.offer(token);
			}
			else if (Variable.isVariable(token)){
				// If it's a variable value, get the value of that variable first
				Variable v = Variable.getVariable(p, token);
				post.offer(String.valueOf(v.getValue(token)));
			}
			else if (Function.isFunction(token)){
				// If it's a function call, calculate it before adding it
				if (Function.isUserFunction(token)){
					p.getFunction(token);
				}
				else {
					Function.doFn(et, token, p);
				}
			}
			else if (isOperator(token)){
				if (ops.isEmpty()){
					ops.push(token);
				}
				else{
					while (precedence(token) <= precedence(ops.peek()) || ! ops.isEmpty()) {
						post.offer(ops.pop());
					}
					ops.push(token);
				}
			}
			else if (token.equals("(")){
				ops.push(token);
			}
			else if (token.equals(")")){
				while (! ops.peek().equals("(")){
					post.offer(ops.pop());
				}
				ops.pop();
			}
		}
		while (! ops.empty()){
			post.offer(ops.pop());
		}
	}

	public double eval(BASICProgram p, EditText et){
		try {
			// The stack with the values to be operated on, in their order
			Stack<Double> runningTotal = new Stack<Double>();
			// The two values to be operated on
			double val1, val2;
			// The operator to use on the two values
			String op;

			if (! post.isEmpty() & post.size() < 2)
				return Double.parseDouble(post.poll());

			// so only parse in one item, and receive nothing.
			while (! post.isEmpty()){
				if (isNumber(post.peek())){
					runningTotal.push(Double.parseDouble(post.poll()));
				}
				else if (isOperator(post.peek())){
					val2 = runningTotal.pop();
					val1 = runningTotal.pop();
					op = post.poll();
					if (op.equals("^")){
						runningTotal.push(Math.pow(val1, val2));
					}
					else if (op.equals("*")){
						runningTotal.push(val1 * val2);
					}
					else if (op.equals("/")){
						runningTotal.push(val1 / val2);
					}
					else if (op.equals("+")){
						runningTotal.push(val1 + val2);
					}
					else if (op.equals("-")){
						runningTotal.push(val1 - val2);
					}
				}
			}	
			return runningTotal.pop();
		}
		catch (Exception e){
			et.append("ILLEGAL FORMULA - LINE NUMBER " + String.valueOf(p.getCurrentLine()) + "\n");
			et.append(e + "\n");
			p.stopExec();
			return Double.MAX_VALUE;
		}
	}

	private int precedence(String token){
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
	 * @param p - an instance of BASICProgram
	 * @param et - an instance of EditText for output
	 * @param expTok - a Tokenizer, for tokenizing the expression
	 * @return A new Expression, with data, and convert to postfix
	 */
	public static Expression getExp(BASICProgram p, EditText et, Tokenizer expTok){
		PriorityQueue<String> expr = new PriorityQueue<String>();

		String token = expTok.nextToken();

		while (expTok.hasMoreTokens()){
			expr.offer(token);
			token = expTok.nextToken();
		}

		Expression e = new Expression(expr, p, et);
		return e;
	}

	public static boolean isOperator(String token){
		for (int i = 0; i < operators.length; i++){
			if (token.equals(operators[i]))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the input String is a number in the format:
	 * ±.123/±.123E456/±123.456/±123.456E789
	 * (± refers to positive/negative number, not the symbol itself)
	 * as compared by regex pattern match
	 * @param input String
	 * @return true if it matches / false if it doesn't
	 */
	public static boolean isNumber(String input){
		// It's a public static just to make it easier to use in BP, which does a check every-so-often.
		try {
			return Pattern.matches(regexNumber, input);
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean hasExponent(String input){
		// It's a public static just to make it easier to use in BP, which does a check every-so-often.
		try {
			return Pattern.matches(regexExponent, input);
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}

	public static double[] separateNumber(String input){
		Pattern p = Pattern.compile(regexExponent);
		String[] split = p.split(input);
		double[] number = { Double.parseDouble(split[1]), Double.parseDouble(split[2]) };
		return number;
	}

	public static double calculateExponent(String input){
		double[] number = separateNumber(input);
		return Math.pow(number[0], number[1]);
	}
}
