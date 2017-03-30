package com.kalinya.performance.enums;

import com.kalinya.util.StringUtil;

/**
 * The currency basis (local currency or base currency)
 * @author Stephen
 *
 */
public enum CurrencyBasis {
	LOCAL,BASE;

	public String getName() {
		return StringUtil.toTitleCase(name());
	}
}
