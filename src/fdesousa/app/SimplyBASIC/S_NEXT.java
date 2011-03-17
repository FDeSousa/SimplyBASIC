package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_NEXT extends Statement {

	public S_NEXT(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		String vName;
		S_FOR forNext;
		
		if (t.hasMoreTokens()){
			vName = t.nextToken();
			if (Variable.isVariable(vName) & 
					Variable.checkVariableType(vName) == Variable.NUM){
				forNext = p.getFor(vName);
				forNext.doStNext();
			}
			else {
				et.append("INVALID VARIABLE - LINE NUMBER " + p.getCurrentLine() + "\n");
				p.stopExec();
			}
		}
		else {
			et.append("NEXT WITHOUT VARIABLE - LINE NUMBER " + p.getCurrentLine() + "\n");
			p.stopExec();
		}
	}
}
