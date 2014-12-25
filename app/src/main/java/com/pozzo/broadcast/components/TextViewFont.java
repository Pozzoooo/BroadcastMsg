package com.pozzo.broadcast.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pozzo.broadcast.R;

/**
 * A default TextView, but easy to change font style.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class TextViewFont extends TextView {

	public TextViewFont(Context context) {
		super(context);
	}

	public TextViewFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAttributes(context, attrs);
	}

	public TextViewFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setAttributes(context, attrs);
	}

	private void setAttributes(Context ctx, AttributeSet attrs) {
		if(isInEditMode())
			return;

		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.textViewFont);
		String customFont = a.getString(R.styleable.textViewFont_font);
		setCustomFont(ctx, customFont);
		a.recycle();
	}

	public boolean setCustomFont(Context ctx, String asset) {
		Typeface tf = null;
		tf = Typeface.createFromAsset(ctx.getAssets(), asset);  

		setTypeface(tf);
		return true;
	}
}
