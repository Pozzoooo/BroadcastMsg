package com.pozzo.broadcast.database;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

import com.pozzo.broadcast.vo.LogObj;
import com.pozzo.broadcast.vo.LogObj.Action;
import com.pozzo.broadcast.vo.LogObj.How;

/**
 * We will log actions on our application, to get a better knowledge of what is happening and when 
 * 	is happening, so we can pass by making bad things and improving the app =].
 * This database should not accept updates, cause it makes no sense for logging.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-23
 */
public class LogCr {
	public static final String TB_NAME = "log";

	/**Just to have a control.*/
	public static final String _ID = "_id";
	/**What happened.*/
	public static final String ACTION = "action";
	/**How does it happen, from which way?*/
	public static final String HOW = "how";
	/**When it happened.*/
	public static final String DATE = "date";
	/**What is this, can you explain a little more?*/
	public static final String DESCRIPTION = "description";
	/**Loose FK for wakeEntry when applicable, I choose for a loose one cause of deletion.*/
	public static final String FK_WAKE_ENTRY = "fk_wake_entry";

	/**
	 * Create Table SQL.
	 */
	public static final String TB_CREATE = "create table " + TB_NAME + "( " +
			_ID + " integer primary key autoincrement, " +
			ACTION + " varchar not null, " +
			HOW + " varchar, " +
			DATE + " bigint not null, " +
			DESCRIPTION + " varchar, " +
			FK_WAKE_ENTRY + " integer " +
			");";

	/**
	 * @return ContentValues for given log.
	 */
	public static ContentValues getContentValues(LogObj log) {
		ContentValues values = new ContentValues();

		values.put(ACTION, log.getAction().getValue());
		values.put(HOW, log.getHow().getValue());
		values.put(DATE, log.getDate().getTime());
		values.put(DESCRIPTION, log.getDescription());
		values.put(FK_WAKE_ENTRY, log.getWakeEntryId());

		return values;
	}

	/**
	 * @param cursor containing the log object.
	 * @return The log contained in the given cursor.
	 */
	public static LogObj objectFrom(Cursor cursor) {
		LogObj log = new LogObj();

		log.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
		log.setAction(Action.valueOf(cursor.getString(cursor.getColumnIndex(ACTION))));
		log.setHow(How.valueOf(cursor.getString(cursor.getColumnIndex(HOW))));
		log.setDate(new Date(cursor.getLong(cursor.getColumnIndex(DATE))));
		log.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
		log.setWakeEntryId(cursor.getInt(cursor.getColumnIndex(FK_WAKE_ENTRY)));

		return log;
	}
}
