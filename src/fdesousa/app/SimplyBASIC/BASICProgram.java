package fdesousa.app.SimplyBASIC;

import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import android.widget.EditText;

public class BASICProgram implements Runnable{

	private TreeMap<Integer, String> codeList = new TreeMap<Integer, String>();
	private Set<Integer> lNs;
	private Set<Integer> RETURNKeySet;

	private String progName = "", userName = "";
	// Using the tokenizer again here, will be making good use of this too
	private Tokenizer t = new Tokenizer();
	// cL = current line, nL = next line, pL = previous line, rL = return line, lL = last line
	private int cL = 0;// nL = 0, pL = 0, rL = 0, lL = 0;
	
	// dataStore is used for keeping data from DATA statements in a FIFO for later access.
	// It's stupid using the dataStore to store strings, but as it can store both integers AND
	// real numbers (all float as far as BASIC is concerned), I'll resolve this later 
	private Queue<String> dataStore;

	// RUN!
	public void run(EditText etCW) {
		try {
			// Do the first-run, to get all DATA stored into a FIFO list
			run();
			
			// Re-initialise lNs here for use while final run is going
			lNs = codeList.keySet();
			if (! lNs.iterator().hasNext()){
				return;
			}
			do {
				cL = lNs.iterator().next();
				Statement statement = new Statement(this, t, etCW);
				statement.doSt();
			} while (lNs.iterator().hasNext());
		}
		catch (Exception e){
			etCW.append(e.toString().toUpperCase() + ".\n");
		}
	}
	
	public void run() {
		// The generic, auto-generated, must-have version
		// Will see implementation as a first-run method
		// This will check for DATA statements, and store those into a FIFO list
		String s = null;
		try {
			lNs = codeList.keySet();
			// Just make sure it actually has something
			if (! lNs.iterator().hasNext()){
				return;
			}
			do {
				cL = lNs.iterator().next();
				t.reset(codeList.get(cL));
				s = t.nextToken();
				
				if (s.compareTo("DATA") == 0){
					while (t.hasMoreTokens()){
						s = t.nextToken();
						if (s.compareTo(",") != 0 && CommandInterpreter.isNumber(s)){
							dataStore.offer(s);
						}
					}
				}
			} while (lNs.iterator().hasNext());
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

			etCW.append("\n\tUSER NAME: " + userName
					+ "\tPROGRAM NAME: " + progName);

			int cL = lineNumbers.iterator().next();

			while (lineNumbers.iterator().hasNext()){
				etCW.append("\t" + cL + "\t" + C_LIST_codeList.get(cL).toString());
				cL = lineNumbers.iterator().next();
			}
		}
		else {
			// This actually comes out better than it seems, except for the lineNumber'=null' bit
			etCW.append("\n\tUSER NAME: " + userName
					+ "\tPROGRAM NAME: " + progName
					+ "\n\tINVALID LINE NUMBER SPECIFIED"
					+ "\n\tMUST BE BETWEEN " + codeList.firstKey() 
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
	
	// Using these nice and simple Getters and Setters for using 
	// during GOSUB / GOTO / IF .. THEN / FOR .. NEXT statements
	public Set<Integer> getlNs() {
		return lNs;
	}

	public void setlNs(Set<Integer> lNs) {
		this.lNs = lNs;
	}

	public Set<Integer> getRETURNKeySet() {
		return RETURNKeySet;
	}

	public void setRETURNKeySet(Set<Integer> RETURNKeySet) {
		this.RETURNKeySet = RETURNKeySet;
	}

}
