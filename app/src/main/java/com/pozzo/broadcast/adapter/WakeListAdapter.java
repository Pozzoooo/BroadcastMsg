package com.pozzo.broadcast.adapter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pozzo.broadcast.R;
import com.pozzo.broadcast.database.WakeEntryCr;
import com.pozzo.broadcast.helper.NetworkUtils;
import com.pozzo.broadcast.vo.WakeEntry;

/**
 * How we will show our entry list.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class WakeListAdapter extends CursorAdapter {
	private final String PORT_SEPARATOR = ":";
	private LayoutInflater inflater;
	private String curentNetwork;

	//For date
	private DateFormat formatterDate;
	private DateFormat formatterTime;
	private Date before20;//20 hours before now

	public WakeListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		inflater = LayoutInflater.from(context);
		//TODO refresh when reconnected or disconnected.
		curentNetwork = NetworkUtils.getNetworkSsid(context);

		formatterDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
		formatterTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
		before20 = new Date(System.currentTimeMillis() - (20 * 60 * 60 * 1000));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View line = inflater.inflate(R.layout.wake_entry_row, parent, false);
		return line;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		WakeEntry entry = WakeEntryCr.objectFrom(cursor);

		TextView lAddr = (TextView) view.findViewById(R.id.lAddr);
		TextView lMacAddr = (TextView) view.findViewById(R.id.lMacAddr);
		TextView lName = (TextView) view.findViewById(R.id.lName);
		ImageView iTrigger = (ImageView) view.findViewById(R.id.iTrigger);
		TextView lLastSent = (TextView) view.findViewById(R.id.lLastSent);
		TextView lWolCount = (TextView) view.findViewById(R.id.lWolCount);

		lAddr.setText(entry.getIp() + PORT_SEPARATOR + entry.getPort());
		lMacAddr.setText(entry.getMacAddress());
		lName.setText(entry.getName());
		lWolCount.setText("" + entry.getWolCount());

		//To show date
		Date lastWolSent = entry.getLastWolSentDate();
		if(lastWolSent != null) {
			//We show date for 20 hours before this moment
			if(before20.before(lastWolSent))
				lLastSent.setText(formatterTime.format(lastWolSent));
			else
				lLastSent.setText(formatterDate.format(lastWolSent));
		} else {
			lLastSent.setText("");
		}

		//if has trigger, show icon, if trigger is the same current network show active icon.
		int icon = 0;//This is not the fastest way to do it, but it looks cleaner to me.
		if(entry.getTriggerSsid() != null && !entry.getTriggerSsid().isEmpty()) {
			iTrigger.setVisibility(View.VISIBLE);
			icon = entry.getTriggerSsid().equals(curentNetwork) 
					? R.drawable.ic_action_network_wifi_on : R.drawable.ic_action_network_wifi;
			iTrigger.setImageResource(icon);
		} else {
			iTrigger.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public WakeEntry getItem(int position) {
		Cursor cursor = (Cursor) super.getItem(position);
		if(cursor == null)
			return null;

		return WakeEntryCr.objectFrom(cursor);
	}
}
