package com.kalinya.harness;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.kalinya.instrument.Bond;
import com.kalinya.instrument.Bonds;
import com.kalinya.instrument.Mapper;
import com.kalinya.oc.util.MessageLog;
import com.kalinya.party.PartyBuilder;
import com.kalinya.performance.Configurator;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Application;
import com.olf.openrisk.application.Session;

public class InstrumentImporter {
	private static Session session;
	private static MessageLog messageLog;
	public static void main(String[] args) {
		try {
			Application application = Application.getInstance();
			session = application.attach();
			messageLog = new MessageLog(session, InstrumentImporter.class);
			messageLog.info("START [" + InstrumentImporter.class.getSimpleName() + "]");
						
			//Retrieve bond details from file
			//File instruments = new File(Configurator.INSTRUMENT_IMPORT_FILE_PATH_USD);
			File file = new File(Configurator.INSTRUMENT_IMPORT_FILE_PATH_NON_USD);
			JAXBContext context = JAXBContext.newInstance(Bonds.class, Bond.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Bonds bonds = (Bonds) unmarshaller.unmarshal(file);
			
			//Get mapping details required for destination
			Mapper mapper = new Mapper(true);
			
			//Get subset of instruments to import from file
			Set<String> referencesToImport = getReferencesToImport();
			
			if(Configurator.CREATE_MISSING_ISSUERS) {
				createMissingIssuers(bonds);
			}
			bonds.printRequiredStaticData(messageLog);
			//import the bonds
			bonds.push(messageLog, mapper, true, referencesToImport);
		} catch (JAXBException e) {
			messageLog.logException(e);
		} finally {
			messageLog.info("END [" + InstrumentImporter.class.getSimpleName() + "]");
			PluginUtil.dispose(session);
			System.out.println("Done");
		}
	}

	private static void createMissingIssuers(Bonds bonds) {
		Map<String, String> issuers = bonds.getDistinctMappedIssuers();
		PartyBuilder partyBuilder = new PartyBuilder(messageLog);
		for(String businessUnitName: issuers.keySet()) {
			partyBuilder.createIssuer(businessUnitName, issuers.get(businessUnitName));
		}
	}

	/**
	 * Enter the CUSIP or ISIN of a subset of instruments to import from the XML
	 * file. If the Set is empty, all instruments in the XML file will be
	 * imported.
	 * 
	 * @return
	 */
	private static Set<String> getReferencesToImport() {
		Set<String> references = new HashSet<String>();
		//references.add("912796FV6");
		//references.add("045167DG5");
		//references.add("CA135087ZL16");
		return references;
	}
}
