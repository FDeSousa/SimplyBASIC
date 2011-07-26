package fdesousa.app.SimplyBASIC.Commands;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.framework.Command;
import fdesousa.app.SimplyBASIC.framework.TextIO;

public class New extends Command {
	TextIO textIO;

	public New(Terminal terminal) {
		super(terminal);
		textIO = this.terminal.getTextIO();
	}

	@Override
	public void run() {
		textIO.write("NEW PROGRAM NAME-- ");
		terminal.setBasicProgram(new BASICProgram(terminal, textIO.readLine(), null));
	}
}