package com.pozzo.broadcast.frags;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pozzo.broadcast.R;

/**
 * Will help user with some extra informations.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-04
 */
public class HelpDialog extends DialogFragment {

	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.help);

		String msg = getArguments().getString("msg");

		View contentView = inflater.inflate(R.layout.help_frag, container, false);
		Button bClose = (Button) contentView.findViewById(R.id.bClose);
		TextView lMsg = (TextView) contentView.findViewById(R.id.lMsg);
		TextView lLink = (TextView) contentView.findViewById(R.id.lLink);
		lMsg.setText(msg);
		lLink.setMovementMethod(LinkMovementMethod.getInstance());

		bClose.setOnClickListener(onClose);
		return contentView;
	}

	private OnClickListener onClose = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

	/**
	 * Following the rules for future needs.
	 * 
	 * @msg To be shown on help pop up.
	 */
	public static HelpDialog newInstance(String msg) {
		HelpDialog instance = new HelpDialog();
		Bundle args = new Bundle();
		args.putString("msg", msg);
		instance.setArguments(args);
		return instance;
	}
}
