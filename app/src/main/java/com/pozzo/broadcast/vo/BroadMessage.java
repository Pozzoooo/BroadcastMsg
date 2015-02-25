package com.pozzo.broadcast.vo;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * What a user will save to send on network.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class BroadMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String macAddress;
	private String ip;
	private int port;
	private String triggerSsid;
	private Date deletedDate;
	private Date lastWolSentDate;
	private int wolCount;
	private String message;

	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setTriggerSsid(String triggerSsid) {
		this.triggerSsid = triggerSsid;
	}
	public String getTriggerSsid() {
		return triggerSsid;
	}
	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}
	public Date getDeletedDate() {
		return deletedDate;
	}
	public void setLastWolSentDate(Date lastWolSentDate) {
		this.lastWolSentDate = lastWolSentDate;
	}
	public Date getLastWolSentDate() {
		return lastWolSentDate;
	}
	public void setWolCount(int wolCount) {
		this.wolCount = wolCount;
	}
	public int getWolCount() {
		return wolCount;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return new value.
	 */
	public int increasCount() {
		return ++wolCount;
	}
}
