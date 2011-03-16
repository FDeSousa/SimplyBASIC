package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_PRINT extends Statement {

	public S_PRINT(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		String token = t.nextToken();
		// while t has more tokens, keep them coming, and evaluate whatever needs evaluation on-the-spot, before printing
		// start/end of printable string: '"'
		// separator of sections: ','
		
	}
}
