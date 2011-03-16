package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_READ extends Statement {

	public S_READ(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		do {
			String token = t.nextToken();
			if (! token.equals(",")){
				if (Variable.isVariable(token)){
					if (p.hasData()){
						Variable v = Variable.getVariable(p, token);
						v.assignValueToVariable(p.getData(), token);
					}
					else {
						etCW.append("NO DATA.\n");
						p.stopExec();
					}
				}
				else {
					etCW.append("ILLEGAL VARIABLE.\n");
					p.stopExec();
				}
			}
		} while (t.hasMoreTokens());
	}
}
