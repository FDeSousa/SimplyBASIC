package fdesousa.app.SimplyBASIC;

public class BASICProgram {
	
	private String progName = "", userName = "";
	private String[][] listing = new String[65535][255];
	private String output = "";
	private int cL = 0, nL = 0;
	
	public BASICProgram(String progName, String userName){
		this.progName = progName;
		this.userName = userName;
		// Very simple, just initialises the variables here
		// giving the program a name and user name attributed
	}
	
	public BASICProgram(String progName, String userName, String[][] listing){
		this.progName = progName;
		this.userName = userName;
		this.listing = listing;
		// As above, but this is used for an older program, to load the listing
	}
	
	public String[][] C_LIST(){
		return listing;
	}
	
	public String[][] C_LIST(int lN){
		String[][] list = new String[65535][255];
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
	
}
