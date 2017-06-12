package com.kalinya.performance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.CsvHeader;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.util.Assertions;
import com.kalinya.util.BaseSet;
import com.kalinya.util.DateUtil;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.Timer;

final public class SecurityMasters extends BaseSet<SecurityMaster> {
	private static final long serialVersionUID = 7909870254975777703L;

	public SecurityMasters() {
		super();
	}

	public SecurityMaster getSecurityMaster(String instrumentId) {
		for(SecurityMaster securityMaster: getSet()) {
			if(securityMaster.getInstrumentId().equalsIgnoreCase(instrumentId)) {
				return securityMaster;
			}
		}
		throw new IllegalArgumentException(String.format("InstrumentId [%s] is not in the security master",instrumentId));
	}
	
	public IndustryGroup getIndustryGroup(String instrumentId) {
		return getSecurityMaster(instrumentId).getIndustryGroup();
	}
	
	public Sector getSector(String instrumentId) {
		return getSecurityMaster(instrumentId).getSector();
	}
	
	public RiskGroup getRiskGroup(String instrumentId) {
		return getSecurityMaster(instrumentId).getRiskGroup();
	}
	
	public InstrumentClass getInstrumentClass(String instrumentId) {
		return getSecurityMaster(instrumentId).getInstrumentClass();
	}
	
	public AssetClass getAssetClass(String instrumentId) {
		return getSecurityMaster(instrumentId).getAssetClass();
	}

	public Date getMaturityDate(String instrumentId) {
		return getSecurityMaster(instrumentId).getMaturityDate();
	}

	public Set<String> getInstrumentIds() {
		Set<String> instrumentIds = new HashSet<>();
		for(SecurityMaster securityMaster: getSet()) {
			instrumentIds.add(securityMaster.getInstrumentId());
		}
		return instrumentIds;
	}
	
	public static SecurityMasters load() {
		return load(Configurator.SECURITY_MASTER_DATABASE_FILE_PATH);
	}
	
	/**
	 * Loads the collection of SecurityMaster records
	 * 
	 * @param filePath
	 *            The path to the CSV file
	 * @return
	 */
	public static SecurityMasters load(String filePath) {
		Timer timer = new Timer();
		timer.start("LoadSecurityMasterData");
		SecurityMasters securityMasterData = new SecurityMasters();
		CSVParser csvParser = null;
		try {
			Assertions.notNull(filePath, "SecurityMasterDatabaseFilePath");
			InputStream inputStream = new FileInputStream(filePath);
			Reader reader = new InputStreamReader(new BOMInputStream(inputStream));
			csvParser = new CSVParser(reader, CSVFormat.EXCEL.withHeader().withIgnoreHeaderCase().withIgnoreSurroundingSpaces().withTrim());

			if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
				Map<String, Integer> headerMap = csvParser.getHeaderMap();
				System.out.println("Header Map [" + (headerMap != null ? headerMap.toString() : "null") + "]");
			}

			List<CSVRecord> csvRecords = csvParser.getRecords();
			for(CSVRecord csvRecord: csvRecords) {
				long recordNumber = csvRecord.getRecordNumber();

				String instrumentId = csvRecord.get(CsvHeader.INSTRUMENT_ID.getName());
				String maturityDateStr = csvRecord.get(CsvHeader.MATURITY_DATE.getName());
				String assetClassStr = csvRecord.get(CsvHeader.ASSET_CLASS.getName());
				String riskGroupStr = csvRecord.get(CsvHeader.RISK_GROUP.getName());
				String industryGroupStr = csvRecord.get(CsvHeader.INDUSTRY_GROUP.getName());
				String sectorStr = csvRecord.get(CsvHeader.SECTOR.getName());
				String instrumentClassStr = csvRecord.get(CsvHeader.INSTRUMENT_CLASS.getName());

				Date maturityDate = DateUtil.parseDate(maturityDateStr);
				AssetClass assetClass = AssetClass.fromName(assetClassStr);
				RiskGroup riskGroup = RiskGroup.fromName(riskGroupStr);
				IndustryGroup industryGroup = IndustryGroup.fromName(industryGroupStr);
				Sector sector = Sector.fromName(sectorStr);
				InstrumentClass instrumentClass = InstrumentClass.fromName(instrumentClassStr);

				SecurityMaster securityMaster = new SecurityMaster(instrumentId, maturityDate, industryGroup, sector,
						riskGroup, instrumentClass, assetClass);

				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					System.out.println("Record [" + recordNumber + "] SecurityMaster [" + securityMaster.toString() + "]");
				}
				securityMasterData.add(securityMaster);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(csvParser);
			timer.stop();
		}
		return securityMasterData;
	}

	public static DebugLevel getDebugLevel() {
		return Configurator.debugLevel;
	}
}
