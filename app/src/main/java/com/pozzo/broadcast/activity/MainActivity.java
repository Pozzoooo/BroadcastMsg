package com.pozzo.broadcast.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.broadcast.R;
import com.pozzo.broadcast.business.MessageBusiness;
import com.pozzo.broadcast.business.WidgetControlBusiness;
import com.pozzo.broadcast.database.WakeEntryCr;
import com.pozzo.broadcast.frags.EntriesListFrag;
import com.pozzo.broadcast.frags.HelpDialog;
import com.pozzo.broadcast.receiver.GongWidget;
import com.pozzo.broadcast.vo.BroadMessage;

/**
 * Well, this is our Main Activity =D.
 * Our flow is quite simples, user creates an entry, it is saved on Sqlite and showed here, user 
 * 	can interact by editing and using each entry.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class MainActivity extends Activity implements OnQueryTextListener {
	public static final String PARAM_SHOW_DELETEDS = WakeEntryCr.DELETED_DATE;
	private static final int REQ_ADD = 0x1;
	private static final int REQ_DEL = 0x2;

	private boolean showDeleteds;

	private EntriesListFrag entryListFrag;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		entryListFrag = (EntriesListFrag)
				getFragmentManager().findFragmentById(R.id.fragEntryList);

		checkEmptiness();

		new MessageBusiness().startNetworkService(this);

		Bundle extras = getIntent().getExtras();
		if(extras != null)
			showDeleteds = extras.getBoolean(MainActivity.PARAM_SHOW_DELETEDS);

		if(showDeleteds) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		//Hide what should be only visible on main one.
		String action = getIntent().getAction();
		boolean visibility = !showDeleteds 
				&& !AppWidgetManager.ACTION_APPWIDGET_CONFIGURE.equals(action);
		menu.findItem(R.id.add).setVisible(visibility);
		menu.findItem(R.id.showDeletedList).setVisible(visibility);

        searchView = (SearchView) menu.findItem(R.id.mActionSearch).getActionView();
        searchView.setQueryHint("");
        searchView.setOnQueryTextListener(this);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * User wants to add a new Entry.
	 */
	public void onAdd(MenuItem item) {
		Intent intent = new Intent(this, AddWakeEntryActivity.class);
		startActivityForResult(intent, REQ_ADD);
	}

	/**
	 * User is claiming for help, lets help him!
	 */
	public void onHelp(MenuItem item) {
		HelpDialog dialog = HelpDialog.newInstance(getString(R.string.helpMsg));
		dialog.show(getFragmentManager(), "help");
	}

	/**
	 * User wants to see deleted entries.
	 */
	public void onShowDeletedList(MenuItem item) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(PARAM_SHOW_DELETEDS, true);
		startActivityForResult(intent, REQ_DEL);
	}

	/**
	 * Event to show the log list.
	 */
	public void onShowLogList(MenuItem item) {
		Intent intent = new Intent(this, LogListActivity.class);
		startActivity(intent);
	}

	public void onSort(MenuItem item) {
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ADD:
			boolean result = resultCode == RESULT_OK;
			if(result)//Refresh if something new.
				entryListFrag.refresh();
			break;
		case REQ_DEL:
			//it was going to be quite dirt code to better control this
			//and I guess user in most of the time was going to recover 
			//something, so I let it to always refresh.
			entryListFrag.refresh();
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	/**
	 * Check if we have some entry, if not, we try to make it easy to user.
	 */
	private void checkEmptiness() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					SharedPreferences pref = getSharedPreferences("configs", MODE_PRIVATE);
					if(pref.getBoolean("firstTimeMainActivity", true)) {
						if(new MessageBusiness().isEmpty()) {
							return true;
						}
					}
				} catch(SQLiteDatabaseLockedException e) {
					//Looks like something specific is happening on some device
					BugSenseHandler.sendException(e);//Let's keep tracking
				}
				return false;
			}

			protected void onPostExecute(Boolean result) {
				if(result)
					onAdd(null);
			}
		}.execute();
	}

	/**
	 * This is a specific method for widget creation handle.
	 * 
	 * @param entry to be wakened on widget event
	 */
	public void widgetSelection(BroadMessage...entry) {
		/*
		 * This approach actually give me a little of doubt, testing on my Nexus 4 with Android 4.4
		 *  I could run it all on main thread, but it don't seems the correct approach for a 
		 *  database operation, but also, it get little weird because I need it to be done exactly 
		 *  now to give no delays for user choice.
		 */
		new AsyncTask<BroadMessage, Void, BroadMessage>() {
			private int appWidgetId;

			@Override
			protected void onPreExecute() {
				//Get given widget ID
				Bundle extras = getIntent().getExtras();
				appWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			}

			@Override
			protected BroadMessage doInBackground(BroadMessage... entry) {
				//We insert widget relations for later use on widget event 
				new WidgetControlBusiness().insert(appWidgetId, entry);
				return entry[0];
			}

			protected void onPostExecute(BroadMessage entry) {
				//Refresh widget
				GongWidget.updateWidget(appWidgetId, entry, MainActivity.this);

				//Return success
				Intent result = new Intent();
				result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				setResult(RESULT_OK, result);
				finish();
			}
		}.execute(entry);
	}

    @Override
    public boolean onSearchRequested() {
    	if(searchView != null) {
    		searchView.setIconified(false);
    		searchView.requestFocus();
    	}
    	return super.onSearchRequested();
    }

	@Override
	public boolean onQueryTextSubmit(String query) {
		return entryListFrag.onQueryTextSubmit(query);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return entryListFrag.onQueryTextChange(newText);
	}
}
