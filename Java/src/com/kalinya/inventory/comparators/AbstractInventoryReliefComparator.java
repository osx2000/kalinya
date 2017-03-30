package com.kalinya.inventory.comparators;

import java.util.Comparator;

import com.kalinya.stubs.TransactionStub;

public abstract class AbstractInventoryReliefComparator implements Comparator<TransactionStub> {
	@Override
	abstract public int compare(TransactionStub t1, TransactionStub t2);
}
