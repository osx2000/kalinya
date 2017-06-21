package com.kalinya.oc.util;

import com.kalinya.enums.InstrumentReferenceType;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.trading.EnumTranStatus;
import com.olf.openrisk.trading.EnumTranType;
import com.olf.openrisk.trading.Instrument;
import com.olf.openrisk.trading.InstrumentType;

public class InstrumentUtil {

	/**
	 * Retrieves the instrument from the database. Will create the instrument if
	 * it does not exist
	 * 
	 * @param session
	 * @param instrumentType
	 * @param instrumentReferenceType
	 * @param instrumentId
	 * @return
	 * @see #getInstrument(Session, InstrumentReferenceType, String)
	 */
	public static Instrument getInstrument(Session session, InstrumentType instrumentType, InstrumentReferenceType instrumentReferenceType, String instrumentId) {
		Instrument instrument = null;
		try {
			instrument = getInstrument(session, instrumentReferenceType, instrumentId);
		} catch (Exception e) {
			//continue;
			instrument = null;
		}
		if(instrument == null) {
			instrument = session.getTradingFactory().createInstrument(instrumentType);
		}
		return instrument;
	}

	/**
	 * Retrieves the instrument from the database. Will throw an exception if
	 * the instrument does not exist
	 * 
	 * @param session
	 * @param instrumentReferenceType
	 * @param instrumentId
	 * @return
	 * @see #getInstrument(Session, InstrumentType, InstrumentReferenceType,
	 *      String)
	 */
	public static Instrument getInstrument(Session session, InstrumentReferenceType instrumentReferenceType, String instrumentId) {
		switch(instrumentReferenceType) {
		case REFERENCE:
			return InstrumentUtil.retrieveInstrumentByReference(session, instrumentId);
		case CUSIP:
			return InstrumentUtil.retrieveInstrumentByCusip(session, instrumentId);
		case TICKER:
			return InstrumentUtil.retrieveInstrumentByTicker(session, instrumentId);
		case ISIN:
			return InstrumentUtil.retrieveInstrumentByIsin(session, instrumentId);
		default:
			throw new UnsupportedOperationException("InstrumentReferenceType [" + instrumentReferenceType + "] is not yet supported");
		}
	}

	public static Instrument retrieveInstrumentByReference(Session session, String instrumentReference) {
		return session.getTradingFactory().retrieveInstrumentByReference(instrumentReference);
	}

	public static Instrument retrieveInstrumentByTicker(Session session, String ticker) {
		return retrieveInstrumentByHeaderTableFieldName(session, "ticker", ticker);
	}

	public static Instrument retrieveInstrumentByCusip(Session session, String cusip) {
		return retrieveInstrumentByHeaderTableFieldName(session, "cusip", cusip);
	}

	public static Instrument retrieveInstrumentByIsin(Session session, String isin) {
		return retrieveInstrumentByHeaderTableFieldName(session, "isin", isin);
	}

	public static Instrument retrieveInstrumentByHeaderTableFieldName(Session session, String headerTableFieldName, String reference) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ab.ins_num");
		sql.append("\nFROM ab_tran ab JOIN header h ON h.ins_num = ab.ins_num");
		sql.append("\nWHERE h." + headerTableFieldName + " = '" + reference + "' ");
		sql.append("\n AND ab.tran_status = " + EnumTranStatus.Validated.getValue());
		sql.append("\n AND ab.tran_type = " + EnumTranType.Holding.getValue());
		Table table = null;
		try {
			table = session.getIOFactory().runSQL(sql.toString());
			if(table != null && table.getRowCount() == 1) {
				int insNum = table.getInt("ins_num", 0);
				return session.getTradingFactory().retrieveInstrument(insNum);
			}
			throw new RuntimeException("Failed to retrieve " + headerTableFieldName + " [" + reference + "]");
		} finally {
			PluginUtil.dispose(table);
		}
	}
}
