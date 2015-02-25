package com.pozzo.broadcast.database;

import android.content.ContentValues;

/**
 * To persist and control widgets and which BroadMessage they gonna use.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-10
 */
public class WidgetControlCr {
	public static final String TB_NAME = "widgetControl";

	public static final String _ID = "_id";
	public static final String WIDGET_ID = "widget_id";
	public static final String WAKE_ENTRY = "wake_entry";

	/**
	 * Create Table SQL.
	 * //TODO Create FK and handle deletions (deletes widget)
	 */
	public static final String TB_CREATE = "create table " + TB_NAME + "( " +
			_ID + " integer primary key autoincrement, " +
			WIDGET_ID + " integer not null, " +
			WAKE_ENTRY + " integer not null);";

	/**
	 * Chose for not creating VO for this object because I saw a cleaner approach like this,
	 *  but I still creating here the ContentValue to maintain the pattern.
	 * 
	 * @return Which should be persisted at widgetControl table.
	 */
	public static ContentValues getContentValues(int widgetId, long wakeEntryId) {
		ContentValues values = new ContentValues();

		values.put(WIDGET_ID, widgetId);
		values.put(WAKE_ENTRY, wakeEntryId);

		return values;
	}
}
