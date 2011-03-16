package fdesousa.app.SimplyBASIC;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import android.widget.EditText;

public class BASICProgram implements Runnable{

	private TreeMap<Integer, String> codeList = new TreeMap<Integer, String>();
	private Set<Integer> lNs;
	
	private String progName = "", userName = "";
	// Using the tokenizer again here, will be making good use of this too
	private Tokenizer t = new Tokenizer();
	// cL = current line, nL = next line, pL = previous line, rL = return line, lL = last line
	private int cL = 0;// nL = 0, pL = 0, rL = 0, lL = 0;

	private boolean cont = true;
	private boolean stop = false;

	// Using Calendar to get the running time in milliseconds
	Calendar timer = Calendar.getInstance();
	long startTime;

	/**
	 * The main run() method here, and the one that's called by CommandInterpreter
	 * It executes run() to get all DATA values plotted into a Queue
	 * An EditText is required for output only
	 */
	public void run(EditText etCW) {
		try {
			startTime = timer.getTimeInMillis();
			stop = false;
			// Do the first-run, to get all DATA stored into a FIFO list
			//run();
			// Re-initialise lNs here for use while final run is going
			lNs = codeList.keySet();
			cL = lNs.iterator().next();
			
			do {
				t.reset(codeList.get(cL));
				Statement statement = new Statement();
				statement.doSt(this, t, etCW);

				// To avoid issues with IF/GOTO/GOSUB/NEXT, have to get next line before end of loop
				// But then, to avoid not being able to process the last line, since this is do..while
				// we should do a check, and use boolean variables, to make sure all is in check
				if (lNs.iterator().hasNext()){
					cL = lNs.iterator().next();
				}
				else {
					cont = false;
				}
			} while (cont && ! stop);
		}
		catch (Exception e){
			etCW.append(e.toString().toUpperCase() + ".\n");
		}
	}

	// dataStore is used for keeping data from DATA statements in a FIFO for later access.
	// It's stupid using the dataStore to store strings, but as it can store both integers AND
	// real numbers (all float as far as BASIC is concerned), I'll resolve this later 
	private PriorityQueue<Double> dataStore = new PriorityQueue<Double>();
	
	public double getData(){
		return dataStore.poll();
	}
	
	public boolean hasData(){
		return (! dataStore.isEmpty());
	}
	
	public void run() {
		// The generic, auto-generated, must-have version of run(), defined by Runnable
		// Will see implementation as a first-run method placing DATA in FIFO list
		String s = "";
		try {
			lNs = codeList.keySet();

			cL = lNs.iterator().next();
			do {
				t.reset(codeList.get(cL));
				s = t.nextToken();
				if (s.equals(Statement.statements[Statement.S_DATA])){
					Statement dataSt = new S_DATA();
					dataSt.doSt(this, t, null);
				}
				
				if (lNs.iterator().hasNext()){
					cL = lNs.iterator().next();
				}
				else {
					cont = false;
				}
			} while (cont && ! stop);
		}
		catch (Exception e){
			return;
		}
	}

	// Two ways to instantiate a BASIC Program, with or without source code.
	public BASICProgram(String userName, String progName){
		// Used for HELLO, NEW
		setProgName(progName);
		setUserName(userName);
		// Very simple, just initialises the variables here
		// giving the program a name and user name attributed
	}

	public BASICProgram(String userName, String progName, TreeMap<Integer, String> oldCodeList){
		// Used for HELLO, OLD
		setProgName(progName);
		setUserName(userName);
		codeList = oldCodeList;
		// As above, but this is used for an older program, to load the listing
	}


	public void C_NEW(String progName){				
		setProgName(progName);
		// Simple, just re-initialises the variables
	}

	public boolean C_SCRATCH(){
		codeList.clear();
		return true;
	}

	public void C_OLD(String progName, TreeMap<Integer, String> oldCodeList){
		setProgName(progName);
		codeList = oldCodeList;
	}

	public void C_LIST(EditText etCW, int lN){
		// Return parts of program code listing

		if (lN >= codeList.firstKey() && lN <= codeList.lastKey()){
			SortedMap<Integer,String> C_LIST_codeList = codeList.tailMap(lN);
			Set<Integer> lineNumbers = C_LIST_codeList.keySet();

			etCW.append("\nUSER NAME: " + userName
					+ "\nPROGRAM NAME: " + progName);

			int cL = lineNumbers.iterator().next();

			while (lineNumbers.iterator().hasNext()){
				etCW.append(cL + "\t" + C_LIST_codeList.get(cL).toString());
				cL = lineNumbers.iterator().next();
			}
		}
		else {
			// This actually comes out better than it seems, except for the lineNumber'=null' bit
			etCW.append("\nUSER NAME: " + userName
					+ "\nPROGRAM NAME: " + progName
					+ "\nINVALID LINE NUMBER SPECIFIED"
					+ "\nMUST BE BETWEEN " + codeList.firstKey() 
					+ " AND " + codeList.lastKey());
		}
	}

	// Boring parts of the class below. Not the meat of it.
	public String addLine(int lN, String inputLine){
		try{ 
			codeList.put(lN, inputLine);
			return null;
		}
		catch(Exception e){
			return e.toString();
		}
	}

	public long getTimeToExecute(){
		long tte = timer.getTimeInMillis() - startTime;
		return tte;
	}

	// Stop execution of a next iteration
	public void stopExec(){
		stop = true;
	}

	public void setProgName(String progName) {
		this.progName = progName;
	}

	public String getProgName() {
		return progName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public int getCurrentLine(){
		return cL;
	}
	
	public void addData(double data){
		dataStore.offer(data);
	}

	// Variables are of type Variable, and can be a Number or Number Array
	private TreeMap<String, Variable> variables = new TreeMap<String, Variable>();
	public void putVar(Variable v){
		variables.put(v.getName(), v);
	}
	public Variable getVar(String vName){
		if (variables.containsKey(vName)){
			return variables.get(vName);			
		}
		else {
			return null;
		}
	}
	public boolean varExists(String vName){
		return variables.containsKey(vName);
	}
	public int varType(String vName){
		Variable v = variables.get(vName);
		return v.getType();
	}
	
	// Using these nice and simple Getters and Setters for using 
	// during GOSUB / GOTO / IF .. THEN / FOR .. NEXT statements
	public Set<Integer> getlNs() {
		return lNs;
	}

	public void setlNs(Set<Integer> lNs) {
		this.lNs = lNs;
	}

	// To be able to handle more than one RETURN in a program, they're stored
	// in a Stack, so in this way, they are accessed in a LIFO order
	private Set<Integer> RETURNKeySet;
	private Stack<Set<Integer>> RETURNs;

	public Set<Integer> getRETURNKeySet() {
		RETURNKeySet = RETURNs.pop();
		return RETURNKeySet;
	}

	public void setRETURNKeySet(Set<Integer> RETURNKeySet) {
		this.RETURNKeySet = RETURNKeySet;
		RETURNs.push(this.RETURNKeySet);
	}

	// To aid with GOTO/GOSUB/etc, we make a tail set of the code list, 
	// which will then act as our new set of keys to execute
	public Set<Integer> getTailMap(int lN){
		Set<Integer> lineNumbers = codeList.tailMap(lN).keySet();
		return lineNumbers;
	}

	// As FOR loops can be numerous, we handle them similarly to GOTO/GOSUB .. RETURN
	// by having a stack, waiting for a NEXT statement to execute it
	private Stack<String> forNexts = new Stack<String>();

	public void newFor(String line){
		forNexts.push(line);
	}

	public String getFor(){
		return forNexts.pop();
	}

}
