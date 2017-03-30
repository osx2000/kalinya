package com.kalinya.oc.util;

import com.olf.openjvs.FileUtil;
import com.olf.openjvs.Util;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.control.EnumJobStatus;
import com.olf.openrisk.internal.OpenRiskException;

public class EodEmail {
	/* 
	 * The name of a TPM workflow that will email details of a log file
	 */
	private final static String TPM_EMAIL_DEFINITION_NAME = "Email File";
	/**
	 * DIR_NODE_PATH is the folder in the database where the log fils will be
	 * saved before they are emailed.
	 * <p>
	 * Note that the log files are saved to the daily reports directory; this is
	 * an additional location that supports emailing the log file.
	 * <p>
	 * E.g. DIR_NODE_PATH = "/User/Log Files/"
	 */
	private final static String DIR_NODE_PATH = "/User/Log Files/";

	private Session session;
	private String caller = "Unknown Caller";
	private EnumJobStatus jobStatus = EnumJobStatus.Failed;
	private String message;
	private String recipients;
	private String subject;
	private String dirNodeDestination;

	/**
	 * A class that supports sending email using the mail service in a TPM workflow
	 * @param session The Findur Session
	 * @param caller The plugin that called the email service.  This will be included in the email subject line and used to lookup a log file
	 * @param jobStatus The status of the task
	 * @param message The email message
	 * @param recipients The (optional) list of recipients.  If null, the default list of mail recipients on teh TPM workflow will be used
	 */
	public EodEmail(Session session, String caller, EnumJobStatus jobStatus, String message, String recipients) {
		if(caller == null) {
			throw new IllegalStateException("Failed to identify the caller of the EOD Email");
		}
		this.session = session;
		this.caller = caller;
		this.jobStatus = jobStatus;
		this.message = message;
		this.recipients = recipients;
		setSubject();
		saveLogToDb();
	}

	/**
	 * Sets the email subject
	 * @return
	 */
	private void setSubject() {
		this.subject = "[" + jobStatus.getName() + "] " + caller + " ";
	}
	
	/**
	 * Saves the log to the database so it can be attached to the email by the TPM service
	 * @return
	 */
	private void saveLogToDb() {
		try {
			session.getDebug().logLine("Saving log to database");
			String sourceFile = Util.reportGetDirForToday() + "\\" + caller + ".log";
			FileUtil.importFileToDB(sourceFile, DIR_NODE_PATH);
			// dirNodeDestination is the full path plus log file name in the database
			this.dirNodeDestination = DIR_NODE_PATH + caller + ".log";
		} catch (Exception e) {
			session.getDebug().logLine("No log file found for called [" + caller + "]");
			session.getDebug().logLine(e.getMessage());
			//If there is no log file to save then do not attach the log file to the email
		}
	}
	
	/**
	 * Sends the email
	 */
	public void send() {
		try {
			TpmWorkflow tpmWorkflow = new TpmWorkflow(TPM_EMAIL_DEFINITION_NAME);
			tpmWorkflow.setTpmVariableAsString("subject", subject);
			tpmWorkflow.setTpmVariableAsString("message", message);
			if(dirNodeDestination != null) {
				tpmWorkflow.setTpmVariableAsBoolean("sendAttachment", true);
				tpmWorkflow.setTpmVariableAsFile("attachment", dirNodeDestination);
			}
			if(recipients != null) {
				// Optional email recipients. If this is not set then the
				// recipients will be set by the TPM workflow configuration
				tpmWorkflow.setTpmVariableAsString("recipients", recipients);
			}
			session.getDebug().logLine("Starting workflow: " + TPM_EMAIL_DEFINITION_NAME);
			tpmWorkflow.startInstance();
		} catch (Exception e) {
			session.getDebug().printLine(e.getClass() + ": " + e.getMessage());
			throw new OpenRiskException(e);
		}
	}
}
