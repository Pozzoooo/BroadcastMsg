package com.pozzo.broadcast.helper;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.broadcast.database.LogCr;
import com.pozzo.broadcast.database.WakeEntryCr;
import com.pozzo.broadcast.database.WidgetControlCr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Sqlite Helper...............
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class SqliteHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 5;
	private static final String DB_NAME = "db.db";

	public SqliteHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	/**
	 * System called.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(WakeEntryCr.TB_CREATE);
			db.execSQL(WidgetControlCr.TB_CREATE);
			db.execSQL(LogCr.TB_CREATE);
		} catch(RuntimeException e) {
			//Some devices seems to call it more than once, I need to check why!
			BugSenseHandler.sendException(e);
		}
	}

	/**
	 * System called.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN " 
					+ WakeEntryCr.DELETED_DATE + " bigint;");
		case 2:
			db.execSQL(WidgetControlCr.TB_CREATE);
		case 3:
			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN " 
					+ WakeEntryCr.LAST_WOL_SENT_DATE + " bigint;");
			db.execSQL(LogCr.TB_CREATE);
		case 4:
			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN " 
					+ WakeEntryCr.WOL_COUNT + " integer;");
		default:
			break;
		}
	}
}
