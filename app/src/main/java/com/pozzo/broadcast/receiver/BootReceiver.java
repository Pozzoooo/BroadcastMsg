package com.pozzo.broadcast.receiver;

import com.pozzo.broadcast.business.WakeBusiness;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Will receive boot broadcast if user has any active trigger, otherwise it should be disabled.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-07-05
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//Business will decide if it really starts or not, but anyway we should not be here if not.
		new WakeBusiness().startNetworkService(context);
	}

	/**
	 * We should not enable if not necessary.
	 * 
	 * @param enabled To be set.
	 * @param context needed to changes.
	 */
	public static void startListening(boolean enabled, Context context) {
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
		        enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED 
		        		: PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		        PackageManager.DONT_KILL_APP);
	}
}
