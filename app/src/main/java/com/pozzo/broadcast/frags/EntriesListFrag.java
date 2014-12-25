package com.pozzo.broadcast.frags;

import java.io.IOException;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.broadcast.R;
import com.pozzo.broadcast.activity.AddWakeEntryActivity;
import com.pozzo.broadcast.activity.MainActivity;
import com.pozzo.broadcast.adapter.WakeListAdapter;
import com.pozzo.broadcast.business.WakeBusiness;
import com.pozzo.broadcast.database.ConexaoDBManager;
import com.pozzo.broadcast.database.WakeEntryCr;
import com.pozzo.broadcast.exception.InvalidMac;
import com.pozzo.broadcast.helper.NetworkUtils;
import com.pozzo.broadcast.listener.SwipeDismissListViewTouchListener;
import com.pozzo.broadcast.loder.SimpleCursorLoader;
import com.pozzo.broadcast.vo.LogObj;
import com.pozzo.broadcast.vo.LogObj.Action;
import com.pozzo.broadcast.vo.LogObj.How;
import com.pozzo.broadcast.vo.WakeEntry;

/**
 * Shows and manage Entry lists.
 * 
 * @param MainActivity.PARAM_SHOW_DELETEDS to show only deletes.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 * @see WakeListAdapter
 * @see WakeEntry
 */
public class EntriesListFrag extends ListFragment 
		implements OnQueryTextListener, LoaderCallbacks<Cursor> {
	private ConexaoDBManager conexao;
	private SQLiteDatabase loaderDb;
	private boolean showDeleteds;
	private String action;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getLoaderManager().initLoader(1, null, this);

		ListView listEntries = getListView();
		if(!AppWidgetManager.ACTION_APPWIDGET_CONFIGURE.equals(action)) {
			listEntries.setOnItemClickListener(onSendWake);
			listEntries.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listEntries.setMultiChoiceModeListener(multiChoice);
		} else {
			listEntries.setOnItemClickListener(onChioce);
			listEntries.setChoiceMode(ListView.CHOICE_MODE_NONE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.saved_entries_frag, container, false);

		return contentView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		conexao = new ConexaoDBManager(activity);
		action = activity.getIntent().getAction();

		Bundle extras = activity.getIntent().getExtras();
		if(extras != null)
			showDeleteds = extras.getBoolean(MainActivity.PARAM_SHOW_DELETEDS);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//We maintain the connection during our Activity lifecycle.
		conexao.close();
	}

	private MultiChoiceModeListener multiChoice = new MultiChoiceModeListener() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(showDeleteds ? 
	        		R.menu.single_selection_deleted : R.menu.single_selection, menu);
	        return true;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.mDelete:
				deleteCheckedItems(getListView().getCheckedItemIds());
				break;

			case R.id.mEdit:
				//Not supposed to happen when more than one item selected, 
				// but if so, we just pick the first and go on.
				long[] ids = getListView().getCheckedItemIds();
				edit((int) ids[0]);
				break;

			case R.id.mRecover:
				recoverCheckedItems(getListView().getCheckedItemIds());
				break;
			default:
				return false;
			}
			mode.finish();
			return true;
		}
		
		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			//In all new checks we check what to show
			int count = getListView().getCheckedItemCount();
			MenuItem mEdit = mode.getMenu().findItem(R.id.mEdit);
			if(mEdit != null)//if not here, I dont need to hide xD.
				mEdit.setVisible(count == 1);
		}
	};

	/**
	 * Remove all checked items on ListView.
	 */
	private void deleteCheckedItems(long... ids) {
		new WakeBusiness().trash(ids);
		refresh();
	}

	/**
	 * Recover deleted items back to normal lits.
	 */
	private void recoverCheckedItems(long... ids) {
		new WakeBusiness().recover(ids);
		refresh();
	}

	/**
	 * Edit a single item.
	 * 
	 * @param itemId to be edited.
	 */
	private void edit(int itemId) {
		WakeBusiness bus = new WakeBusiness();
		WakeEntry entry = bus.get(itemId);
		Intent intent = new Intent(getActivity(), AddWakeEntryActivity.class);
		intent.putExtra(AddWakeEntryActivity.PARAM_WAKE_ENTRY, entry);
		startActivity(intent);
	}

	/**
	 * Interaction with list.
	 */
	private OnItemClickListener onSendWake = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WakeEntry entry = (WakeEntry) getListAdapter().getItem(position);
			wake(entry);
		}
	};

	/**
	 * Interaction with list.
	 */
	private OnItemClickListener onChioce = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WakeEntry entry = (WakeEntry) getListAdapter().getItem(position);

			//TODO It usually is done at onAttach method, is this acceptable?
			if(!(getActivity() instanceof MainActivity)) {
				IllegalArgumentException bug = new IllegalArgumentException(
							"Widget creationg bug, fragment attached in different activity!");
				BugSenseHandler.sendException(bug);
				throw bug;
			}
			MainActivity activity = (MainActivity) getActivity();
			activity.widgetSelection(entry);
		}
	};

	/**
	 * Ok, User wants to wake something UP, lets do it!
	 */
	private void wake(final WakeEntry entry) {
		new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				try {
					LogObj log = new LogObj(How.defaul, entry.getId(), Action.sent);
					new WakeBusiness().wakeUp(entry, log);
				} catch (IOException e) {
					return R.string.ioSentError;
				} catch (InvalidMac e) {
					return R.string.valuesError;
				}
				return null;
			}

			protected void onPostExecute(Integer result) {
				refresh();
				if(result == null)
					Toast.makeText(getActivity(), getString(R.string.wakeSentTo) + entry.getName(), 
							Toast.LENGTH_LONG).show();
				else
					Toast.makeText(getActivity(), getString(result), Toast.LENGTH_LONG).show();
			}
		}.execute();
	}

	/**
	 * @return The fields to be ordered by direct related to database.
	 */
	private String getOrderBy() {
		String order = "";
		String ssid = NetworkUtils.getNetworkSsid(getActivity());
		if(ssid != null)
			order += WakeEntryCr.TRIGGER_SSID + " = '" + ssid + "' DESC,";

		order += getDefaultOrderBy();
		return order;
	}

	/**
	 * @return Default order we defined.
	 */
	private String getDefaultOrderBy() {
		return WakeEntryCr.LAST_WOL_SENT_DATE + "," +
				WakeEntryCr.WOL_COUNT + "," +
				WakeEntryCr.NAME;
	}

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
	 * Refresh current loader manger.
	 */
	public void refresh() {
		getLoaderManager().restartLoader(1, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new SimpleCursorLoader(getActivity()) {
			@Override
			public Cursor loadInBackground() {
				loaderDb = conexao.getDb();
				String where = WakeEntryCr.DELETED_DATE 
						+ (showDeleteds ? " is not null" : " is null");
				return loaderDb.query(
						WakeEntryCr.TB_NAME, null, where, null, null, null, getOrderBy());
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		final ListView listView = getListView();
		WakeListAdapter adapter = new WakeListAdapter(getActivity(), data, 0);
		adapter.setFilterQueryProvider(filter);
		setListAdapter(adapter);

		SwipeDismissListViewTouchListener touchListener =
            new SwipeDismissListViewTouchListener(listView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return !showDeleteds;//TODO why do you reset the position?
                    }

                    @Override
                    public void onDismiss(ListView listView, final int[] reverseSortedPositions) {
                        new AsyncTask<Void, Void, Void>() {
                        	protected Void doInBackground(Void... params) {
                        		WakeBusiness bus = new WakeBusiness();
                        		WakeListAdapter adapter = (WakeListAdapter) getListAdapter();
                        		for(int it : reverseSortedPositions) {
                        			bus.trash(adapter.getItem(it).getId());
                        		}
                                refresh();
                        		return null;
                        	}

                        	protected void onPostExecute(Void result) {
                        	}
                        }.execute();
                    }
                });

		listView.setOnTouchListener(touchListener);
		listView.setOnScrollListener(touchListener.makeScrollListener());
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		setListAdapter(null);
		loaderDb.close();
	}

	/**
	 * For search purpose.
	 */
	private FilterQueryProvider filter = new FilterQueryProvider() {
		@Override
		public Cursor runQuery(CharSequence constraint) {
			String where = " AND " + WakeEntryCr.DELETED_DATE 
					+ (showDeleteds ? " is not null" : " is null");
			String query = "%" + constraint + "%";
			//I do search for many fields
			return loaderDb.query(WakeEntryCr.TB_NAME, null, "(" + WakeEntryCr.NAME + " like ? OR " 
					+ WakeEntryCr.IP + " like ? OR " + WakeEntryCr.MAC_ADDRESS + " like ? OR " 
					+ WakeEntryCr.TRIGGER_SSID + " like ?) " + where, 
					new String[] {query, query, query, query}, null, null, getOrderBy());
		}
	};
}
