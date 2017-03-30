package com.kalinya.stubs;

public class Session {

	private String sessionName;

	//Disable default ctor
	@SuppressWarnings("unused")
	private Session() {
	}
	
	public Session(String sessionName) {
		this.sessionName = sessionName;
	}
	
	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() + "> SessionName [" + getSessionName() + "]";
	}
	
	public String getSessionName() {
		return sessionName;
	}
}
