package com.kalinya.harness;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.kalinya.instrument.Bonds;
import com.kalinya.instrument.Mapper;
import com.kalinya.instrument.Mappings;
import com.kalinya.oc.util.MessageLog;
import com.kalinya.performance.Configurator;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.StringUtil;
import com.olf.openrisk.application.Application;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.staticdata.EnumReferenceTable;
import com.olf.openrisk.staticdata.StaticDataFactory;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.trading.EnumInsType;
import com.olf.openrisk.trading.EnumTranStatus;
import com.olf.openrisk.trading.EnumTranType;

@SuppressWarnings("unused")
public class InstrumentExporter {
	private static Session session;

	public static void main(String[] args) {
		MessageLog messageLog = null;
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
			bonds.setDescription("USD Government Bills and Bonds");
			marshaller.marshal(bonds, file);
			System.out.println("Done");
		} catch (Exception e) {
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
		return getBondsByBaseInsType(
				new EnumInsType[] { EnumInsType.MmGbill, EnumInsType.GovtBond },
				getUsdCurrency(), 
				true);
	}
	
	private static Set<String> getNonUsdGovernmentBillsAndBonds() {
		return getBondsByBaseInsType(
				new EnumInsType[] {EnumInsType.MmGbill, EnumInsType.GovtBond},
				getNonUsdCurrencies(),
				true);
	}
	
	private static Set<String> getInflationLinkedBonds() {
		return getBondsByBaseInsType(
				new EnumInsType[] {EnumInsType.BondRefIndexLinked, /*IDX-BOND*/
				EnumInsType.BondInflationLinkedAmort }, /* IBOND */
				getUsdCurrency(), 
				true);
	}
	
	private static Set<String> getBondsByBaseInsType(EnumInsType[] instrumentTypes, String[] currencyNames, boolean excludeZeroCouponBonds) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT " + Configurator.INSTRUMENT_REFERENCE_TYPE.toString());
		sql.append("\nFROM ab_tran ab");
		sql.append("\nJOIN header h ON h.ins_num = ab.ins_num");
		sql.append("\nWHERE ab.tran_status = " + EnumTranStatus.Validated.getValue());
		sql.append("\n AND ab.tran_type = " + EnumTranType.Holding.getValue());
		sql.append("\n AND ab.base_ins_type IN (" + PluginUtil.joinEnumIds(instrumentTypes, ",") + ")");
		sql.append("\n AND LEN(" + Configurator.INSTRUMENT_REFERENCE_TYPE.toString() + ") > 0");

		if(currencyNames != null && currencyNames.length > 0) {
			sql.append("\n AND ab.currency IN (" + getCurrencyIdsCsv(currencyNames) + ")");
		}
		if(excludeZeroCouponBonds) {
			sql.append("\n AND ab.price > 0.0");
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
		String[] currencies = new String[]{"CAD","CNY","EUR","GBP","JPY"};
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
