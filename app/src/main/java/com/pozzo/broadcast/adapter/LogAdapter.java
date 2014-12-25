package com.pozzo.broadcast.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pozzo.broadcast.R;
import com.pozzo.broadcast.database.LogCr;
import com.pozzo.broadcast.database.WakeEntryCr;
import com.pozzo.broadcast.vo.LogObj;

/**
 * Adapter to show log rows.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-09-07
 */
public class LogAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	private DateFormat format;
	private Resources resources;

	public LogAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		inflater = LayoutInflater.from(context);
		format = SimpleDateFormat.getInstance();
		resources = context.getResources();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View line = inflater.inflate(R.layout.log_row, parent, false);
		return line;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		LogObj log = LogCr.objectFrom(cursor);
		//We do expect to receive this name on it
		int nameIdx = cursor.getColumnIndex(WakeEntryCr.NAME);
		String wakeEntryName = nameIdx >=0 ? cursor.getString(nameIdx) : null;

		TextView lAction = (TextView) view.findViewById(R.id.lAction);
		TextView lHow = (TextView) view.findViewById(R.id.lHow);
		TextView lDescription = (TextView) view.findViewById(R.id.lDescription);
		TextView lDate = (TextView) view.findViewById(R.id.lDate);
		TextView lWakeEntryName = (TextView) view.findViewById(R.id.lWakeEntryName);

		//TODO Does this deserves a LruCache?
		lAction.setText(resources.getString(log.getAction().getTextRes()));
		lAction.setTextColor(resources.getColor(log.getAction().getColor()));
		lHow.setText(resources.getString(log.getHow().getTextRes()));
		lHow.setTextColor(resources.getColor(log.getHow().getColor()));
		lDescription.setText(log.getDescription());
		lDate.setText(format.format(log.getDate()));
		lWakeEntryName.setText(wakeEntryName);
	}

	@Override
	public LogObj getItem(int position) {
		Cursor cursor = (Cursor) super.getItem(position);
		if(cursor == null)
			return null;

		return LogCr.objectFrom(cursor);
	}
}
