package com.kalinya.inventory.closeoutdefinitions;

import com.kalinya.stubs.TransactionStub;

/*
 * Transaction match key class for matching shareable transactions. Uses
 * matching criteria of Instrument, Portfolio (IP)
 */
public class TranMatchKeyIP extends TranMatchKeyI {
	
	public TranMatchKeyIP(TransactionStub transaction) {
		super(transaction);
	}

	@Override
	public boolean matchesOnInternalPortfolio() {
		//Matches on portfolio
		return true;
	}
}
