/*
 * BASICProgram.java - Implement the BASIC Program, to store and run the code.
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

package fdesousa.app.SimplyBASIC;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import android.widget.EditText;

public class BASICProgram implements Runnable{

	TreeMap<Integer, String> masterCodeList;
	private TreeMap<Integer, String> codeList = new TreeMap<Integer, String>();
	private Set<Entry<Integer, String>> lines;		// Holds the set of lines for iter
	private Iterator<Entry<Integer, String>> iter;	// Used to iterate through Set
	private Entry<Integer, String> cL;				// Holds an individual entry

	private String progName = "", userName = "";
	// Using the tokenizer again here, will be making good use of this too
	private Tokenizer t = new Tokenizer();
	// cL = current line, nL = next line, pL = previous line, rL = return line, lL = last line

	private boolean stop = new Boolean(false);

	EditText et;
	
	// Using Calendar to get the running time in milliseconds
	Calendar timer = Calendar.getInstance();
	long startTime;

	/**
	 * The main run() method here, and the one that's called by CommandInterpreter
	 * It executes run() to get all DATA values plotted into a Queue
	 * An EditText is required for output only
	 */
	public void run(EditText edtxt) {
		try {
			et = edtxt;
			// We want to keep a backup codeList, as the key sets are all linked to codeList,
			// this might give us trouble if something is changed/added/removed.
			masterCodeList = codeList;
			// To calculate the running time, we set a start time
			startTime = timer.getTimeInMillis();
			
			// reset stop, just in case it's been done at a bad time
			stop = false;
			
			// Do the first-run, to get all DATA stored into a FIFO list
			run();
			// Re-initialise lNs here for use while final run is going
			lines = codeList.entrySet();
			iter = lines.iterator();
			
			while (iter.hasNext() & ! stop){
				cL = iter.next();
				t.reset(cL.getValue());
				Statement s = new Statement(this, t, et);
				s.doSt();
			}
			// Now, it's ended the run, so we can revert back to the original codeList
			codeList = masterCodeList;
		}
		catch (Exception e){
			et.append(e.toString().toUpperCase() + " " + String.valueOf(getCurrentLine()) + ".\n");
			codeList = masterCodeList;
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
		String s = new String();
		try {
			
			lines = codeList.entrySet();
			iter = lines.iterator();
			
			while (iter.hasNext() & ! stop){
				cL = iter.next();
				t.reset(cL.getValue());
				s = t.nextToken();
				if (s.equals(Statement.statements[Statement.S_DATA])){
					Statement dataSt = new S_DATA(this, t, et);
					dataSt.doSt();
				}
				else if (s.equals(Statement.statements[Statement.S_END]) &
						iter.hasNext()){
					et.append("END IS NOT LAST - LINE NUMBER " + String.valueOf(cL.getKey()) + "\n");
					stopExec();
				}
			}
			// Reset stop just here so the other run can continue
			stop = false;
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

	public void C_LIST(EditText et){
		// Return parts of program code listing
		try {
			et.append("USER NAME: " + userName
					+ "\nPROGRAM NAME: " + progName 
					+ "\n");

			lines = codeList.entrySet();
			iter = lines.iterator();
			
			while (iter.hasNext()){
				cL = iter.next();
				et.append(cL.getKey() + "\t" + cL.getValue() + "\n");
			}
		}
		catch (Exception e){
			et.append(e.toString() + "\n");
		}
	}

	public int getFirstLine(){
		return codeList.firstKey();
	}

	// Boring parts of the class below. Not the meat of it.
	public void addLine(int lN, String inputLine){
			codeList.put(lN, inputLine);
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
		return cL.getKey();
	}

	public void addData(double data){
		dataStore.offer(data);
	}

	// Variables are of type Variable, and can be a Number or Number Array
	private Map<String, Variable> variables = new TreeMap<String, Variable>();
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

	// Functions are of type Function, and can be user-defined functions only
	private Map<String, Function> functions = new TreeMap<String, Function>();
	public void putFunction(Function f){
		functions.put(f.getName(), f);
	}
	public Function getFunction(String fnName){
		if (functions.containsKey(fnName)){
			return functions.get(fnName);			
		}
		else {
			return null;
		}
	}
	public boolean fnExists(String fnName){
		return functions.containsKey(fnName);
	}

	/**
	 * Get the line number Set to work on. <br>
	 * Useful for GOTO/GOSUB .. RETURN/IF .. THEN/FOR .. NEXT statements
	 */
	public Set<Entry<Integer, String>> getlNs() {
		return lines;
	}

	/**
	 * Set the line number Set to a new one. <br>
	 * Useful for GOTO/GOSUB .. RETURN/IF .. THEN/FOR .. NEXT statements
	 * @param lNs - Set containing the new line numbers to work with
	 */
	public void setlNs(Set<Entry<Integer, String>> lNs) {
		lines = lNs;
	}

	// To be able to handle more than one RETURN in a program, they're stored
	// in a Stack, so in this way, they are accessed in a LIFO order
	private Set<Entry<Integer, String>> RETURNKeySet;
	private Stack<Set<Entry<Integer, String>>> RETURNs;

	/**
	 * Get the top RETURN keyset
	 * @return RETURN keyset of entries
	 */
	public Set<Entry<Integer, String>> getRETURNKeySet() {
		RETURNKeySet = RETURNs.pop();
		return RETURNKeySet;
	}
	public boolean getRETURNKeySetisEmpty(){
		return RETURNKeySet.isEmpty();
	}

	/**
	 * Add a new RETURN keyset
	 * @param RETURNKeySet - a keyset of entries for RETURN
	 */
	public void putRETURNKeySet(Set<Entry<Integer, String>> RETURNKeySet) {
		this.RETURNKeySet = RETURNKeySet;
		RETURNs.push(this.RETURNKeySet);
	}

	// To aid with GOTO/GOSUB/etc, we make a tail set of the code list, 
	// which will then act as our new set of keys to execute
	/**
	 * Return a set of entries from the tailmap, starting from key as defined by lN.<br>
	 * Aids in running GOTO/GOSUB/etc, to make a new set of keys to execute
	 * @param ln - the line number to start tailmap on
	 * @return Set lineNumbers, entries from tailmap
	 */
	public Set<Entry<Integer, String>> getTailSet(int lN){
		Set<Entry<Integer, String>> lineNumbers = codeList.tailMap(lN).entrySet();
		return lineNumbers;
	}

	// As FOR loops can be numerous, we handle them similarly to GOTO/GOSUB .. RETURN
	// by having a stack, waiting for a NEXT statement to execute it
	private TreeMap<String, S_FOR> forNexts = new TreeMap<String, S_FOR>();

	public void newFor(String key, S_FOR forLoop){
		forNexts.put(key, forLoop);
	}

	public S_FOR getFor(String key){
		return forNexts.get(key);
	}
}
