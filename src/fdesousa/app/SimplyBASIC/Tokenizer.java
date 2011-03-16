package fdesousa.app.SimplyBASIC;
/**
 * Greatly inspired by the LexicalTokenizer class
 * from Cocoa, the BASIC Interpreter written in Java
 * by Chuck McManis.
 * As I looked at my own theories, and looked at his code, I realised how 
 * inefficient mine was, so this is taking his theory, and applying my own 
 * code to it. Hopefully somewhat simplified for reading, and using newer
 * classes that Java now has access to, and built-in as standard.
 * As required by McManis, the Cocoa home page link:
 * http://www.mcmanis.com/chuck/java/cocoa/index.html
 */
public class Tokenizer {

	private int curPos = 0;		// Marks the current position in the char array
	private int markPos = 0;	// Used to mark a position temporarily
	private String t = "";	// The current token that's being worked with
	private char buffer[];		// Holds the characters to analyse, easier to move between chars
	// than in a String, that involves .substring(char position)

	public Tokenizer() {
		// Constructor doesn't really need initialisation of anything for
		// the moment as it's used more than once
	}

	public String nextToken(){
		// token is the returned String
		t = "";
		// Return EOL if curPos is also EOL or greater
		if (curPos >= buffer.length){
			return "\n";
		}
		// We don't need no stinkin' spaces here!
		eatSpace();

		// Check what to do with current Character
		switch (buffer[curPos]){
		// All of [+ - * / ^ = ( )] are parsed immediately and alone
		case '+':
		case '-':
		case '*':
		case '/':
		case '^':
		case '=':
		case '(':
		case ')':
			t += buffer[curPos];
			curPos++;
			break;
			// All of [< > . ,] may have additional operators/chars
			// If the next char is '=', then token is '<=' or '>='
		case '<':
		case '>':
			t += buffer[curPos];
			curPos++;
			if(buffer[curPos + 1] == '='){
				t += buffer[curPos + 1];
				curPos++;
			}
			break;

			// If the next char is a number, token is a decimal number
		case '.':
			t += buffer[curPos];
			curPos++;
			while (isDigit(buffer[curPos]) && hasMoreTokens()){
				t += buffer[curPos];
				curPos++;
			}
			break;

			// Eat space, then check if the next char is a Letter
		case ',':
			t += buffer[curPos];
			/** Will be ignoring this for now, a comma is just a comma, nothing else
			 * Mostly because it gets complicated from here.
			 * Commas don't just separate variables, but also numbers (integers/decimals)
			 * For now, let the class that requested nextToken, decide what to do after a comma
			 * 
			eatSpace();
			// If the next char is a letter, check char after that
			if (isLetter(buffer[curPos])) {
				// If next char is also letter, it's probably a new token, to be ignored
				if (isLetter(buffer[curPos + 1])) {
					// Do not want the token to include this char, so ignore it, return token as-is
					return token;
				// If next char is number, it's a variable (letter & number)
				} else if (isDigit(buffer[curPos + 1])) {
					token += buffer[curPos] + buffer[curPos + 1];
					curPos += 2;
				// If next char is something different, current token is a variable (letter)
				} else {
					token += buffer[curPos];
					curPos++;
				}
			}
			 */
			break;

			// Still need to add: case '"'
			// that handles " and what's held inside them, i.e. for PRINT statement
		case '"':
			curPos++;
			while (buffer[curPos] == '"' && hasMoreTokens()){
				t += buffer[curPos++];
			}
			break;

		default:
			// Under default, if it's not one of the many conditions above
			// then check if it's a letter or digit, and the operation continues

			// Get the whole sequence of digits and letters, no matter what
			while (isLetter(buffer[curPos]) || isDigit(buffer[curPos])) {
				t += buffer[curPos++];
				// A little costly on processor time, doing these checks every time a letter
				// or character is added, but it could minimise mistakes
				
				// Check if it's a standard BASIC function, if it is return it immediately
				for (int i = 0; i < Function.functions.length; i++){
					if (t.contains(Function.functions[i]))
						return t;
				}
				// Check if it's a system command, if it is return it immediately
				for (int i = 0; i < CommandInterpreter.commands.length; i++){
					if (t.contains(CommandInterpreter.commands[i]))
						return t;
				}
				// Check if it's a BASIC command, if it is return it immediately			
				for (int i = 0; i < Statement.statements.length; i++){
					if (t.contains(Statement.statements[i]))
						return t;
				}
			}

			// Check if this thing is a variable
			if (Variable.isVariable(t)){
				// It is! So now get the rest of it (if there is a rest of it)
				if (peek(false) == '('){
					// Right, get the whole of the variable's arguments
					do {
						t += buffer[curPos++];
					} while (buffer[curPos - 1] != ')');
				}
				// Either way, return t now, don't check for anything else
				return t;
			}
			
			// Check if it's a function then
			if (Function.isFunction(t)){
				// It certainly is! Get the arguments!
				if (peek(false) == '('){
					// Since it lists arguments, get them!
					do {
						t += buffer[curPos++];
						// Function call can include a reference to one array element
						if (buffer[curPos] == '('){
							do {
								t += buffer[curPos++];
							} while (buffer[curPos - 1] != ')');
						}
					} while (buffer[curPos - 1] != ')');
				}
				return t;
			}
			
			// Since it's none of the above, it's likely a number
			if (Expression.isNumber(t)){
				if (buffer[curPos] == '.'){
					t += buffer[curPos++];
					while (isDigit(buffer[curPos])){
						t += buffer[curPos++];
					}
					// BASIC handles exponents with the letter E, at which point, number after it
					// is the exponent of the given number i.e. in 111.222E333, 333 is the exponent
					if (buffer[curPos] == 'E'){
						t += buffer[curPos++];
						while (isDigit(buffer[curPos])){
							t += buffer[curPos++];
						}
					}
				}
				return t;
			}

			// Commenting out this enormous section for now, it's a bit redundant, as I can
			// do these checks after adding the letters/digits to the token
			/**
			if (isLetter(buffer[curPos])){
				// If it's a letter, begin the hunt for a named variable or command
				token += buffer[curPos++];
				// Check what this next char is.
				// If it's a letter, assume a command, loop to find the rest of it
				if (isLetter(buffer[curPos])){
					while (isLetter(buffer[curPos]) && hasMoreTokens()){
						token += buffer[curPos++];
					}
				}
				// If it's a digit, assume a variable name with number (i.e. A1)
				// Variables: one letter/one letter and one/two digits
				else if (isDigit(buffer[curPos])){
					token += buffer[curPos++];
				}
				// If it has an open parentheses right after the first letter, assume it's
				// an array identifier, and keep going until it finds a close parentheses
				// Variable arrays: one letter & one digit & ( <index> )
				else if (buffer[curPos] == '('){
					// It's odd, to check curPos - 1, but like this, we can still get the close parentheses
					// into the token. This might be changed later anyway, just that I want it parsing the whole
					// of the relevant section first.
					// Will likely let the tokenizer resolve it by simply parsing the close parentheses, and letting
					// the calling class resolve itself.
					while (buffer[curPos - 1] != ')'){
						token += buffer[curPos++];
					}
				}
				// Just in case, added a break here. You never know, it might save lives
				break;
			}
			else if (isDigit(buffer[curPos])){
				// If it's a digit, begin looking for the rest of the number
				while ((isDigit(buffer[curPos]) || buffer[curPos] == '.')
						&& hasMoreTokens()){
					// While it's a digit or a decimal-point, add it to token
					token += buffer[curPos++];
				}
			}
			 */
			break;
		}
		// Handles the return, no matter what
		return t.trim();
	}

	// Methods and functions for doing the admin stuff
	public boolean hasMoreTokens(){
		// Simple enough. If current position is less than buffer length, returns true
		return (curPos < buffer.length);
	}

	public char peek(boolean eatSpaces){
		char c;
		mark();
		if (eatSpaces == true) { 
			eatSpace(); 
		}
		c = buffer[curPos];
		resetToMark();
		return c;
	}

	public void mark(){
		// Set mark to current position for reparsing
		markPos = curPos;
	}

	public void resetToMark(){
		// Reset current position to mark position
		curPos = markPos;
	}

	/**
	 * Get a String containing a variable as a token
	 * @return String containing variable and arguments
	 */
	public String getVariable(){
		t = "";

		t = nextToken();
		if (peek(false) == '('){
			do {
				t += nextToken();
			} while (t.substring(t.length()-2) != ")");			
		}
		return t.trim();
	}

	public static double[] separateNumber(String inputNumber){
		// Odd one to explain, but this should separate the main section
		// of a given number (input as a String) from its exponent (if it has one)
		// and return the main number in separated[0] and exponent in separated[1]
		double[] separated = { 0.0, 0.0 };
		String number = null, exponent = null;
		int i = 0;

		while (isDigit(inputNumber.charAt(i)) && i < inputNumber.length()){
			number += inputNumber.charAt(i++);
		}
		if (inputNumber.charAt(i) == 'E'){
			i++;
			while (isDigit(inputNumber.charAt(i)) && i < inputNumber.length()){
				exponent += inputNumber.charAt(i++);
			}
		}
		separated[0] = Double.parseDouble(number);
		separated[1] = Double.parseDouble(exponent);
		return separated;
	}

	public String getWholeLine(){
		String line = "";
		mark();
		reset();
		while (hasMoreTokens()){
			line += buffer[curPos++];
		}
		resetToMark();
		return line.trim();
	}

	public String getRestOfLine(){
		// Send back the rest of the line, minus what's already been sent
		// Useful for BASIC commands
		String line = "";
		mark();
		while (hasMoreTokens()){
			line += buffer[curPos++];
		}
		// Reset curPos to markPos, just in case it's still needed
		resetToMark();
		return line.trim();
	}

	// Three types of reset are used/needed
	public void reset(){
		// Reset only the pointer
		curPos = 0;
	}

	public void reset(char[] buf){
		// Reset char array and pointer
		buffer = buf;
		curPos = 0;
	}

	public void reset(String in){
		// Reset char array with parsed in string, and pointer
		buffer = in.toCharArray();
		curPos = 0;
	}

	// Who would have thought? A decade and a half, and Java still
	// doesn't have operations you can perform with char arrays
	// Hence the inclusion of isSpace, isDigit, isLetter
	public static boolean isSpace(char c){
		// Check for ' ' (space) or '\t' (tab)
		return ((c == ' ') || (c == '\t'));
	}

	public static boolean isDigit(char c){
		// Checks if char is between '0' and '9'
		return ((c >= '0') && (c <= '9'));
	}

	public static boolean isLetter(char c){
		// Simply checks if the current char is between 'A' and 'Z'
		return ((c>= 'A') && (c <= 'Z'));
	}

	private void eatSpace(){
		// Was part of nextToken, but moved, it's used a fair amount
		while (isSpace(buffer[curPos]) && hasMoreTokens()){
			curPos++;
		}
	}

}
