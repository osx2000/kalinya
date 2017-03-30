package com.kalinya.stubs;

import java.math.BigDecimal;
import java.util.Date;

import com.kalinya.inventory.comparators.PriceHifoComparator;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.PluginUtil;

public class TransactionStub implements Comparable<TransactionStub>, TranLookup {
	private String instrumentId;
	private String transactionId;
	private String portfolio;
	private String account;
	private String businessUnit;
	private String legalEntity;
	private Date tradeDate;
	private BigDecimal originalPosition;
	private BigDecimal remainingPosition;
	private BigDecimal price;
	private BigDecimal realizedPnl;

	public TransactionStub(String instrumentId, String transactionId, String portfolio, String account, Date tradeDate, BigDecimal originalPosition, BigDecimal price) {
		setInstrumentId(instrumentId);
		setTransactionId(transactionId);
		setPortfolio(portfolio);
		setAccount(account);
		setTradeDate(tradeDate);
		setOriginalPosition(originalPosition);
		setRemainingPosition(originalPosition);
		setPrice(price);
		//TODO: set businessUnit and legalEntity
	}

	private void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}
	
	private void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	private void setAccount(String account) {
		this.account = account;
	}

	private void setPortfolio(String portfolio) {
		this.portfolio = portfolio;
	}

	private void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	
	private void setOriginalPosition(BigDecimal originalPosition) {
		this.originalPosition = originalPosition;
	}
	
	private void setRemainingPosition(BigDecimal remainingPosition) {
		this.remainingPosition = remainingPosition;
	}
	
	private void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() 
				//+ ">, InsId [" + getInstrumentId()
				+ ">, TranId [" + getTransactionId() + "]";
				/*+ "], Portfolio [" + getPortfolio()
				+ "], Account [" + getAccount()
				+ "], TradeDate [" + PluginHelper.formatDate(getTradeDate()) 
				+ "], OrigPosition [" + PluginHelper.formatDouble(getOriginalPosition().doubleValue()) 
				+ "], RemainingPosition [" + PluginHelper.formatDouble(getRemainingPosition().doubleValue())
				+ "], Price [" + PluginHelper.formatPrice(getPrice().doubleValue()) + "]";*/
	}

	public boolean isFutures() {
		//TODO
		return false;
	}

	@Override
	public String getInstrumentId() {
		return instrumentId;
	}

	@Override
	public String getTransactionId() {
		return transactionId;
	}
	
	@Override
	public String getPortfolio() {
		return portfolio;
	}
	
	@Override
	public String getBusinessUnit() {
		// TODO Auto-generated method stub
		return businessUnit;
	}

	@Override
	public String getLegalEntity() {
		return legalEntity;
	}

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public Date getTradeDate() {
		return tradeDate;
	}

	@Override
	public BigDecimal getOriginalPosition() {
		return originalPosition;
	}

	@Override
	public BigDecimal getRemainingPosition() {
		return remainingPosition;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	public void closeoutPosition(BigDecimal closeoutPosition) {
		/*
		 * Get the amount to closeout. It will be the smaller of the
		 * closeoutPosition amount and the remaining quantity of this
		 * transaction
		 */
		BigDecimal amountToCloseout = closeoutPosition.abs().min(remainingPosition.abs());
		amountToCloseout = amountToCloseout.multiply(NumberUtil.newBigDecimal(closeoutPosition.signum()));
		remainingPosition = remainingPosition.subtract(amountToCloseout);
	}

	@Override
	public int compareTo(TransactionStub o) {
		//I don't think we want this on the transaction
		return new PriceHifoComparator().compare(this, o);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
        int result = instrumentId == null ? 0 : instrumentId.hashCode();
        result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
        return result;
	}
	
	@Override
	public boolean equals(Object o) {
        if(!(o instanceof TransactionStub)) {
            return false;
        }
        if(instrumentId.equalsIgnoreCase(((TransactionStub) o).getInstrumentId())
        		&& transactionId.equalsIgnoreCase(((TransactionStub) o).getTransactionId())) {
        	return true;
        }
        return false;
    }

	public BigDecimal getRealizedPnl() {
		return realizedPnl;
	}

	public void setRealizedPnl(BigDecimal realizedPnl) {
		this.realizedPnl = realizedPnl;
	}

}
