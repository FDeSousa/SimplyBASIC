/*
 * SimplyBASIC.java - Implement the onCreate method.
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

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

/**
 * <h1>SimplyBASIC.java</h1>
 * Called when the application is loaded, off-loads onto the<br>
 * CommandInterpreter class instance.
 * @version 0.1
 * @author Filipe De Sousa
 */
public class SimplyBASIC extends Activity {
	EditText et;
	Terminal terminal;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//	Odd set of errors with R.layout.main here, must investigate

		// Declare and initialise variables used class-wide
		et = (EditText)findViewById(R.id.etMain);
		terminal = new Terminal(this, et);
		
		terminal.run();
	}
	
	public void end() {
		this.finish();
	}
}