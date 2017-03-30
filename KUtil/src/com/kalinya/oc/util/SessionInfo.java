package com.kalinya.oc.util;

import java.util.concurrent.TimeUnit;

import com.kalinya.util.PluginUtil;
import com.olf.openjvs.OException;
import com.olf.openjvs.SystemUtil;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.internal.OpenRiskException;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.utility.Disposable;

public class SessionInfo implements Disposable {
	private Session session;
	private Table sessionInfoTable = null;
	private Table runSiteStatus;
	@SuppressWarnings("unused")
	private SessionInfo() {
		throw new IllegalArgumentException("Use ctor SessionInfo(Session session)");
	}
	public SessionInfo(Session session) {
		this.session = session;
	}
	public Table getAllProcessInfoAsTable() {
		getSessionInfoTable();
		return sessionInfoTable;
	}
	public Table getRunsiteStatusAsTable() {
		getRunSiteStatus();
		return runSiteStatus;
	}
	private void getRunSiteStatus() {
		if(runSiteStatus == null) {
			try {
				runSiteStatus = session.getTableFactory().fromOpenJvs(
						com.olf.openjvs.Services.runsiteGetStatusAll());
			} catch (OException e) {
				throw new OpenRiskException(e);
			}
		}
	}
	private void getSessionInfoTable() {
		if(sessionInfoTable == null) {
			try {
				sessionInfoTable = session.getTableFactory().fromOpenJvs(
						SystemUtil.getSessionTable("*", 
								(int) TimeUnit.SECONDS.toSeconds(5), 
								1));
			} catch (OException e) {
				throw new OpenRiskException(e);
			}
		}
	}
	
	@Override
	public void dispose() {
		PluginUtil.dispose(sessionInfoTable);
		PluginUtil.dispose(runSiteStatus);
	}
}