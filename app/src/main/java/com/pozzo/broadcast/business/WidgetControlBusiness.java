package com.pozzo.broadcast.business;

import java.util.List;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.broadcast.dao.WidgetControlDao;
import com.pozzo.broadcast.vo.BroadMessage;
import com.pozzo.broadcast.vo.LogObj;
import com.pozzo.broadcast.vo.LogObj.Action;
import com.pozzo.broadcast.vo.LogObj.How;

/**
 * Business logic for Widget control.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-10
 */
public class WidgetControlBusiness {

	/**
	 * Insert a new widget with its related entries ids.
	 * 
	 * @param widgetId added.
	 * @param wakeEntryId related to the given widget.
	 */
	public void insert(int widgetId, BroadMessage...wakeEntries) {
		long[] ids = new long[wakeEntries.length];
		for(int i=0; i<wakeEntries.length; ++i)
			ids[i] = wakeEntries[i].getId();

		new WidgetControlDao().insert(widgetId, ids);

		//And than log it pleas.
		for(BroadMessage it : wakeEntries)
			new LogBusiness().insert(//I do save widget id as description, just for reference.
					new LogObj(How.widgetHome, ""+widgetId, it.getId(), Action.newHomeWidget));
	}

	/**
	 * To remove from our widget database.
	 * 
	 * @param widgetId to be removed.
	 * @return number of rows removed.
	 */
	public int delete(int ...widgetId) {
		if(widgetId == null || widgetId.length == 0) {
			//Handling only for acknowledge behavior
			BugSenseHandler.sendException(new RuntimeException("Sent 0 or null: " + widgetId));
			return 0;//Deleting nothing?
		}
		return new WidgetControlDao().delete(widgetId);
	}

	/**
	 * Delete all rows on WidgetControl table.
	 * 
	 * @return number of removed entries.
	 */
	public int deleteAll() {
		return new WidgetControlDao().deleteAll();
	}

	/**
	 * Get all wakeEntries ids attached to the given widget.
	 * 
	 * @param widgetId related.
	 * @return BroadMessage ids.
	 */
	public List<Long> getWakeEntriesFromWidget(int widgetId) {
		return new WidgetControlDao().getWakeEntriesFromWidget(widgetId);
	}
}
