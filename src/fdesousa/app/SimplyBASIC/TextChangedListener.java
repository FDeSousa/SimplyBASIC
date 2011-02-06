package fdesousa.app.SimplyBASIC;

import java.util.Iterator;

import android.text.Editable;
import android.text.TextWatcher;

public class TextChangedListener implements TextWatcher {

	String prevText, curText, matchedValue, value;
	int longestMatch, savedPos;

	public void afterTextChanged(Editable s){

		String curText = s.toString();

		if(prevText.length() < curText.length()
				&& curText.startsWith(prevText)) {

			prevText = curText;

			//						longestMatch = 0; 
			//						matchedValue = null; 
			//						Iterator<String> it = s.;
			//							s.completions.iterator(); 
			//						
			//						while(it.hasNext()) { 
			//							String value = it.next(); 
			//
			//			if(value.startsWith(curText)) { 
			//				int valueLen = value.length(); 
			//
			//				if(valueLen > longestMatch) { 
			//					longestMatch = valueLen; 
			//					matchedValue = value; 
			//				} 
			//			} 
		}

		//		if(null != matchedValue) { 
		//			savedPos = this.getSelectionStart(); 
		//			unRegisterListener(); 
		//			this.setText(matchedValue); 
		//			registerListener(); 
		//			if(-1 != savedPos) { 
		//				if(savedPos &gt; longestMatch) { 
		//					savedPos = longestMatch; 
		//				} 
		//				this.setSelection(savedPos, longestMatch); 
		//			}	 
		//		} 
		//
		//	} 
		//	prevText = curText;

	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after){}

	public void onTextChanged(CharSequence s, int start, int before, int count){}
}
