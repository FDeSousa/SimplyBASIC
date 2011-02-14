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
	
	private int curPos = 0, prevPos = 0, markPos = 0;
	private char buffer[];
	
	public Tokenizer() {
		// Doesn't really need initialization of anything for the moment
	}
	
	public String nextToken(){
		// token is returned
		String token = "";
		// Return EOL if curPos is also EOL or greater
		if (curPos >= buffer.length){
			return "\n";
		}
		// Save position
		prevPos = curPos;
		// We don't need no stinkin' spaces here!
		eatSpace();
		
		// Check what to do with current Character
		switch (buffer[curPos]){
		// All of [+ - * / ^ = ( )] are parsed immediately
		case '+':
		case '-':
		case '*':
		case '/':
		case '^':
		case '=':
		case '(':
		case ')':
			token += buffer[curPos];
			curPos++;
			return token;
		// All of [< > . ,] may have additional operators/chars
		// If the next char is '=', then token is '<=' or '>='
		case '<':
		case '>':
			token += buffer[curPos];
			curPos++;
			if(buffer[curPos + 1] == '='){
				token += buffer[curPos + 1];
				curPos++;
			}
			return token;
			
		// If the next char is a number, token is a decimal number
		case '.':
			token += buffer[curPos];
			curPos++;
			while (isDigit(buffer[curPos])){
				token += buffer[curPos];
				curPos++;
			}
			return token;
			
		// Eat space, then check if the next char is a Letter
		case ',':
			token += buffer[curPos];
			/* Will be ignoring this for now, a comma is just a comma, nothing else
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
			return token;
		
			
			
		default:
			break;
		}
			
		// Just to shut the IDE up for a bit, default to returning empty token
		return token;
	}
	
	// Who would have thought? A decade and a half, and Java still
	// doesn't have operations you can perform with char arrays
	// Hence the inclusion of isSpace, isDigit, isLetter
	private boolean isSpace(char c){
		return ((c == ' ') || (c == '\t'));
	}
	
	private boolean isDigit(char c){
		return ((c >= '0') && (c <= '9'));
	}
	
	private boolean isLetter(char c){
		return ((c>= 'A') && (c <= 'Z'));
	}
	
	private void eatSpace(){
		// Was part of nextToken, but moved, it's used a fair amount
		while (isSpace(buffer[curPos])){
			curPos++;
		}
	}
	
}
