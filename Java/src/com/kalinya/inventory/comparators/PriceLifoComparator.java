package com.kalinya.inventory.comparators;

import com.kalinya.stubs.TransactionStub;

public class PriceLifoComparator extends AbstractInventoryReliefComparator {
	@Override
	public int compare(TransactionStub t1, TransactionStub t2) {
		if(!t1.getInstrumentId().equalsIgnoreCase(t2.getInstrumentId())) {
			return t2.getInstrumentId().compareTo(t1.getInstrumentId());
		}
		if(t1.getPrice().compareTo(t2.getPrice()) == 0) {
			return t1.getTransactionId().compareTo(t2.getTransactionId());
		}
		return t1.getPrice().compareTo(t2.getPrice());
	}
}
