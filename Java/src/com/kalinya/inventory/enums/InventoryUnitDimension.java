package com.kalinya.inventory.enums;

import com.kalinya.inventory.comparators.AbstractInventoryReliefComparator;
import com.kalinya.inventory.comparators.PriceHifoComparator;

public enum InventoryUnitDimension {
	HIFO_BY_PORTFOLIO(
			new PriceHifoComparator(),
			new TranMatchDimension[] { TranMatchDimension.INSTRUMENT, TranMatchDimension.INTERNAL_PORTFOLIO }),
	HIFO_BY_PORTFOLIO_BY_ACCOUNT(
			new PriceHifoComparator(),
			new TranMatchDimension[] { TranMatchDimension.INSTRUMENT, TranMatchDimension.INTERNAL_PORTFOLIO, TranMatchDimension.INTERNAL_ACCOUNT});

	private AbstractInventoryReliefComparator inventoryReliefComparator;
	private TranMatchDimension[] inventoryMatchDimensions;

	InventoryUnitDimension(AbstractInventoryReliefComparator inventoryReliefComparator,
			TranMatchDimension[] inventoryMatchDimensions) {
		this.inventoryReliefComparator = inventoryReliefComparator;
		this.inventoryMatchDimensions = inventoryMatchDimensions;
	}
	
	public AbstractInventoryReliefComparator getInventoryReliefComparator() {
		return inventoryReliefComparator;
	}
	
	public TranMatchDimension[] getInventoryMatchDimensions() {
		return inventoryMatchDimensions;
	}
}
