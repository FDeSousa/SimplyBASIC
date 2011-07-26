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

import fdesousa.app.SimplyBASIC.framework.Function;
import fdesousa.app.SimplyBASIC.framework.Statement;
import fdesousa.app.SimplyBASIC.framework.TextIO;
import fdesousa.app.SimplyBASIC.framework.Variable;
import fdesousa.app.SimplyBASIC.Statements.*;

/**
 * <h1> BASICProgram.java </h1>
 * The Program that a user writes code for, using SimplyBASIC
 * @version 0.1
 * @author Filipe De Sousa
 */
public class BASICProgram implements Runnable {
	public final static String[] STATEMENTS = {
		"IF", "THEN", "FOR", "TO", "STEP", 
		"NEXT", "LET", "READ", "DATA", 
		"PRINT", "GOTO", "GOSUB", "RETURN", 
		"DIM", "DEF", "FN", "END", "REM" };

	public final static int IF		=  0;	// Start of IF...THEN statement
	public final static int THEN	=  1;	// Continues of IF...THEN statement
	public final static int FOR		=  2;	// Start of FOR...TO...STEP statement
	public final static int TO		=  3;	// Defines limit of FOR...TO...STEP statement
	public final static int STEP	=  4;	// The number to (in/de)crement by in FOR
	public final static int NEXT	=  5;	// (in/de)crements variable defined by FOR with variable 
	public final static int LET		=  6;	// Assignment statement
	public final static int READ	=  7;	// Reads the next Data value (FIFO ordering)
	public final static int DATA	=  8;	// Provides data values for the program
	public final static int PRINT	=  9;	// Print something to screen
	public final static int GOTO	= 10;	// Unconditional transferal of program execution to a different line
	public final static int GOSUB	= 11;	// As GOTO, but can be used to define a sub-routine that is returnable
	public final static int RETURN	= 12;	// Returns execution to where GOSUB left off
	public final static int DIM		= 13;	// Used to define one- or two-dimensional arrays
	public final static int DEF		= 14;	// Used to define a function
	public final static int FN		= 15;	// Beginning two letters of a user-defined function
	public final static int END		= 16;	// Ends the program on that line, no matter what
	public final static int REM		= 17;	// Signifies the line is a comment, and should be ignored by interpreter

	//	All the members related to the code listing and operation listed here
	private Map<Integer, String> codeListing;		//	Holds the line numbers and line's Strings for execution
	private Set<Entry<Integer, String>> lines;		//	Holds the set of lines for iter
	private Iterator<Entry<Integer, String>> iter;	//	Used to iterate through Set
	private Entry<Integer, String> cL;				//	Holds an individual entry

	//	The name of the program that the user chooses
	private String programName;
	// Using the tokenizer again here, will be making good use of this too
	private Tokenizer tokenizer;
	private String command;
	private Statement s;
	private Terminal terminal;
	private TextIO textIO;
	//	running is a flag to tell the loop whether or not to stop,
	//+	it's volatile to prevent reordering of lines within loop
	private volatile boolean running;
	// Using Calendar to get the running time in milliseconds
	Calendar timer;
	long startTime;

	/**
	 * The one and only constructor for BASICProgram takes all the necessary parameters
	 * @param terminal - the instance of Terminal we'll use for FileIO/TextIO access
	 * @param userName - the user's own chosen name
	 * @param programName - the name of the program the user will be writing/loading
	 * @param codeListing - the old code listing if available, if null will make new Map
	 */
	public BASICProgram(Terminal terminal, String programName, Map<Integer, String> codeListing) {
		this.terminal = terminal;
		this.programName = programName;
		this.tokenizer = terminal.getTokenizer();
		this.textIO = terminal.getTextIO();
		//	Place parsed codeListing in our locally accessible one if it's not null
		if (codeListing != null)
			this.codeListing = codeListing;
		//	Otherwise make a nice, fresh, new, clean one
		else
			this.codeListing = new TreeMap<Integer, String>();
		running = false;
		timer = Calendar.getInstance();
	}

	public void doSt() {
		if (tokenizer.hasNext())
			command = tokenizer.next();

		if (command.equals(STATEMENTS[IF]))
			s = new If(terminal);
		else if (command.equals(STATEMENTS[FOR]))
			s = new For(terminal);
		else if (command.equals(STATEMENTS[NEXT]))
			s = new Next(terminal);
		else if (command.equals(STATEMENTS[LET]))
			s = new Let(terminal);
		else if (command.equals(STATEMENTS[READ]))
			s = new Read(terminal);
		else if (command.equals(STATEMENTS[DATA]))
			return;	// As we have a first-run to get DATA, it's safer to
					// acknowledge, but ignore it in this run instance
		else if (command.equals(STATEMENTS[PRINT]))
			s = new Print(terminal);
		else if (command.equals(STATEMENTS[GOTO]))
			s = new Goto(terminal);
		else if (command.equals(STATEMENTS[GOSUB]))
			s = new GoSub(terminal);
		else if (command.equals(STATEMENTS[RETURN]))
			s = new Return(terminal);
		else if (command.equals(STATEMENTS[DIM]))
			s = new Dim(terminal);
		else if (command.equals(STATEMENTS[DEF]))
			s = new Def(terminal);
		else if (command.equals(STATEMENTS[END]))
			s = new End(terminal);
		else if (command.equals(STATEMENTS[REM]))
			return;    // When encountering a REM statement, the line is ignored, so for
		// safety, acknowledge but ignore the statement here by using return
		else {
			textIO.writeLine("ILLEGAL INSTRUCTION - LINE NUMBER " + cL.getKey());
			running = false;
			return;
		}
		//	Since all the statements we want to execute do not return, and all the statements
		//+	and conditions that we do not want to execute do return before reaching this point,
		//+	we can safely write this one line of code to run the statement
		s.doSt();
	}

	/**
	 * The main run() method here, and the one that's called by CommandInterpreter
	 * It executes run() to get all DATA values plotted into a Queue
	 */
	public void run() {
		try {
			// To calculate the running time, we set a start time
			startTime = timer.getTimeInMillis();

			// Do the first-run, to get all DATA stored into a FIFO list
			firstRun();
			// Re-initialise lNs here for use while final run is going
			lines = codeListing.entrySet();
			iter = lines.iterator();
			running = true;

			while (running && iter.hasNext()) {
				cL = iter.next();
				tokenizer.reset(cL.getValue());
				doSt();
			}
		} catch (Exception e) {
			textIO.writeLine(e.toString().toUpperCase() + " " + cL.getKey());
		}
	}

	// dataStore is used for keeping data from DATA statements in a FIFO for later access.
	// It's stupid using the dataStore to store strings, but as it can store both integers AND
	// real numbers (all float as far as BASIC is concerned), I'll resolve this later 
	private PriorityQueue<Double> dataStore = new PriorityQueue<Double>();

	public double getData() {
		return dataStore.poll();
	}

	public boolean hasData() {
		return (!dataStore.isEmpty());
	}

	/**
	 * Called by run() to check through and collect all of the DATA in the program
	 */
	public void firstRun() {
		String s = new String();
		running = true;
		try {
			lines = codeListing.entrySet();
			iter = lines.iterator();

			while (iter.hasNext() & running) {
				cL = iter.next();
				tokenizer.reset(cL.getValue());
				s = tokenizer.next();
				if (s.equals(BASICProgram.STATEMENTS[BASICProgram.DATA])) {
					Statement dataSt = new Data(terminal);
					dataSt.doSt();
				} else if (s.equals(BASICProgram.STATEMENTS[BASICProgram.END]) & iter.hasNext()) {
					textIO.writeLine("END IS NOT LAST - LINE NUMBER " + cL.getKey());
					stop();
				}
			}
		} catch (Exception e) {
			return;
		}
	}

	/**
	 * Clears the code listing while keeping all other elements of program intact
	 */
	public void scratch() {
		codeListing.clear();
	}

	/**
	 * Lists the code listing, returning it as a String
	 */
	public String list() {
		StringBuilder out = new StringBuilder();

		out.append(programName);
		out.append("\n");

		lines = codeListing.entrySet();
		iter = lines.iterator();

		while (iter.hasNext()) {
			cL = iter.next();
			out.append(cL.getKey());
			out.append("\t");
			out.append(cL.getValue());
			out.append("\n");
		}

		return out.toString();
	}

	public int getFirstLine() {
		return ((TreeMap<Integer, String>) codeListing).firstKey();
	}

	// Boring parts of the class below. Not the meat of it.
	public void addLine(int lN, String inputLine) {
		codeListing.put(lN, inputLine);
	}

	public long getTimeToExecute() {
		long tte = timer.getTimeInMillis() - startTime;
		return tte;
	}

	// Stop execution of a next iteration
	public void stop() {
		running = false;
	}

	public void setProgName(String progName) {
		this.programName = progName;
	}

	public String getProgName() {
		return programName;
	}

	public int getCurrentLine() {
		return cL.getKey();
	}

	public void addData(double data) {
		dataStore.offer(data);
	}

	// Variables are of type Variable, and can be a Number or Number Array
	private Map<String, Variable> variables = new TreeMap<String, Variable>();

	public void putVar(Variable v) {
		variables.put(v.getName(), v);
	}

	public Variable getVar(String vName) {
		if (variables.containsKey(vName)) {
			return variables.get(vName);			
		} else {
			return null;
		}
	}

	public boolean varExists(String vName) {
		return variables.containsKey(vName);
	}

	public int varType(String vName) {
		Variable v = variables.get(vName);
		return v.getType();
	}

	// Functions are of type Function, and can be user-defined functions only
	private Map<String, Function> functions = new TreeMap<String, Function>();

	public void putFunction(Function f) {
		functions.put(f.getName(), f);
	}

	public Function getFunction(String fnName) {
		if (functions.containsKey(fnName)) {
			return functions.get(fnName);			
		} else {
			return null;
		}
	}

	public boolean fnExists(String fnName) {
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

	public boolean getRETURNKeySetisEmpty() {
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
	public Set<Entry<Integer, String>> getTailSet(int lN) {
		return ((TreeMap<Integer, String>) codeListing).tailMap(lN).entrySet();
	}

	// As FOR loops can be numerous, we handle them similarly to GOTO/GOSUB .. RETURN
	// by having a stack, waiting for a NEXT statement to execute it
	private Map<String, For> forNexts = new TreeMap<String, For>();

	public void newFor(String key, For forLoop) {
		forNexts.put(key, forLoop);
	}

	public For getFor(String key) {
		return forNexts.get(key);
	}
}
