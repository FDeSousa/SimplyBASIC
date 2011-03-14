package fdesousa.app.SimplyBASIC;

import java.util.Queue;
import java.util.Stack;
import java.util.regex.Pattern;
import android.widget.EditText;

public class Expression {

	static final String[] operators = { "^", "*", "/", "+", "-" };

	Queue<String> in = null, post = null;
	Stack<String> ops = null;

	public static String regexNumber = "^-?\\d*\\.?\\d*[E]?\\d+$";
	public static String regexExponent = "^(-?\\d*\\.?\\d+)[E]{1}(\\d+)$";

	public Expression (Queue<String> expression){
		in = expression;
	}

	private void inToPost(BASICProgram p){
		String token = null;

		while (! in.isEmpty()) {
			token = in.poll();
			// This used to be simpler, it was originally just isNumber(token)
			// but as a number can also have the letter 'E', to mark the exponent, 
			// things have been changed around a little bit to match the possibility
			if (isNumber(token)){
				if (hasExponent(token)){
					// If there's an exponent, calculate it first
					String temp = String.valueOf(calculateExponent(token));
					token = temp;
				}
				// Add the number to the Queue
				post.offer(token);
			}
			else if (Variable.isVariable(token)){
				// If it's a variable value, get the value of that variable first
				String[] varArgs = Variable.splitVariable(token);
				Variable v = p.getVar(varArgs[0]);
				if (v.getType() == Variable.NUM){			// Variable type of Number
					// Offer up the value of the named Variable to the Queue
					post.offer(String.valueOf(v.getValue()));
				}
				else if (v.getType() == Variable.S_ARR){	// Variable type of NumberArray(Single-Dim)
					// Offer up the value of the chosen element from the Variable to the Queue
					post.offer(String.valueOf(v.getValueOfElementInS_DIM(Integer.parseInt(varArgs[varArgs.length-1]))));
				}
				else if (v.getType() == Variable.M_ARR){	// Variable type of NumberArray(Multi-Dim)
					// Offer up the value of the chosen element from the Variable to the Queue
					post.offer(String.valueOf(v.getValueOfElementInM_DIM(Integer.parseInt(varArgs[varArgs.length-2]), 
							Integer.parseInt(varArgs[varArgs.length-1]))));
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

	@SuppressWarnings("null")
	public double eval(BASICProgram p, Tokenizer t, EditText etCW){
		// TODO: Evaluation of expression here
		Stack<Double> runningTotal = null;
		double val1, val2;
		String val;
		
		inToPost(p); // in, ops and post are all accessible by this method, 
					 // so only parse in one item, and receive nothing.
		while (! post.isEmpty()){
			val = post.poll();
			// TODO: Remove after use directly below
			etCW.append(val+"\n"); // To check what's in the stream now
			if (isNumber(val)){
				runningTotal.push(Double.parseDouble(val));
			}
			else if (isOperator(val)){
				val2 = runningTotal.pop();
				val1 = runningTotal.pop();
				// TODO: Remove after use directly below
				etCW.append("Val1"+String.valueOf(val1)+"\t"+String.valueOf(val2)+"\n"); // To check what's being operated on
				if (val.equals("^")){
					runningTotal.push(Math.pow(val1, val2));
				}
				else if (val.equals("*")){
					runningTotal.push(val1 * val2);
				}
				else if (val.equals("/")){
					runningTotal.push(val1 / val2);
				}
				else if (val.equals("+")){
					runningTotal.push(val1 + val2);
				}
				else if (val.equals("-")){
					runningTotal.push(val1 - val2);
				}
				// TODO: Remove after use directly below
				etCW.append("Top of runTot"+String.valueOf(runningTotal.peek())+"\n"); // To check what's being operated on
			}
		}
		/*
		Queue<String> infix = null;
		// Get all the tokens from the parsed expression into Queue infix
		Tokenizer eT = new Tokenizer();
		eT.reset(expression);
		while (eT.hasMoreTokens()){
			infix.offer(eT.nextToken());
		}
		// Convert infix to postfix, place results in a queue to evaluate
		Queue<String> postfix = inToPost(infix);
		 */		
		return runningTotal.pop();
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

	public static boolean isOperator(String token){
		for (int i = 0; i < operators.length; i++){
			if (token.equals(operators[i]))
				return true;
		}
		return false;
	}

	public static boolean isNumber(String input){
		// It's a public static just to make it easier to use in BP, which does a check every-so-often.
		try {
			/**
			 * What the regex below matches:
			 * ±.123/±.123E456/±123.456/±123.456E789
			 * (± refers to positive/negative number, not the symbol itself)
			 */
			return Pattern.matches(regexNumber, input);
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean hasExponent(String input){
		// It's a public static just to make it easier to use in BP, which does a check every-so-often.
		try {
			/**
			 * What the regex below matches:
			 * ±.123E456/±123.456E789/±123E456
			 * (± refers to positive/negative number, not the symbol itself)
			 */
			return Pattern.matches(regexExponent, input);
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}

	public static double[] separateNumber(String input){
		Pattern p = Pattern.compile(regexExponent);
		String[] split = p.split(input);
		double[] number = { Double.parseDouble(split[split.length-2]), Double.parseDouble(split[split.length-1]) };
		return number;
	}

	public static double calculateExponent(String input){
		double[] number = separateNumber(input);
		return Math.pow(number[0], number[1]);
	}
}
