package fdesousa.app.SimplyBASIC.framework;

import fdesousa.app.SimplyBASIC.Terminal;

public abstract class Command {
	protected final Terminal terminal;
	
	/**
	 * Constructor method for this super-class only takes an instance
	 * of Terminal as each command may vary in their requirement for a
	 * BASICProgram/TextIO/FileIO/etc. instance
	 * @param terminal an instance of Terminal
	 */
	public Command(Terminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * The main portions of each command will be run with this method
	 */
	public abstract void run();
}
