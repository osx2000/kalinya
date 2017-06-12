package com.kalinya.performance;

import org.apache.commons.csv.CSVFormat;

import com.kalinya.enums.DebugLevel;
import com.kalinya.optimization.examples.EfficientFrontierExample;
import com.kalinya.optimization.examples.EfficientFrontierExampleNoShorts;
import com.kalinya.optimization.examples.RomlExample;

public class Configurator {
	
	public static final String ROOT_PATH = "C:\\Users\\Stephen\\Dropbox\\Kalinya\\Plugins\\Data\\";
	
	public static final String CREDIT_RATINGS_FILE_PATH = "C:\\Users\\Stephen\\workspace\\Data\\CreditRatings.xml";
	public static final String MARKET_VALUES_FILE_PATH = "C:\\Users\\Stephen\\workspace\\Data\\MarketValues.xml";
	public static final String PRICES_FILE_PATH = ROOT_PATH + "Prices.csv";
	public static final String POSITIONS_FILE_PATH = ROOT_PATH + "positions.csv";
	public static final String POSITIONS_FILE_PATH_THREE_DAYS = ROOT_PATH + "positions.3days.csv";
	public static final String POSITIONS_FILE_PATH_TWO_YEARS = ROOT_PATH + "positions.2years.csv";
	public static final String POSITIONS_FILE_PATH_MULTIPLE_PORTFOLIOS = ROOT_PATH + "positions.multipleportfolios.csv";
	public static final String POSITIONS_FILE_PATH_PURCHASE = ROOT_PATH + "positions.purchase.csv";

	public static final String PERFORMANCE_RESULTS_EXTRACT_FILE_PATH = ROOT_PATH + "performance_results.csv";
	public static final String SECURITY_MASTER_FILE_PATH = ROOT_PATH + "security_master.csv";
	public static final String PORTFOLIOS_FILE_PATH = ROOT_PATH + "portfolios.csv";
	public static final String BENCHMARK_ASSOCIATIONS_FILE_PATH = ROOT_PATH + "benchmark_associations.csv";
	
	private static final String U_ROOT_FILE_PATH = "U:\\sdube\\data\\";
	public static final String U_PERFORMANCE_RESULTS_EXPORT_FILE_PATH = U_ROOT_FILE_PATH + "performance_results.csv";
	
	public static final String OPTIMIZATION_ASSET_RETURNS_FILE_PATH = ROOT_PATH + "optimization_asset_returns.csv";
	public static final String EFFICIENT_FRONTIER_WEIGHTS_EXTRACT_FILE_PATH = ROOT_PATH + "optimization_efficient_frontier_weights.csv";
	
	/*
	 * CSV configuration
	 */
	private static final String NEW_LINE_SEPARATOR = "\n";
	public static final CSVFormat CSV_FILE_FORMAT = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

	/*Performance Source default parameters*/
	public static final String ACS_RULE_NAME_MARKET_VALUE = "Performance Market Value";

	public static final String SERIALIZED_FILE_PATH = ROOT_PATH + "PerformanceValues.ser";

	public static final String SCENARIO_RESULTS_FILE_PATH = ROOT_PATH + "scenario_results.csv";
	public static final String POSITIONS_FILE_PATH_SCENARIO_RESULTS = ROOT_PATH + "positions.scenario_results.csv";

	//Database details
	private static final String DATABASE_PATH = ROOT_PATH + "\\database\\";
	public static final String PORTFOLIOS_DATABASE_FILE_PATH = DATABASE_PATH + "portfolios.kptf";
	public static final String SECURITY_MASTER_DATABASE_FILE_PATH = DATABASE_PATH + "security_master.ksec";
	public static final String BENCHMARK_ASSOCIATIONS_DATABASE_FILE_PATH = DATABASE_PATH + "benchmark_associations.kbmk";
	public static final String POSITIONS_DATABASE_FILE_PATH = DATABASE_PATH + "positions.kpos";
	public static final String PRICES_DATABASE_FILE_PATH = DATABASE_PATH + "prices.kprc";
	public static final String PERFORMANCE_VALUES_DATABASE_FILE_PATH = DATABASE_PATH + "performance_values.kpv";

	public static DebugLevel debugLevel = DebugLevel.LOW;
	
	public static String getRCode(Class<?> clazz) {
		if(clazz.equals(EfficientFrontierExample.class)) {
			return ROOT_PATH + "EfficientFrontierExample.R";
		} else if(clazz.equals(EfficientFrontierExampleNoShorts.class)) {
			return ROOT_PATH + "EfficientFrontierExampleNoShorts.R";
		} else if(clazz.equals(RomlExample.class)) {
			return ROOT_PATH + "RomlExample.R";
		}
		throw new IllegalArgumentException(String.format("Unknown caller [%s]", clazz.getSimpleName()));
	}



}
