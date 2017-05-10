package com.kalinya.performance;

import org.apache.commons.cli.Option;
import com.kalinya.enums.DayWeighting;
import org.apache.commons.cli.Option.Builder;

public enum RuntimeArgumentName {
	ATTACH_TO_FINDUR("f","attach-to-findur", false, 1, "Attach to Findur session. Defaults to [false]"),
	START_DATE("sd","start-date", false, 1, "Enter the runtime start date"),
	END_DATE("ed","end-date", false, 1, "Enter the runtime end date"),
	PORTFOLIOS("p","portfolios", false, Option.UNLIMITED_VALUES, ',', "Enter the portfolios to include"),
	PERFORMANCE_DIMENSIONS("pd","performance-dimensions", true, 1, "Enter the runtime performance dimensions"), 
	POSITIONS_FILE_PATH("pfp","positions-file-path", false, 1, "Enter the path to the positions file"), 
	SECURITY_MASTER_FILE_PATH("smfp","security-master-file-path", false, 1, "Enter the path to the security master CSV file"), 
	PORTFOLIOS_FILE_PATH("ptfp","portfolios-file-path", false, 1, "Enter the path to the portfolio details CSV file"), 
	BENCHMARK_ASSOCIATIONS_FILE_PATH("bafp","benchmark-associations-file-path", false, 1, "Enter the path to the benchmark associations CSV file"), 
	PERFORMANCE_RESULTS_EXTRACT_FILE_PATH("refp","results-extract-file-path", false, 1, "Enter the path to the performance results extract CSV file"), 
	DAY_WEIGHTING("dw","day-weighting", false, 1,"Enter the day weighting [" + DayWeighting.getNames() + "]");
	
	private String shortName;
	private String longName;
	private boolean isRequired;
	private int numberOfArguments;
	private char valueSeparator = ',';
	private String description;
	
	RuntimeArgumentName(String shortName, String longName, boolean isRequired, int numberOfArguments, String description) {
		this(shortName, longName, isRequired, numberOfArguments, ' ', description);
	}
	
	RuntimeArgumentName(String shortName, String longName, boolean isRequired, int numberOfArguments, char valueSeparator, String description) {
		this.shortName = shortName;
		this.longName = longName;
		this.isRequired = isRequired;
		this.numberOfArguments = numberOfArguments;
		this.valueSeparator = valueSeparator;
		this.description = description;
	}
	
	
	String getShortName() {
		return shortName;
	}
	String getLongName() {
		return longName;
	}
	public boolean isRequired() {
		return isRequired;
	}
	public String getDescription() {
		return description;
	}
	public int getNumberOfArguments() {
		return numberOfArguments;
	}
	
	public Option getOption() {
		Builder optionBuilder = Option.builder(getShortName())
				.longOpt(getLongName())
				.required(isRequired())
				.valueSeparator(getValueSeparator())
				.desc(getDescription())
				.numberOfArgs(getNumberOfArguments());
		
		return optionBuilder.build();
	}
	
	public char getValueSeparator() {
		return valueSeparator;
	}
}
