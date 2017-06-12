package com.kalinya.performance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.kalinya.application.FindurSession;
import com.kalinya.enums.DayWeighting;
import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.performance.dimensions.PerformanceDimensions.Predefined;
import com.kalinya.performance.portfoliostatistics.PortfolioStatistics;
import com.kalinya.util.Assertions;
import com.kalinya.util.DateUtil;
import com.olf.openrisk.application.Session;

final public class PerformanceFactory {
	private FindurSession findurSession;
	public static final int SCALE = 12;
	public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

	private DebugLevel debugLevel;
	private DayWeighting dayWeighting = DayWeighting.END_OF_DAY;

	private PerformanceFactory() {
		setDebugLevel(DebugLevel.LOW);
	}

	public PerformanceFactory(FindurSession findurSession) {
		this();
		this.findurSession = findurSession;
	}

	@Override
	public String toString() {
		return getFindurSession().toString();
	}

	public FindurSession getFindurSession() {
		return findurSession;
	}

	public Session getSession() {
		return findurSession.getSession();
	}

	public DebugLevel getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(DebugLevel debugLevel) {
		this.debugLevel = debugLevel;
	}

	public int getScale() {
		return SCALE ;
	}

	public RoundingMode getRoundingMode() {
		return ROUNDING_MODE;
	}

	public PerformanceDimensions createPerformanceDimensions() {
		return new PerformanceDimensions();
	}

	public Portfolios getPortfolios(DataSource dataSource) {
		return dataSource.getPortfolios();
	}

	public Positions getPositions(DataSource dataSource) {
		return dataSource.getPositions();
	}

	public SecurityMasters getSecurityMasters(DataSource dataSource) {
		return dataSource.getSecurityMasterData();
	}

	public Instruments getInstruments(DataSource dataSource) {
		return dataSource.getInstruments();
	}

	public InstrumentLegs getInstrumentLegs(DataSource dataSource) {
		return dataSource.getInstrumentLegs();
	}

	public Cashflows getCashflows(DataSource dataSource) {
		return dataSource.getCashflows();
	}

	//TODO: looks like we don't need BenchmarkAssociations
	public PerformanceResult calculateResults(Portfolios portfolios, BenchmarkAssociations benchmarkAssociations,
			SecurityMasters securityMasters, Instruments instruments, InstrumentLegs instrumentLegs,
			Positions positions, Cashflows cashflows, PerformanceDimensions performanceDimensions) {
		Assertions.notNullOrEmpty("Portfolios", portfolios);
		Assertions.notNullOrEmpty("BenchmarkAssociations", benchmarkAssociations);
		Assertions.notNullOrEmpty("SecurityMasters", securityMasters);
		Assertions.notNullOrEmpty("Instruments", instruments);
		Assertions.notNullOrEmpty("InstrumentLegs", instrumentLegs);
		Assertions.notNullOrEmpty("Positions", positions);
		Assertions.notNullOrEmpty("Cashflows", cashflows);
		Assertions.notNullOrEmpty("PerformanceDimensions", performanceDimensions);
		performanceDimensions.validate();

		PerformanceResult performanceResult = null;
		if(performanceDimensions.equals(PerformanceDimensions.BY_DATE_BY_PORTFOLIO)) {
			performanceResult = new PortfolioPerformanceResult(this, portfolios, benchmarkAssociations,
					securityMasters, instruments, instrumentLegs, positions, cashflows);
		} else {
			performanceResult = new PerformanceResult(this, portfolios, benchmarkAssociations,
					securityMasters, instruments, instrumentLegs, positions, cashflows);
		}
		//TODO: toString() method doesn't resolve until there are performanceDimensions
		performanceResult.calculateReturns(performanceDimensions);
		return performanceResult;
	}

	public PerformanceResult calculateResults(DataSource dataSource, PerformanceDimensions performanceDimensions) {
		Assertions.notNull("DataSource", dataSource);
		Assertions.notNullOrEmpty("PerformanceDimensions", performanceDimensions);
		performanceDimensions.validate();
		PerformanceResult performanceResult = new PerformanceResult(this, dataSource);
		performanceResult.calculateReturns(performanceDimensions);
		return performanceResult;
	}
	
	public PortfolioStatistics createPortfolioStatistics() {
		return new PortfolioStatistics(this);
	}
	
	public static BigDecimal getChainLinkedReturn(BigDecimal...ratesOfReturn) {
		BigDecimal chainLinkedRateOfReturn = BigDecimal.ONE;
		for(BigDecimal rateOfReturn: ratesOfReturn) {
			if(rateOfReturn == null) {
				rateOfReturn = BigDecimal.ZERO;
			}
			chainLinkedRateOfReturn = chainLinkedRateOfReturn.multiply(rateOfReturn.add(BigDecimal.ONE));
		}
		return chainLinkedRateOfReturn.subtract(BigDecimal.ONE);
	}

	public static BigDecimal getChainLinkedReturn(Collection<? extends BigDecimal> ratesOfReturn) {
		return getChainLinkedReturn(ratesOfReturn.toArray(new BigDecimal[ratesOfReturn.size()]));
	}
	
	public void setDayWeighting(DayWeighting dayWeighting) {
		this.dayWeighting = dayWeighting;
	}

	public DayWeighting getDayWeighting() {
		return dayWeighting;
	}

	public RuntimeArguments parseRuntimeArguments(String[] args) {
		CommandLineParser commandLineParser = new DefaultParser();
		Options commandLineOptions = RuntimeArguments.getCommandLineOptions();
		if(args == null || args.length == 0) {
			args = getTestArguments();
		}
		boolean attachToFindur = false;
		Date startDate = null;
		Date endDate = null;
		Portfolios portfolios = null;
		DayWeighting dayWeighting = null;
		String performanceDimensionsName = null;
		String positionsFilePath = null;
		String securityMasterFilePath = null;
		String portfoliosFilePath = null;
		String benchmarkAssociationsFilePath = null;
		String performanceResultsExtractFilePath = null;
		try {
			// parse the command line arguments
			CommandLine commandLine = commandLineParser.parse(commandLineOptions, args);

			RuntimeArgumentName optionName = RuntimeArgumentName.ATTACH_TO_FINDUR;
			if(commandLine.hasOption(optionName.getLongName())) {
				attachToFindur = Boolean.valueOf(commandLine.getOptionValue(optionName.getLongName()));
			}
			optionName = RuntimeArgumentName.START_DATE;
			if(commandLine.hasOption(optionName.getLongName())) {
				startDate = DateUtil.parseDate(commandLine.getOptionValue(optionName.getLongName()));
			}
			optionName = RuntimeArgumentName.END_DATE;
			if(commandLine.hasOption(optionName.getLongName())) {
				endDate = DateUtil.parseDate(commandLine.getOptionValue(optionName.getLongName()));
			}
			optionName = RuntimeArgumentName.PORTFOLIOS;
			if(commandLine.hasOption(optionName.getLongName())) {
				String[] portfolioNames = commandLine.getOptionValues(optionName.getLongName());
				portfolios = getPortfolios(portfolioNames);
			}
			optionName = RuntimeArgumentName.PERFORMANCE_DIMENSIONS;
			if(commandLine.hasOption(optionName.getLongName())) {
				performanceDimensionsName  = commandLine.getOptionValue(optionName.getLongName());
			}
			optionName = RuntimeArgumentName.POSITIONS_FILE_PATH;
			if(commandLine.hasOption(optionName.getLongName())) {
				positionsFilePath  = commandLine.getOptionValue(optionName.getLongName());
			}
			optionName = RuntimeArgumentName.SECURITY_MASTER_FILE_PATH;
			if(commandLine.hasOption(optionName.getLongName())) {
				securityMasterFilePath  = commandLine.getOptionValue(optionName.getLongName());
			}
			optionName = RuntimeArgumentName.PORTFOLIOS_FILE_PATH;
			if(commandLine.hasOption(optionName.getLongName())) {
				portfoliosFilePath  = commandLine.getOptionValue(optionName.getLongName());
			}
			optionName = RuntimeArgumentName.BENCHMARK_ASSOCIATIONS_FILE_PATH;
			if(commandLine.hasOption(optionName.getLongName())) {
				benchmarkAssociationsFilePath = commandLine.getOptionValue(optionName.getLongName());
			}
			optionName = RuntimeArgumentName.PERFORMANCE_RESULTS_EXTRACT_FILE_PATH;
			if(commandLine.hasOption(optionName.getLongName())) {
				performanceResultsExtractFilePath  = commandLine.getOptionValue(optionName.getLongName());
			}
			optionName = RuntimeArgumentName.DAY_WEIGHTING;
			if(commandLine.hasOption(optionName.getLongName())) {
				String dayWeightingName = commandLine.getOptionValue(optionName.getLongName());
				dayWeighting = DayWeighting.fromName(dayWeightingName);
			}
		} catch(ParseException e) {
			throw new RuntimeException("Unexpected exception parsing parameters [" + e.getMessage() + "]");
		}
		RuntimeArguments runtimeArguments = new RuntimeArguments.Builder()
				.withStartDate(startDate)
				.withEndDate(endDate)
				.withPortfolios(portfolios)
				.withDayWeighting(dayWeighting)
				.attachToFindur(attachToFindur)
				.withPerformanceDimensions(performanceDimensionsName)
				.withPositionsFilePath(positionsFilePath)
				.withSecurityMasterFilePath(securityMasterFilePath)
				.withPortfoliosFilePath(portfoliosFilePath)
				.withBenchmarkAssociationsFilePath(benchmarkAssociationsFilePath)
				.withPerformanceResultsExtractFilePath(performanceResultsExtractFilePath)
				.build();
		return runtimeArguments;
	}
	
	public static Portfolios getPortfolios(String[] portfolioNames) {
		Portfolios portfolios = Portfolios.create();
		if(portfolioNames != null) {
			for(String portfolioName: portfolioNames) {
				portfolios.add(Portfolio.create(portfolioName));
			}
		}
		return portfolios;
	}

	private static String[] getTestArguments() {
		List<String> args = new ArrayList<>();
		args.add("--" + RuntimeArgumentName.ATTACH_TO_FINDUR.getLongName() + "=false");
		args.add("--" + RuntimeArgumentName.START_DATE.getLongName() + "=1-Jan-2017");
		args.add("--" + RuntimeArgumentName.END_DATE.getLongName() + "=4-Jan-2017");
		String portfolioNames = null;
		portfolioNames = "CashFundAssets";
		portfolioNames = "CashFundAssets,CashFundLiabilities";
		args.add("--" + RuntimeArgumentName.PORTFOLIOS.getLongName() + "=" + portfolioNames );
		
		//File path arguments
		args.add("--" + RuntimeArgumentName.POSITIONS_FILE_PATH.getLongName() 
						//+ "=" + Configurator.POSITIONS_FILE_PATH_MULTIPLE_PORTFOLIOS);
						+ "=" + Configurator.POSITIONS_FILE_PATH_PURCHASE);
		args.add("--" + RuntimeArgumentName.SECURITY_MASTER_FILE_PATH.getLongName() 
						+ "=" + Configurator.SECURITY_MASTER_FILE_PATH);
		args.add("--" + RuntimeArgumentName.PORTFOLIOS_FILE_PATH.getLongName() 
						+ "=" + Configurator.PORTFOLIOS_FILE_PATH);
		args.add("--" + RuntimeArgumentName.BENCHMARK_ASSOCIATIONS_FILE_PATH.getLongName() 
						+ "=" + Configurator.BENCHMARK_ASSOCIATIONS_FILE_PATH);
		args.add("--" + RuntimeArgumentName.PERFORMANCE_RESULTS_EXTRACT_FILE_PATH.getLongName() 
						+ "=" + Configurator.PERFORMANCE_RESULTS_EXTRACT_FILE_PATH);
		args.add("--" + RuntimeArgumentName.DAY_WEIGHTING.getLongName() + "=" + DayWeighting.END_OF_DAY.getName());
		
		//PerformanceDimensions arguments
		Predefined dim = null;
		//a. Core dimensions
		dim = PerformanceDimensions.Predefined.ByDate;
		dim = PerformanceDimensions.Predefined.CumulativeByPortfolio;
		dim = PerformanceDimensions.Predefined.CumulativeByLeg;
		dim = PerformanceDimensions.Predefined.ByDateByLeg;
		dim = PerformanceDimensions.Predefined.ByDateByPortfolio;
		
		//b. Using security master meta data
		dim = PerformanceDimensions.Predefined.ByDateByIndustry;
		dim = PerformanceDimensions.Predefined.ByDateBySector;
		dim = PerformanceDimensions.Predefined.ByDateByAssetClass;
		dim = PerformanceDimensions.Predefined.ByDateByRiskGroup;
		dim = PerformanceDimensions.Predefined.ByDateByInstrumentClass;
		
		//c. Using multiple dimensions of security master meta data 
		dim = PerformanceDimensions.Predefined.ByDateByInstrumentClassByIndustry;
		
		dim = PerformanceDimensions.Predefined.ByDateByLeg;
		
		args.add("--" + RuntimeArgumentName.PERFORMANCE_DIMENSIONS.getLongName() + "=" + dim.getName());
		
		return args.toArray(new String[args.size()]);
	}
}
