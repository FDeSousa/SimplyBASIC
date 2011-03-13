package fdesousa.app.SimplyBASIC;

import java.util.Queue;
import java.util.Stack;
import java.util.regex.Pattern;

import android.widget.EditText;

public class Expression {
	
	static final String[] operators = { "^", "*", "/", "+", "-" };
	
	@SuppressWarnings("null")
	private Queue<String> inToPost(Queue<String> infixStack){
		Queue<String> in = infixStack, post = null;
		Stack<String> ops = null;
		String token = null;
		
		while (! in.isEmpty()) {
			token = in.poll();
			// This used to be simpler, it was originally just isNumber(token)
			// but as a number can also have the letter 'E', to mark the exponent, 
			// things have been changed around a little bit to match the possibility
			if (! isOperator(token) && ! (token.equals("(") || token.equals(")"))){
				post.offer(token);
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
		
		return post;
	}
	
	public double eval(Tokenizer t, EditText etCW){
		String expression = "";
		Queue<String> infix = null;
		// Get all the tokens from the parsed expression into Queue infix
		Tokenizer eT = new Tokenizer();
		eT.reset(expression);
		while (eT.hasMoreTokens()){
			infix.offer(eT.nextToken());
		}
		// Convert infix to postfix, place results in a queue to evaluate
		Queue<String> postfix = inToPost(infix);
		
		// TODO: Evaluation of expression here
		
		return 0.0;
	}
	
	public int precedence(String token){
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
			 * 1 ^		Start of String
			 * 2 -?		Match zero or one '-' (Negative marker)
			 * 3 \\d*	Match zero or more digits
			 * 4 \\.?	Match zero of one '.'
			 * 5 \\d*	Match zero or more digits
			 * 6 [E]?	Match zero or one 'E' (Exponent marker)
			 * 7 \\d+	Match one or more digits
			 * 8 $		End of String
			 * ---------
			 * Example:	±.123/±.123E456/±123.456/±123.456E789
			 */
			return Pattern.matches("^-?\\d*\\.?\\d*[E]?\\d+$", input);
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
			 * 1 ^		Start of String
			 * 2 -?		Match zero or one '-' (Negative marker)
			 * 3 \\d*	Match zero or more digits
			 * 4 \\.?	Match zero of one '.'
			 * 5 \\d+	Match one or more digits
			 * 6 [E]{1}	Match exactly one 'E' (Exponent marker)
			 * 7 \\d+	Match one or more digits
			 * 8 $		End of String
			 * ---------
			 * Example:	±.123E456/±123.456E789/±123E456
			 */
			return Pattern.matches("^-?\\d*\\.?\\d+[E]{1}\\d+$", input);
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}
}
