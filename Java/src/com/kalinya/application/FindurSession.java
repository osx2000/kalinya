package com.kalinya.application;

import com.kalinya.performance.PerformanceFactory;
import com.olf.openrisk.application.Debug;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.table.Table;

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

	/**
	 * Returns an object to assist with debugging
	 */
	public Debug getDebug() {
		if(getSession() != null) {
			return getSession().getDebug();
		}
		throw new IllegalStateException("There is no active Findur session");
	}

	/**
	 * Displays the table contents in a window
	 */
	public void viewTable(Table table) {
		getDebug().viewTable(table);
	}
	
	//TODO: getTradingFactory(), etc
}
