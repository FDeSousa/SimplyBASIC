package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_RETURN extends Statement {

	public S_RETURN(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		p.setlNs(p.getRETURNKeySet());
		p.setRETURNKeySet(null);
	}
}
