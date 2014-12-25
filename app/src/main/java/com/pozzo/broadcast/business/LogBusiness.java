package com.pozzo.broadcast.business;

import com.pozzo.broadcast.dao.LogDao;
import com.pozzo.broadcast.vo.LogObj;

/**
 * Well, we do have a business for this log =D.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-24
 */
public class LogBusiness {

	/**
	 * Insert a new log to log database.
	 * 
	 * @param log to be inserted.
	 */
	public void insert(LogObj log) {
		//TODO We intend to control log size limit in the future here.
		new LogDao().insert(log);
	}

	/**
	 * Get a persisted log by its id.
	 * 
	 * @param id related to needed log.
	 * @return related to given log or null.
	 */
	public LogObj get(int id) {
		return new LogDao().get(id);
	}

	/**
	 * Completely clears the log list... forever.
	 * Be careful pleas.
	 */
	public void clear() {
		new LogDao().clear();
	}
}
