package fdesousa.app.SimplyBASIC;

import java.util.Set;
import java.util.regex.Pattern;
import android.widget.EditText;

public class S_FOR extends Statement {
	/*
	 * FOR statement syntax is:
	 * 	FOR v = assign TO limit
	 * or
	 * 	FOR v = assign TO limit STEP count
	 */
	private String regexNOSTEP = "^\\s*(\\w{1}\\d?)\\s*[=]{1}(.+)[T]{1}[O]{1}(.+)$";
	private String regexSTEP = "^\\s*(\\w{1}\\d?)\\s*[=]{1}(.+)[T]{1}[O]{1}(.+)[S]{1}[T]{1}[E]{1}[P]{1}(.+)$";
	/*
	 * The variable and the three expressions associated with FOR:
	 * 	v is the named variable to increment/decrement with FOR .. NEXT
	 * 	assign is the expression to assign v with first
	 * 	limit is the expression v is limited to
	 * 	count if the expression v is (in/de)cremented by
	 */
	private Variable v;
	private Expression assgn, limit, count;
	/*
	 * As the value of the expressions is all we care about, these
	 * store them.
	 *	first = assign.eval
	 *	last = limit.eval
	 *	step = count.eval
	 */
	private double first, last, step;
	// Store the code list to return to
	private Set<Integer> codeList;
	
	/**
	 * 
	 * @param pgm
	 * @param tok
	 * @param edtxt
	 */
	public S_FOR(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	/**
	 * Do the statement first-time, when run by Statement
	 */
	public void doSt(){
		// Get the FOR expressions, and calculate them
		getFORExp();
		if (v.getValue() < last){
			codeList = p.getlNs();
			p.newFor(getName(), this);			
		}
		else {
			et.append("ERROR ASSIGNING FOR - LINE NUMBER " + p.getCurrentLine() + "\n");
			p.stopExec();
		}
	}

	/**
	 * Do the statement a subsequent time, when run by NEXT
	 */
	public void doStNext(){
		if (v.getValue() < last){
			v.setValue(v.getValue() + step);
			p.setlNs(codeList);
		}
	}

	/**
	 * Get the FOR expressions, for either type of FOR statement.<br>
	 * Once these calculations are done, set the variables to measure against
	 */
	private void getFORExp(){
		String line = t.getRestOfLine();
		Pattern pt;
		String[] args;
		Tokenizer expTok = new Tokenizer();
		
		if (Pattern.matches(regexSTEP, line)){
			pt = Pattern.compile(regexSTEP);
			args = pt.split(line);
			/*
			 * Upon using .split we get:
			 * 0	The whole line
			 * 1	Named Variable
			 * 2	Assignment Expression
			 * 3	Limit Expression
			 * 4	Step Expression
			 */
			v = Variable.getVariable(p, args[1]);
			expTok.reset(args[2]);
			assgn = Expression.getExp(p, et, expTok);
			first = assgn.eval(p, et);
			v.setValue(first);
			
			expTok.reset(args[3]);
			limit = Expression.getExp(p, et, expTok);
			last = limit.eval(p, et);
			
			expTok.reset(args[4]);
			count = Expression.getExp(p, et, expTok);
			step = count.eval(p, et);
		}
		else if (Pattern.matches(regexNOSTEP, line)){
			pt = Pattern.compile(regexNOSTEP);
			args = pt.split(line);
			/*
			 * Upon using .split we get:
			 * 0	The whole line
			 * 1	Named Variable
			 * 2	Assignment Expression
			 * 3	Limit Expression
			 */
			v = Variable.getVariable(p, args[1]);
			expTok.reset(args[2]);
			assgn = Expression.getExp(p, et, expTok);
			first = assgn.eval(p, et);
			v.setValue(first);

			expTok.reset(args[3]);
			limit = Expression.getExp(p, et, expTok);
			last = limit.eval(p, et);

			step = 0.0;
		}
		else {
			et.append("INVALID FOR - LINE NUMBER " + p.getCurrentLine() + "\n");
			p.stopExec();
		}
	}
	
	public static S_FOR getFOR(BASICProgram p, String vName){
		return p.getFor(vName);
	}

	public String getName() {
		// TODO Auto-generated method stub
		return v.getName();
	}
}
