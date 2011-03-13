package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_LET extends Statement {

	public S_LET(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		String vName = t.nextToken();
		String exp = "";
		t.mark();
		if (t.nextToken().equals("=")){
			while (t.hasMoreTokens()){
				
			}
		}
		
	}
}
