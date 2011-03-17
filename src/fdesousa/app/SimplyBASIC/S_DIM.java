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
				@SuppressWarnings("unused")
				Variable v = new Variable(vName);
				// Since putting all of the constructors of Variable into one
				// unified constructor, that figures out, splits, and then assigns
				// initialises itself, it's much easier here
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
