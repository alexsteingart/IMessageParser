package com.ams.imessageparser;

import java.util.Map;
import java.util.TreeMap;

public class Message {
	
	private String messageId;
	private String fromNumber;
	private String fromName;
	private Map<String,String> to = new TreeMap<String,String>();
	private String body;
	private Long receivedTime;
	private String conversationId;
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getFromNumber() {
		return fromNumber;
	}
	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public Map<String, String> getTo() {
		return to;
	}
	public void setTo(Map<String, String> to) {
		this.to = to;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public void addTo(String number, String name){
		to.put(number,name);
	}
	public void setReceivedTime(Long receivedTime) {
		this.receivedTime = receivedTime;
	}
	public Long getReceivedTime(){
		return receivedTime;
		
		
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	
	
}