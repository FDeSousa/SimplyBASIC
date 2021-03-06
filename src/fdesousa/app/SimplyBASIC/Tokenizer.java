/*
 * Tokenizer.java - Implement the Tokenizer.
 *
 * Copyright (c) 2011 Filipe De Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package fdesousa.app.SimplyBASIC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fdesousa.app.SimplyBASIC.framework.Expression;
import fdesousa.app.SimplyBASIC.framework.Function;
import fdesousa.app.SimplyBASIC.framework.Variable;

/**
 * <h1>Tokenizer.java</h1>
 * Separates tokens in an input line into separate Strings depending<br>
 * upon what they represent i.e. Number, Operator, Variable, etc.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class Tokenizer {

	private int curPos; // Marks the current position in the char array
	private int markPos; // Used to mark a position temporarily
	private String t; // The current token that's being worked
	// with
	private char buffer[]; // Holds the characters to analyse, easier to move
	// between chars

	// than in a String, that involves .substring(char position)

	public Tokenizer() {
		curPos = 0;
		markPos = 0;
		t = new String();
	}

	/**
	 * Gets and returns the next token as String. A token can be:
	 * <ul>
	 * 	<li> A letter </li>
	 * 	<li> A digit </li>
	 * 	<li> A literal string of characters </li>
	 * 	<li> A number (w or w/o Negative/Decimal/Exponent) </li>
	 * </ul>
	 * @return String containing token
	 */
	public String next() {
		try {
			// t is the returned String token
			t = new String();
			// Return EOL if curPos is also EOL or greater
			if (curPos >= buffer.length) {
				t = "\n";
			}
			// We don't need no stinking spaces here!

			if (hasNext()){
				eatSpace();
			}

			if (hasNext()){
				// Check what to do with current Character
				switch (buffer[curPos]) {
				// All of [+ - * / ^ = ( )] are parsed immediately and alone
				case '+':
				case '-':
					t += buffer[curPos++];
					if (isDigit(buffer[curPos])){
						return getNumber();
					}
					return t;
				case '*':
				case '/':
				case '^':
				case '=':
				case '(':
				case ')':
				case ',':
					t += buffer[curPos++];
					return t;
					// All of [< > .] may have additional operators/chars
					// If the next char is '=', then token is '<=' or '>='
				case '<':
				case '>':
					t += buffer[curPos++];
					if (buffer[curPos] == '=') {
						t += buffer[curPos++];
					}
					return t;

					// If the next char is a number, token is a decimal number
				case '.':
					if (isDigit(buffer[curPos + 1])) {	// If next char is a digit
						t += '0' + buffer[curPos++];	// Add a leading zero to '.'
						return getNumber();				// And get the whole number
					}
					else {
						t += buffer[curPos++];			// Else, just get the '.'
					}
					return t;

					// Return everything inside and including '"'
				case '"':
					boolean endQuote = false;
					t += buffer[curPos++];
					while (! endQuote & hasNext()){
						if (buffer[curPos] == '"')
							endQuote = true;
						t += buffer[curPos++];
					}
					return t;

				default:
					// Under default, if it's not one of the many conditions above
					// then check if it's a letter or digit, and the operation continues

					// Get the whole sequence of digits and letters, no matter what
					while ((isLetter(buffer[curPos]) || isDigit(buffer[curPos])) & hasNext()) {
						t += buffer[curPos++];
						// A little costly on processor time, doing these checks every
						// time a letter
						// or character is added, but it could minimise mistakes

						// Check if it's a system command, if it is return it immediately
						for (int i = 0; i < Terminal.COMMANDS.length; i++) {
							if (t.equals(Terminal.COMMANDS[i]))
								return t;
						}
						// Check if it's a BASIC command, if it is return it immediately
						for (int i = 0; i < BASICProgram.STATEMENTS.length; i++) {
							if (t.equals(BASICProgram.STATEMENTS[i]))
								return t;
						}
					}

					// Check if this thing is a variable
					if (Variable.isVariable(t)) {
						// It is! So now get the rest of it (if there is a rest of it)
						if (hasNext() && peek(false) == '(') {
							// Right, get the whole of the variable's arguments
							do {
								t += buffer[curPos++];
							} while (buffer[curPos - 1] != ')' & curPos < buffer.length);
						}
						// Either way, return t now, don't check for anything else
						return t;
					}

					// Check if it's a function then
					if (Function.isFunction(t)) {
						// It certainly is! Get the arguments!
						if (hasNext() && peek(false) == '(') {
							// Since it lists arguments, get them!
							do {
								t += buffer[curPos++];
								// Function call can include a reference to one array
								// element
								if (buffer[curPos] == '(') {
									do {
										t += buffer[curPos++];
									} while (buffer[curPos - 1] != ')' & hasNext());
								}
							} while (buffer[curPos - 1] != ')' & hasNext());
						}
						return t;
					}

					// Since it's none of the above, it's likely a number
					if (Expression.isNumber(t)) {
						// It is! We're back in business!
						return getNumber();
					}
					break;
				}
				// Handles the return, no matter what
				return t;
			}
			else {
				return "\n";
			}
		}
		catch (ArrayIndexOutOfBoundsException e){
			if (t.equals("")){
				return "\n";
			}
			else {
				return t;				
			}
		}
	}

	private String getNumber(){
		String num = t;
		try {

			while (isDigit(buffer[curPos]) & hasNext()){
				num += buffer[curPos++];
			}

			// BASIC handles numbers as double/integer, so look for a decimal place
			if (buffer[curPos] == '.'){
				// If this next character is a decimal place, keep getting characters 
				num += buffer[curPos++];
				while (isDigit(buffer[curPos]) & hasNext()){
					// But only get them while they're digits
					num += buffer[curPos++];
				}
			}
			// BASIC handles exponents with the letter E, at which
			// point, number after it
			// is the exponent of the given number i.e. in 111.222E333,
			// 333 is the exponent
			if (buffer[curPos] == 'E'){
				// If this next character is an E for Exponent, get the rest of it
				num += buffer[curPos++];
				if ((buffer[curPos] == '-' || buffer[curPos] == '+' 
					|| isDigit(buffer[curPos])) & hasNext()){

					num += buffer[curPos++];
				}
				while (isDigit(buffer[curPos]) & hasNext()){

					// But only if the rest of it consists of digits
					num += buffer[curPos++];
				}
			}

			return num;
		}
		catch (NumberFormatException e){
			return "\n";
		}
		catch (ArrayIndexOutOfBoundsException e){
			return num;
		}
	}

	// Methods and functions for doing the "admin" stuff below
	/**
	 * Returns true if there is one or more chars in the buffer, 
	 * returns false otherwise
	 */
	public boolean hasNext() {
		// Simple enough. If current position is less than buffer length,
		// returns true
		return (curPos < buffer.length);
	}

	/**
	 * Takes a peek at the next char, before resetting current position
	 * back to the original point. <br> If eatSpaces is true, it skips the
	 * space characters to get the next character from buffer.
	 * @param eatSpaces - if true, it removes spaces before the next char
	 * @return the next char in the buffer
	 */
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

		t = next();
		if (peek(false) == '(') {
			do {
				t += next();
			} while (t.substring(t.length() - 2) != ")");
		}
		return t.trim();
	}

	public String getWholeLine() {
		String line = "";
		mark();
		reset();
		while (hasNext()) {
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
		while (hasNext()) {
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
		try {
			String regexQuoted = "^[\"]{1}(.*)[\"]{1}$";
			Pattern p = Pattern.compile(regexQuoted);
			Matcher m = p.matcher(input);
			// This pattern matches and returns anything within quotation marks
			if (m.find()){
				return m.group(1);
			}
			else {
				return null;
			}
		}
		catch (IllegalStateException e){
			return "";
		}
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
		return ((c >= '0') & (c <= '9'));
	}

	public static boolean isLetter(char c) {
		// Simply checks if the current char is between 'A' and 'Z'
		return ((c >= 'A') & (c <= 'Z'));
	}

	private void eatSpace() {
		// Was part of nextToken, but moved, it's used a fair amount
		while (isSpace(buffer[curPos]) & hasNext()){
			curPos++;
		}
	}
}