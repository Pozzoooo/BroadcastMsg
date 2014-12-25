package com.pozzo.broadcast.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.pozzo.broadcast.R;
import com.pozzo.broadcast.business.WakeBusiness;
import com.pozzo.broadcast.frags.WakeEntryFrag;
import com.pozzo.broadcast.helper.ItemMenuHelper;
import com.pozzo.broadcast.vo.WakeEntry;

/**
 * Where our lovely User will input a new Entry.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class AddWakeEntryActivity extends Activity {
	public static final String PARAM_WAKE_ENTRY = "paramWakeEntry";

	private WakeEntryFrag wakeFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_wake_entry_activity);

		WakeEntry entry = null;
		Bundle extras = getIntent().getExtras();
		if(extras != null)
			entry = (WakeEntry) extras.getSerializable(PARAM_WAKE_ENTRY);

		wakeFrag = WakeEntryFrag.newWakeEntryFrag(entry);
		getFragmentManager().beginTransaction().add(R.id.fragment_container, wakeFrag).commit();

		ItemMenuHelper.setDoneDiscard(getActionBar(), this);
	}

	/**
	 * Discard input.
	 */
	public void onDiscard(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}

	/**
	 * Confirm input.
	 */
	public void onDone(View v) {
		//We validate, but let user keep saving.
		wakeFrag.validateMac();

		WakeEntry entry = wakeFrag.getWakeEntry();
		new WakeBusiness().replace(entry, this);

		setResult(RESULT_OK);
		finish();
	}
}
