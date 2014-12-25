package com.pozzo.broadcast.frags;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

import com.pozzo.broadcast.adapter.LogAdapter;
import com.pozzo.broadcast.database.ConexaoDBManager;
import com.pozzo.broadcast.database.LogCr;
import com.pozzo.broadcast.database.WakeEntryCr;
import com.pozzo.broadcast.loder.SimpleCursorLoader;
import com.pozzo.broadcast.vo.LogObj;

/**
 * This fragment will show all logged events.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-09-07
 */
public class LogListFrag extends ListFragment 
		implements OnQueryTextListener, LoaderCallbacks<Cursor> {
	private ConexaoDBManager conexao;
	private SQLiteDatabase loaderDb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//We use the default layout.
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getLoaderManager().initLoader(1, null, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		conexao = new ConexaoDBManager(activity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		conexao.close();
	}

	/**
	 * Loder inplementation
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new SimpleCursorLoader(getActivity()) {
			@Override
			public Cursor loadInBackground() {
				loaderDb = conexao.getDb();
				return loaderDb.rawQuery(getQuery(null), null);
			}
		};
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//We shoe the description if user touches a line
		LogObj obj = (LogObj) getListAdapter().getItem(position);
		if(obj.getDescription() != null)
			Toast.makeText(getActivity(), obj.getDescription(), Toast.LENGTH_LONG).show();
	}

	/**
	 * Made this one to make it easier for search implemetation.
	 * 
	 * @param where Or null if not used.
	 * @return which should be used for quering.
	 */
	private String getQuery(String where) {
		//Easy
		if(where == null)
			where = "";
		else
			where = " WHERE " + where;

		/* SELECT log.*, wakeEntry.name FROM log 
		 * 	LEFT JOIN wakeEntry ON log.fk_wake_entry = wakeEntry._id
		 *  ORDER BY log._id DESC
		 *  
		 *  Well, I know it looks dirty, but it is a pay back to make all reusable.
		 */
		return "SELECT " + LogCr.TB_NAME + ".*, " 
				+ WakeEntryCr.TB_NAME + "." + WakeEntryCr.NAME 
				+ " FROM " + LogCr.TB_NAME + " LEFT JOIN " + WakeEntryCr.TB_NAME 
				+ " ON " + LogCr.TB_NAME + "." + LogCr.FK_WAKE_ENTRY + "="
					+ WakeEntryCr.TB_NAME + "." + WakeEntryCr._ID
				+ where
				+ " ORDER BY " + LogCr.TB_NAME + "." + LogCr._ID + " DESC";
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		LogAdapter adapter = new LogAdapter(getActivity(), data, 0);
		adapter.setFilterQueryProvider(filter);
		setListAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		setListAdapter(null);
		loaderDb.close();
	}

	/**
	 * Refresh current loader manger.
	 */
	public void refresh() {
		getLoaderManager().restartLoader(1, null, this);
	}

	/**
	 * Search implementation
	 */
	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		((Filterable) getListAdapter()).getFilter().filter(newText);
		return true;
	}

	/**
	 * For the search field.
	 */
	private FilterQueryProvider filter = new FilterQueryProvider() {
		@Override
		public Cursor runQuery(CharSequence constraint) {
			String query = "%" + constraint.toString() + "%";
			return loaderDb.rawQuery(getQuery(LogCr.HOW + " like ? OR " 
					+ LogCr.ACTION + " like ? OR " + LogCr.DESCRIPTION + " like ? OR " 
					+ WakeEntryCr.TB_NAME + "." + WakeEntryCr.NAME + " like ?"), 
					new String[] {query, query, query, query});
		}
	};
}
