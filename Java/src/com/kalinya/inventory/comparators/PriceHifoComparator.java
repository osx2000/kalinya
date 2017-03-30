package com.kalinya.inventory.comparators;

import com.kalinya.stubs.TransactionStub;

public class PriceHifoComparator extends AbstractInventoryReliefComparator {
	
	public PriceHifoComparator(){
	}
	
	@Override
	public int compare(TransactionStub t1, TransactionStub t2) {
		if(!t2.getInstrumentId().equalsIgnoreCase(t1.getInstrumentId())) {
			return t2.getInstrumentId().compareTo(t1.getInstrumentId());
		}
		if(t2.getPrice().compareTo(t1.getPrice()) == 0) {
			return t2.getTransactionId().compareTo(t1.getTransactionId());
		}
		return t2.getPrice().compareTo(t1.getPrice());
	}
}
