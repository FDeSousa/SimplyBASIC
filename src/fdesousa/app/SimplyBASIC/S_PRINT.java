package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_PRINT extends Statement {

	public S_PRINT(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		String token = t.nextToken();
		// while t has more tokens, keep them coming, and evaluate whatever needs evaluation on-the-spot, before printing
		// start/end of printable string: '"'
		// separator of sections: ','
		
	}
}
