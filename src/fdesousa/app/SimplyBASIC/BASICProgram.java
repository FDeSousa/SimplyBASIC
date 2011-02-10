package fdesousa.app.SimplyBASIC;

public class BASICProgram {
	
	private String progName = "", userName = "";
	private String[][] listing = null;
	private String output = "";
	private int cL = 0, nL = 0;
	
	public BASICProgram(String userName, String progName){
		// Used for HELLO, NEW
		this.setProgName(progName);
		this.setUserName(userName);
		// Very simple, just initialises the variables here
		// giving the program a name and user name attributed
	}
	
	public BASICProgram(String userName, String progName, String[][] listing){
		// Used for HELLO, OLD
		this.setProgName(progName);
		this.setUserName(userName);
		this.listing = listing;
		// As above, but this is used for an older program, to load the listing
	}
	
	public void C_NEW(String progName){				
		this.setProgName(progName);
		this.setUserName(userName);
		// Simple, just re-initialises the variables
	}
	
	public void C_OLD(String progName, String[][] listing){
		this.setProgName(progName);
		this.setUserName(userName);
		this.listing = listing;
		// As above, but this is used to load a program
	}
	
	public String[][] C_LIST(){
		// Return full program code listing
		return listing;
	}
	
	public String[][] C_LIST(int lN){
		// Return program listing from line number: lN
		String[][] list = null;
		cL = lN;
		while(cL <= listing.length){
			if (listing[cL] != null){
				list[cL] = listing[cL];
				cL++;
			}
			else{
				cL++;
			}
		}
		cL = 0;
		return list;
	}
	
	public String addLine(String[] tokens){
		try{
			int n = Integer.parseInt(tokens[0].trim());
			listing[n] = tokens;
			return null;
		}
		catch(Exception e){
			return e.toString();
		}
	}
	
	public String[] nextLine(){
		
		return listing[cL];
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
