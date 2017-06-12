package com.kalinya.harness;

import com.kalinya.performance.BenchmarkAssociations;
import com.kalinya.performance.Configurator;
import com.kalinya.performance.PerformanceResult;
import com.kalinya.performance.Portfolios;
import com.kalinya.performance.SecurityMasters;

public class TestHarness {

	public static void main(String[] args) {
		SecurityMasters securityMasterData = SecurityMasters.load();
		System.out.println(String.format("SecurityMaster Data %s", securityMasterData.toVerboseString()));
		
		Portfolios portfolios = Portfolios.load();
		System.out.println(String.format("Portfolio Data %s", portfolios.toVerboseString()));

		BenchmarkAssociations benchmarkAssociations = BenchmarkAssociations.load();
		System.out.println(String.format("BenchmarkAssociations Data %s", benchmarkAssociations.toVerboseString()));
		
		String performanceValuesFilePath = Configurator.PERFORMANCE_RESULTS_EXTRACT_FILE_PATH;
		PerformanceResult performanceResults = null;
		
	}
}
