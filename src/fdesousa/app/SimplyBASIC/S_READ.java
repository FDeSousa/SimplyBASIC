package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_READ extends Statement {

	public S_READ(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		do {
			String token = t.nextToken();
			if (! token.equals(",")){
				if (Variable.isVariable(token)){
					if (p.hasData()){
						Variable v = Variable.getVariable(p, token);
						v.setValue(token, p.getData());
					}
					else {
						et.append("NO DATA.\n");
						p.stopExec();
					}
				}
				else {
					et.append("ILLEGAL VARIABLE.\n");
					p.stopExec();
				}
			}
		} while (t.hasMoreTokens());
	}
}
