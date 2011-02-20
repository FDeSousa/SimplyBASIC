package fdesousa.app.SimplyBASIC;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import android.widget.EditText;

public class BASICProgram implements Runnable{
	
	// Creating a hashtable to hold the code listing in
	// Hashtable has capacity of 2011 (sexy prime number)
	// Hashtable has load factor of 0.75
	// With this capacity and load factor, should handle 1508
	// Both of these are to have a trade-off between memory use and performance
	private Hashtable<String, String[]> codeList = new Hashtable<String, String[]>(2011, 0.75f);
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
	
	public BASICProgram(String userName, String progName, Hashtable<String, String[]> oldCodeList){
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

	public void C_OLD(String progName, Hashtable<String, String[]> oldCodeList){
		setProgName(progName);
		codeList = oldCodeList;
	}
	
//	public void C_LIST(EditText editText){
//		// Return full program code listing
//		EditText etCW = editText;
//		
//		Enumeration<String> lineNumberList = codeList.keys();
//		String lineNumber = "";
//		String[] outputTokens;
//		if (lineNumberList != null){
//			while (lineNumberList.hasMoreElements()){
//				lineNumber = lineNumberList.nextElement().toString();
//				outputTokens = codeList.get(lineNumber);
//				etCW.append(lineNumber + "\t");
//				for(int i = 0; i < outputTokens.length; i++){
//					etCW.append(outputTokens[i] + "\n");
//				}
//			}
//		}
//		else{
//			etCW.append("NOTHING TO DISPLAY.\n> ");
//		}
//	}
	
	public void C_LIST(EditText editText, int lN){
		// Return parts of program code listing
		EditText etCW = editText;

		Enumeration<String> lineNumberList = codeList.keys();
		String lineNumber = "";
		String[] outputTokens;
		if (lineNumberList != null){
			while (lineNumberList.hasMoreElements()){
				lineNumber = lineNumberList.nextElement().toString();
				int lineNumberInteger = new Integer(lineNumber);
				if ((int)lineNumberInteger >= lN){
					outputTokens = codeList.get(lineNumber);
					etCW.append(lineNumber + "\t");
					for(int i = 0; i < outputTokens.length; i++){
						etCW.append(outputTokens[i] + "\n");
					}
				}
			}
		}
		else{
			etCW.append("NOTHING TO DISPLAY.\n> ");
		}

		//Enumeration listEnum = codeList.keys();
		//while (listEnum.hasMoreElements()){
		//	String lineNumber = (String)listEnum.nextElement();
			//Object listObject = codeList.get(lineNumber);
		//	etCW.append("\t" + lineNumber + "\n");
		//}
		
	}
	
	public String addLine(String lN, String[] inputTokens){
		try{
			String lineNumber = lN;
			String[] tokens = inputTokens; 
			codeList.put(lineNumber, tokens);
			return null;
		}
		catch(Exception e){
			//etCW.append(e.toString());
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
