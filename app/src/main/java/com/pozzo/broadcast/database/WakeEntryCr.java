package com.pozzo.broadcast.database;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

import com.pozzo.broadcast.vo.WakeEntry;

/**
 * We kind of represents our object table here, and make some helpers for manipulation.
 * 
 * @author Luiz Gustavo Pozoz
 * @since 2014-05-03
 */
public class WakeEntryCr {
	public static final String TB_NAME = "wakeEntry";

	public static final String _ID = "_id";
	public static final String MAC_ADDRESS = "mac";
	public static final String NAME = "name";
	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String TRIGGER_SSID = "trigger";
	public static final String DELETED_DATE = "deleted_date";
	//I was in doubt about FK, denormalization or just querying when needed.
	public static final String LAST_WOL_SENT_DATE = "last_wol_sent_date";
	//it will count the number of wol sented
	public static final String WOL_COUNT = "wol_count";

	/**
	 * Create Table SQL.
	 */
	public static final String TB_CREATE = "create table " + TB_NAME + "( " +
			_ID + " integer primary key autoincrement, " +
			MAC_ADDRESS + " varchar unique not null, " +
			NAME + " varchar, " + 	
			IP + " varchar not null, " +
			PORT + " integer not null, " +
			TRIGGER_SSID + " varchar, " +
			DELETED_DATE + " bigint, " +
			LAST_WOL_SENT_DATE + " bigint, " +
			WOL_COUNT + " integer" +
		");";

	/**
	 * @return ContentValues for given entry.
	 */
	public static ContentValues getContentValues(WakeEntry entry) {
		ContentValues values = new ContentValues();

		if(entry.getId() != 0)
			values.put(_ID, entry.getId());
		values.put(MAC_ADDRESS, entry.getMacAddress());
		values.put(NAME, entry.getName());
		values.put(IP, entry.getIp());
		values.put(PORT, entry.getPort());
		values.put(TRIGGER_SSID, entry.getTriggerSsid());
		values.put(WOL_COUNT, entry.getWolCount());

		//Special handle for deleted date.
		if(entry.getDeletedDate() != null)
			values.put(DELETED_DATE, entry.getDeletedDate().getTime());
		else//If recycled I set it null.
			values.putNull(DELETED_DATE);

		//Only updates last sent when defined.
		if(entry.getLastWolSentDate() != null)
			values.put(LAST_WOL_SENT_DATE, entry.getLastWolSentDate().getTime());

		return values;
	}

	/**
	 * @return Object contained on current position of given cursor.
	 */
	public static WakeEntry objectFrom(Cursor cursor) {
		WakeEntry entry = new WakeEntry();

		entry.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
		entry.setMacAddress(cursor.getString(cursor.getColumnIndex(MAC_ADDRESS)));
		entry.setName(cursor.getString(cursor.getColumnIndex(NAME)));
		entry.setIp(cursor.getString(cursor.getColumnIndex(IP)));
		entry.setPort(cursor.getInt(cursor.getColumnIndex(PORT)));
		entry.setTriggerSsid(cursor.getString(cursor.getColumnIndex(TRIGGER_SSID)));
		entry.setWolCount(cursor.getInt(cursor.getColumnIndex(WOL_COUNT)));

		//Special handle for nullable fields.
		int idx = cursor.getColumnIndex(DELETED_DATE);
		if(!cursor.isNull(idx))
			entry.setDeletedDate(new Date(cursor.getLong(idx)));

		idx = cursor.getColumnIndex(LAST_WOL_SENT_DATE);
		if(!cursor.isNull(idx))
			entry.setLastWolSentDate(new Date(cursor.getLong(idx)));

		return entry;
	}
}
