package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_GOTO extends Statement {

	public S_GOTO(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		if (t.hasMoreTokens()) {
			String token = t.nextToken();
			if (Expression.isNumber(token)) {
				int lN = Integer.parseInt(token);
				p.setlNs(p.getTailMap(lN));
			}
			else {
				etCW.append("ILLEGAL LINE NUMBER - LINE " + p.getCurrentLine());
				p.stopExec();
			}
		}
		else {
			etCW.append("MISSING LINE NUMBER - LINE " + p.getCurrentLine());
			p.stopExec();
		}
	}
}
