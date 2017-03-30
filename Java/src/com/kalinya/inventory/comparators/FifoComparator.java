package com.kalinya.inventory.comparators;

import com.kalinya.stubs.TransactionStub;

public class FifoComparator extends AbstractInventoryReliefComparator {
	@Override
	public int compare(TransactionStub t1, TransactionStub t2) {
		if(!t1.getInstrumentId().equalsIgnoreCase(t2.getInstrumentId())) {
			return t2.getInstrumentId().compareTo(t1.getInstrumentId());
		}
		if(t1.getTradeDate().compareTo(t2.getTradeDate()) == 0) {
			return t1.getTransactionId().compareTo(t2.getTransactionId());
		}
		return t1.getTradeDate().compareTo(t2.getTradeDate());
	}
}
