package com.pozzo.broadcast.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.CapturedViewProperty;
import android.widget.TextView;

/**
 * Simple implementation for an automatic label.
 * Two ways to set a label:
 * 	1. The first given String will be used as label (if you set more than once).
 * 	2. Give us a String with ':' and we convert the first part as label.
 * 
 * @author Luiz Gustavo Pozzo
 */
public class LabeldTextView extends TextView {
	private static final String LABEL_SEPARATOR = ": ";
	private int labelIdx;

	public LabeldTextView(Context context) {
		super(context);
	}
	public LabeldTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public LabeldTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Special set text for auto label creation.
	 * 
	 * @see super{@link #setText(CharSequence, BufferType)}
	 */
	@Override
	public void setText(CharSequence text, BufferType type) {
		//Do we already setted a label?
		if(labelIdx <= 0) {
			//Does the string have a 'dynamic' string?
			int hasLabel = text.toString().indexOf(':');
			labelIdx = hasLabel > 0 ? hasLabel : text.length();
		} else if(text.length() > 0) {
			//We add label to the current text if it is already set
			text = getText() + LABEL_SEPARATOR + text;
		}

		super.setText(text, type);
	}

	/**
	 * Normal getText will return full text, including label.
	 * That is necessary for correct show on layout (I am not intending to 
	 * 	remade the entire component).
	 * 
	 * @return Full text, with label.
	 * @see #getTextNoLabel()
	 */
	@Override
	@CapturedViewProperty
	public CharSequence getText() {
		return super.getText();
	}

	/**
	 * @return Return the text without the label.
	 */
	public CharSequence getTextNoLabel() {
		CharSequence text = super.getText();
		//We sum the separator length to remove non-desired helpers ^^
		return text.subSequence(
				labelIdx + LABEL_SEPARATOR.length(), text.length());
	}
}
