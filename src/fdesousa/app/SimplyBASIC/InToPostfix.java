package fdesousa.app.SimplyBASIC;

import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;

public class InToPostfix {
	
	public InToPostfix() {
		super();
		
	}

	private Stack<String> stack;
	@SuppressWarnings("unused")
	private Queue<String> Pfix;
	
	private String temp = "";
	
	public Queue<String> InfixToPostfix (String Ifix) {
		
		String str;
		
		Queue<String> Pfix = new LinkedList<String>();
		stack = new Stack<String>();
				
		//stack.push("(");										//A
		
		Ifix.trim();											//B
		
		
		for (int i=0;i<Ifix.length();i++){
			str = Ifix.substring(i, i+1);
			
			if (str.matches("[A-Z]")){							//C
				Pfix.offer(str);
			} //end if
			
			else if (str.matches("\\d")){
				
				// Check to see if the number is single-digit or not
				while (str.matches("\\d|[.]")){
					
					if (Ifix.substring(i+1, i+2).matches("\\d|[.]") && i+2 < Ifix.length()){
						//As the number hasn't finished, 
						//append it to str
						str += Ifix.substring(i+1, i+2);
						i++;
					} //end if
					
					else{
						Pfix.offer(str);
					} //end else
				} //end while
			} //end else if
			
			else if (str == "("){								//D
				stack.push(str);
			} //end else if
			
			else if (str == ")"){								//E
				while ((temp = stack.pop()) != "("){
					Pfix.offer(temp);
				} //end while
				
			} //end else if
			
			else if (multiDivOps(str) == true){					//F
				
				boolean escapeWhile = false;
				temp = stack.peek();
				
				while (escapeWhile != true){
					
					if (addSubOps(temp) == true || temp == "("){
						escapeWhile = true;
					} //end if
					
					else{
						Pfix.offer(temp);
					} //end else
				} //end while
				
				stack.push(str);
			} //end else if
			
			else if (addSubOps(str) == true){					//G
				
				boolean escapeWhile = false;
				temp = stack.peek();
				
				while (escapeWhile != true){
					
					if (temp == "("){
						escapeWhile = true;
					} //end if
					
					else{
						Pfix.offer(temp);
					} //end else
				} //end while
				
				stack.push(str);
			} //end else if
			
			else{
				
			}
				
			// Skipping step H for now, assuming no invalid chars
			
			while ((temp = stack.pop()) != "("){				//I
				Pfix.offer(temp);
			} //end while
			
			// Skipping step J for now, ignoring EOL char
			
		} //end for
		
		return Pfix;
	} //end public InfixToPostfix
	
	private boolean multiDivOps(String str){
		if (str == "*" || str == "/" || str == "%"){
			return true;
		} //end if
		else
			return false;
	} //end private multiDivOps
	
	private boolean addSubOps(String str){
		if (str == "+" || str == "-"){
			return true;
		} //end if
		else
			return false;
	} //end private addSubOps
}

