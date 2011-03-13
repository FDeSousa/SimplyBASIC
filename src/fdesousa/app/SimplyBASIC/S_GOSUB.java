package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_GOSUB extends Statement {

	public S_GOSUB(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		if (t.hasMoreTokens()) {
			String token = t.nextToken();
			if (Expression.isNumber(token)) {
				int lN = Integer.parseInt(token);
				p.setRETURNKeySet(p.getlNs());
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
