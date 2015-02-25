package com.pozzo.broadcast.helper;

import com.pozzo.broadcast.vo.BroadMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by ghost on 04/02/15.
 */
public class MessageSender {

	/**
	 * Submite given message.
	 *
	 * @param message to be sent.
	 * @throws IOException Something wrong with network.
	 */
	public void send(BroadMessage message) throws IOException {
		DatagramSocket client = new DatagramSocket();
		client.connect(InetAddress.getByName(message.getIp()), message.getPort());

		String msg = message.getMessage();
		DatagramPacket dPacket = new DatagramPacket(msg.getBytes(), msg.length());
		client.send(dPacket);
		client.close();
	}
}
