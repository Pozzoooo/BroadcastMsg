package com.pozzo.broadcast.receiver;

import java.io.IOException;
import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.pozzo.broadcast.R;
import com.pozzo.broadcast.business.WakeBusiness;
import com.pozzo.broadcast.business.WidgetControlBusiness;
import com.pozzo.broadcast.exception.InvalidMac;
import com.pozzo.broadcast.vo.LogObj;
import com.pozzo.broadcast.vo.WakeEntry;
import com.pozzo.broadcast.vo.LogObj.Action;
import com.pozzo.broadcast.vo.LogObj.How;

/**
 * This is our widget provider, all widgets events and updates should be handled here.
 * 
 * @see xml/gong_widget.xml
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-06
 */
public class GongWidget extends AppWidgetProvider {
	private static final String ACTION = "com.pozzo.wakeonlan.WIDGET_ACTION";
	private static final String ID = "id";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		WidgetControlBusiness bus = new WidgetControlBusiness();
		WakeBusiness wakeBus = new WakeBusiness();

		for (int it : appWidgetIds) {
			WakeEntry entry = null;
			List<Long> entriesIds = bus.getWakeEntriesFromWidget(it);
			if(!entriesIds.isEmpty()) {
				//We take only the first one for naming purpouse
				entry = wakeBus.get(entriesIds.get(0));
			}

			updateWidget(it, entry, context);
		}
	}

	/**
	 * Centered widget updater.
	 * 
	 * @param widgetId Id from widget to be updated.
	 * @param entry attached to the widget.
	 * @param context Current context.
	 */
	public static void updateWidget(int widgetId, WakeEntry entry, Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.gong_widget);
		Intent intent = new Intent(context, GongWidget.class);
		intent.setAction(GongWidget.ACTION);
		intent.putExtra(GongWidget.ID, widgetId);
		PendingIntent pendingIntent = 
			PendingIntent.getBroadcast(context, widgetId, intent, 0);
		if(entry != null)
			remoteView.setTextViewText(R.id.lTitle, entry.getName());
		remoteView.setOnClickPendingIntent(R.id.bWidget, pendingIntent);
		appWidgetManager.updateAppWidget(widgetId, remoteView);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION)) {
			int widgetId = intent.getExtras().getInt(ID);
			List<Long> wakeEntryIds = 
				new WidgetControlBusiness().getWakeEntriesFromWidget(widgetId);
			new Wake(context).execute(wakeEntryIds.toArray(new Long[0]));
		} else {
			super.onReceive(context, intent);
		}
	}

	/**
	 * Executes for us in background posting success updates.
	 */
	class Wake extends AsyncTask<Long, String, String> {
		private Context context;

		/**
		 * @param context is used for feedback.
		 */
		private Wake(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(Long... params) {
			try {
				WakeBusiness wakeBus = new WakeBusiness();
				WakeEntry entry;
				LogObj log;
				for(Long it : params) {
					entry = wakeBus.get(it);
					log = new LogObj(How.widgetHome, it.longValue(), Action.sent);
					wakeBus.wakeUp(entry, log);
					publishProgress(entry.getName());
				}
			} catch (IOException e) {
				return context.getString(R.string.ioSentError);
			} catch (InvalidMac e) {
				return context.getString(R.string.valuesError);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			Toast.makeText(context, context.getString(R.string.wakeSentTo) + values[0], 
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(String result) {
			if(result != null)
				Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		new WidgetControlBusiness().delete(appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		//We clear our database to make sure we have no leak
		new WidgetControlBusiness().deleteAll();
	}
}
