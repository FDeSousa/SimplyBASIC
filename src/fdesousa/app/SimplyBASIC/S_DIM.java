package fdesousa.app.SimplyBASIC;

import android.widget.EditText;

public class S_DIM extends Statement {

	public S_DIM(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		while (t.hasMoreTokens()){
			String vName = t.nextToken();
			// Check if it's a Variable
			if (Variable.isVariable(vName)){
				String[] args = Variable.splitVariable(vName);
				@SuppressWarnings("unused")
				Variable v;
				if (Variable.checkVariableType(vName) == Variable.S_ARR){
					v = new Variable(args[1], Integer.parseInt(args[2]));
				}
				else if (Variable.checkVariableType(vName) == Variable.M_ARR){
					v = new Variable(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				}
				else{
					et.append("ILLEGAL VARIABLE - LINE " + p.getCurrentLine() + "\n");
				}
			}
			else if (t.equals(",")){
				;	// Don't do anything with it, just acknowledge its existence
			}
			else {
				et.append("ILLEGAL VARIABLE - LINE " + p.getCurrentLine() + "\n");
			}
		}
	}
}
