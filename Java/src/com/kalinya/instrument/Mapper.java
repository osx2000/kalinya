package com.kalinya.instrument;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.kalinya.enums.InstrumentReferenceType;
import com.kalinya.oc.util.MessageLog;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.trading.EnumLegFieldId;
import com.olf.openrisk.trading.EnumToolset;
import com.olf.openrisk.trading.EnumTranStatus;
import com.olf.openrisk.trading.EnumTranType;

@XmlType(propOrder = { "instrumentTypeMap", "issuerBusinessUnitMap", "issuerLegalEntityMap", "indexMap",
		"instrumentGroupMap", "portfolioGroupMap", "pricingModelMap", "holidayScheduleMap", "tickerToCusipMap", "tickerToIsinMap","isinToCusipMap", "cusipToIsinMap"})
@XmlRootElement
public class Mapper {
	private boolean isEnabled;
	private MessageLog messageLog;
	private Session session;
	private Map<String,String> instrumentTypeMap;
	private Map<String,String> issuerBusinessUnitMap;
	private Map<String,String> issuerLegalEntityMap;
	private Map<String,String> indexMap;
	private Map<String,String> instrumentGroupMap;
	private Map<String,String> portfolioGroupMap;
	private Map<String,String> pricingModelMap;
	private Map<String,String> holidayScheduleMap;
	private Map<EnumLegFieldId, String> fieldValueOverrides;
	private Map<String, String> tickerToCusipMap;
	private Map<String, String> tickerToIsinMap;
	private Map<String, String> cusipToIsinMap;
	private Map<String, String> isinToCusipMap;

	public Mapper() {
		isEnabled = false;
		instrumentTypeMap = new LinkedHashMap<String,String>();
		issuerBusinessUnitMap = new LinkedHashMap<String,String>();
		issuerLegalEntityMap = new LinkedHashMap<String,String>();
		indexMap = new LinkedHashMap<String,String>();
		instrumentGroupMap = new LinkedHashMap<String,String>();
		portfolioGroupMap = new LinkedHashMap<String,String>();
		pricingModelMap = new LinkedHashMap<String,String>();
		holidayScheduleMap = new LinkedHashMap<String,String>();
		fieldValueOverrides = new HashMap<EnumLegFieldId, String>();
		tickerToCusipMap = new HashMap<String, String>();
		tickerToIsinMap = new HashMap<String, String>();
	}

	/**
	 * Object that supports mapping static and reference data between systems
	 * 
	 * @param useMappings
	 *            <ul>
	 *            <li>
	 *            {@code true} will implement the mappings;</li>
	 *            <li>
	 *            {@code false} will ignore mappings so that importing will use
	 *            the raw XML file</li>
	 *            </ul>
	 */
	public Mapper(boolean useMappings) {
		this();
		if(useMappings) {
			isEnabled = true;
			instrumentTypeMap = Mappings.getInstrumentTypeMap();
			issuerBusinessUnitMap = Mappings.getIssuerBusinessUnitMap();
			issuerLegalEntityMap = Mappings.getIssuerLegalEntityMap();
			indexMap = Mappings.getIndexMap();
			instrumentGroupMap = Mappings.getInstrumentGroupMap();
			portfolioGroupMap = Mappings.getPortfolioGroupMap();
			pricingModelMap = Mappings.getPricingModelMap();
			holidayScheduleMap = Mappings.getHolidayScheduleMap();
			fieldValueOverrides = Mappings.getFieldValueOverrides();
		} else {
			isEnabled = false;
			instrumentTypeMap = new HashMap<String, String>();
			issuerBusinessUnitMap = new HashMap<String, String>();
			issuerLegalEntityMap = new HashMap<String, String>();
			indexMap = new HashMap<String, String>();
			instrumentGroupMap = new HashMap<String, String>();
			portfolioGroupMap = new HashMap<String, String>();
			pricingModelMap = new HashMap<String, String>();
			holidayScheduleMap = new HashMap<String, String>();
			fieldValueOverrides = new HashMap<EnumLegFieldId, String>();
		}
	}

	public Mapper(MessageLog messageLog, boolean useMappings) {
		this(useMappings);
		setMessageLog(messageLog);
		setSession(messageLog.getSession());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<" + this.getClass().getSimpleName() + ">");
		if(getInstrumentTypeMap().size() > 0) {
			sb.append(" InstrumentTypeMap [" + getInstrumentTypeMap().size() + "]");
		}
		if(getIssuerBusinessUnitMap().size() > 0) {
			sb.append(" IssuerBusinessUnitMap [" + getIssuerBusinessUnitMap().size() + "]");
		}
		if(getIndexMap().size() > 0) {
			sb.append(" IndexMap [" + getIndexMap().size() + "]");
		}
		if(getInstrumentGroupMap().size() > 0) {
			sb.append(" InstrumentGroupMap [" + getInstrumentGroupMap().size() + "]");
		}
		if(getPortfolioGroupMap().size() > 0) {
			sb.append(" PortfolioGroupMap [" + getPortfolioGroupMap().size() + "]");
		}
		if(getFieldValueOverrides().size() > 0) {
			sb.append(" FieldValueOverrides [" + getFieldValueOverrides().size() + "]");
		}
		return sb.toString();
	}

	public MessageLog getMessageLog() {
		return messageLog;
	}

	@XmlTransient
	public void setMessageLog(MessageLog messageLog) {
		this.messageLog = messageLog;
	}

	public final Session getSession() {
		return session;
	}

	@XmlTransient
	private void setSession(Session session) {
		if(this.session == null) {
			this.session = session;
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public Map<String,String> getInstrumentTypeMap() {
		return instrumentTypeMap;
	}

	@XmlElement
	public void setInstrumentTypeMap(Map<String,String> instrumentTypeMap) {
		this.instrumentTypeMap = instrumentTypeMap;
	}

	public Map<String,String> getIssuerBusinessUnitMap() {
		return issuerBusinessUnitMap;
	}

	@XmlElement
	public void setIssuerBusinessUnitMap(Map<String,String> issuerBusinessUnitMap) {
		this.issuerBusinessUnitMap = issuerBusinessUnitMap;
	}

	public Map<String,String> getIssuerLegalEntityMap() {
		return issuerLegalEntityMap;
	}

	@XmlElement
	public void setIssuerLegalEntityMap(Map<String,String> issuerLegalEntityMap) {
		this.issuerLegalEntityMap = issuerLegalEntityMap;
	}

	public Map<String, String> getIndexMap() {
		return indexMap;
	}

	@XmlElement
	public void setIndexMap(Map<String, String> indexMap) {
		this.indexMap = indexMap;
	}

	public Map<String, String> getInstrumentGroupMap() {
		return instrumentGroupMap;
	}

	@XmlElement
	public void setInstrumentGroupMap(Map<String, String> instrumentGroupMap) {
		this.instrumentGroupMap = instrumentGroupMap;
	}

	public Map<String, String> getPortfolioGroupMap() {
		return portfolioGroupMap;
	}

	@XmlElement
	public void setPortfolioGroupMap(Map<String, String> portfolioGroupMap) {
		this.portfolioGroupMap = portfolioGroupMap;
	}

	public Map<String,String> getPricingModelMap() {
		return pricingModelMap;
	}

	@XmlElement
	public void setPricingModelMap(Map<String,String> pricingModelMap) {
		this.pricingModelMap = pricingModelMap;
	}

	public Map<String,String> getHolidayScheduleMap() {
		return holidayScheduleMap;
	}

	@XmlElement
	public void setHolidayScheduleMap(Map<String,String> holidayScheduleMap) {
		this.holidayScheduleMap = holidayScheduleMap;
	}

	public Map<EnumLegFieldId, String> getFieldValueOverrides() {
		return fieldValueOverrides;
	}

	@XmlTransient
	public void setFieldValueOverrides(Map<EnumLegFieldId,String> fieldValueOverrides) {
		this.fieldValueOverrides = fieldValueOverrides;
	}

	public Map<String, String> getTickerToCusipMap() {
		return tickerToCusipMap;
	}

	@XmlElement
	public void setTickerToCusipMap(Map<String,String> tickerToCusipMap) {
		this.tickerToCusipMap = tickerToCusipMap;
	}

	public Map<String, String> getTickerToIsinMap() {
		return tickerToIsinMap;
	}

	@XmlElement
	public void setTickerToIsinMap(Map<String,String> tickerToIsinMap) {
		this.tickerToIsinMap = tickerToIsinMap;
	}
	
	public Map<String, String> getCusipToIsinMap() {
		return cusipToIsinMap;
	}

	@XmlElement
	public void setCusipToIsinMap(Map<String,String> cusipToIsinMap) {
		this.cusipToIsinMap = cusipToIsinMap;
	}
	
	public Map<String, String> getIsinToCusipMap() {
		return isinToCusipMap;
	}

	@XmlElement
	public void setIsinToCusipMap(Map<String,String> isinToCusipMap) {
		this.isinToCusipMap = isinToCusipMap;
	}

	public int size() {
		return getInstrumentTypeMap().size() 
				+ getIssuerBusinessUnitMap().size() 
				+ getIndexMap().size() 
				+ getInstrumentGroupMap().size()
				+ getPortfolioGroupMap().size();
	}

	public void retrieveTickerToIsinMap() {
		retrieveInstrumentIdentifierMap(getTickerToIsinMap(), InstrumentReferenceType.TICKER, InstrumentReferenceType.ISIN);
	}
	
	public void retrieveTickerToCusipMap() {
		retrieveInstrumentIdentifierMap(getTickerToCusipMap(), InstrumentReferenceType.TICKER, InstrumentReferenceType.CUSIP);
	}
	
	public void retrieveCusipToIsinMap() {
		retrieveInstrumentIdentifierMap(getCusipToIsinMap(), InstrumentReferenceType.CUSIP, InstrumentReferenceType.ISIN);
	}
	
	public void retrieveIsinToCusipMap() {
		retrieveInstrumentIdentifierMap(getIsinToCusipMap(), InstrumentReferenceType.ISIN, InstrumentReferenceType.CUSIP);
	}
	
	private void retrieveInstrumentIdentifierMap(Map<String, String> map, InstrumentReferenceType key, InstrumentReferenceType value) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT h.ticker, h.cusip, h.isin, ab.toolset, ab.tran_status");
		sql.append("\nFROM header h");
		sql.append("\nJOIN ab_tran ab ON ab.ins_num = h.ins_num");
		sql.append("\n AND ab.tran_type = " + EnumTranType.Holding.getValue());
		sql.append("\n AND ab.tran_status IN (" + EnumTranStatus.Validated.getValue() + "," + EnumTranStatus.Matured.getValue() + ")");
		sql.append("\n AND ab.toolset IN (" + EnumToolset.Bond.getValue() + "," + EnumToolset.MoneyMarket.getValue() + ")");
		Table table = null;
		try {
			table = getSession().getIOFactory().runSQL(sql.toString());
			String none = "None";
			if(table != null) {
				int rowCount = table.getRowCount();
				for(int rowId = 0; rowId < rowCount; rowId++) {
					String keyString = table.getString(key.toString().toLowerCase(), rowId);
					String valueString = table.getString(value.toString().toLowerCase(), rowId);
					if(key != null && keyString.trim().length() > 0 && !keyString.equalsIgnoreCase(none)
							&& value != null && valueString.trim().length() > 0 && !valueString.equalsIgnoreCase(none)) {
						map.put(keyString, valueString);
					}
				}
			}
		} finally {
			PluginUtil.dispose(table);
		}
	}
}
