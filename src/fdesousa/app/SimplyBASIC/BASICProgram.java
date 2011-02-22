package fdesousa.app.SimplyBASIC;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import android.widget.EditText;

public class BASICProgram implements Runnable{
	
	// Creating a hashtable to hold the code listing in
	// Hashtable has capacity of 2011 (sexy prime number)
	// Hashtable has load factor of 0.75
	// With this capacity and load factor, should handle 1508
	// Both of these are to have a trade-off between memory use and performance
	private TreeMap<Integer, String> codeList = new TreeMap<Integer, String>();
	//private SortedSet<String> lines;
	//private Set<String> codeList = Collections.synchronizedSortedSet(lines);
	private String progName = "", userName = "";
	
	//private String output = "";
	// Current Line (cL), New Line (nL), Last Line (lL) are pointers
	private int cL = 0, nL = 0, lL = 0;
	
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
	
	@Override
	public void run() {
		// TODO Add code to run
		// As class implements Runnable, this must override, and be used
		
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
	
	public void C_LIST(EditText editText, int lN){
		// Return parts of program code listing
		EditText etCW = editText;
		
		// For now, this just lists the program as-is, straight from the Treemap
		/*
		Enumeration<Integer> lineNumberList = (Enumeration<Integer>) codeList.keySet();
		String lineNumber = "";
		String outputTokens;
		if (lineNumberList != null){
			while (lineNumberList.hasMoreElements()){
				lineNumber = lineNumberList.nextElement().toString();
				int lineNumberInteger = new Integer(lineNumber);
				if ((int)lineNumberInteger >= lN){
					outputTokens = codeList.get(lineNumber);
					etCW.append(lineNumber + "\t");
					for(int i = 0; i < outputTokens.length(); i++){
						etCW.append(outputTokens + "\n");
					}
				}
			}
		}
		else{
			etCW.append("NOTHING TO DISPLAY.\n> ");
		}
		*/
		
		if (lN >= codeList.firstKey() && lN <= codeList.lastKey()){
			SortedMap<Integer,String> C_LIST_codeList = codeList.tailMap(lN);
			Set<Integer> lineNumbers = C_LIST_codeList.keySet();
			
			etCW.append("\n\tUSER NAME: " + userName
					+ "\tPROGRAM NAME: " + progName);
			
			int n = lineNumbers.iterator().next();
			
			while (lineNumbers.iterator().hasNext()){
				etCW.append("\t" + n + "\t" + C_LIST_codeList.get(n).toString());
				n = lineNumbers.iterator().next();
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

}
