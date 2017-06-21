package com.kalinya.instrument;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.kalinya.enums.InstrumentReferenceType;
import com.kalinya.oc.util.MessageLog;
import com.kalinya.performance.Configurator;
import com.kalinya.util.BaseSet;
import com.kalinya.util.StringUtil;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.internal.OpenRiskException;
import com.olf.openrisk.utility.Disposable;

@XmlType(propOrder={"xmlVersion", "description", "sourceDbVersion", "sourceDbServer", "sourceDbName", "extractDateTime", "size", "instrumentReferenceName", "bondSet", "mapper"})
@XmlRootElement
public class Bonds extends BaseSet<Bond> implements Disposable {
	private static final long serialVersionUID = -1765336618519582141L;
	private MessageLog messageLog;
	private Session session;
	private String xmlVersion;
	private String extractDateTime;
	private String sourceDbName;
	private String sourceDbServer;
	private String sourceDbVersion;
	private int size;
	private Set<Bond> bondSet;
	private List<String> updates;
	private Mapper mapper;
	private String instrumentReferenceName;
	private InstrumentReferenceType instrumentReferenceType;
	private String description;

	public Bonds() {
		super();
		setUpdates(new ArrayList<String>());
		setXmlVersion("2017.02.21");
	}
	
	public Bonds(MessageLog messageLog, Mapper mapper, Collection<String> references) {
		this();
		setMessageLog(messageLog);
		setSession(messageLog.getSession());
		setSourceDbServer(session.getServerName());
		setSourceDbName(session.getDatabaseName());
		setSourceDbVersion(session.getBranchTag());
		setInstrumentReferenceType(Configurator.INSTRUMENT_REFERENCE_TYPE);
		setInstrumentReferenceName(getInstrumentReferenceType().toString());
		setMapper(mapper);
		pull(references);
	}
	
	public void pull(Collection<String> references) {
		int i = 0;
		int size = references.size();
		String reference = "";
		Iterator<String> it = references.iterator();
		try {
			while(it.hasNext()) {
				i++;
				reference = it.next();
				messageLog.info("Retrieving [" + i + "] of [" + size + "] Ref [" + reference + "]");
				Bond bond = new Bond(messageLog, mapper, reference);
				add(bond);
			}
			setBondSet(getSet());
			setSize(size());
		} catch (Exception e) {
			messageLog.error("Processing [" + reference + "]");
			messageLog.logException(e);
			throw new OpenRiskException(e);
		}
		setExtractDateTime(StringUtil.formatTime(Calendar.getInstance().getTime()));
	}

	public void push(MessageLog messageLog, boolean overwriteExistingInstruments) {
		push(messageLog, null, overwriteExistingInstruments);
	}
	
	public void push(MessageLog messageLog, Mapper mapper,
			boolean overwriteExistingInstruments) {
		push(messageLog, null, overwriteExistingInstruments, new HashSet<String>());
	}
	
	public void push(MessageLog messageLog, Mapper mapper, boolean overwriteExistingInstruments, Set<String> referencesToImport) {
		if(getSet() == null || bondSet.size() == 0) {
			throw new IllegalStateException("The set " + (getSet()==null?"is null":"contains no instruments"));
		}
		addAll(bondSet);
		setMessageLog(messageLog);
		setSession(messageLog.getSession());
		setMapper(mapper);
		Map<Bond, List<String>> updates = new HashMap<Bond, List<String>>();
		Bond bond = null;
		Iterator<Bond> it = getSet().iterator();
		int i = 0;
		try {
			while(it.hasNext()) {
				i++;
				bond = it.next();
				if (referencesToImport == null || referencesToImport.size() == 0
						|| referencesToImport.contains(bond.getInstrumentReference(getInstrumentReferenceType()))) {
					getMessageLog().info("Processing [" + i + "] of [" + size() + "]");
					updates.put(bond, bond.push(messageLog, overwriteExistingInstruments, getMapper(), getInstrumentReferenceType()));
				}
			}
		} catch (Exception e) {
			getMessageLog().error("Processing [" + bond.toString() + "]");
			getMessageLog().logException(e);
			throw new OpenRiskException(e);
		}
	}
	
	public Set<Bond> getBondSet() {
		return bondSet;
	}
	
	@XmlElement(name="bond")
	public void setBondSet(Set<Bond> set) {
		bondSet = set;
		if(bondSet != null && bondSet.size() > 0 && getSet().size() == 0) {
			addAll(bondSet);
		}
	}
	
	public final MessageLog getMessageLog() {
		return messageLog;
	}
	
	private void setMessageLog(MessageLog messageLog) {
		if(this.messageLog == null) {
			this.messageLog = messageLog;
		}
	}

	public final Session getSession() {
		return session;
	}
	
	private void setSession(Session session) {
		if(this.session == null) {
			this.session = session;
		}
	}
	
	public Mapper getMapper() {
		return mapper;
	}
	
	@XmlElement
	private void setMapper(Mapper mapper) {
		if(this.mapper == null || mapper != null) {
			this.mapper = mapper;
		}
	}

	@Override
	public String toString() {
		return "Count [" + size() + "] HasMapper ["
				+ (getMapper() != null && getMapper().size() > 0) + "]";
	}

	@Override
	public void dispose() {
	}
	
	public String getXmlVersion() {
		return xmlVersion;
	}

	@XmlElement
	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}

	public String getSourceDbServer() {
		return sourceDbServer;
	}

	@XmlElement
	public void setSourceDbServer(String sourceDbServer) {
		this.sourceDbServer = sourceDbServer;
	}

	public String getSourceDbName() {
		return sourceDbName;
	}

	@XmlElement
	public void setSourceDbName(String sourceDbName) {
		this.sourceDbName = sourceDbName;
	}

	public String getSourceDbVersion() {
		return sourceDbVersion;
	}

	@XmlElement
	public void setSourceDbVersion(String sourceDbVersion) {
		this.sourceDbVersion = sourceDbVersion;
	}
	
	public void setInstrumentReferenceType(InstrumentReferenceType instrumentReferenceType) {
		this.instrumentReferenceType = instrumentReferenceType;
	}
	
	@XmlTransient
	public InstrumentReferenceType getInstrumentReferenceType() {
		if(instrumentReferenceType == null) {
			return InstrumentReferenceType.valueOf(getInstrumentReferenceName());
		}
		return instrumentReferenceType;
	}

	public String getInstrumentReferenceName() {
		return instrumentReferenceName;
	}

	@XmlElement
	public void setInstrumentReferenceName(String instrumentReferenceName) {
		this.instrumentReferenceName = instrumentReferenceName;
	}
	
	public String getExtractDateTime() {
		return extractDateTime;
	}

	@XmlElement
	public void setExtractDateTime(String extractDateTime) {
		this.extractDateTime = extractDateTime;
	}

	public List<String> getUpdates() {
		return updates;
	}

	private void setUpdates(List<String> updates) {
		this.updates = updates;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getDescription() {
		return description;
	}
	
	@XmlElement
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void printRequiredStaticData(MessageLog messageLog) {
		setMessageLog(messageLog);
		getMessageLog().info("Required Instrument Types " + getDistinctMappedInstrumentTypes());
		getMessageLog().info("Required Issuers " + getDistinctMappedIssuers());
		getMessageLog().info("Required Instrument Groups " + getDistinctMappedInstrumentGroups());
		getMessageLog().info("Required Portfolio Groups " + getDistinctMappedPortfolioGroups());
		getMessageLog().info("Required Indices " + getDistinctIndices());
		getMessageLog().info("Required Holiday Schedules " + getDistinctHolidaySchedules());
		getMessageLog().info("Required Pricing Models " + getDistinctMappedPricingModels());
	}
	
	public Map<String, String> getDistinctMappedIssuers() {
		Map<String, String> issuers = new HashMap<String, String>();
		for(Bond bond: getBondSet()) {
			String issuerBusinessUnit = bond.getIssuerBusinessUnit();
			String mappedIssuerBusinessUnit = getMapper().getIssuerBusinessUnitMap().get(issuerBusinessUnit);
			if(mappedIssuerBusinessUnit != null) {
				issuerBusinessUnit = mappedIssuerBusinessUnit;
			}
			String issuerLegalEntity = bond.getIssuerLegalEntity();
			String mappedIssuerLegalEntity = getMapper().getIssuerLegalEntityMap().get(issuerLegalEntity);
			if(mappedIssuerLegalEntity != null) {
				issuerLegalEntity = mappedIssuerLegalEntity;
			}
			issuers.put(mappedIssuerBusinessUnit, mappedIssuerLegalEntity);
		}
		return issuers;
	}
	
	public Set<String> getDistinctMappedInstrumentGroups() {
		Set<String> set = new HashSet<String>();
		for(Bond bond: getBondSet()) {
			String value = bond.getInstrumentGroup();
			String mappedValue = getMapper().getInstrumentGroupMap().get(value);
			if(mappedValue != null) {
				value = mappedValue;
			}
			if(value != null) {
				set.add(value);
			}
		}
		return set;
	}
	
	public Set<String> getDistinctMappedPortfolioGroups() {
		Set<String> set = new HashSet<String>();
		for(Bond bond: getBondSet()) {
			String value = bond.getPortfolioGroup();
			String mappedValue = getMapper().getPortfolioGroupMap().get(value);
			if(mappedValue != null) {
				value = mappedValue;
			}
			if(value != null) {
				set.add(value);
			}
		}
		return set;
	}
	
	public Set<String> getDistinctMappedPricingModels() {
		Set<String> set = new HashSet<String>();
		for(Bond bond: getBondSet()) {
			String value = bond.getPricingModel();
			String mappedValue = getMapper().getPricingModelMap().get(value);
			if(mappedValue != null) {
				value = mappedValue;
			}
			if(value != null) {
				set.add(value);
			}
		}
		return set;
	}
	
	public Set<String> getDistinctMappedInstrumentTypes() {
		Set<String> set = new HashSet<String>();
		for(Bond bond: getBondSet()) {
			String value = bond.getInstrumentTypeName();
			String mappedValue = getMapper().getInstrumentTypeMap().get(value);
			if(mappedValue != null) {
				value = mappedValue;
			}
			if(value != null) {
				set.add(value);
			}
		}
		return set;
	}
	
	public Set<String> getDistinctHolidaySchedules() {
		Set<String> set = new HashSet<String>();
		for(Bond bond: getBondSet()) {
			String value = bond.getFirstLeg().getHolidaySchedules();
			String mappedValue = getMapper().getHolidayScheduleMap().get(value);
			if(mappedValue != null) {
				value = mappedValue;
			}
			if(value != null) {
				set.add(value);
			}
		}
		return set;
	}
	
	public Set<String> getDistinctIndices() {
		Set<String> set = new HashSet<String>();
		for(Bond bond: getBondSet()) {
			//Get DiscountingIndex
			String value = bond.getFirstLeg().getDiscountingIndex();
			String mappedValue = getMapper().getIndexMap().get(value);
			if(mappedValue != null) {
				value = mappedValue;
			}
			if(value != null) {
				set.add(value);
			}
			
			//Get ProjectionIndex
			value = bond.getFirstLeg().getProjectionIndex();
			mappedValue = getMapper().getIndexMap().get(value);
			if(mappedValue != null) {
				value = mappedValue;
			}
			if(value != null) {
				set.add(value);
			}
			
			//Get RepoIndex
			value = bond.getFirstLeg().getRepoIndex();
			mappedValue = getMapper().getIndexMap().get(value);
			if(mappedValue != null) {
				value = mappedValue;
			}
			if(value != null) {
				set.add(value);
			}
		}
		return set;
	}
}