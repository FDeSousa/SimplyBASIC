package fdesousa.app.SimplyBASIC;

import java.util.Queue;

import android.widget.EditText;

public class S_LET extends Statement {

	public S_LET(){}

	@SuppressWarnings("null")
	@Override
	public void doSt(BASICProgram p, Tokenizer t, EditText etCW){
		double result = 0.0;
		String vName = t.nextToken();
		String token = t.nextToken();
		// Let Variable sort itself out, and return a Variable to work with
		Variable v = Variable.getRestOfVariable(p, t, etCW, vName, token);
		
		token = t.nextToken();
		// If token is an equals sign, the expression begins next
		if (token.equals("=")){
			Queue<String> expression = null;
			// Very simple for the moment, will only handle numbers and symbols, hoping to mend that asap
			while (t.hasMoreTokens()){
				expression.offer(t.nextToken());
			}
			Expression e = new Expression(expression);
			result = e.eval(p, t, etCW);	// Expression we want to evaluate was parsed while instantiating 'e'
		}
		
		// Once the expression has been resolved, have to put it somewhere, ideally in the named variable
		if (v.getType() == Variable.NUM){
			v.setValue(result);
		}
		else if (v.getType() == Variable.S_ARR){
			String[] args = Variable.splitVariable(vName);
			v.setValueOfElementInS_DIM(Integer.parseInt(args[args.length-1]), result);
			// Get one argument from vName, the single 'row' identifier
			// Use this as the 'cell' identifier to place results in
		}
		else if (v.getType() == Variable.M_ARR){
			String[] args = Variable.splitVariable(vName);
			v.setValueOfElementInM_DIM(Integer.parseInt(args[args.length-2]), 
					Integer.parseInt(args[args.length-1]), result);
			// Get two arguments from vName, the 'row' and 'column' identifiers
			// Use these to identify the 'cell' to place results in
		}
		etCW.append("\n" + String.valueOf(result));
	}
}
