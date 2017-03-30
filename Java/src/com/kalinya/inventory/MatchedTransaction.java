package com.kalinya.inventory;

import java.math.BigDecimal;
import java.util.Date;

import com.kalinya.stubs.TransactionStub;
import com.kalinya.util.StringUtil;

public class MatchedTransaction {

	private TransactionStub taxLot;
	private TransactionStub closeoutTransaction;
	private BigDecimal position;
	private Date closeoutDate;
	private BigDecimal realizedPnl;

	public MatchedTransaction(TransactionStub taxLot, TransactionStub closeoutTransaction, BigDecimal position) {
		this.taxLot = taxLot;
		this.closeoutTransaction = closeoutTransaction;
		this.position = position;
		this.closeoutDate = closeoutTransaction.getTradeDate();
		calculateRealizedPnl();
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() 
				+ ">, InsId [" + taxLot.getInstrumentId() 
				+ "], TaxLot [" + taxLot.getTransactionId() 
				+ "], CloseoutTransaction [" + closeoutTransaction.getTransactionId() 
				+ "], Position [" + position 
				+ "], RealizedP&L [" + StringUtil.formatDouble(realizedPnl.doubleValue()) + "]";
	}

	private void calculateRealizedPnl() {
		BigDecimal closeoutQuantity = position.abs().min(taxLot.getRemainingPosition().abs());
		closeoutQuantity = closeoutQuantity.multiply(new BigDecimal(closeoutTransaction.getRemainingPosition().signum()));
		realizedPnl = closeoutQuantity.multiply(closeoutTransaction.getPrice().subtract(taxLot.getPrice()).negate());
	}

	public TransactionStub getTaxLot() {
		return taxLot;
	}

	public TransactionStub getClosedOutTransaction() {
		return closeoutTransaction;
	}

	public BigDecimal getPositionClosedOut() {
		return position;
	}

	public Date getCloseOutDate() {
		return closeoutDate;
	}

	public BigDecimal getRealizedPnl() {
		return realizedPnl;
	}
}
