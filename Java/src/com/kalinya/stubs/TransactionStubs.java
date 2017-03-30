package com.kalinya.stubs;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class TransactionStubs {
	Set<TransactionStub> transactions;
	//TODO: maybe change this from a Set to a Map?
	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() + ">, Size [" + transactions.size() + "]";
	}

	public TransactionStubs() {
		transactions = new HashSet<>();
	}

	public TransactionStubs(Comparator<? super TransactionStub> comparator) {
		transactions = new TreeSet<>(comparator);
	}

	public void add(TransactionStub transactionStub) {
		transactions.add(transactionStub);
	}

	public void addAll(TransactionStubs transactionStubs) {
		this.transactions.addAll(transactionStubs.getTransactions());
	}

	public Set<TransactionStub> getTransactions() {
		return transactions;
	}

	public BigDecimal getNetPosition() {
		return getNetPosition(null);
	}

	public BigDecimal getNetPosition(Date date) {
		BigDecimal netPosition = BigDecimal.ZERO;
		for(TransactionStub transactionStub: transactions) {
			if(date == null || transactionStub.getTradeDate().compareTo(date) <= 0) {
				netPosition = netPosition.add(transactionStub.getRemainingPosition());
			}
		}
		return netPosition;
	}

	public String getInstrumentIdentifier(String transactionIdentifier) {
		for(TransactionStub transactionStub: transactions) {
			if(transactionStub.getTransactionId().equalsIgnoreCase(transactionIdentifier)) {
				return transactionStub.getInstrumentId();
			}
		}
		throw new IllegalStateException("Failed to retrieve any transactions with a transaction identifier [" + transactionIdentifier + "]");
	}

	public TransactionStub getTransaction(String transactionIdentifier) {
		for(TransactionStub transactionStub: transactions) {
			if(transactionIdentifier.equalsIgnoreCase(transactionStub.getTransactionId())) {
				return transactionStub;
			}
		}
		throw new IllegalArgumentException("Failed to find transaction identifier [" + transactionIdentifier + "]");
	}

	public int getCount() {
		return getTransactions().size();
	}

	public boolean contains(String transactionId) {
		for(TransactionStub transaction: transactions) {
			if(transaction.getTransactionId().equalsIgnoreCase(transactionId)) {
				return true;
			}
		}
		return false;
	}
}
