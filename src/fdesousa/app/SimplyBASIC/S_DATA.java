package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_DATA extends Statement {

	public S_DATA(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		String s = "";
		while (t.hasMoreTokens()){
			s = t.nextToken();
			if (! s.equals(",")){
				if (Expression.isNumber(s)){
					p.addData(Double.parseDouble(s));
				}
				else {
					et.append("ILLEGAL CONSTANT.\n" + 
							"LINE NUMBER " + p.getCurrentLine() +".\n");
					p.stopExec();
					return;
				}
			}
		}
	}
}
