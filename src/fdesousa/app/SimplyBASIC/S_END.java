package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_END extends Statement {

	public S_END(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		p.stopExec();
		etCW.append("\nTIME: " + (p.getTimeToExecute() / 100.0) + " SECONDS");
	}
}
