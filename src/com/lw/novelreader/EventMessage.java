package com.lw.novelreader;

public class EventMessage {
 
	public static final int START_READ = 1;
	
	public int msgId;
	
	public int arg;
	
	public Object obj;
	
	public EventMessage(int msg,int arg) {
		this.msgId = msg;
		this.arg = arg;
	}
	
	public EventMessage(int msg,int arg,Object obj) {
		this.msgId = msg;
		this.arg = arg;
		this.obj = obj;
	}
}
