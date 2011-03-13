package fdesousa.app.SimplyBASIC;

public class Variable {
	public final static int NUM = 0;	// Just a simple number
	public final static int S_ARR = 1;	// Single-dimension array
	public final static int M_ARR = 2;	// Multi-dimension array
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
}
