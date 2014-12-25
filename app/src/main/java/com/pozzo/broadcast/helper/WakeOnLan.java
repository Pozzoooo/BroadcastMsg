package com.pozzo.broadcast.helper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Locale;

import com.pozzo.broadcast.exception.InvalidMac;

/**
 * Here the magic happens, this is why our app exists.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class WakeOnLan {

	/**
	 * HEYYYY!!!! WAKE!!
	 * 
	 * @param addres Maybe a broadcast or a public address to redirect.
	 * @param macStr Machine's Mac address. (the one which will wake up).
	 * @throws IOException Ops.
	 */
	public void wakeUp(final String addres, String macStr, int port) 
			throws IOException, InvalidMac {
		//Firstly I ignore any non desirable character, like : and -.
		macStr = macStr.toUpperCase(Locale.US);
		macStr = macStr.replaceAll("[^0-9A-F]", "");

		//Some times I prefer the 'clear' hard-code than a small for.
		try {
			int i=0;
			final byte[] mac = new byte[] {
					(byte) Integer.parseInt(macStr.substring(i, i+=2), 16), 
					(byte) Integer.parseInt(macStr.substring(i, i+=2), 16), 
					(byte) Integer.parseInt(macStr.substring(i, i+=2), 16), 
					(byte) Integer.parseInt(macStr.substring(i, i+=2), 16), 
					(byte) Integer.parseInt(macStr.substring(i, i+=2), 16), 
					(byte) Integer.parseInt(macStr.substring(i, i+=2), 16)
					};

			wakeUp(mac, addres, port);
		} catch (StringIndexOutOfBoundsException e) {
			//We convert the error to make it more clear.
			throw new InvalidMac();
		}
	}

	/**
	 * Magic package built.
	 * 
	 * @throws IOException
	 */
	public void wakeUp(byte[] mac, String ip, int port) throws IOException {
        DatagramSocket client = new DatagramSocket();
        client.connect(InetAddress.getByName(ip), port);

        // WOL packet contains a 6-bytes trailer and 
        //	16 times a 6-bytes sequence containing the MAC address.
        byte[] packet = new byte[17 * 6];

        // Trailer of 6 times 0xFF.
        for (int i = 0; i < 6; i++)
            packet[i] = (byte) 0xFF;

        // Body of magic packet contains 16 times the MAC address.
        for (int i = 1; i <= 16; i++)
            for (int j = 0; j < 6; j++)
                packet[i * 6 + j] = mac[j];

        // Submit WOL packet.
        DatagramPacket dPacket = new DatagramPacket(packet, packet.length);
        client.send(dPacket);
        client.close();
    }
}
