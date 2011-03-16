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
		this.setValue(value);
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
	 * Setter for the value of Variable type NUM
	 * Set the value of this Variable - type NUM
	 * @param value - Value for this Variable
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * Getter for the value of Variable type NUM
	 * Get the value of this Variable - type NUM
	 * @return value - Value of thise Variable
	 */
	public double getValue() {
		return value;
	}

	// Getter/Setter for S_DIM
	// Get upper bounds of S_DIM array
	public int getUpperBoundsOfS_DIM(){
		return dim1;
	}
	// Set/Get the value of an element of variable of type S_DIM
	public void setValueOfElementInS_DIM(int index, double value){
		S_DIM[index] = value;
	}
	public double getValueOfElementInS_DIM(int index){
		return S_DIM[index];
	}

	// Getter/Setter for M_DIM
	// Get upper bounds of M_DIM array
	public int[] getUpperBoundsOfM_DIM(){
		int[] uBounds = { dim1, dim2 };
		return uBounds;
	}
	// Set/Get the value of an element of variable of type M_DIM
	public void setValueOfElementInM_DIM(int index1, int index2, double value){
		M_DIM[index1][index2] = value;
	}
	public double getValueOfElementInM_DIM(int index1, int index2){
		return M_DIM[index1][index2];
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
	
	/**
	 * Assign a value to the current called initialised variable
	 * @param value - the value to be assigned
	 * @param vName - the String containing the variable's name and arguments
	 */
	public void assignValueToVariable(double value, String vName){
		if (type == NUM){
			setValue(value);
		}
		else if (type == S_ARR){
			String[] args = splitVariable(vName);
			setValueOfElementInS_DIM(Integer.parseInt(args[2]), value);
			// Get one argument from vName, the single 'row' identifier
			// Use this as the 'cell' identifier to place results in
		}
		else if (type == M_ARR){
			String[] args = splitVariable(vName);
			setValueOfElementInM_DIM(Integer.parseInt(args[2]), Integer.parseInt(args[3]), value);
			// Get two arguments from vName, the 'row' and 'column' identifiers
			// Use these to identify the 'cell' to place results in
		}
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