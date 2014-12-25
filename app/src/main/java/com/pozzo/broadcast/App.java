package com.pozzo.broadcast;

import android.app.Application;

/**
 * This is our APP!
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class App extends Application {
	private static App appContext;

	@Override
	public void onCreate() {
		super.onCreate();
//		BugSenseHandler.initAndStartSession(this, "22c71818");

		appContext = this;
	}

	/**
	 * Not My favorite approach, but I want to make it clean, and our app is simple based on 
	 * 	Activities, so it is not suppose to be a problem.
	 */
	public static App getAppContext() {
		return appContext;
	}
}
