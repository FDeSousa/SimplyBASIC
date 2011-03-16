package fdesousa.app.SimplyBASIC;

import java.util.PriorityQueue;

import android.widget.EditText;

public class S_DEF extends Statement {

	public S_DEF(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		// Next token after "DEF" will be the function call name
		String fnName = t.nextToken();
		// Get the argument from within fnName, this is the variable to look for
		String fnVarName = Function.getArg(fnName);
		// Create a new Variable for use with this user-defined Function
		Variable fnVar = new Variable(fnVarName, 0.0);
		// Put this new variable into BASIC Program
		p.putVar(fnVar);
		
		PriorityQueue<String> expression = new PriorityQueue<String>();
		
		// Since we have the basic stuff sorted, get the expression associated with this new Function
		String token = t.nextToken();
		// If token is an equals sign, the expression begins next
		if (token.equals("=")){
			// Very simple for the moment, will only handle numbers and symbols, hoping to mend that asap
			while (t.hasMoreTokens()){
				token = t.nextToken();
				expression.offer(token);
			}
		}
		// Name the associated Expression, but don't initialise it just yet
		Expression fnExp = new Expression();
		// Reset the queue
		fnExp.reset(expression);
		Function fn = new Function(fnName, fnExp, fnVar);
		p.putFunction(fn);
	}
}
