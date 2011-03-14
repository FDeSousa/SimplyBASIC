package fdesousa.app.SimplyBASIC;

import java.util.regex.Pattern;

import android.widget.EditText;

public class Variable {
	// Error / Number / Single-Dimension Array / Multiple-Dimension Array
	public final static int ERR = -1, NUM = 0, S_ARR = 1, M_ARR = 2;
	// Regular Expressions for Number, Single- or Multi-Dim Array variables
	// regexNUM will determine if the named variable is a regular number variable
	public final static String regexNUM = "^[A-Z]{1}\\d?$";
	// regexVarName will get the name of a variable array
	public final static String regexVarName = "^([A-Z]{1})(?=[(]{1})";
	// regexS_ARR determines if the variable is a single-dim array, and can also return the variable name and sole argument
	public final static String regexS_ARR = "^[A-Z]{1}[(]{1}\\s*(\\d+|\\w?\\d?)\\s*[)]{1}$";
	// regexM_ARR determines if the variable is a multi-dim array, and can also return the variable name and both arguments
	public final static String regexM_ARR = "^[A-Z]{1}[(]{1}\\s*(\\d+|\\w?\\d?)\\s*[,]{1}\\s*(\\d+|\\w?\\d?)\\s*[)]{1}$";

	private String name;
	private int type;
	private double value;
	private double[] S_DIM;
	private double[][] M_DIM;
	private int dim1, dim2;

	public Variable (String name, double value){
		// Constructor for a standard array
		this.name = name;
		this.setValue(value);
		type = NUM;
	}

	public Variable (String name, int dimension){
		this.name = name;
		dim1 = dimension;
		type = S_ARR;
	}

	public Variable (String name, int dim1, int dim2){
		this.name = name;
		this.dim1 = dim1;
		this.dim2 = dim2;
		type = M_ARR;
	}

	// Simple as, get a String containing name of variable
	public String getName(){
		return name;
	}

	// Get the type of variable
	public int getType(){
		return type;
	}

	// Getter/Setter for NUM
	// Set/Get the value of a variable of type NUM
	public void setValue(double value) {
		this.value = value;
	}
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
			return -1;
		}
		return -1;
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
	
	public static Variable getRestOfVariable(BASICProgram p, Tokenizer t, EditText etCW, String vName, String token){
		if (token.equals("(")){
			vName += token;
			do {
				token = t.nextToken();
				vName += token;
			// Seems a bit redundant doing two operations here, but it makes the while below simpler
			} while (! token.equals(")"));
		} // Got the whole of the variable and its arguments above
		
		int chk = checkVariableType(vName);
		String[] args = splitVariable(vName);
		
		// TODO: Remove what's below, it's just to check the args
		String toPrint = "";
		for (int i = 0; i < args.length; i++){
			toPrint += args[i] + "\t";
		}
		etCW.append("\n"+toPrint+"\n");	
		// TODO: Remove up to here asap
		
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