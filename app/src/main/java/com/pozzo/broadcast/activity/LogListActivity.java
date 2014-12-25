package com.pozzo.broadcast.activity;

import com.pozzo.broadcast.R;
import com.pozzo.broadcast.business.LogBusiness;
import com.pozzo.broadcast.frags.LogListFrag;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

/**
 * Will hold log fragments.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-07-09
 */
public class LogListActivity extends Activity implements OnQueryTextListener {
	private LogListFrag logListFrag;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_activity);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		logListFrag = (LogListFrag) getFragmentManager().findFragmentById(R.id.fragLogList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log_list, menu);

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
	 * Users wants to clear log list.
	 */
	public void onClear(MenuItem menu) {
		new LogBusiness().clear();
		logListFrag.refresh();
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
		return logListFrag.onQueryTextSubmit(query);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return logListFrag.onQueryTextChange(newText);
	}
}
