package com.kalinya.inventory.comparators;

import com.kalinya.stubs.TransactionStub;

public class LifoComparator extends AbstractInventoryReliefComparator {
	@Override
	public int compare(TransactionStub t1, TransactionStub t2) {
		/*if(!t1.getInstrumentId().equalsIgnoreCase(t2.getInstrumentId())) {
			return t2.getInstrumentId().compareTo(t1.getInstrumentId());
		}
		if(t1.getTradeDate().compareTo(t2.getTradeDate()) == 0) {
			return t2.getTransactionId().compareTo(t1.getTransactionId());
		}
		return t2.getTradeDate().compareTo(t1.getTradeDate());*/

		//Would this below work?
		return new FifoComparator().compare(t2, t1);
	}
}
