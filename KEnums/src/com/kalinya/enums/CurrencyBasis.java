package com.kalinya.enums;
import org.apache.commons.lang3.text.WordUtils;
/**
 * The currency basis (local currency or base currency)
 *
 */
public enum CurrencyBasis {
	LOCAL,BASE;

	public String getName() {
		return WordUtils.capitalizeFully(name());
	}
}
