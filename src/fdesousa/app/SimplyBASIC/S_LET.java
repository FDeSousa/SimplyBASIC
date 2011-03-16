package fdesousa.app.SimplyBASIC;

import java.util.PriorityQueue;
import java.util.Queue;

import android.widget.EditText;

public class S_LET extends Statement {

	public S_LET(){}

	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		double result = 0.0;
		String vName = t.nextToken();
		// Let Variable sort itself out, and return a Variable to work with
		Variable v = Variable.getVariable(p, vName);
		
		String token = t.nextToken();
		// If token is an equals sign, the expression begins next
		if (token.equals("=")){
			PriorityQueue<String> expression = new PriorityQueue<String>();
			// Very simple for the moment, will only handle numbers and symbols, hoping to mend that asap
			while (t.hasMoreTokens()){
				token = t.nextToken();
				expression.offer(token);
			}
			Expression e = new Expression(expression);
			result = e.eval(p, t, etCW);	// Expression we want to evaluate was parsed while instantiating 'e'
		}
		
		// Once the expression has been resolved, have to put it somewhere, ideally in the named variable
		v.assignValueToVariable(result, vName);
		
		etCW.append("\n" + String.valueOf(result));
	}
}
