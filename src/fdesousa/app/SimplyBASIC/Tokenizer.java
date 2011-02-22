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
	private int prevPos = 0;	// Marks the position in the char array of the last token
	private int markPos = 0;	// Used to mark a position temporarily
	private char buffer[];		// Holds the characters to analyse, easier to move between chars
	// than in a String, that involves .substring(char position)

	public Tokenizer() {
		// Constructor doesn't really need initialisation of anything for
		// the moment as it's used more than once
	}

	public String nextToken(){
		// token is the returned String
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
			break;
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
			break;

			// If the next char is a number, token is a decimal number
		case '.':
			token += buffer[curPos];
			curPos++;
			while (isDigit(buffer[curPos])){
				token += buffer[curPos];
				curPos++;
			}
			break;

			// Eat space, then check if the next char is a Letter
		case ',':
			token += buffer[curPos];
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
			while (buffer[curPos] == '"'){
				token += buffer[curPos++];
				// Let's see if this works, and then I can extend its use
				// it's legal, but frowned upon, but curPos++ should only be
				// incremented AFTER buffer[curPos] value has been found.
				//curPos++;
			}
			break;

		default:
			// Under default, if it's not one of the many conditions above
			// then check if it's a letter or digit, and the operation continues
			
			if (isLetter(buffer[curPos])){
				// If it's a letter, begin the hunt for a named variable or command
				token += buffer[curPos++];
				//curPos++;
				// Check what this next char is.
				// If it's a letter, assume a command, loop to find the rest of it
				if (isLetter(buffer[curPos])){
					while (isLetter(buffer[curPos])){
						token += buffer[curPos++];
						//curPos++;
					}
				}
				// If it's a digit, assume a variable name with number (i.e. A1)
				else if (isDigit(buffer[curPos])){
					token += buffer[curPos++];
					//curPos++;
				}
				// Just in case, added a break here. You never know, it might save lives
				break;
			}
			else if (isDigit(buffer[curPos])){
				// If it's a digit, begin looking for the rest of the number
				while (isDigit(buffer[curPos]) || buffer[curPos] == '.'){
					// While it's a digit or a decimal-point, add it to token
					token += buffer[curPos++];
					//curPos++;
				}
			}
			
			break;
		}

		// Handles the return, no matter what
		return token;
	}

	// Methods and functions for doing the admin stuff
	public boolean hasMoreTokens(){
		// Simple enough. If current position is less than buffer length, returns true
		return (curPos < buffer.length);
	}

	public void mark(){
		// Set mark to current position for reparsing
		markPos = curPos;
	}

	public void resetToMark(){
		// Reset current position to mark position
		curPos = markPos;
	}
	
	public String getRestOfLine(){
		// Send back the rest of the line, minus what's already been sent
		// Useful for BASIC commands
		String line = null;
		mark();
		while (hasMoreTokens()){
			line += buffer[curPos++];
		}
		// Reset curPos to markPos, just in case it's still needed
		resetToMark();
		return line;
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
	private boolean isSpace(char c){
		// Check for ' ' (space) or '\t' (tab)
		return ((c == ' ') || (c == '\t'));
	}

	private boolean isDigit(char c){
		// Checks if char is between '0' and '9'
		return ((c >= '0') && (c <= '9'));
	}

	private boolean isLetter(char c){
		// Simply checks if the current char is between 'A' and 'Z'
		return ((c>= 'A') && (c <= 'Z'));
	}

	private void eatSpace(){
		// Was part of nextToken, but moved, it's used a fair amount
		while (isSpace(buffer[curPos])){
			curPos++;
		}
	}

}
