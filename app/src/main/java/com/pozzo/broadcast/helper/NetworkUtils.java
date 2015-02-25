package com.pozzo.broadcast.helper;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Some useful netowrk methods. 
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class NetworkUtils {

	/**
	 * We cannot trust our user, I know they do the best they can, but you know they are good in 
	 * 	not doing what we expect.
	 * 
	 * @param macStr to be checked.
	 * @return false if not a valid mac.
	 */
	public boolean isValidMac(String macStr) {
		macStr = cleanMac(macStr);
		//Mac should have exactly 12 heaxadcimal characters, no more, no less.
		return macStr.length() == 12;
	}

	/**
	 * We make a 'standarization' of our macs, so it got easier to checks and manipulate as we like.
	 */
	public String cleanMac(String macStr) {
		return macStr.toUpperCase(Locale.US).replaceAll("[^0-9A-F]", "");
	}

	/**
	 * @return 9 probably.
	 */
	public static int getDefaultWakePort() {
		return 80;
	}

	/**
	 * Get IP address from first non-localhost interface.
	 * 
	 * @return  address or null
	 * @throws SocketException 
	 */
	public InetAddress getIPAddress() throws SocketException {
		List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
		for (NetworkInterface intf : interfaces) {
			List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
			for (InetAddress addr : addrs) {
				if (!addr.isLoopbackAddress() 
						&& InetAddressUtils.isIPv4Address(addr.getHostAddress())) {
					return addr;
				}
			}
		}
		return null;
	}

	/**
	 * Get the current connected network name.
	 * 
	 * @param context needed to request info.
	 * @return Current connected network SSID or null if not connected.
	 */
	public static String getNetworkSsid(Context context) {
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		String networkSsid = wifiInfo.getSSID();
		//If it is a valid UTF8 name we handle it
		if(networkSsid != null && networkSsid.endsWith("\"") 
				&& networkSsid.startsWith("\"")) {
			networkSsid = networkSsid.substring(1, networkSsid.length()-1);
			return networkSsid;
		}
		return null;
	}

	/**
	 * This method will be useful for sending broadcast message to wake up in a local network.
	 * 
	 * @return Broadcast for the current connected network.
	 * @throws SocketException
	 */
	public InetAddress getMyBroadcast() throws SocketException {
		return getBroadcast(getIPAddress());
	}

	/**
	 * Based on and inetAddres it will get the Broadcast address.
	 * 
	 * @param inetAddr
	 * @return
	 * @throws SocketException
	 */
	public InetAddress getBroadcast(InetAddress inetAddr) throws SocketException {
		if(inetAddr != null) {
			NetworkInterface inter = NetworkInterface.getByInetAddress(inetAddr);
			List<InterfaceAddress> addresses = inter.getInterfaceAddresses();
	
			if(addresses == null || addresses.isEmpty())
				return null;
	
			for(InterfaceAddress it : addresses) {
				if(it.getBroadcast() != null)
					return it.getBroadcast();
			}
		}

		return null;
	}
}
