package com.kalinya.application;

import com.kalinya.performance.PerformanceFactory;
import com.olf.openrisk.application.Session;

public final class FindurSession {
	Session session;
	PerformanceFactory performanceFactory;
	
	public FindurSession() {
	}
	
	public FindurSession(Session session) {
		this.session = session;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<" + this.getClass().getSimpleName() + ">");
		if(getSession() != null) {
			sb.append("Session [" + getSession().toString() + "]");
		}
		return sb.toString();
	}
	
	public Session getSession() {
		return session;
	}
	
	public PerformanceFactory getPerformanceFactory() {
		if(performanceFactory == null) {
			this.performanceFactory = new PerformanceFactory(this);
		}
		return performanceFactory;
	}
	
	//TODO: getTradingFactory(), etc
}
