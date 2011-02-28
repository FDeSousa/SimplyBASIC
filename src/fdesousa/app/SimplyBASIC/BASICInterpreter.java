package fdesousa.app.SimplyBASIC;

import java.util.Set;
import java.util.TreeMap;
import android.widget.EditText;

public class BASICInterpreter implements Runnable{

	private TreeMap<Integer, String> codeList = new TreeMap<Integer, String>();
	private EditText etCW;
	// Using the tokenizer again here, will be making good use of this too
	private Tokenizer t = new Tokenizer();
	// cL = current line, nL = next line, pL = previous line, rL = return line
	private int cL = 0, nL = 0, pL = 0, rL = 0;
	
	public BASICInterpreter(TreeMap<Integer, String> programListing, EditText editText) {
		// TODO Auto-generated constructor stub
		codeList = programListing;
		etCW = editText;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Set<Integer> lNs = codeList.keySet();
			
			if (! lNs.iterator().hasNext()){
				return;
			}
			
			
			
			do {
				
			} while (lNs.iterator().hasNext());
		}
		catch (Exception e){
			etCW.append(e.toString() + "\n");
		}
	}

}
