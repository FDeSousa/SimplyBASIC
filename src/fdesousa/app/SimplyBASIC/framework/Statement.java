package fdesousa.app.SimplyBASIC.framework;

import fdesousa.app.SimplyBASIC.Terminal;

public abstract class Statement {
	protected final Terminal terminal;
	
	public Statement(Terminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * The main portions of each statement will be run with this method
	 */
	public abstract void doSt();
}
