package com.pozzo.broadcast.database;

import com.pozzo.broadcast.App;
import com.pozzo.broadcast.helper.SqliteHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * It will help us manage our connection.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class ConexaoDBManager {
	private static SQLiteDatabase db;
	private Context context;

	public ConexaoDBManager(Context context) {
		this.context = context;
	}

	public ConexaoDBManager() {
		this(App.getAppContext());//Google, tell me a better way pleas
	}

	/**
	 * @return A new connection if non created yet, or an existing and maybe already in use one.
	 * 
	 * TODO This looks like an wasy point for leaking resource or context, needs approach review.
	 */
	public SQLiteDatabase getDb() {
		if(db == null || !db.isOpen())
			db = new SqliteHelper(context).getWritableDatabase();
		return db;
	}

	/**
	 * It's done!
	 */
	public void close() {
		db.close();
	}
}
