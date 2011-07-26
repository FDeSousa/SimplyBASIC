package fdesousa.app.SimplyBASIC.Commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import fdesousa.app.SimplyBASIC.BASICProgram;
import fdesousa.app.SimplyBASIC.Terminal;
import fdesousa.app.SimplyBASIC.Tokenizer;
import fdesousa.app.SimplyBASIC.framework.Command;
import fdesousa.app.SimplyBASIC.framework.FileIO;
import fdesousa.app.SimplyBASIC.framework.TextIO;

public class Old extends Command {
	Tokenizer tokenizer;
	TextIO textIO;
	FileIO fileIO;
	BASICProgram program;
	private Map<Integer, String> codeListing;

	public Old(Terminal terminal) {
		super(terminal);
		tokenizer = this.terminal.getTokenizer();
		textIO = this.terminal.getTextIO();
		fileIO = this.terminal.getFileIO();
		this.codeListing = new TreeMap<Integer, String>();
	}

	@Override
	public void run() {
		BufferedReader in = null;
		String line;
		String filename;
		textIO.write("OLD PROGRAM NAME-- ");
		filename = textIO.readLine();

		try {
			//	Many grouped statements here, but basically, get an instance of InputStream
			//+	from FileIO, to create new InputStreamReader instance, parsed to new instance of
			//+	BufferedReader called 'in', with the file name entered by the user
			in = new BufferedReader(new InputStreamReader(fileIO.readFile(filename)));
			//	From here, we read line-by-line until a line is returned as null
			while ((line = in.readLine()) != null) {
				tokenizer.reset(line);
				codeListing.put(Integer.valueOf(tokenizer.next()).intValue(), tokenizer.getRestOfLine());
			}
		} catch (IOException e) {
			textIO.writeLine("ERROR READING FILE");
		} finally {
			try {
				//	Close the input stream in finally to make sure it's always closed
				if (in != null)
					in.close();
			} catch (IOException e) {
				//	Be naughty and silently ignore
			}
		}
		//	Run the constructor, sending the new instance of BASICProgram to terminal for storage
		terminal.setBasicProgram(new BASICProgram(terminal, filename, codeListing));
	}
}