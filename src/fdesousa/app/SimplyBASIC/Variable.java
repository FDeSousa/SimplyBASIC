package fdesousa.app.SimplyBASIC;

import java.util.regex.Pattern;

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
	public final static String regexNUM = "^[A-Z]{1}\\d?$";
	
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
	public final static String regexM_ARR = "^[A-Z]{1}[(]{1}\\s*(\\d+|\\w?\\d?)\\s*[,]{1}\\s*(\\d+|\\w?\\d?)\\s*[)]{1}$";

	// General declarations here
	private String name;
	private int type;
	private double value;
	private double[] S_DIM;
	private double[][] M_DIM;
	private int dim1, dim2;

	/**
	 * Declare a new regular number Variable
	 * @param name	- Name of this variable
	 * @param value	- Value of this variable
	 */
	public Variable (String name, double value){
		// Constructor for a standard array
		this.name = name;
		this.setValue(name, value);
		type = NUM;
	}

	/**
	 * Declare a new single-dimension number array Variable
	 * @param name		- Name of this variable
	 * @param dimension	- Array bounds, dimension limit
	 */
	public Variable (String name, int dimension){
		this.name = name;
		dim1 = dimension;
		S_DIM = new double[dim1];
		type = S_ARR;
	}

	/**
	 * Declare a new multi-dimension number array Variable
	 * @param name	- Name of thise variable
	 * @param dim1	- Array bounds, dimension limit of dimension 1
	 * @param dim2	- Array bounds, dimension limit of dimension 2
	 */
	public Variable (String name, int dim1, int dim2){
		this.name = name;
		this.dim1 = dim1;
		this.dim2 = dim2;
		M_DIM = new double[dim1][dim2];
		type = M_ARR;
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
	 * Get the value of this Variable - type NUM/S_DIM/M_DIM
	 * @param vName - the variable's token, to determine and return value of that cell/variable
	 * @return value - Value of this Variable
	 */
	public double getValue(String vName){
		String[] varArgs = splitVariable(vName);
		if (type == S_ARR){
			int index = Integer.parseInt(varArgs[2]);
			return S_DIM[index];
		}
		else if (type == M_ARR){
			int dim1 = Integer.parseInt(varArgs[2]);
			int dim2 = Integer.parseInt(varArgs[3]);
			return M_DIM[dim1][dim2];
		}
		else {
			// If it's not a single-/multi-dimension array, return the value
			return value;			
		}
	}

	/**
	 * Setter for the value of Variable type NUM/S_DIM/M_DIM
	 * Set the value of this Variable - type NUM/S_DIM/M_DIM
	 * @param vName - the variable's token, to determine the variable name and cell
	 * @param value - the value to place in the variable
	 */
	public void setValue(String vName, double value){
		String[] varArgs = splitVariable(vName);
		if (type == S_ARR){
			int index = Integer.parseInt(varArgs[2]);
			S_DIM[index] = value;
		}
		else if (type == M_ARR){
			int dim1 = Integer.parseInt(varArgs[2]);
			int dim2 = Integer.parseInt(varArgs[3]);
			M_DIM[dim1][dim2] = value;
		}
		else {
			this.value = value;
		}
	}

	/**
	 * Setter for the value of Variable type NUM exclusively
	 * Set the value of this Variable - type NUM exclusively
	 * @param value - the value to place in the variable
	 */
	public void setValue(double value){
		this.value = value;
	}
	
	// Check if the input String is a variable
	public static boolean isVariable(String input){
		return (Pattern.matches(regexNUM, input) | 
				Pattern.matches(regexS_ARR, input) | 
				Pattern.matches(regexM_ARR, input));
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
		catch(NumberFormatException e){
			return ERR;
		}
		return ERR;
	}
	
	public static String[] splitVariable(String input){
		String REGEX;
		int varType = checkVariableType(input);
		// Set the regular expression from one of the pre-defined ones
		switch (varType){
			case S_ARR:
				REGEX = regexS_ARR;
				break;
			case M_ARR:
				REGEX = regexM_ARR;
				break;
			default:
				REGEX = regexVarName;
				break;
		}
		// Compile the pattern from the REGEX, and split into arguments
		Pattern p = Pattern.compile(REGEX);
		String[] varArgs = p.split(input);
		return varArgs;
	}
	
	public static Variable getVariable(BASICProgram p, String vName){
		
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
		else if (tV == null && chk == NUM){
			// As it doesn't exist and it's a simple number, create it
			Variable v = new Variable(args[0], 0.0); // The variable value will be updated by LET, so leave it as 0.0
			p.putVar(v);
			return v;
		}
		return null;
	}
}