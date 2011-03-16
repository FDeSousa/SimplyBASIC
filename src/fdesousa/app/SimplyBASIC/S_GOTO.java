package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_GOTO extends Statement {

	public S_GOTO(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		if (t.hasMoreTokens()) {
			String token = t.nextToken();
			if (Expression.isNumber(token)) {
				int lN = Integer.parseInt(token);
				p.setlNs(p.getTailMap(lN));
			}
			else {
				et.append("ILLEGAL LINE NUMBER - LINE " + p.getCurrentLine());
				p.stopExec();
			}
		}
		else {
			et.append("MISSING LINE NUMBER - LINE " + p.getCurrentLine());
			p.stopExec();
		}
	}
}
