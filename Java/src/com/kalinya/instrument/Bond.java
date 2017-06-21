package com.kalinya.instrument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.kalinya.enums.InstrumentReferenceType;
import com.kalinya.oc.util.InstrumentUtil;
import com.kalinya.oc.util.MessageLog;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.trading.EnumInstrumentFieldId;
import com.olf.openrisk.trading.EnumTranStatus;
import com.olf.openrisk.trading.EnumTransactionFieldId;
import com.olf.openrisk.trading.Instrument;
import com.olf.openrisk.trading.InstrumentType;


@XmlType(propOrder = { "reference", "cusip", "isin", "ticker",
		"issuerBusinessUnit", "issuerLegalEntity", "portfolioGroup", "instrumentGroup",
		"pricingModel","settlementDate","firstLeg","secondLeg"})
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlRootElement(name = "bond")
public class Bond implements Comparable<Bond> {
	private MessageLog messageLog;
	private Session session;
	private Mapper mapper;
	private List<String> updates;
	
	private Instrument instrument;
	private BondLeg firstLeg;
	private BondLeg secondLeg;
	
	private String instrumentTypeName;
	private String reference;
	private String ticker;
	private String cusip;
	private String isin;
	private String issuerBusinessUnit;
	private String issuerLegalEntity;
	private String settlementDate;
	private String pricingModel;

	private String instrumentGroup;
	private String portfolioGroup;
	private InstrumentReferenceType instrumentReferenceType;

	public Bond() {
		updates = new ArrayList<String>();
	}

	//TODO: support ins info fields
	public Bond(MessageLog messageLog, Mapper mapper, String instrumentReference) {
		this(messageLog, mapper, instrumentReference, null);
	}
	
	public Bond(MessageLog messageLog, Mapper mapper, Instrument instrument) {
		this(messageLog, mapper, null, instrument);
	}
	
	private Bond(MessageLog messageLog, Mapper mapper, String instrumentReference, Instrument instrument) {
		this();
		this.messageLog = messageLog;
		this.session = messageLog.getSession();
		setMapper(mapper);
		try {
			if(instrument == null) {
				setInstrument(instrumentReference);
			}
			setFirstLeg(new BondLeg(getMessageLog(), getMapper(), getInstrument(), 0));
			if(getInstrument().getLegCount() == 2) {
				setSecondLeg(new BondLeg(getMessageLog(), getMapper(), getInstrument(), 1));
			}

			/**
			 * TODO: this is a dirty object.  We need to be able to instantiate a Bond from -
			 *  a. Using a reference from the database
			 *  b. Using an instrument from a database
			 *  c. Using the input xml
			 *  d. Using an input csv
			 */
			
			//Instrument fields
			setInstrumentTypeName(getInstrument().getValueAsString(EnumInstrumentFieldId.InstrumentType));
			setReference(getInstrument().getValueAsString(EnumInstrumentFieldId.ReferenceString));
			setIsin(getInstrument().getValueAsString(EnumInstrumentFieldId.Isin));
			setTicker(getInstrument().getValueAsString(EnumInstrumentFieldId.Ticker));
			setCusip(getInstrument().getValueAsString(EnumInstrumentFieldId.Cusip));
			setSettlementDate(getInstrument().getTransaction().getValueAsString(EnumTransactionFieldId.SettleDate));
			setPricingModel(getInstrument().getValueAsString(EnumInstrumentFieldId.PricingModel));
			setIssuerBusinessUnit(getInstrument().getValueAsString(EnumInstrumentFieldId.ExternalBusinessUnit));
			setIssuerLegalEntity(getInstrument().getValueAsString(EnumInstrumentFieldId.IssuerLegalEntity));
			
			setInstrumentGroup(getInstrument().getValueAsString(EnumInstrumentFieldId.InstrumentGroup));
			setPortfolioGroup(getInstrument().getValueAsString(EnumInstrumentFieldId.PortfolioGroup));
		} catch (Exception e) {
			messageLog.logException(e);
		} finally {
			PluginUtil.dispose(getInstrument());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ref [" + getReference() + "] ");
		sb.append("InsType [" + getInstrumentTypeName() + "] ");
		sb.append("ISIN [" + getIsin() + "] ");
		sb.append("Ticker [" + getTicker() + "] ");
		sb.append("Issuer [" + getIssuerBusinessUnit() + "]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return (reference == null ? 0 : reference.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Bond)) {
			return false;
		}
		return this.compareTo((Bond) obj) == 0;
	}

	@Override
	public int compareTo(Bond that) {
		if (this == that) {
			return 0;
		}

		int i = getReference().compareTo(that.getReference());
		if(i != 0) return i;
		
		i = getIsin().compareTo(that.getIsin());
		if(i != 0) return i;
		
		i = getTicker().compareTo(that.getTicker());
		if(i != 0) return i;
		
		i = getCusip().compareTo(that.getCusip());
		if(i != 0) return i;

		return 0;
	}
	
	public BondLeg getFirstLeg() {
		return firstLeg;
	}
	
	@XmlElement(name="bondLeg")
	private void setFirstLeg(BondLeg bondLeg) {
		this.firstLeg = bondLeg;
	}
	
	public BondLeg getSecondLeg() {
		return secondLeg;
	}
	
	@XmlElement(name="bondLeg")
	private void setSecondLeg(BondLeg bondLeg) {
		this.secondLeg = bondLeg;
	}

	public List<String> push(MessageLog messageLog,
			boolean overwriteExistingInstruments,
			Mapper mapper, InstrumentReferenceType instrumentReferenceType) {
		setMapper(mapper);
		setInstrumentReferenceType(instrumentReferenceType);
		return push(messageLog);
	}
	
	public List<String> push(MessageLog messageLog) {
		setMessageLog(messageLog);
		setSession(messageLog.getSession());
		String instrumentReference = getInstrumentReference(getInstrumentReferenceType());
		messageLog.info("Processing " + this.getClass().getSimpleName() + " with reference [" + instrumentReference + "]");
		try {
			if(setInstrument(instrumentReference)) {
				boolean newInstrument = getInstrument().getInstrumentId() == 0;
				pushInstrumentUpdates();
				if(getUpdates().size() > 0) {
					messageLog.info("Processing to Validated [" + instrumentReference + "]");
					instrument.process(EnumTranStatus.Validated);
					messageLog.info("Updated [" + instrumentReference + "]");
					if(!newInstrument) {
						messageLog.info("Fields updated");
						messageLog.info(getUpdates());
					}
					
					//Refresh the instrument and confirm that no updates are now required
					setInstrument(instrumentReference);
					getUpdates().clear();
					pushInstrumentUpdates();
					if(getUpdates().size() > 0) {
						messageLog.warning("WARNING: there are remaining updates the were not effective.  These fields still require updates");
						messageLog.info(getUpdates());
					}
				} else {
					messageLog.info("No updates required [" + instrumentReference + "]");
				}
			}
			return updates;
		} finally {
			PluginUtil.dispose(instrument);
		}
	}

	private void setInstrumentReferenceType(InstrumentReferenceType instrumentReferenceType) {
		this.instrumentReferenceType = instrumentReferenceType;
	}
	
	public InstrumentReferenceType getInstrumentReferenceType() {
		return instrumentReferenceType;
	}

	public Mapper getMapper() {
		return mapper;
	}
	
	@XmlTransient
	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}
	
	public List<String> getUpdates() {
		return updates;
	}
	
	private Map<String,String> getInstrumentTypeMap() {
		return mapper.getInstrumentTypeMap();
	}
	
	private Map<String,String> getIssuerBusinessUnitMap() {
		return mapper.getIssuerBusinessUnitMap();
	}
	
	private Map<String,String> getIssuerLegalEntityMap() {
		return mapper.getIssuerLegalEntityMap();
	}
	
	private Map<String,String> getInstrumentGroupMap() {
		return mapper.getInstrumentGroupMap();
	}
	
	private Map<String,String> getPortfolioGroupMap() {
		return mapper.getPortfolioGroupMap();
	}

	private Map<String,String>  getPricingModelMap() {
		return mapper.getPricingModelMap();
	}

	public Instrument getInstrument() {
		return instrument;
	}
	
	private boolean setInstrument(String instrumentReference) {
		//TODO: use InstrumentHelper
		try {
			switch(getInstrumentReferenceType()) {
			case REFERENCE:
				instrument = InstrumentUtil.retrieveInstrumentByReference(getSession(), instrumentReference);
				
				break;
			case CUSIP:
				instrument = InstrumentUtil.retrieveInstrumentByCusip(getSession(), instrumentReference);
				break;
			case TICKER:
				instrument = InstrumentUtil.retrieveInstrumentByTicker(getSession(), instrumentReference);
				break;
			case ISIN:
				instrument = InstrumentUtil.retrieveInstrumentByIsin(getSession(), instrumentReference);
				break;
			default:
				throw new UnsupportedOperationException("InstrumentReferenceType [" + instrumentReferenceType + "] is not yet supported");
			}
		} catch (Exception e) {
			//continue;
			instrument = null;
		}
		if(instrument == null) {
			instrument = getSession().getTradingFactory().createInstrument(getInstrumentType());
			messageLog.info("Creating [" + instrumentReference + "]");
		}
		return true;
	}
	
	private void pushInstrumentUpdates() {
		getUpdates().clear();
		update(EnumInstrumentFieldId.InstrumentType, getInstrumentTypeName());
		update(EnumInstrumentFieldId.ReferenceString, getReference());
		update(EnumInstrumentFieldId.Ticker, getTicker());
		update(EnumInstrumentFieldId.Cusip, getCusip());
		update(EnumInstrumentFieldId.Isin, getIsin());
		//update(EnumInstrumentFieldId.Clip, getClipRed());
		update(EnumInstrumentFieldId.ExternalBusinessUnit, getIssuerBusinessUnit());
		update(EnumInstrumentFieldId.IssuerLegalEntity, getIssuerLegalEntity());
		update(EnumTransactionFieldId.SettleDate, getSettlementDate());
		update(EnumInstrumentFieldId.PricingModel, getPricingModel());
		
		//Secondary Page
		update(EnumInstrumentFieldId.InstrumentGroup, getInstrumentGroup());
		update(EnumInstrumentFieldId.PortfolioGroup, getPortfolioGroup());
		
		//Leg fields
		getUpdates().addAll(getFirstLeg().pushInstrumentLegUpdates(getMessageLog(), getMapper(), getInstrument().getLeg(0)));
		if(getInstrument().getLegCount() == 2) {
			getUpdates().addAll(getSecondLeg().pushInstrumentLegUpdates(getMessageLog(), getMapper(), getInstrument().getLeg(1)));
		}
	}

	private void update(EnumInstrumentFieldId fieldId, String value) {
		value = getMappedValue(fieldId, value);
		if(!getInstrument().getValueAsString(fieldId).equalsIgnoreCase(value)) {
			getInstrument().setValue(fieldId, value);
			getUpdates().add("EnumInstrumentFieldId." + fieldId.toString());
		}
	}
	
	private String getMappedValue(EnumInstrumentFieldId fieldId, String value) {
		switch(fieldId) {
		case InstrumentType:
			if(getInstrumentTypeMap() != null) {
				String newInstrumentTypeName = getInstrumentTypeMap().get(value);
				if(newInstrumentTypeName != null) {
					value = newInstrumentTypeName;
				}
			}
			break;
		case ExternalBusinessUnit:
			if(getIssuerBusinessUnitMap() != null) {
				String newFieldValue = getIssuerBusinessUnitMap().get(value);
				if(newFieldValue != null) {
					value = newFieldValue;
				}
			}
			break;
		case IssuerLegalEntity:
			if(getIssuerLegalEntityMap() != null) {
				String newFieldValue = getIssuerLegalEntityMap().get(value);
				if(newFieldValue != null) {
					value = newFieldValue;
				}
			}
			break;
		case PortfolioGroup:
			if(getPortfolioGroupMap() != null) {
				String newFieldValue = getPortfolioGroupMap().get(value);
				if(newFieldValue != null) {
					value = newFieldValue;
				}
			}
			break;
		case InstrumentGroup:
			if(getInstrumentGroupMap() != null) {
				String newFieldValue = getInstrumentGroupMap().get(value);
				if(newFieldValue != null) {
					value = newFieldValue;
				}
			}
			break;
		case PricingModel:
			if(getPricingModelMap() != null) {
				String newFieldValue = getPricingModelMap().get(value);
				if(newFieldValue != null) {
					value = newFieldValue;
				}
			}
			break;
		default:
			break;

		}

		return value;
	}

	private void update(EnumTransactionFieldId fieldId, String value) {
		if(!getInstrument().getTransaction().getValueAsString(fieldId).equalsIgnoreCase(value)) {
			getInstrument().getTransaction().setValue(fieldId, value);
			getUpdates().add("Instrument.EnumTransactionFieldId." + fieldId.toString());
		}
	}
	
	private void setMessageLog(MessageLog messageLog) {
		if(this.messageLog == null) {
			this.messageLog = messageLog;
		}
	}

	private void setSession(Session session) {
		if(this.session == null) {
			this.session = session;
		}
	}

	public final MessageLog getMessageLog() {
		return messageLog;
	}

	public final Session getSession() {
		return session;
	}

	public String getTicker() {
		return ticker;
	}

	@XmlElement
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getIsin() {
		return isin;
	}

	@XmlElement
	public void setIsin(String isin) {
		this.isin = isin;
	}
	
	public String getIssuerBusinessUnit() {
		return issuerBusinessUnit;
	}
	
	@XmlElement
	public void setIssuerBusinessUnit(String issuerBusinessUnit) {
		this.issuerBusinessUnit = issuerBusinessUnit;
	}

	public String getIssuerLegalEntity() {
		return issuerLegalEntity;
	}

	@XmlElement
	public void setIssuerLegalEntity(String issuerLegalEntity) {
		this.issuerLegalEntity = issuerLegalEntity;
	}

	public String getReference() {
		return reference;
	}

	@XmlAttribute
	public void setReference(String reference) {
		this.reference = reference;
	}

	private InstrumentType getInstrumentType() {
		return getSession().getStaticDataFactory().getReferenceObject(InstrumentType.class, getInstrumentTypeName());
	}
	
	public String getInstrumentTypeName() {
		return instrumentTypeName;
	}

	@XmlAttribute
	public void setInstrumentTypeName(String instrumentTypeName) {
		this.instrumentTypeName = instrumentTypeName;
	}
	
	public String getCusip() {
		return cusip;
	}

	@XmlElement
	public void setCusip(String cusip) {
		this.cusip = cusip;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	@XmlElement
	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getPricingModel() {
		return pricingModel;
	}

	@XmlElement
	public void setPricingModel(String pricingModel) {
		this.pricingModel = pricingModel;
	}

	public final String getInstrumentGroup() {
		return instrumentGroup;
	}

	@XmlElement
	public final void setInstrumentGroup(String instrumentGroup) {
		this.instrumentGroup = instrumentGroup;
	}

	public final String getPortfolioGroup() {
		return portfolioGroup;
	}

	@XmlElement
	public final void setPortfolioGroup(String portfolioGroup) {
		this.portfolioGroup = portfolioGroup;
	}

	/**
	 * Returns the CUSIP/ISIN/Reference/Ticker of the instrument according to
	 * the InstrumentReference enum parameter
	 * 
	 * @param instrumentReference
	 * @return
	 */
	public String getInstrumentReference(InstrumentReferenceType instrumentReference) {
		switch(instrumentReference) {
		case CUSIP:
			return getCusip();
		case ISIN:
			return getIsin();
		case REFERENCE:
			return getReference();
		case TICKER:
			return getTicker();
		default:
			throw new IllegalArgumentException("Illegal InstrumentReference enum [" + instrumentReference.toString() + "]");
		}
	}
}

