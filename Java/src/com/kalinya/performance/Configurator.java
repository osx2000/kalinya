package com.kalinya.performance;

import org.apache.commons.csv.CSVFormat;

public class Configurator {
	
	public static final String ROOT_FILE_PATH = "C:\\Users\\Stephen\\Dropbox\\Kalinya\\Plugins\\Data\\";
	
	public static final String CREDIT_RATINGS_FILE_PATH = "C:\\Users\\Stephen\\workspace\\Data\\CreditRatings.xml";
	public static final String MARKET_VALUES_FILE_PATH = "C:\\Users\\Stephen\\workspace\\Data\\MarketValues.xml";
	public static final String PRICES_FILE_PATH = ROOT_FILE_PATH + "Prices.csv";
	public static final String POSITIONS_FILE_PATH = ROOT_FILE_PATH + "positions.csv";
	public static final String POSITIONS_FILE_PATH_THREE_DAYS = ROOT_FILE_PATH + "positions.3days.csv";
	public static final String POSITIONS_FILE_PATH_TWO_YEARS = ROOT_FILE_PATH + "positions.2years.csv";
	public static final String POSITIONS_FILE_PATH_MULTIPLE_PORTFOLIOS = ROOT_FILE_PATH + "positions.multipleportfolios.csv";

	public static final String PERFORMANCE_RESULTS_EXPORT_FILE_PATH = ROOT_FILE_PATH + "performance_results.csv";
	public static final String SECURITY_MASTER_FILE_PATH = ROOT_FILE_PATH + "security_master.csv";
	public static final String PORTFOLIOS_FILE_PATH = ROOT_FILE_PATH + "portfolios.csv";
	public static final String BENCHMARK_ASSOCIATIONS_FILE_PATH = ROOT_FILE_PATH + "benchmark_associations.csv";
	
	private static final String U_ROOT_FILE_PATH = "U:\\sdube\\data\\";
	public static final String U_PERFORMANCE_RESULTS_EXPORT_FILE_PATH = U_ROOT_FILE_PATH + "performance_results.csv";
	
	/*
	 * CSV configuration
	 */
	private static final String NEW_LINE_SEPARATOR = "\n";
	public static final CSVFormat CSV_FILE_FORMAT = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

	/*Performance Source default parameters*/
	public static final String ACS_RULE_NAME_MARKET_VALUE = "Performance Market Value";

	public static final String SERIALIZED_FILE_PATH = ROOT_FILE_PATH + "PerformanceValues.ser";



}