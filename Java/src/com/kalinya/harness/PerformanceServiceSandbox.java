package com.kalinya.harness;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

import com.kalinya.application.FindurSession;
import com.kalinya.performance.Configurator;
import com.kalinya.performance.PerformanceFactory;
import com.kalinya.performance.PerformanceResult;
import com.kalinya.performance.PerformanceValue;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.PortfolioPerformanceResult;
import com.kalinya.performance.Portfolios;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.performance.portfoliostatistics.PortfolioStatistics;
import com.kalinya.util.BaseSet;
import com.kalinya.util.PluginUtil;

public class PerformanceServiceSandbox {

	public static void main(String[] args) {
		/*
		 * TODO:
		 * Support DayWeighting
		 * Support RoR calculation with/without fees
		 * Handle non-USD cash flows
		 * Handle SortinoRatio
		 * BenchmarkAssociations needs to have a BenchmarkType
		 * Add StartDate, EndDate to getPositions() filters
		 * Create BenchmarkType to support PRIMARY, RISK_FREE
		 * 
		 * Findur Integration 
		 *  + Support retrieval of net contributions from perf_sec_values
		 *  + Support retrieval of industry group, etc
		 *  + Support aggregation by Portfolio Group
		 *  
		 * Create Interface for retrieval of instrument details (getInstrumentId, etc)
		 * Create PerformanceValues class to simplify some of the methods (.getLatestPerformanceValue(), etc)
		 * Handle purchase
		 * Handle maturity
		 * Handle currency fruit salad
		 * Support RoR calculations with base/local cash flows
		 * Design MTD/QTD/LTD RoR solution
		 * Build JUnit tests 
		 * Test AssetClass, RiskGroup and other PerformanceDimensions
		 * Test multiple SecurityMaster PerformanceDimensions (e.g. ByInstrumentClassBySector)
		 * Consider redesigning SecurityMaster dimensions to support dynamic creation of Findur static data
		 * Look for methods in PerformanceResult that could be made static
		 * Handle ratings upgrades/downgrades
		 */
		FindurSession findurSession = new FindurSession();
		/*DataSource1 dataSource = null;
		dataSource = DataSource.FINDUR_PMM
				.withStartDate(DateUtil.parseDate("8-Mar-2017"))
				.withEndDate(DateUtil.parseDate("9-Mar-2017"))
				.withPortfolios(getPortfolios())
				.withResultsExtractFilePath(Configurator.U_PERFORMANCE_RESULTS_EXPORT_FILE_PATH);

		dataSource = DataSource1.CSV
				//.withPositionsFilePath(Configurator.POSITIONS_FILE_PATH)
				//.withPositionsFilePath(Configurator.POSITIONS_FILE_PATH_THREE_DAYS)
				.withSecurityMasterFilePath(Configurator.SECURITY_MASTER_FILE_PATH)
				.withPositionsFilePath(Configurator.POSITIONS_FILE_PATH_MULTIPLE_PORTFOLIOS)
				.withResultsExtractFilePath(Configurator.PERFORMANCE_RESULTS_EXPORT_FILE_PATH);
		if(dataSource.requiresFindurSession()) {
			Application application = Application.getInstance();
			Session session = application.attach();
			findurSession = new FindurSession(session);
		}*/
		PerformanceFactory pf = findurSession.getPerformanceFactory();

		PerformanceDimensions performanceDimensions = null;
		//performanceDimensions = PerformanceDimensions.BY_DATE;
		//performanceDimensions = PerformanceDimensions.CUMULATIVE_BY_LEG;
		//performanceDimensions = PerformanceDimensions.CUMULATIVE_BY_PORTFOLIO;
		performanceDimensions = PerformanceDimensions.BY_DATE_BY_LEG;
		performanceDimensions = PerformanceDimensions.BY_DATE_BY_PORTFOLIO;

		DataSource csvDataSource =  DataSource.CSV
											.withPositionsFilePath(Configurator.POSITIONS_FILE_PATH_MULTIPLE_PORTFOLIOS)
											.withPortfoliosFilePath(Configurator.PORTFOLIOS_FILE_PATH)
											.withSecurityMasterFilePath(Configurator.SECURITY_MASTER_FILE_PATH)
											.withBenchmarkAssociationsFilePath(Configurator.BENCHMARK_ASSOCIATIONS_FILE_PATH)
											//TODO: fix this because the order is critical
											.withPortfoliosFilter(getPortfolios())
											.withResultsExtractFilePath(Configurator.PERFORMANCE_RESULTS_EXPORT_FILE_PATH)
											.build();
		
		
		PerformanceResult performanceResults = null;
		//performanceResults = pf.calculateResults(csvDataSource, performanceDimensions);
		performanceResults = pf.calculateResults(csvDataSource.getPortfolios(), csvDataSource.getBenchmarkAssociations(), csvDataSource.getSecurityMasterData(),
				csvDataSource.getInstruments(), csvDataSource.getInstrumentLegs(), csvDataSource.getPositions(),
				csvDataSource.getCashflows(), performanceDimensions);
		if (csvDataSource.requiresFindurSession()) {
			findurSession.getSession().getDebug().viewTable(performanceResults.asTable());
			if(performanceDimensions.equals(PerformanceDimensions.BY_DATE_BY_PORTFOLIO)) {
				csvDataSource.extractToUserTable("USER_perf_results_by_portfolio");
			}
			if(performanceDimensions.equals(PerformanceDimensions.BY_DATE_BY_LEG)) {
				performanceResults.extractToUserTable("USER_perf_results_by_leg");
			}
		}
		performanceResults.printToCsvFile(csvDataSource.getResultsExtractFilePath());
		System.out.println(String.format("Extracted to [%s]", csvDataSource.getResultsExtractFilePath()));
		performanceResults.extractToSerializedFile(Configurator.SERIALIZED_FILE_PATH);
		//deserializePerformanceResults();
		System.out.println("Absolute results: " + performanceResults.toString());
		
		boolean examinePortfolioStatistics = false;
		if(examinePortfolioStatistics  && performanceResults instanceof PortfolioPerformanceResult) {
			PortfolioStatistics portfolioStatistics = pf.createPortfolioStatistics();
			portfolioStatistics.add(PortfolioStatistics.ACTIVE_RETURN);
			portfolioStatistics.add(PortfolioStatistics.EXCESS_RETURN);
			portfolioStatistics.add(PortfolioStatistics.TRACKING_ERROR);
			portfolioStatistics.add(PortfolioStatistics.STANDARD_DEIVATION);
			portfolioStatistics.add(PortfolioStatistics.SHARPE_RATIO);
			portfolioStatistics.calculate((PortfolioPerformanceResult) performanceResults, csvDataSource.getBenchmarkAssociations());
		}
	}

	private static void deserializePerformanceResults() {
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		try {
			fileIn = new FileInputStream(Configurator.SERIALIZED_FILE_PATH);
			in = new ObjectInputStream(fileIn);
			Map<PerformanceDimensions, PerformanceValue> performanceValues = (Map<PerformanceDimensions, PerformanceValue>) in.readObject();
			System.out.println(BaseSet.getCollectionElementsAsString(performanceValues));
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(in);
			PluginUtil.close(fileIn);
		}
	}

	private static Portfolios getPortfolios() {
		Portfolios portfolios = new Portfolios();
		portfolios.add(new Portfolio("20002name"));
		portfolios.add(new Portfolio("20001name"));
		portfolios.add(new Portfolio("CashFundAssets"));
		portfolios.add(new Portfolio("CashFundLiabilities"));
		portfolios.add(new Portfolio("LongTermAssets"));
		portfolios.add(new Portfolio("20004name"));
		/*portfolios.add("20005name");
		portfolios.add("20006name");*/
		return portfolios;
	}
}