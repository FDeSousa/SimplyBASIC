package fdesousa.app.SimplyBASIC.Commands;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.Tokenizer;
import fdesousa.app.SimplyBASIC.framework.Command;
import fdesousa.app.SimplyBASIC.framework.FileIO;
import fdesousa.app.SimplyBASIC.framework.TextIO;

public class Save extends Command {
	Tokenizer tokenizer;
	TextIO textIO;
	FileIO fileIO;
	BASICProgram program;

	public Save(Terminal terminal) {
		super(terminal);
		textIO = this.terminal.getTextIO();
		fileIO = this.terminal.getFileIO();
		program = this.terminal.getBasicProgram();
	}

	@Override
	public void run() {
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new OutputStreamWriter(fileIO.writeFile(program.getProgName())));
			//	BASICProgram.list() returns a String containing the full program listing,
			//+	so we can just write that out in one method call, rather than looping
			out.write(program.list());
		} catch (IOException e) {
			textIO.writeLine("ERROR WRITING TO FILE");
		} finally {
			try {
				//	Close the output stream in finally to make sure it's always closed
				if (out != null)
					out.close();
			} catch (IOException e) {
				//	Be naughty and silently ignore
			}
		}
	}
}