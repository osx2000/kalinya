package com.kalinya.inventory.closeoutdefinitions;

import java.util.HashSet;
import java.util.Set;

import com.kalinya.inventory.enums.TranMatchDimension;

public class TranMatchCriteria {
	Set<TranMatchDimension> tranMatchCriteria;

	TranMatchCriteria() {
		tranMatchCriteria = new HashSet<TranMatchDimension>();
		//Instrument is a mandatory tran match criterion
		tranMatchCriteria.add(TranMatchDimension.INSTRUMENT);
		if(matchesOnInternalPortfolio()) {
			tranMatchCriteria.add(TranMatchDimension.INTERNAL_PORTFOLIO);
		}
		if(matchesOnInternalAccount()) {
			tranMatchCriteria.add(TranMatchDimension.INTERNAL_ACCOUNT);
		}
		if(matchesOnInternalLegalEntity()) {
			tranMatchCriteria.add(TranMatchDimension.INTERNAL_LEGAL_ENTITY);
		}
		if(matchesOnInternalBusinessUnit()) {
			tranMatchCriteria.add(TranMatchDimension.INTERNAL_BUSINESS_UNIT);
		}
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() 
				+ ">: MatchesOnInstrument [" + matchesOnInstrument() 
				+ "}, MatchesOnInternalPortfolio [" + matchesOnInternalPortfolio()
				+ "}, MatchesOnInternalAccount [" + matchesOnInternalAccount()
				+ "], MatchesOnInternalLegalEntity [" + matchesOnInternalLegalEntity() 
				+ "}, MatchesOnInternalBusinessUnit [" + matchesOnInternalBusinessUnit() + "]";
	}

	public final boolean matchesOnInstrument() {
		//The definition must match on the instrument.  This must not be overridden
		return true;
	}

	public boolean matchesOnInternalPortfolio() {
		//Default implementation matches on portfolio
		return true;
	}

	public boolean matchesOnInternalAccount() {
		return false;
	}

	public boolean matchesOnInternalLegalEntity() {
		return false;
	}

	private boolean matchesOnInternalBusinessUnit() {
		return false;
	}
}
