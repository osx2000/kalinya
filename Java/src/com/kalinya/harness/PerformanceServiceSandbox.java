package com.kalinya.harness;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Map;

import com.kalinya.application.FindurSession;
import com.kalinya.enums.DayWeighting;
import com.kalinya.performance.Configurator;
import com.kalinya.performance.PerformanceFactory;
import com.kalinya.performance.PerformanceResult;
import com.kalinya.performance.PerformanceValue;
import com.kalinya.performance.PortfolioPerformanceResult;
import com.kalinya.performance.Portfolios;
import com.kalinya.performance.RuntimeArguments;
import com.kalinya.performance.datasource.CSVDataSource;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.performance.datasource.FindurPmmDataSource;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.performance.portfoliostatistics.PortfolioStatistics;
import com.kalinya.util.BaseSet;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Application;
import com.olf.openrisk.application.Session;

public class PerformanceServiceSandbox {

	public static void main(String[] args) {
		/*
		 * TODO:
		 * Repair FindurPmm class
		 * Support RoR calculation with/without fees
		 * Handle non-USD cash flows
		 * Handle SortinoRatio
		 * BenchmarkAssociations needs to have a BenchmarkType
		 * Create BenchmarkType to support PRIMARY, RISK_FREE
		 * 
		 * Findur Integration 
		 *  + Support retrieval of net contributions from perf_sec_values
		 *  + Support retrieval of industry group, etc
		 *  + Support aggregation by Portfolio Group
		 *  
		 * Create Interface for retrieval of instrument details (getInstrumentId, etc)
		 * Create PerformanceValues class to simplify some of the methods (.getLatestPerformanceValue(), etc)
		 * Handle maturity
		 * Handle currency fruit salad
		 * Support RoR calculations with base/local cash flows
		 * Design MTD/QTD/LTD RoR solution
		 * Build JUnit tests 
		 * Consider redesigning SecurityMaster dimensions to support dynamic creation of Findur static data
		 * Look for methods in PerformanceResult that could be made static
		 * Handle ratings upgrades/downgrades
		 * 
		 */

		FindurSession findurSession = new FindurSession();
		PerformanceFactory pf = findurSession.getPerformanceFactory();

		//Get runtime arguments
		RuntimeArguments runtimeArguments = pf.parseRuntimeArguments(args);
		boolean attachToFindur = runtimeArguments.getAttachToFindur();
		Date startDate = runtimeArguments.getStartDate();
		Date endDate = runtimeArguments.getEndDate();
		Portfolios portfolios = runtimeArguments.getPortfolios();
		String performanceResultsExtractFilePath = runtimeArguments.getPerformanceResultsExtractFilePath();
		DayWeighting dayWeighting = runtimeArguments.getDayWeighting();
		pf.setDayWeighting(dayWeighting);
		
		PerformanceDimensions performanceDimensions = null;

		//Use PerformanceDimensions from the command line interface
		performanceDimensions = runtimeArguments.getPerformanceDimensions();
		
		DataSource dataSource = null;

		if(attachToFindur) {
			Application application = Application.getInstance();
			Session session = application.attach();
			findurSession = new FindurSession(session);
			dataSource = new FindurPmmDataSource.Builder(findurSession)
					.withPortfoliosFilter(portfolios)
					//DateUtil.parseDate("8-Mar-2017")
					.withStartDate(startDate)
					//DateUtil.parseDate("9-Mar-2017")
					.withEndDate(endDate)
					.withResultsExtractFilePath(performanceResultsExtractFilePath)
					.build();
		} else {
			String positionsFilePath = runtimeArguments.getPositionsFilePath();
			String securityMasterFilePath = runtimeArguments.getSecurityMasterFilePath();
			String portfoliosFilePath = runtimeArguments.getPortfoliosFilePath();
			String benchmarkAssociationsFilePath = runtimeArguments.getBenchmarkAssociationsFilePath();
			dataSource = new CSVDataSource.Builder()
					.withPortfoliosFilter(portfolios)
					.withStartDate(startDate)
					.withEndDate(endDate)
					.withPositionsFilePath(positionsFilePath)
					.withSecurityMasterFilePath(securityMasterFilePath)
					.withPortfoliosFilePath(portfoliosFilePath)
					.withBenchmarkAssociationsFilePath(benchmarkAssociationsFilePath)
					.withResultsExtractFilePath(performanceResultsExtractFilePath)
					.build();
		}

		System.out.println(String.format("DataSource Details [%s]", dataSource.toString()));
		PerformanceResult performanceResults = null;
		dataSource.loadData();
		performanceResults = pf.calculateResults(dataSource.getPortfolios(), dataSource.getBenchmarkAssociations(), dataSource.getSecurityMasterData(),
				dataSource.getInstruments(), dataSource.getInstrumentLegs(), dataSource.getPositions(),
				dataSource.getCashflows(), performanceDimensions);
		if (dataSource.requiresFindurSession()) {
			findurSession.viewTable(performanceResults.asTable());
			if(performanceDimensions.equals(PerformanceDimensions.BY_DATE_BY_PORTFOLIO)) {
				//dataSource.extractToUserTable("USER_perf_results_by_portfolio");
			}
			if(performanceDimensions.equals(PerformanceDimensions.BY_DATE_BY_LEG)) {
				performanceResults.extractToUserTable("USER_perf_results_by_leg");
			}
		}
		performanceResults.extractToCsvFile(dataSource.getResultsExtractFilePath());
		System.out.println(String.format("Extracted to [%s]", dataSource.getResultsExtractFilePath()));
		performanceResults.extractToSerializedFile(Configurator.SERIALIZED_FILE_PATH);
		System.out.println("Absolute results: " + performanceResults.toString());

		boolean examinePortfolioStatistics = false;
		if(examinePortfolioStatistics  && performanceResults instanceof PortfolioPerformanceResult) {
			PortfolioStatistics portfolioStatistics = pf.createPortfolioStatistics();
			portfolioStatistics.add(PortfolioStatistics.ACTIVE_RETURN);
			portfolioStatistics.add(PortfolioStatistics.EXCESS_RETURN);
			portfolioStatistics.add(PortfolioStatistics.TRACKING_ERROR);
			portfolioStatistics.add(PortfolioStatistics.STANDARD_DEIVATION);
			portfolioStatistics.add(PortfolioStatistics.SHARPE_RATIO);
			portfolioStatistics.calculate((PortfolioPerformanceResult) performanceResults, dataSource.getBenchmarkAssociations());
		}
	}

	@SuppressWarnings("unused")
	private static void deserializePerformanceResults() {
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		try {
			fileIn = new FileInputStream(Configurator.SERIALIZED_FILE_PATH);
			in = new ObjectInputStream(fileIn);
			@SuppressWarnings("unchecked")
			Map<PerformanceDimensions, PerformanceValue> performanceValues = (Map<PerformanceDimensions, PerformanceValue>) in.readObject();
			System.out.println(BaseSet.getCollectionElementsAsString(performanceValues));
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(in);
			PluginUtil.close(fileIn);
		}
	}
}