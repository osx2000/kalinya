package com.kalinya.inventory.closeoutdefinitions;

import com.kalinya.stubs.TransactionStub;

/**
 * Transaction match key class for matching shareable transactions. Uses
 * matching criteria of Instrument, Portfolio, Account (IPA)
 */
public class TranMatchKeyIPA extends TranMatchKeyIP {

	public TranMatchKeyIPA(TransactionStub transaction) {
		super(transaction);
	}

	@Override
	public boolean matchesOnInternalAccount() {
		return true;
	}
}
