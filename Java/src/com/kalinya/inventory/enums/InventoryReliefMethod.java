package com.kalinya.inventory.enums;

import java.math.BigDecimal;
import java.util.Comparator;

import com.kalinya.inventory.comparators.FifoComparator;
import com.kalinya.inventory.comparators.LifoComparator;
import com.kalinya.inventory.comparators.PriceHifoComparator;
import com.kalinya.inventory.comparators.PriceLifoComparator;
import com.kalinya.stubs.TransactionStub;

public enum InventoryReliefMethod {
	FIFO(new FifoComparator(), new FifoComparator()),
	LIFO(new LifoComparator(), new LifoComparator()),
	PRICE_HIFO(new PriceHifoComparator(), new PriceHifoComparator()),
	/**
	 * {@code MINIMIZE_REALIZED_PNL} requires that the relief method differ
	 * based on the position sign. Long positions are relieved by closing out
	 * the tax lot brought on at the highest price first. Short positions are
	 * relieved by closing out the tax lot with the lowest price.
	 */
	MINIMIZE_REALIZED_PNL(new PriceHifoComparator(), new PriceLifoComparator());
	
	private Comparator<TransactionStub> longPositionComparator;
	private Comparator<TransactionStub> shortPositionComparator;

	InventoryReliefMethod(Comparator<TransactionStub> longPositionComparator, Comparator<TransactionStub> shortPositionComparator) {
		setLongPositionComparator(longPositionComparator);
		setShortPositionComparator(shortPositionComparator);
	}

	public Comparator<TransactionStub> getLongPositionComparator() {
		return longPositionComparator;
	}

	private void setLongPositionComparator(Comparator<TransactionStub> longPositionComparator) {
		this.longPositionComparator = longPositionComparator;
	}

	public Comparator<TransactionStub> getShortPositionComparator() {
		return shortPositionComparator;
	}

	private void setShortPositionComparator(Comparator<TransactionStub> shortPositionComparator) {
		this.shortPositionComparator = shortPositionComparator;
	}

	public Comparator<? super TransactionStub> getComparatorForPosition(BigDecimal remainingPosition) {
		if(remainingPosition.signum() >= 0) {
			return getLongPositionComparator();
		} 
		return getShortPositionComparator();
	}
	
}
