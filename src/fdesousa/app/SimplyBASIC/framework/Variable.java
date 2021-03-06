/*
 * Variable.java - Implement a Variable.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fdesousa.app.SimplyBASIC.BASICProgram;

/**
 * <h1>Variable.java</h1>
 * A class concerning itself with the operations of a BASIC Variable,<br>
 * such as getting, setting and instantiating a Variable
 * @version 0.1
 * @author Filipe De Sousa
 */
public class Variable {
	/**
	 * ERR	 =	-1	-	Error number, not match with any below
	 * NUM	 =	 0	-	Variable is a regular Number Variable
	 * S_ARR =	 1	-	Variable is a Single Dimension Array Number Variable
	 * M_ARR =	 2	-	Variable is a Multi Dimension Array Number Variable
	 */
	public final static int ERR = -1, NUM = 0, S_ARR = 1, M_ARR = 2;

	/**
	 * regexNUM will match and get the name of a regular Number Variable
	 */
	public final static String regexNUM = "^([A-Z]{1}\\d?)$";

	/**
	 * regexVarName will get the name of a variable array
	 * Upon using Pattern.split(), the returned array is:
	 * [0] Whole Variable S_/M_DIM array that matches
	 * [1] Array name 
	 */
	public final static String regexVarName = "^([A-Z]{1})(?=[(]{1})";

	/**
	 * regexS_ARR determines if the variable is a single-dim array, and can also return the variable name and sole argument
	 * Upon using Pattern.split(), the returned array is:
	 * [0]	Whole Variable S_DIM Array that matches
	 * [1]	Array name
	 * [2]	Array argument
	 */
	public final static String regexS_ARR = "^([A-Z]{1})[(]{1}\\s*(\\d+|\\w?\\d?)\\s*[)]{1}$";

	/**
	 * regexM_ARR determines if the variable is a multi-dim array, and can also return the variable name and both arguments
	 * Upon using Pattern.split(), the returned array is:
	 * [0]	Whole Variable M_DIM Array that matches
	 * [1]	Array name
	 * [2]	Array argument 1
	 * [3]	Array argument 2
	 */
	public final static String regexM_ARR = "^([A-Z]{1})[(]{1}\\s*(\\d+|\\w?\\d?)\\s*[,]{1}\\s*(\\d+|\\w?\\d?)\\s*[)]{1}$";

	// General declarations here
	private String name;
	private int type;
	private double value;
	private double[] S_DIM;
	private double[][] M_DIM;
	private int dim1, dim2;

	/**
	 * Declare a new variable, the type of which is defined by
	 * the token in vName
	 * @param vName - token with argument(s) for new Variable
	 */
	public Variable(String vName){
		try {
			if (checkVariableType(vName) == NUM){
				name = vName;
				type = NUM;
			}
			else if (checkVariableType(vName) == S_ARR){
				String[] args = splitVariable(vName);
				name = args[1];
				dim1 = Integer.valueOf(args[2].trim()).intValue();
				S_DIM = new double[dim1];
				type = S_ARR;
			}
			else if (checkVariableType(vName) == M_ARR){
				String[] args = splitVariable(vName);
				name = args[1];
				dim1 = Integer.valueOf(args[2].trim()).intValue();
				dim2 = Integer.valueOf(args[3].trim()).intValue();
				M_DIM = new double[dim1][dim2];
				type = M_ARR;
			}
		}
		catch (IllegalStateException e){
			return;
		}
	}

	/**
	 * Get a String containing the name of this Variable
	 * @return Variable name
	 */
	public String getName(){
		return name;
	}

	/**
	 * Get the type of this Variable
	 * @return type - type of this Variable
	 */
	public int getType(){
		return type;
	}

	/**
	 * Getter for the value of Variable type NUM/S_DIM/M_DIM
	 * @param vName - the variable call token, to determine and return value of that cell/variable
	 * @return value - Value of this Variable
	 */
	public double getValue(String vName){
		try {
			String[] varArgs = splitVariable(vName);
			if (type == S_ARR){
				int index = Integer.valueOf(varArgs[2].trim()).intValue();
				return S_DIM[index];
			}
			else if (type == M_ARR){
				int dim1 = Integer.valueOf(varArgs[2].trim()).intValue();
				int dim2 = Integer.valueOf(varArgs[3].trim()).intValue();
				return M_DIM[dim1][dim2];
			}
			else {
				// If it's not a single-/multi-dimension array, return the value
				return value;			
			}
		}
		catch (IllegalStateException e){
			return Double.MIN_VALUE;
		}
	}

	public double getValue(){
		return value;
	}

	/**
	 * Setter for the value of Variable type NUM/S_DIM/M_DIM
	 * @param vName - the variable call token, to determine the variable name and cell
	 * @param value - the value to place in the variable
	 */
	public void setValue(String vName, double value){
		try {
			String[] varArgs = splitVariable(vName);
			if (type == S_ARR){
				int index = Integer.valueOf(varArgs[2].trim()).intValue();
				S_DIM[index] = value;
			}
			else if (type == M_ARR){
				int dim1 = Integer.valueOf(varArgs[2].trim()).intValue();
				int dim2 = Integer.valueOf(varArgs[3].trim()).intValue();
				M_DIM[dim1][dim2] = value;
			}
			else {
				this.value = value;
			}
		}
		catch (IllegalStateException e){
			return;
		}
	}

	/**
	 * Setter for the value of Variable type NUM exclusively
	 * @param value - the value to place in the variable
	 */
	public void setValue(double value){
		// Could save some processor time at some point
		// Especially useful for FOR statement, as it's assumed
		// that the named variable can only be of type NUM
		this.value = value;
	}

	// Check if the input String is a variable
	public static boolean isVariable(String input){
		try {
			return (Pattern.matches(regexNUM, input) || 
					Pattern.matches(regexS_ARR, input) || 
					Pattern.matches(regexM_ARR, input));
		}
		catch (IllegalStateException e){
			return false;
		}
	}

	// Static Variable operations to check type and split it
	public static int checkVariableType(String input){
		try{
			if (Pattern.matches(regexM_ARR, input))
				return M_ARR;
			else if (Pattern.matches(regexS_ARR, input))
				return S_ARR;
			else if (Pattern.matches(regexNUM, input))
				return NUM;
		}
		catch(Exception e){
			return ERR;
		}
		return ERR;
	}

	public static String[] splitVariable(String input){
		try {
			String REGEX;
			String[] varArgs;
			int varType = checkVariableType(input);
			// Set the regular expression from one of the pre-defined ones
			switch (varType){
			case M_ARR:
				REGEX = regexM_ARR;
				varArgs = new String[3];
				break;
			case S_ARR:
				REGEX = regexS_ARR;
				varArgs = new String[2];
				break;
			case NUM:
				REGEX = regexNUM;
				varArgs = new String[1];
				varArgs[0] = input;
				return varArgs;
			default: 
				return null;
			}
			// Compile the pattern from the REGEX, and split into arguments
			Pattern p = Pattern.compile(REGEX);
			Matcher m = p.matcher(input);
			for (int i = 0; i <= m.groupCount(); i++){
				varArgs[i] = m.group(i);
			}
			return varArgs;
		}
		catch (IllegalStateException e){
			return null;
		}
	}

	public static Variable getVariable(BASICProgram p, String vName){
		try {
			int chk = checkVariableType(vName);
			String[] args = splitVariable(vName);

			Variable tV = p.getVar(args[0]);
			if (tV != null){	// If it's not null, it exists			
				if (tV.getType() == chk){	// If the type is the same
					return tV;	// Return the tempVariable
				}
				else {
					return null;
				}
			}
			else if (tV == null & chk == NUM){
				// As it doesn't exist and it's a simple number, create it
				Variable v = new Variable(args[0]);
				p.putVar(v);
				return v;
			}
			return null;
		}
		catch (IllegalStateException e){
			return null;
		}
	}
}