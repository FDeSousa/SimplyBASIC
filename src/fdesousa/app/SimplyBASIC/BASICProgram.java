package fdesousa.app.SimplyBASIC;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import android.widget.EditText;

public class BASICProgram{

	private TreeMap<Integer, String> codeList = new TreeMap<Integer, String>();
	//private SortedSet<String> lines;
	//private Set<String> codeList = Collections.synchronizedSortedSet(lines);
	private String progName = "", userName = "";
	
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
	
	public void run(EditText editText) {
		// TODO Add code to run
		// As class implements Runnable, this must override, and be used
		// Brings in etCW as editText, so it can be used for printing to
		
		// This class no longer implements Runnable, leave that to BASICInterpreter class
		BASICInterpreter BI = new BASICInterpreter(codeList, editText);
		try {
			BI.run();
		}
		catch (Exception e) {
			editText.append(e.toString() + "\n");
		}
		// implement BI, and come back to it
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
