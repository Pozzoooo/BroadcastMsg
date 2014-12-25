package com.pozzo.broadcast.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pozzo.broadcast.database.ConexaoDBManager;
import com.pozzo.broadcast.database.LogCr;
import com.pozzo.broadcast.vo.LogObj;

/**
 * Log persistence.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-23
 */
public class LogDao {

	/**
	 * Insert a new log to log database.
	 * It does not control entries limits.
	 * It will not replace an existing entry.
	 * 
	 * @param log to be inserted.
	 */
	public void insert(LogObj log) {
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		db.insert(LogCr.TB_NAME, null, LogCr.getContentValues(log));
	}

	/**
	 * Go get a specific log by its id.
	 * 
	 * @param id related to the required log.
	 * @return log related to id or null if not found.
	 */
	public LogObj get(int id) {
		LogObj log = null;
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		Cursor cursor = db.query(LogCr.TB_NAME, null, LogCr._ID + "=?", new String[] {""+id}, 
				null, null, null);

		if(cursor.moveToNext()) {
			log = LogCr.objectFrom(cursor);
		}

		return log;
	}

	/**
	 * Completely clears the log list... forever.
	 */
	public void clear() {
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		db.delete(LogCr.TB_NAME, null, null);
	}
}
