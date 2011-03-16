package fdesousa.app.SimplyBASIC;

import java.util.PriorityQueue;

import android.widget.EditText;

public class S_PRINT extends Statement {

	public S_PRINT(BASICProgram pgm, Tokenizer tok, EditText edtxt){
		super(pgm, tok, edtxt);
	}

	@Override
	public void doSt(){
		String token = new String();
		String outLine = new String();
		// while t has more tokens, keep them coming, and evaluate whatever needs evaluation on-the-spot, before printing
		// start/end of printable string: '"'
		// separator of sections: ','
		while (t.hasMoreTokens()){
			token = t.nextToken();
			
			if (token.contains("\"")){
				// If the token has a double-quotation mark, it's a literal, print it
				outLine += Tokenizer.removeQuotes(token);
			}
			else if (token.equals(",")){
				// Acknowledge commas as new line indicator
				outLine += "\n";
			}
			else {
				// Assume it's an expression, figure that out first
				PriorityQueue<String> ex = new PriorityQueue<String>();
				while (! token.equals(",")){
					ex.offer(token);
					token = t.nextToken();
				}
				Expression e = new Expression(ex, p, et);
				outLine += String.valueOf(e.eval(p, et));
			}
		}
		et.append(outLine + "\n");
	}
}
