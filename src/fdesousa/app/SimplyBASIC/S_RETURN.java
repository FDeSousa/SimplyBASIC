package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_RETURN extends Statement {

	public S_RETURN(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		p.setlNs(p.getRETURNKeySet());
		p.setRETURNKeySet(null);
	}
}
