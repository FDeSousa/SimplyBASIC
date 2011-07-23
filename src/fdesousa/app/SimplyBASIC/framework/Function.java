/*
 * Function.java - Implement a Function.
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

import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.Tokenizer;

/**
 * <h1>Function.java</h1>
 * <p>This class handles a Function, which consists of an Expression,<br>
 * an a named Variable, both for a User Function or a BASIC Function.</p>
 * <p>BASIC Functions are handled by a static method.</p>
 * <p>User Functions are instantiated and stored by BASICProgram.</p>
 * @version 0.1
 * @author Filipe De Sousa
 */
public class Function{
	Terminal terminal;
	
	final static String[] functions = { 
		"SIN", "COS", "TAN", "ATN", "EXP", 
		"ABS", "LOG", "SQR", "RND", "INT" };
	final static int FN_SIN = 0;	// SINE function
	final static int FN_COS = 1;	// COSINE function
	final static int FN_TAN = 2;	// TANGENT function
	final static int FN_ATN = 3;	// ARCTANGENT function
	final static int FN_EXP = 4;	// Natural exponential function
	final static int FN_ABS = 5;	// Modulus function
	final static int FN_LOG = 6;	// Logarithm function
	final static int FN_SQR = 7;	// Square root function
	final static int FN_RND = 8;	// Randomiser function
	final static int FN_INT = 9;	// Integer operation

	// Matches a User Function name [FNA.]
	// When split produces full function call in group 0, function name in group 1
	public static String regexUserFunctions = "^([F]{1}[N]{1}[A-Z]{1}).*$";	
	// Matches and gets a User Function call with arguments [FNA(.)]
	// When split produces full function call in group 0, function name in group 1, function arguments in group 2
	public static String regexUserFunctionTokens = "^([F]{1}[N]{1}[A-Z]{1})[(](.+)[)]$";
	// This regex gets the argument, no matter what type of function it is
	public static String regexFunctionTokens = "^([A-Z]{3})[(](.+)[)]$";

	private String fnName;
	private Expression fnExpression;
	private Variable fnVariable;
	
	public Function (Terminal terminal, String fnName, Expression fnExpression, Variable fnVariable) {
		this.terminal = terminal;
		this.fnName = fnName;
		this.fnExpression = fnExpression;
		this.fnVariable = fnVariable;
	}

	public String getName() {
		return fnName;
	}
	
	public static String getFnName(String token) {
		Pattern pT = Pattern.compile(regexFunctionTokens);
		Matcher m = pT.matcher(token);
		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}

	public Expression getExpression() {
		return fnExpression;
	}

	public Variable getVariable() {
		return fnVariable;
	}

	public double doUserFn(String token) {
		Pattern pT = Pattern.compile(regexFunctionTokens);
		Matcher m = pT.matcher(token);
		if (m.find()) {
			double arg = evalArg(terminal, m.group(2));
			// Assign the arg value to the Function's named variable, 
			// which is used by user functions exclusively
			fnVariable.setValue(arg);
			// So now, onto the meat of it, do the function!
			// Convert the function's expression into postfix (this reorganises it
			// while also resolving the variable names included in the expression)
			fnExpression.inToPost();
			// And evaluate that expression!
			return fnExpression.eval();
		} else {
			return 0.0;			
		}
	}
	
	public static double doFn(Terminal terminal, String token) {
		if (token.equals(functions[FN_RND])) {
			return Math.random();
		}	// Save some processor time, if it's the RANDOM function
			// just return a random number right away
		
		Pattern pT = Pattern.compile(regexFunctionTokens);
		Matcher m = pT.matcher(token);
		double arg;
		if (m.find()) {
			arg = evalArg(terminal, m.group(2));
		} else {
			return 0.0;
		}
		
		if (token.equals(functions[FN_SIN])) {
			//	Sine operation
			return Math.sin(arg);
		} else if (token.equals(functions[FN_COS])) {
			//	Cosine operation
			return Math.cos(arg);
		} else if (token.equals(functions[FN_TAN])) {
			//	Tangent operation
			return Math.tan(arg);
		} else if (token.equals(functions[FN_ATN])) {
			//	Arctangent operation
			return Math.atan(arg);
		} else if (token.equals(functions[FN_EXP])) {
			//	Natural exponential operation
			return Math.exp(arg);
		} else if (token.equals(functions[FN_ABS])) {
			//	Modulus/Absolute operation
			return Math.abs(arg);
		} else if (token.equals(functions[FN_LOG])) {
			//	Logarithm operation
			return Math.log(arg);
		} else if (token.equals(functions[FN_SQR])) {
			//	Square Root operation
			return Math.sqrt(arg);
		} else if (token.equals(functions[FN_INT])) {
			//	Integer operation
			return Math.round(arg);
		} else {
			// Return 0.0 is none-of-the-above
			return 0.0;
		}
	}
	
	public static double evalArg(Terminal terminal, String argument) {
		double arg = 0.0;
		// Have to do a few checks here first, as a Function call can accept a Variable or Number literal
		if (Variable.isVariable(argument)) {
			// If it's a variable, get the variable, and get the value from the variable
			Variable v = Variable.getVariable(terminal.getBasicProgram(), argument);
			arg = v.getValue(argument);
		} else if (Expression.isNumber(argument)) {
			// If it's a number, do one important test: does it have an exponent?
			if (Expression.hasExponent(argument)) {
				// Since it does, calculate it, and set arg to be that new value
				arg = Expression.calculateExponent(argument);
			} else {
				// Since it doesn't, just parse the converted value
				arg = Double.valueOf(argument.trim()).doubleValue();
			}
		} else {
			// Since it's not a Variable or a Number, assume it's an expression within the
			// Function call's arguments list, tokenize into a queue, to get the results from
			// a new Expression instance into arg.
			PriorityQueue<String> expr = new PriorityQueue<String>();
			Tokenizer expTok = new Tokenizer();
			expTok.reset(argument);
			
			while (expTok.hasNext()) {
				expr.offer(expTok.next());
			}
			Expression e = new Expression(expr, terminal);
			arg = e.eval();
		}
		return arg;
	}
	
	public static boolean isFunction (String token) {
		boolean isFN = false;
		if (Pattern.matches(regexUserFunctions, token))
			isFN = true;

		for (int i = 0; i < functions.length; i++) {
			if (token.equals(functions[i]))
				return true;
		}
		return isFN;
	}

	public static boolean isUserFunction (String token) {
		if (Pattern.matches(regexUserFunctions, token))
			return true;
		else
			return false;
	}
	
	public static String getArg(String token) {
		Pattern pT = Pattern.compile(regexFunctionTokens);
		Matcher m = pT.matcher(token);
		
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
}