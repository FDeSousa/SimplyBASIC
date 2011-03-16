package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_DATA extends Statement {

	public S_DATA(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		String s = "";
		while (t.hasMoreTokens()){
			s = t.nextToken();
			if (! s.equals(",")){
				if (Expression.isNumber(s)){
					p.addData(Double.parseDouble(s));
				}
				else {
					etCW.append("ILLEGAL CONSTANT.\n" + 
							"LINE NUMBER " + p.getCurrentLine() +".\n");
					p.stopExec();
					return;
				}
			}
		}
	}
}
