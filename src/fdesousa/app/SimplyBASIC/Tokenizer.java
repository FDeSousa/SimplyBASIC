package fdesousa.app.SimplyBASIC;

import java.util.regex.Pattern;

public class Tokenizer {

	private int curPos = 0; // Marks the current position in the char array
	private int markPos = 0; // Used to mark a position temporarily
	private String t = new String(); // The current token that's being worked
										// with
	private char buffer[]; // Holds the characters to analyse, easier to move
							// between chars

	// than in a String, that involves .substring(char position)

	public Tokenizer() {
		// Constructor doesn't really need initialisation of anything for
		// the moment as it's used more than once
	}

	public String nextToken() {
		// t is the returned String token
		t = "";
		// Return EOL if curPos is also EOL or greater
		if (curPos >= buffer.length) {
			return "\n";
		}
		// We don't need no stinkin' spaces here!
		eatSpace();

		// Check what to do with current Character
		switch (buffer[curPos]) {
		// All of [+ - * / ^ = ( )] are parsed immediately and alone
		case '+':
		case '-':
		case '*':
		case '/':
		case '^':
		case '=':
		case '(':
		case ')':
			t += buffer[curPos++];
			break;
		// All of [< > . ,] may have additional operators/chars
		// If the next char is '=', then token is '<=' or '>='
		case '<':
		case '>':
			t += buffer[curPos];
			curPos++;
			if (peek(false) == '=') {
				t += buffer[curPos++];
			}
			break;

		// If the next char is a number, token is a decimal number
		case '.':
			t += buffer[curPos];
			curPos++;
			while (isDigit(buffer[curPos]) && hasMoreTokens()) {
				t += buffer[curPos++];
			}
			break;

		// Eat space, then check if the next char is a Letter
		case ',':
			t += buffer[curPos++];
			break;

		// Return everything inside double quotes "
		case '"':
			t += buffer[curPos++];
			while (buffer[curPos] != '"' && hasMoreTokens()) {
				t += buffer[curPos++];
			}
			break;

		default:
			// Under default, if it's not one of the many conditions above
			// then check if it's a letter or digit, and the operation continues

			// Get the whole sequence of digits and letters, no matter what
			while (isLetter(buffer[curPos]) || isDigit(buffer[curPos])) {
				t += buffer[curPos++];
				// A little costly on processor time, doing these checks every
				// time a letter
				// or character is added, but it could minimise mistakes

				// Check if it's a standard BASIC function, if it is return it
				// immediately
				for (int i = 0; i < Function.functions.length; i++) {
					if (t.contains(Function.functions[i]))
						return t;
				}
				// Check if it's a system command, if it is return it
				// immediately
				for (int i = 0; i < CommandInterpreter.commands.length; i++) {
					if (t.contains(CommandInterpreter.commands[i]))
						return t;
				}
				// Check if it's a BASIC command, if it is return it immediately
				for (int i = 0; i < Statement.statements.length; i++) {
					if (t.contains(Statement.statements[i]))
						return t;
				}
			}

			// Check if this thing is a variable
			if (Variable.isVariable(t)) {
				// It is! So now get the rest of it (if there is a rest of it)
				if (peek(false) == '(') {
					// Right, get the whole of the variable's arguments
					do {
						t += buffer[curPos++];
					} while (buffer[curPos - 1] != ')');
				}
				// Either way, return t now, don't check for anything else
				return t;
			}

			// Check if it's a function then
			if (Function.isFunction(t)) {
				// It certainly is! Get the arguments!
				if (peek(false) == '(') {
					// Since it lists arguments, get them!
					do {
						t += buffer[curPos++];
						// Function call can include a reference to one array
						// element
						if (buffer[curPos] == '(') {
							do {
								t += buffer[curPos++];
							} while (buffer[curPos - 1] != ')');
						}
					} while (buffer[curPos - 1] != ')');
				}
				return t;
			}

			// Since it's none of the above, it's likely a number
			if (Expression.isNumber(t)) {
				if (buffer[curPos] == '.') {
					t += buffer[curPos++];
					while (isDigit(buffer[curPos])) {
						t += buffer[curPos++];
					}
					// BASIC handles exponents with the letter E, at which
					// point, number after it
					// is the exponent of the given number i.e. in 111.222E333,
					// 333 is the exponent
					if (buffer[curPos] == 'E') {
						t += buffer[curPos++];
						while (isDigit(buffer[curPos])) {
							t += buffer[curPos++];
						}
					}
				}
				return t;
			}
			break;
		}
		// Handles the return, no matter what
		return t;
	}

	// Methods and functions for doing the admin stuff
	public boolean hasMoreTokens() {
		// Simple enough. If current position is less than buffer length,
		// returns true
		return (curPos < buffer.length);
	}

	public char peek(boolean eatSpaces) {
		char c;
		mark();
		if (eatSpaces == true) {
			eatSpace();
		}
		c = buffer[curPos];
		resetToMark();
		return c;
	}

	public void mark() {
		// Set mark to current position for reparsing
		markPos = curPos;
	}

	public void resetToMark() {
		// Reset current position to mark position
		curPos = markPos;
	}

	/**
	 * Get a String containing a variable as a token
	 * 
	 * @return String containing variable and arguments
	 */
	public String getVariable() {
		t = "";

		t = nextToken();
		if (peek(false) == '(') {
			do {
				t += nextToken();
			} while (t.substring(t.length() - 2) != ")");
		}
		return t.trim();
	}

	public static double[] separateNumber(String inputNumber) {
		// Odd one to explain, but this should separate the main section
		// of a given number (input as a String) from its exponent (if it has
		// one)
		// and return the main number in separated[0] and exponent in
		// separated[1]
		double[] separated = { 0.0, 0.0 };
		String number = null, exponent = null;
		int i = 0;

		while (isDigit(inputNumber.charAt(i)) && i < inputNumber.length()) {
			number += inputNumber.charAt(i++);
		}
		if (inputNumber.charAt(i) == 'E') {
			i++;
			while (isDigit(inputNumber.charAt(i)) && i < inputNumber.length()) {
				exponent += inputNumber.charAt(i++);
			}
		}
		separated[0] = Double.parseDouble(number);
		separated[1] = Double.parseDouble(exponent);
		return separated;
	}

	public String getWholeLine() {
		String line = "";
		mark();
		reset();
		while (hasMoreTokens()) {
			line += buffer[curPos++];
		}
		resetToMark();
		return line.trim();
	}

	public String getRestOfLine() {
		// Send back the rest of the line, minus what's already been sent
		// Useful for BASIC commands
		String line = "";
		mark();
		while (hasMoreTokens()) {
			line += buffer[curPos++];
		}
		// Reset curPos to markPos, just in case it's still needed
		resetToMark();
		return line.trim();
	}

	// Three types of reset are used/needed
	public void reset() {
		// Reset only the pointer
		curPos = 0;
	}

	public void reset(char[] buf) {
		// Reset char array and pointer
		buffer = buf;
		curPos = 0;
	}

	public void reset(String in) {
		// Reset char array with parsed in string, and pointer
		buffer = in.toCharArray();
		curPos = 0;
	}

	public static String removeQuotes(String input) {
		Pattern p = Pattern.compile("^[\"](\\s*.*\\s*)[\"]$");
		// This pattern matches and returns anything within quotation marks
		String[] args = p.split(input);
		return args[1];
	}

	// Who would have thought? A decade and a half, and Java still
	// doesn't have operations you can perform with char arrays
	// Hence the inclusion of isSpace, isDigit, isLetter
	public static boolean isSpace(char c) {
		// Check for ' ' (space) or '\t' (tab)
		return ((c == ' ') || (c == '\t'));
	}

	public static boolean isDigit(char c) {
		// Checks if char is between '0' and '9'
		return ((c >= '0') && (c <= '9'));
	}

	public static boolean isLetter(char c) {
		// Simply checks if the current char is between 'A' and 'Z'
		return ((c >= 'A') && (c <= 'Z'));
	}

	private void eatSpace() {
		// Was part of nextToken, but moved, it's used a fair amount
		while (isSpace(buffer[curPos]) && hasMoreTokens()) {
			curPos++;
		}
	}
}