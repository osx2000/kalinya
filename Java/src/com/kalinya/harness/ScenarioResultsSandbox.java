package com.kalinya.harness;

import java.util.Date;

import com.kalinya.application.FindurSession;
import com.kalinya.performance.Configurator;
import com.kalinya.performance.PerformanceFactory;
import com.kalinya.performance.datasource.CSVDataSource;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.results.InstrumentResult;
import com.kalinya.results.InstrumentResultEnum;
import com.kalinya.results.InstrumentResults;
import com.kalinya.util.DateUtil;

public class ScenarioResultsSandbox {

	public static void main(String[] args) {

		FindurSession findurSession = new FindurSession();
		@SuppressWarnings("unused")
		PerformanceFactory pf = findurSession.getPerformanceFactory();

		DataSource dataSource = null;
		String positionsFilePath = Configurator.POSITIONS_FILE_PATH_SCENARIO_RESULTS;
		String scenarioResultsFilePath = Configurator.SCENARIO_RESULTS_FILE_PATH;
		String securityMasterFilePath = Configurator.SECURITY_MASTER_FILE_PATH;
		String portfoliosFilePath = Configurator.PORTFOLIOS_FILE_PATH;
		String benchmarkAssociationsFilePath = Configurator.BENCHMARK_ASSOCIATIONS_FILE_PATH;
		dataSource = new CSVDataSource.Builder()
				.withPositionsFilePath(positionsFilePath)
				.withSecurityMasterFilePath(securityMasterFilePath)
				.withPortfoliosFilePath(portfoliosFilePath)
				.withBenchmarkAssociationsFilePath(benchmarkAssociationsFilePath)
				.withScenarioResultsFilePath(scenarioResultsFilePath)
				.build();

		System.out.println(String.format("DataSource Details [%s]", dataSource.toString()));
		//dataSource.loadData();
		
		Date date = DateUtil.today();
		InstrumentResults instrumentResults = dataSource.getInstrumentResults(date);
		System.out.println(String.format("InstrumentResults: %s", instrumentResults.toVerboseString()));
		InstrumentResult result = instrumentResults.getResult(date, "1yBill", InstrumentResultEnum.DURATION, false);
		System.out.println(String.format("1yBill Duration: %s", result.toString()));
		InstrumentResults tBillResults = instrumentResults.getResults(date, "3yBondDecayed2y", false);
		System.out.println(String.format("3yBondDecayed2y Results: %s", tBillResults.toVerboseString()));
	}
}