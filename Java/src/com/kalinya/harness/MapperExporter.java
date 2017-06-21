package com.kalinya.harness;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;

import com.kalinya.instrument.Mapper;
import com.kalinya.oc.util.MessageLog;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Application;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.trading.EnumPricingModel;

public class MapperExporter {
	private static Session session;

	public static void main(String[] args) {
		MessageLog messageLog = null;
		try {
			Application application = Application.getInstance();
			session = application.attach();
			messageLog = new MessageLog(session, MapperExporter.class);

			File mapperFile = new File("O:\\Documents\\sdube\\Instruments\\mapper.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(Mapper.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			Mapper mapper = new Mapper();
			mapper.setInstrumentTypeMap(getInstrumentTypeMap());
			mapper.setIssuerBusinessUnitMap(getIssuerBusinessUnitMap());
			mapper.setIssuerLegalEntityMap(getIssuerLegalEntityMap());
			mapper.setIndexMap(getIndexMap());
			mapper.setHolidayScheduleMap(getHolidayScheduleMap());
			mapper.setInstrumentGroupMap(getInstrumentGroupMap());
			mapper.setPortfolioGroupMap(getPortfolioGroupMap());
			mapper.setPricingModelMap(getPricingModelMap());

			jaxbMarshaller.marshal(mapper, mapperFile);
			System.out.println("Done");
		} catch (Exception e) {
			if(e instanceof MarshalException) {
				messageLog.error(e.toString());
			}
			messageLog.logException(e);
			System.out.println(e.getMessage());
		} finally {
			PluginUtil.dispose(session);
		}
	}

	public static Map<String, String> getInstrumentTypeMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("GBND", "GBND");
		return map;
	}

	public static Map<String, String> getIssuerBusinessUnitMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ISSUER - UST - BU", "ISSUER - UST - BU");
		return map;
	}

	public static Map<String, String> getIndexMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("USDFED.USD", "USDFED.USD");
		//map.put("USDFED.USD", "LIBOR.USD");
		map.put("PX_BONDS_US.USD", "PX_BONDS_US.USD");
		map.put("ZERO_REPO.USD", "ZERO_REPO.USD");
		
		return map;
	}	
	
	public static Map<String, String> getPricingModelMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(EnumPricingModel.Discounting.getName(), EnumPricingModel.DiscountingProceeds.getName());
		return map;
	}

	public static Map<String, String> getHolidayScheduleMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("USNY", "USNY");
		return map;
	}

	public static Map<String, String> getInstrumentGroupMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("USD","USD");
		return map;
	}

	public static Map<String, String> getPortfolioGroupMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("All","All");
		return map;
	}

	public static Map<String, String> getIssuerLegalEntityMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ISSUER - UST - LE","ISSUER - UST - LE");
		return map;
	}	
}