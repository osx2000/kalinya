package com.kalinya.harness;

import com.kalinya.oc.util.MessageLog;
import com.kalinya.party.PartyBuilder;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Application;
import com.olf.openrisk.application.Session;

public class PartyBuilderHarness {

	public static void main(String[] args) {
		Session session = null;
		MessageLog messageLog = null;
		try {
			Application application = Application.getInstance();
			session = application.attach();
			messageLog = new MessageLog(session, PartyBuilderHarness.class);

			PartyBuilder partyBuilder = new PartyBuilder(messageLog);
			partyBuilder.createIssuer("NewIssuer3");
		} catch (Exception e) {
			messageLog.logException(e);
			System.out.println(e.getMessage());
		} finally {
			if(messageLog.hasExceptions()) {
				messageLog.printExceptions();
			}
			PluginUtil.dispose(session);
			System.out.println("Done");
		}
	}
}
