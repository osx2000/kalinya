package com.kalinya.inventory.enums;

public enum TranMatchDimension {
	/**
	 * Inventory will be matched on instrument. Required.
	 */
	INSTRUMENT,
	/**
	 * Inventory will be matched on internal portfolio
	 */
	INTERNAL_PORTFOLIO,
	/**
	 * Inventory will be matched on internal account. This may be required for futures contracts
	 */
	INTERNAL_ACCOUNT,
	/**
	 * Inventory will be matched on internal legal entity
	 */
	INTERNAL_LEGAL_ENTITY,
	/**
	 * Inventory will be matched on internal business unit
	 */
	INTERNAL_BUSINESS_UNIT;
}
