package com.kalinya.inventory.closeoutdefinitions;

import java.util.HashSet;
import java.util.Set;

import com.kalinya.inventory.enums.TranMatchDimension;
import com.kalinya.stubs.TransactionStub;

/*
 * Transaction match key class for matching shareable transactions. Uses
 * matching criteria of Instrument only (I)
 */

public class TranMatchKeyI implements Comparable<TranMatchKeyI> {
	private String instrumentId;
	private String portfolio;
	private String account;
	private String legalEntity;
	private String businessUnit;
	private Set<TranMatchDimension> tranMatchCriteria;

	public TranMatchKeyI(TransactionStub transaction) {
		tranMatchCriteria = new HashSet<TranMatchDimension>();
		//Instrument is a mandatory tran match criterion
		setInstrumentId(transaction.getInstrumentId());
		tranMatchCriteria.add(TranMatchDimension.INSTRUMENT);

		if(matchesOnInternalPortfolio()) {
			setPortfolio(transaction.getPortfolio());
			tranMatchCriteria.add(TranMatchDimension.INTERNAL_PORTFOLIO);
		}
		
		if(matchesOnInternalAccount()) {
			setAccount(transaction.getAccount());
			tranMatchCriteria.add(TranMatchDimension.INTERNAL_ACCOUNT);
		}
		
		if(matchesOnInternalLegalEntity()) {
			setLegalEntity(transaction.getLegalEntity());
			tranMatchCriteria.add(TranMatchDimension.INTERNAL_LEGAL_ENTITY);
		}
		
		if(matchesOnInternalBusinessUnit()) {
			setBusinessUnit(transaction.getBusinessUnit());
			tranMatchCriteria.add(TranMatchDimension.INTERNAL_BUSINESS_UNIT);
		}
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() 
				+ "> InstrumentId [" + getInstrumentId() + "]" 
				+ (matchesOnInternalPortfolio() ? ", Portfolio [" + getPortfolio() + "]" : "")
				+ (matchesOnInternalAccount() ? ", Account [" + getAccount() + "]" : "")
				+ (matchesOnInternalLegalEntity() ? ", LegalEntity [" + getLegalEntity() + "]" : "")
				+ (matchesOnInternalBusinessUnit() ? ", BusinessUnit [" + getBusinessUnit() + "]" : "");
	}

	public final boolean matchesOnInstrument() {
		//The definition must match on the instrument.  This must not be overridden
		return true;
	}

	public boolean matchesOnInternalPortfolio() {
		return false;
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

	private void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}
	
	public String getInstrumentId() {
		return this.instrumentId;
	}

	public String getPortfolio() {
		return portfolio;
	}

	private void setPortfolio(String portfolio) {
		this.portfolio = portfolio;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getLegalEntity() {
		return legalEntity;
	}

	public void setLegalEntity(String legalEntity) {
		this.legalEntity = legalEntity;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof TranMatchKeyI)) {
			return false;
		}
		TranMatchKeyI that = (TranMatchKeyI) o;
		if(!instrumentId.equalsIgnoreCase(that.getInstrumentId())) {
			return false;
		}
		if(matchesOnInternalPortfolio()
				&& !portfolio.equalsIgnoreCase(that.getPortfolio())) {
			return false;
		}
		if(matchesOnInternalAccount()
				&& !account.equalsIgnoreCase(that.getAccount())) {
			return false;
		}
		if(matchesOnInternalLegalEntity()
				&& !legalEntity.equalsIgnoreCase(that.getLegalEntity())) {
			return false;
		}
		if(matchesOnInternalBusinessUnit()
				&& !businessUnit.equalsIgnoreCase(that.getBusinessUnit())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = instrumentId == null ? 0 : instrumentId.hashCode();
		if(matchesOnInternalPortfolio()) {
			result = prime * result + ((portfolio == null) ? 0 : portfolio.hashCode());
		}
		if(matchesOnInternalAccount()) {
			result = prime * result + ((account == null) ? 0 : account.hashCode());
		}
		if(matchesOnInternalLegalEntity()) {
			result = prime * result + ((legalEntity == null) ? 0 : legalEntity.hashCode());
		}
		if(matchesOnInternalBusinessUnit()) {
			result = prime * result + ((businessUnit == null) ? 0 : businessUnit.hashCode());
		}
        return result;
	}
	
	@Override
	public int compareTo(TranMatchKeyI that) {
		if(!instrumentId.equalsIgnoreCase(that.getInstrumentId())) {
			return instrumentId.compareTo(that.getInstrumentId());
		}
		if(matchesOnInternalPortfolio()
				&& !portfolio.equalsIgnoreCase(that.getPortfolio())) {
			return portfolio.compareTo(that.getPortfolio());
		}
		if(matchesOnInternalAccount()
				&& !account.equalsIgnoreCase(that.getAccount())) {
			return account.compareTo(that.getAccount());
		}
		if(matchesOnInternalLegalEntity()
				&& !legalEntity.equalsIgnoreCase(that.getLegalEntity())) {
			return legalEntity.compareTo(that.getLegalEntity());
		}
		if(matchesOnInternalBusinessUnit()
				&& !businessUnit.equalsIgnoreCase(that.getBusinessUnit())) {
			return businessUnit.compareTo(getBusinessUnit());
		}
		return 0;
	}
}
