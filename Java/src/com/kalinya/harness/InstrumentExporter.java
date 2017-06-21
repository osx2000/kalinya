package com.kalinya.harness;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.kalinya.enums.InstrumentReferenceType;
import com.kalinya.instrument.Bonds;
import com.kalinya.instrument.Mapper;
import com.kalinya.instrument.Mappings;
import com.kalinya.oc.util.MessageLog;
import com.kalinya.performance.Configurator;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.StringUtil;
import com.olf.openrisk.application.Application;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.staticdata.BusinessUnit;
import com.olf.openrisk.staticdata.EnumReferenceTable;
import com.olf.openrisk.staticdata.Party;
import com.olf.openrisk.staticdata.StaticDataFactory;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.trading.EnumInsType;
import com.olf.openrisk.trading.EnumTranStatus;
import com.olf.openrisk.trading.EnumTranType;
import com.olf.openrisk.trading.InstrumentType;

@SuppressWarnings("unused")
public class InstrumentExporter {
	private static Session session;
	private static MessageLog messageLog = null;

	public static void main(String[] args) {
		try {
			Application application = Application.getInstance();
			session = application.attach();
			messageLog = new MessageLog(session, InstrumentExporter.class);

			File file = new File(Configurator.INSTRUMENT_IMPORT_FILE_PATH_NON_USD);
			JAXBContext context = JAXBContext.newInstance(Bonds.class, Mapper.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			Mapper mapper = getMapper();

			//Set<String> bondReferences = getUsdGovernmentBillsAndBonds();
			Set<String> bondReferences = getNonUsdGovernmentBillsAndBonds();
			//Set<String> bondReferences = getBondReferences();
			Bonds bonds = new Bonds(messageLog, mapper, bondReferences);
			bonds.printRequiredStaticData(messageLog);
			Set<String> set = null;
			set = bonds.getDistinctInstrumentTypes();
			messageLog.info(String.format("DistinctInstrumentTypes (%s) %s", set.size(), set));
			set = bonds.getDistinctBusinessUnits();
			messageLog.info(String.format("DistinctBusinessUnits (%s) %s", set.size(), set));
			set = bonds.getDistinctLegalEntities();
			messageLog.info(String.format("DistinctLegalEntities (%s) %s", set.size(), set));
			set = bonds.getDistinctPortfolioGroups();
			messageLog.info(String.format("DistinctPortfolioGroups (%s) %s", set.size(), set));
			set = bonds.getDistinctPricingModels();
			messageLog.info(String.format("DistinctPricingModels (%s) %s", set.size(), set));
			set = bonds.getDistinctInstrumentGroups();
			messageLog.info(String.format("DistinctInstrumentGroups (%s) %s", set.size(), set));
			set = bonds.getDistinctIndices();
			messageLog.info(String.format("DistinctIndices (%s) %s", set.size(), set));
			set = bonds.getDistinctHolidaySchedules();
			messageLog.info(String.format("DistinctHolidaySchedules (%s) %s", set.size(), set));
			bonds.setDescription("CNY Government Bills and Bonds");
			marshaller.marshal(bonds, file);
			System.out.println("Done");
		} catch (Exception | UnsatisfiedLinkError e) {
			messageLog.logException(e);
			System.out.println(e.getMessage());
		} finally {
			if(messageLog.hasExceptions()) {
				messageLog.printExceptions();
			}
			PluginUtil.dispose(session);
		}
	}

	private static Mapper getMapper() {
		Mapper mapper = new Mapper();
		mapper.setInstrumentTypeMap(Mappings.getInstrumentTypeMap());
		mapper.setIssuerBusinessUnitMap(Mappings.getIssuerBusinessUnitMap());
		mapper.setIssuerLegalEntityMap(Mappings.getIssuerLegalEntityMap());
		mapper.setIndexMap(Mappings.getIndexMap());
		mapper.setHolidayScheduleMap(Mappings.getHolidayScheduleMap());
		mapper.setInstrumentGroupMap(Mappings.getInstrumentGroupMap());
		mapper.setPortfolioGroupMap(Mappings.getPortfolioGroupMap());
		mapper.setPricingModelMap(Mappings.getPricingModelMap());
		mapper.setFieldValueOverrides(Mappings.getFieldValueOverrides());
		return mapper;
	}

	private static Set<String> getUsdGovernmentBillsAndBonds() {
		return getBondReferences(
				Configurator.INSTRUMENT_REFERENCE_TYPE,
				new EnumInsType[] { EnumInsType.MmGbill, EnumInsType.GovtBond },
				getUsdCurrency(), 
				true,
				null,
				null);
	}
	
	private static Set<String> getNonUsdGovernmentBillsAndBonds() {
		List<InstrumentType> instrumentTypesToExclude = new ArrayList<>();
		addInstrumentType(instrumentTypesToExclude, "AGCY-BOND");
		// Add additional instrument types here

		List<Party> issuerBusinessUnitsToExclude = new ArrayList<>();
		addParty(issuerBusinessUnitsToExclude, "20684sname");
		return getBondReferences(
				Configurator.INSTRUMENT_REFERENCE_TYPE,
				new EnumInsType[] { EnumInsType.MmGbill, EnumInsType.GovtBond }, 
				getNonUsdCurrencies(),
				true, 
				instrumentTypesToExclude.toArray(new InstrumentType[instrumentTypesToExclude.size()]),
				issuerBusinessUnitsToExclude.toArray(new Party[issuerBusinessUnitsToExclude.size()]));
	}

	private static void addParty(List<Party> parties, String partyName) {
		try {
			parties.add(session.getStaticDataFactory().getReferenceObject(BusinessUnit.class, partyName));
		} catch (Exception e) {
			//swallow the exception because the instrument type might not exist in this environment
			messageLog.warning(e.getMessage());
		}
	}

	private static void addInstrumentType(List<InstrumentType> instrumentTypes, String instrumentTypeName) {
		try {
			instrumentTypes.add(session.getStaticDataFactory().getReferenceObject(InstrumentType.class, instrumentTypeName));
		} catch (Exception e) {
			//swallow the exception because the instrument type might not exist in this environment
			messageLog.warning(e.getMessage());
		}
	}

	private static Set<String> getInflationLinkedBonds() {
		return getBondReferences(
				Configurator.INSTRUMENT_REFERENCE_TYPE,
				new EnumInsType[] {EnumInsType.BondRefIndexLinked, /*IDX-BOND*/
				EnumInsType.BondInflationLinkedAmort }, /* IBOND */
				getUsdCurrency(), 
				true,
				null,
				null);
	}
	
	/**
	 * Returns a collection of bond/mnymarket instrument references (ISINs or
	 * CUSIPs)
	 * 
	 * @param instrumentReferenceType
	 *            Lookup by ISIN or CUSIP
	 * @param instrumentTypes
	 *            The Base Ins Types to retrieve
	 * @param currencyNames
	 *            The currency of the instruments to retrieve
	 * @param excludeZeroCouponBonds
	 *            true: exclude zero coupon bonds
	 * @param instrumentTypesToExclude
	 *            InstrumentTypes to exclude (may include custom InsTypes)
	 * @param issuerBusinessUnitsToExclude
	 *            Issuer Business Units to exclude
	 * @return
	 */
	private static Set<String> getBondReferences(InstrumentReferenceType instrumentReferenceType, EnumInsType[] instrumentTypes, String[] currencyNames,
			boolean excludeZeroCouponBonds, InstrumentType[] instrumentTypesToExclude, Party[] issuerBusinessUnitsToExclude) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT " + instrumentReferenceType.toString());
		sql.append("\nFROM ab_tran ab");
		sql.append("\nJOIN header h ON h.ins_num = ab.ins_num");
		sql.append(String.format("\nWHERE ab.tran_status = %s", EnumTranStatus.Validated.getValue()));
		sql.append(String.format("\n AND ab.tran_type = %s", EnumTranType.Holding.getValue()));
		sql.append(String.format("\n AND ab.base_ins_type IN (%s)", PluginUtil.joinEnumIds(instrumentTypes, ",")));
		sql.append(String.format("\n AND LEN(%s) > 0", Configurator.INSTRUMENT_REFERENCE_TYPE.toString()));

		if(currencyNames != null && currencyNames.length > 0) {
			sql.append("\n AND ab.currency IN (" + getCurrencyIdsCsv(currencyNames) + ")");
		}
		if(excludeZeroCouponBonds) {
			sql.append("\n AND ab.price > 0.0");
		}
		if(instrumentTypesToExclude != null && instrumentTypesToExclude.length > 0) {
			sql.append(String.format("\n AND ab.ins_type NOT IN (%s)", getInstrumentTypeIdsCsv(instrumentTypesToExclude)));
		}
		if(issuerBusinessUnitsToExclude != null && issuerBusinessUnitsToExclude.length > 0) {
			sql.append(String.format("\n AND ab.external_bunit NOT IN (%s)", getPartyIdsCsv(issuerBusinessUnitsToExclude)));
		}
		sql.append("\nORDER BY " + Configurator.INSTRUMENT_REFERENCE_TYPE.toString());
		Table table = null;
		try {
			table = session.getIOFactory().runSQL(sql.toString());
			if(table != null && table.getRowCount() > 0) {
				return new HashSet<String>(Arrays.asList(table.getColumnValuesAsString(Configurator.INSTRUMENT_REFERENCE_TYPE.toString())));
			} else {
				return new HashSet<String>();
			}
		} finally {
			PluginUtil.dispose(table);
		}
	}
	
	private static String getInstrumentTypeIdsCsv(InstrumentType[] instrumentTypes) {
		int[] ints = new int[instrumentTypes.length];
		int i = 0;
		for(InstrumentType instrumentType: instrumentTypes) {
			ints[i] = instrumentType.getId();
			i++;
		}
		return StringUtil.join(ints, ",");
	}
	
	private static String getPartyIdsCsv(Party[] parties) {
		int[] ints = new int[parties.length];
		int i = 0;
		for(Party party: parties) {
			ints[i] = party.getId();
			i++;
		}
		return StringUtil.join(ints, ",");
	}

	// For testing
	private static Set<String> getBondReferences() {
		Set<String> references = new HashSet<String>();
		references.add("912796FV6");
		return references;
	}
	
	private static String[] getUsdCurrency() {
		String[] currencies = new String[]{"USD"};
		return currencies;
	}
	
	private static String[] getNonUsdCurrencies() {
		//String[] currencies = new String[]{"CAD","CNY","EUR","GBP","JPY"};
		String[] currencies = new String[]{"CNY", "CNH", "KRW", "MYR", "IDR", "PHP", "THB"};
		return currencies;
	}
	
	private static String getCurrencyIdsCsv(String[] currencyNames) {
		StaticDataFactory sdf = session.getStaticDataFactory();
		int[] ints = new int[currencyNames.length];
		int i = 0;
		for(String currencyName: currencyNames) {
			ints[i] = sdf.getId(EnumReferenceTable.Currency, currencyName);
			i++;
		}
		return StringUtil.join(ints, ",");
	}
}
