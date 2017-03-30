package com.kalinya.stubs;

import java.math.BigDecimal;
import java.util.Date;

public interface TranLookup {
	public String getInstrumentId();
	public String getTransactionId();
	public String getPortfolio();
	public String getBusinessUnit();
	public String getLegalEntity();
	public String getAccount();
	public Date getTradeDate();
	public BigDecimal getOriginalPosition();
	public BigDecimal getRemainingPosition();
	public BigDecimal getPrice();
}
