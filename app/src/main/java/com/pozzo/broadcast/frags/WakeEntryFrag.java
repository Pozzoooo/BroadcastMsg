package com.pozzo.broadcast.frags;

import java.net.InetAddress;
import java.net.SocketException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.broadcast.R;
import com.pozzo.broadcast.helper.NetworkUtils;
import com.pozzo.broadcast.vo.BroadMessage;

/**
 * Fragment which should register a new Mac to our database. 
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class WakeEntryFrag extends Fragment {
	private BroadMessage entry;
	private NetworkUtils utils;

	private EditText eMac;
	private EditText eIp;
	private EditText ePort;
	private EditText eName;
	private EditText eTrigger;
	private EditText eMessage;
	private ViewGroup vgAdvancedSettings;

	//Pre-configs
	private boolean showAdvanced;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		loadPreferences(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		utils = new NetworkUtils();

		if(savedInstanceState != null)
			restoreInstance(savedInstanceState);
	}

	@Override
	public void setArguments(Bundle args) {
		//I am quite unsure about this approach
		super.setArguments(args);
		restoreInstance(args);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.wake_entry_frag, container, false);

		eMac = (EditText) contentView.findViewById(R.id.eMac);
		eIp = (EditText) contentView.findViewById(R.id.eIp);
		ePort = (EditText) contentView.findViewById(R.id.ePort);
		eName = (EditText) contentView.findViewById(R.id.eName);
		eTrigger = (EditText) contentView.findViewById(R.id.eTriggerSsid);
		eMessage = (EditText) contentView.findViewById(R.id.eMessage);
		vgAdvancedSettings = (ViewGroup) contentView.findViewById(R.id.vgAdvancedSettings);

		ImageButton bHelpTrigger = (ImageButton) contentView.findViewById(R.id.bHelpTrigger);
		ImageButton bSsid = (ImageButton) contentView.findViewById(R.id.bSsid);
		CheckBox cbShowAdavancedSettings =
				(CheckBox) contentView.findViewById(R.id.cbShowAdavancedSettings);

		fillLayout();

		showAdvanced(showAdvanced);
		cbShowAdavancedSettings.setChecked(showAdvanced);

		eMac.setOnFocusChangeListener(onMacDone);

		bHelpTrigger.setOnClickListener(onHelpTrigger);
		bSsid.setOnClickListener(onGetSsid);
		cbShowAdavancedSettings.setOnCheckedChangeListener(onShowAdavancedSettings);

		return contentView;
	}

	@Override
	public void onStop() {
		super.onStop();
		savePreferences(getActivity());
	}

	/**
	 * We are going to use some preferences to create a more personalized interface to user.
	 *
	 * @param context to get preferences.
	 */
	private void loadPreferences(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(
				context.getString(R.string.pref_addScreen), Context.MODE_PRIVATE);

		showAdvanced = prefs.getBoolean(context.getString(R.string.key_showAdvanced), false);
	}

	/**
	 * Apply chagens to preferences.
	 * TODO Any sophisticated way to not apply when there is no change, without flag or verification.
	 *
	 * @param context to set preferences.
	 */
	private void savePreferences(Context context) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				context.getString(R.string.pref_addScreen), Context.MODE_PRIVATE).edit();

		prefs.putBoolean(context.getString(R.string.key_showAdvanced), showAdvanced);

		prefs.apply();
	}

	private CompoundButton.OnCheckedChangeListener onShowAdavancedSettings =
			new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			showAdvanced(isChecked);
		}
	};

	/**
	 * It does not change checkbox.
	 *
	 * @param show the advanced settings.
	 */
	private void showAdvanced(boolean show) {
		showAdvanced = show;
		vgAdvancedSettings.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	/**
	 * To help user understand it.
	 */
	private OnClickListener onHelpTrigger = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			HelpDialog dialog = HelpDialog.newInstance(getString(R.string.helpTrigger));
			dialog.show(getFragmentManager(), "help");
		}
	};

	/**
	 * Go get current network ssid used.
	 */
	private OnClickListener onGetSsid = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String ssid = NetworkUtils.getNetworkSsid(getActivity());
			if(ssid == null || ssid.length() < 1) {
				Toast.makeText(getActivity(), R.string.cantGetSsid, Toast.LENGTH_LONG).show();
			} else
				eTrigger.setText(ssid);
		}
	};

	/**
	 * @return The entry.
	 */
	public BroadMessage getWakeEntry() {
		updateObj();
		return entry;
	}

	/**
	 * Updates entry object based on user input.
	 */
	private void updateObj() {
		if(entry == null)
			entry = new BroadMessage();

		entry.setMacAddress(eMac.getText().toString());
		entry.setIp(eIp.getText().toString());
		entry.setName(eName.getText().toString());
		entry.setTriggerSsid(eTrigger.getText().toString());
		entry.setMessage(eMessage.getText().toString());

		try {
			entry.setPort(Integer.parseInt(ePort.getText().toString()));
		} catch(NumberFormatException e) {
			//If user insist to remove port, we let it default as 9
			entry.setPort(NetworkUtils.getDefaultWakePort());
		}
	}

	private Drawable defBackground;
	/**
	 * We need a valid Mac pleas.
	 */
	private OnFocusChangeListener onMacDone = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(!hasFocus) {
				validateMac();
			}
		}
	};

	/**
	 * Interface check valid mac.
	 */
	public boolean validateMac() {
		if (!utils.isValidMac(eMac.getText().toString())) {
			defBackground = eMac.getBackground();
			eMac.setBackgroundResource(R.drawable.apptheme_edit_text_holo_light);
			Toast toast = Toast.makeText(
					getActivity(), R.string.macNotValid, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 50);
			toast.show();
			return true;
		} else {
			eMac.setBackground(defBackground);
			return false;
		}
	}

	/**
	 * Builder.
	 */
	public static WakeEntryFrag newWakeEntryFrag(BroadMessage entry) {
		WakeEntryFrag instance = new WakeEntryFrag();

		Bundle state = new Bundle();
		state.putSerializable("entry", entry);
		instance.setArguments(state);

		return instance;
	}

	/**
	 * Center restore state.
	 */
	public void restoreInstance(Bundle state) {
		entry = (BroadMessage) state.getSerializable("entry");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("entry", entry);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Show user current infos.
	 */
	private void fillLayout() {
		if(entry == null) {
			entry = new BroadMessage();
			entry.setPort(NetworkUtils.getDefaultWakePort());
			try {
				InetAddress myBroad = utils.getMyBroadcast();
				if(myBroad != null)
					entry.setIp(myBroad.getHostAddress());
			} catch(final SocketException e) {
				//We ignore, and user can fill manually
				BugSenseHandler.sendException(e);
			}
		}

		eMac.setText(entry.getMacAddress());
		eName.setText(entry.getName());
		ePort.setText("" + entry.getPort());
		eIp.setText(entry.getIp());
		eTrigger.setText(entry.getTriggerSsid());
		eMessage.setText(entry.getMessage());
	}
}
