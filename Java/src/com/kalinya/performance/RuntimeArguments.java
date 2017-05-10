package com.kalinya.performance;

import java.util.Date;

import org.apache.commons.cli.Options;

import com.kalinya.enums.DayWeighting;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.util.ToStringBuilder;

public final class RuntimeArguments {
	private Date startDate;
	private Date endDate;
	private Portfolios portfolios;
	private DayWeighting dayWeighting;
	private boolean attachToFindur;
	private PerformanceDimensions performanceDimensions;
	private String positionsFilePath;
	private String portfoliosFilePath;
	private String benchmarkAssociationsFilePath;
	private String securityMasterFilePath;
	private String performanceResultsExtractFilePath;
	
	private RuntimeArguments() {
		attachToFindur = false;
		portfolios = new Portfolios();
	}
	
	public RuntimeArguments(Builder builder) {
		this();
		startDate = builder.startDate;
		endDate = builder.endDate;
		portfolios = builder.portfolios;
		dayWeighting = builder.dayWeighting;
		attachToFindur = builder.attachToFindur;
		performanceDimensions = builder.performanceDimensions;
		positionsFilePath = builder.positionsFilePath;
		benchmarkAssociationsFilePath = builder.benchmarkAssociationsFilePath;
		portfoliosFilePath = builder.portfoliosFilePath;
		performanceResultsExtractFilePath = builder.performanceResultsExtractFilePath;
		securityMasterFilePath = builder.securityMasterFilePath;
	}
	
	public static class Builder {
		private Date startDate;
		private Date endDate;
		private Portfolios portfolios;
		private boolean attachToFindur;
		private PerformanceDimensions performanceDimensions;
		private String positionsFilePath;
		private String benchmarkAssociationsFilePath;
		private String portfoliosFilePath;
		private String performanceResultsExtractFilePath;
		private String securityMasterFilePath;
		private DayWeighting dayWeighting;
		
		public Builder() {
		}
		
		public Builder withStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}
		
		public Builder withEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}
		
		public Builder withPortfolios(Portfolios portfolios) {
			this.portfolios = portfolios;
			return this;
		}
		
		public Builder withDayWeighting(DayWeighting dayWeighting) {
			this.dayWeighting = dayWeighting;
			return this;
		}
		
		public Builder withDayWeighting(String dayWeighting) {
			this.dayWeighting = DayWeighting.fromName(dayWeighting);
			return this;
		}
		
		public Builder attachToFindur() {
			return attachToFindur(true);
		}
		
		public Builder attachToFindur(boolean attachToFindur) {
			this.attachToFindur = attachToFindur;
			return this;
		}
		
		public Builder withPerformanceDimensions(String performanceDimensionsName) {
			this.performanceDimensions = PerformanceDimensions.Predefined.fromName(performanceDimensionsName).getPerformanceDimensions();
			return this;
		}
		
		public Builder withPerformanceDimensions(PerformanceDimensions performanceDimensions) {
			this.performanceDimensions = performanceDimensions;
			return this;
		}
		
		public Builder withPositionsFilePath(String positionsFilePath) {
			this.positionsFilePath = positionsFilePath;
			return this;
		}
		
		public Builder withBenchmarkAssociationsFilePath(String benchmarkAssociationsFilePath) {
			this.benchmarkAssociationsFilePath = benchmarkAssociationsFilePath;
			return this;
		}
		
		public Builder withPortfoliosFilePath(String portfoliosFilePath) {
			this.portfoliosFilePath = portfoliosFilePath;
			return this;
		}
		
		public Builder withPerformanceResultsExtractFilePath(String performanceResultsExtractFilePath) {
			this.performanceResultsExtractFilePath = performanceResultsExtractFilePath;
			return this;
		}
		
		public Builder withSecurityMasterFilePath(String securityMasterFilePath) {
			this.securityMasterFilePath = securityMasterFilePath;
			return this;
		}
		
		public RuntimeArguments build() {
			return new RuntimeArguments(this);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("StartDate", getStartDate())
				.append("EndDate", getEndDate())
				.append("Portfolios", getPortfolioNames())
				.append("DayWeighting", dayWeighting.getName())
				.append("AttachToFindur", getAttachToFindur())
				.append("PerformanceDimensions", getPerformanceDimensions().toString())
				.append("PositionsFilePath", getPositionsFilePath())
				.append("SecurityMasterFilePath", getSecurityMasterFilePath())
				.append("PortfoliosFilePath", getPortfoliosFilePath())
				.append("BenchmarkAssociationsFilePath", getBenchmarkAssociationsFilePath())
				.append("PerformanceResultsExtractFilePath", getPerformanceResultsExtractFilePath())
				.withLineBreaks()
				.build();
	}

	public boolean getAttachToFindur() {
		return attachToFindur;
	}

	public Portfolios getPortfolios() {
		return portfolios;
	}

	private String getPortfolioNames() {
		if(getPortfolios() != null) {
			return getPortfolios().toString();
		} else {
			return "";
		}
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public PerformanceDimensions getPerformanceDimensions() {
		return performanceDimensions;
	}

	public static Options getCommandLineOptions() {
		Options commandLineOptions = new Options();

		RuntimeArgumentName argumentName = RuntimeArgumentName.ATTACH_TO_FINDUR;
		commandLineOptions.addOption(argumentName.ATTACH_TO_FINDUR.getOption());
		commandLineOptions.addOption(argumentName.START_DATE.getOption());
		commandLineOptions.addOption(argumentName.END_DATE.getOption());
		commandLineOptions.addOption(argumentName.PORTFOLIOS.getOption());
		commandLineOptions.addOption(argumentName.DAY_WEIGHTING.getOption());
		commandLineOptions.addOption(argumentName.PERFORMANCE_DIMENSIONS.getOption());
		
		//Import/export CSV file path options
		commandLineOptions.addOption(argumentName.POSITIONS_FILE_PATH.getOption());
		commandLineOptions.addOption(argumentName.SECURITY_MASTER_FILE_PATH.getOption());
		commandLineOptions.addOption(argumentName.PORTFOLIOS_FILE_PATH.getOption());
		commandLineOptions.addOption(argumentName.BENCHMARK_ASSOCIATIONS_FILE_PATH.getOption());
		commandLineOptions.addOption(argumentName.PERFORMANCE_RESULTS_EXTRACT_FILE_PATH.getOption());
		
		return commandLineOptions;
	}

	public String getPositionsFilePath() {
		return positionsFilePath;
	}
	
	public String getBenchmarkAssociationsFilePath() {
		return benchmarkAssociationsFilePath;
	}

	public String getPortfoliosFilePath() {
		return portfoliosFilePath;
	}

	public String getSecurityMasterFilePath() {
		return securityMasterFilePath;
	}

	public String getPerformanceResultsExtractFilePath() {
		return performanceResultsExtractFilePath;
	}

	public DayWeighting getDayWeighting() {
		return dayWeighting;
	}
}