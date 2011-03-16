package fdesousa.app.SimplyBASIC;

import java.util.PriorityQueue;
import java.util.regex.Pattern;

import android.widget.EditText;

public class Function {

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

	private String FN_Name;
	private Expression FN_Expression;
	private double result = 0.0;

	public Function (String functionName, Expression functionExpression){
		FN_Name = functionName;
		FN_Expression = functionExpression;
	}
	
	public String getName(){
		return FN_Name;
	}
	
	public double doFn(EditText etCW, String token, BASICProgram p){
		Pattern pT = Pattern.compile(regexFunctionTokens);
		String[] args = pT.split(token);
		double arg = 0.0;
		
		// Have to do a few checks here first, as a Function call can accept a Variable or Number literal
		if (Variable.isVariable(args[2])){
			// If it's a variable, get the variable, and get the value from the variable
			Variable v = Variable.getVariable(p, args[2]);
			arg = v.getValue(args[2]);
		}
		else if (Expression.isNumber(args[2])){
			// If it's a number, do one important test: does it have an exponent?
			if (Expression.hasExponent(args[2])){
				// Since it does, calculate it, and set arg to be that new value
				arg = Expression.calculateExponent(args[2]);
			}
			else {
				// Since it doesn't, just parse the converted value
				arg = Double.parseDouble(args[2]);
			}
		}
		
		// Check the type of function this thing is
		if (Pattern.matches(regexUserFunctions, token)){
			// It's a User Function, so do something with that
		}
		else if (token.equals(functions[FN_SIN])){
			return Math.sin(arg);
		}
		else if (token.equals(functions[FN_COS])){
			return Math.cos(arg);
		}
		else if (token.equals(functions[FN_TAN])){
			return Math.tan(arg);
		}
		else if (token.equals(functions[FN_ATN])){
			return Math.atan(arg);
		}
		else if (token.equals(functions[FN_EXP])){
			return Math.exp(arg);
		}
		else if (token.equals(functions[FN_ABS])){
			return Math.abs(arg);
		}
		else if (token.equals(functions[FN_LOG])){
			return Math.log(arg);
		}
		else if (token.equals(functions[FN_SQR])){
			return Math.sqrt(arg);
		}
		else if (token.equals(functions[FN_RND])){
			return Math.random();
		}
		else if (token.equals(functions[FN_INT])){
			return Math.round(arg);
		}
		return 0.0;
	}

	public static boolean isFunction (String token){
		boolean isFN = false;
		if (Pattern.matches(regexUserFunctions, token))
			isFN = true;

		for (int i = 0; i < functions.length; i++){
			if (token.equals(functions[i]))
				return true;
		}

		return isFN;
	}
}
