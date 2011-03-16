package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_END extends Statement {

	public S_END(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		p.stopExec();
		et.append("\nTIME: " + (p.getTimeToExecute() / 100.0) + " SECONDS");
	}
}
