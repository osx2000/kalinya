package com.kalinya.results;

import java.util.Set;
import java.util.TreeSet;

import com.kalinya.util.ToStringBuilder;

public class ScenarioResults {
	//TODO: extend BaseSet<Results>?
	private static ScenarioResults EMPTY_RESULTS = new ScenarioResults();
	Set<Results> scenarioResults;
	
	public ScenarioResults() {
		scenarioResults = new TreeSet<>();
	}
	
	public static ScenarioResults create() {
		return EMPTY_RESULTS;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.build();
	}

	public InstrumentResults getInstrumentResults() {
		for(Results results: getScenarioResults()) {
			if(results instanceof InstrumentResults) {
				return (InstrumentResults) results;
			}
		}
		return InstrumentResults.create();
	}

	private Set<Results> getScenarioResults() {
		return scenarioResults;
	}
	
}
