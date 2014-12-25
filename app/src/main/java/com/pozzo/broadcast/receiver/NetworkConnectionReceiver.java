package com.pozzo.broadcast.receiver;

import java.io.IOException;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.broadcast.business.WakeBusiness;
import com.pozzo.broadcast.exception.InvalidMac;
import com.pozzo.broadcast.helper.NetworkUtils;
import com.pozzo.broadcast.vo.LogObj;
import com.pozzo.broadcast.vo.LogObj.Action;
import com.pozzo.broadcast.vo.LogObj.How;
import com.pozzo.broadcast.vo.WakeEntry;


/**
 * Network Broadcast Receiver, which will tell me when is the right moment to act.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-09-09
 */
public class NetworkConnectionReceiver extends BroadcastReceiver {
	private String networkSsid;

	@Override
	public void onReceive(final Context ctx, final Intent intent) {
		boolean noConnectivity = 
			intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

		networkSsid = NetworkUtils.getNetworkSsid(ctx);
		if(!noConnectivity && networkSsid != null) {
			Thread background = new Thread() {
				public void run() {
					WakeBusiness wakeBus = new WakeBusiness();
					List<WakeEntry> entries = wakeBus.getByTrigger(networkSsid);
					for(WakeEntry it : entries) {
						try {
							LogObj log = new LogObj(
									How.trigged, it.getId(), Action.sent);
							wakeBus.wakeUp(it, log);
						} catch (IOException e) {
							Log.e("IO", it.getName() + " " + e.getMessage());
							BugSenseHandler.sendException(e);
						} catch(InvalidMac e) {
							//We just ignore the error and wish user are going to fix it soon.
						}
					}
				}
			};
			background.setName("AutoWakingUp");
			background.setDaemon(false);
			background.start();
		}
	}

	/**
	 * Active broadcast receiver to receives network broadcast.
	 */
	public static void startListening(boolean enabled, Context context) {
		BootReceiver.startListening(enabled, context);

		ComponentName receiver = new ComponentName(context, NetworkConnectionReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
		        enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED 
		        		: PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		        PackageManager.DONT_KILL_APP);
	}
}
