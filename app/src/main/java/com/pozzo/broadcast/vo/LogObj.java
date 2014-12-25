package com.pozzo.broadcast.vo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pozzo.broadcast.R;

/**
 * Obj comes from Object if you get any doubt, I put it to not make confusion with the so common
 * 	Log classes.
 * This will represent a log from this specific system.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-23
 */
public class LogObj {
	/**
	 * Actions which should be used on logs.
	 * I am going to use String to make it unlimited and don't care that much about correctness.
	 * Not going to use FK right now, to make it simple for now.... maybe change in the future.
	 * 
	 * @author Luiz Gustavo Pozzo
	 * @since 2014-08-24
	 */
	public static enum Action {
		/**New or edited wake entry.*/
		replaced("replaced", R.string.actionReplaced, R.color.actionReplaced),
		/**Just sent a WOL by any ways.*/
		sent("sent", R.string.actionSent, R.color.actionSent),
		/**Sent a wake entry to trash list.*/
		trashed("trashed", R.string.actionTrashed, R.color.actionTrashed),
		/**Permanently deleted a wake entry.*/
		deleted("deleted", R.string.actionDeleted, R.color.actionDeleted),
		/**Created a home screen widget.*/
		newHomeWidget("newHomeWidget", R.string.actionNewHomeWidget, R.color.actionNewHomeWidget),
		/**Created a lock screen widget.*/
		newLockWidget("newLockWidget", R.string.actionNewLockWidget, R.color.actionNewLockWidget);

		String value;
		int text;
		int color;
		Action(String value, int text, int color) {
			this.value = value;
			this.text = text;
			this.color = color;
		}

		public String getValue() {
			return value;
		}

		public int getTextRes() {
			return text;
		}

		public int getColor() {
			return color;
		}
	}

	/**
	 * Let's limit how you give the how xD.
	 * 
	 * @author Luiz Gustavo Pozzo
	 * @since 2014-08-24
	 */
	public static enum How {
		/**By default interface.*/
		defaul("defaul", R.string.howDefaul, R.color.howDefaul),
		/**Sent a WOL by home widget*/
		widgetHome("widgetHome", R.string.howWidgetHome, R.color.howWidgetHome),
		/**Trigged by network trigger.*/
		trigged("trigged", R.string.howTrigged, R.color.howTrigged);

		String value;
		int text;
		int color;
		How(String value, int text, int color) {
			this.value = value;
			this.text = text;
			this.color = color;
		}

		public String getValue() {
			return value;
		}

		public int getTextRes() {
			return text;
		}

		public int getColor() {
			return color;
		}
	}

	private long id;
	private Action action;
	private How how;
	private Date date;
	private String description;
	private long wakeEntryId;

	{
		//We supply a default date to make log creation simpler.
		date = new Date();
		wakeEntryId = -1;
	}

	public LogObj() {
	}

	/**
	 * @param how does it happened?
	 * @param description to make it more interesting.
	 * @param action See Action inner interface.
	 * @see How
	 * @see Action
	 */
	public LogObj(How how, String description, Action action) {
		this.how = how;
		this.description = description;
		this.action = action;
	}

	/**
	 * @param how does it happened?
	 * @param description to make it more interesting.
	 * @param wakeEntryId Related wake entry to this event.
	 * @param action See Action inner interface. 
	 * @see How
	 * @see Action
	 */
	public LogObj(How how, String description, long wakeEntryId, Action action) {
		this(how, description, action);
		this.wakeEntryId = wakeEntryId;
	}

	/**
	 * @param how does it happened?
	 * @param wakeEntryId Related wake entry to this event.
	 * @param action See Action inner interface. 
	 * @see How
	 * @see Action
	 */
	public LogObj(How how, long wakeEntryId, Action action) {
		this(how, null, wakeEntryId, action);
	}

	/**
	 * How will be set for How.DEFAULT and null description.
	 * 
	 * @param wakeEntryId Related wake entry to this event.
	 * @param action See Action inner interface.
	 * @see How
	 * @see Action
	 */
	public LogObj(long wakeEntryId, Action action) {
		this(How.defaul, wakeEntryId, action);
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public How getHow() {
		return how;
	}
	public void setHow(How how) {
		this.how = how;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setWakeEntryId(long wakeEntryId) {
		this.wakeEntryId = wakeEntryId;
	}
	public long getWakeEntryId() {
		return wakeEntryId;
	}

	@Override
	public String toString() {
		DateFormat format = SimpleDateFormat.getInstance();
		return "[id]"+ id + ",[action]" + action + ",[how]" + how + ",[date]" + format.format(date)
				+ ",[description]" + description + ",[wakeEntryId]" + wakeEntryId;
	}
}
