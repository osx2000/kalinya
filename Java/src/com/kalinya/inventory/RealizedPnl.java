package com.kalinya.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kalinya.enums.DebugLevel;
import com.kalinya.inventory.closeoutdefinitions.TranMatchKeyI;
import com.kalinya.stubs.TransactionStub;
import com.kalinya.stubs.TransactionStubs;

public class RealizedPnl {
	private Map<TranMatchKeyI, Set<TransactionStub>> transactions;
	private Map<String, TransactionStubs> transactionsByInstrumentId;
	private Map<TranMatchKeyI, MatchedTransactions> matchedTransactions;
	private Date date;
	private DebugLevel debugLevel;

	public RealizedPnl(Map<TranMatchKeyI, Set<TransactionStub>> categorizedTransactions, Date date) {
		this.date = date;
		setDebugLevel(DebugLevel.LOW);
		if(categorizedTransactions.size() > 0) {
			setTransactions(categorizedTransactions);
			setMatchedTransactionsByInstrumentId();
		} else {
			System.out.println("No transactions to match");
		}
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() + ">";/*, TranCount [" + transactions.getTransactions().size()
				+ "], InstrumentIdCount [" + transactionsByInstrumentId.size() + "]";*/
	}

	private void setTransactions(Map<TranMatchKeyI, Set<TransactionStub>> categorizedTransactions) {
		this.transactions = categorizedTransactions;
	}

	private void setMatchedTransactionsByInstrumentId() {
		matchedTransactions = new HashMap<>();
	}
	
	/*public TransactionStubs getTransactions() {
		return transactions;
	}*/

	public BigDecimal getAmount(String transactionId) {
		TransactionStub transaction = InventoryService.getTransaction(transactions, transactionId);
		return getAmount(transaction);
	}

	public BigDecimal getAmount(TransactionStub transaction) {
		if(transaction.getRealizedPnl() != null) {
			return transaction.getRealizedPnl();
		}
		TranMatchKeyI tranMatchKey = InventoryService.getTranLookupIndex(transaction);

		setMatchedTransactions(tranMatchKey);
		MatchedTransactions matchedTransactions = getMatchedTransactions(InventoryService.getTranLookupIndex(transaction));
		BigDecimal result = BigDecimal.ZERO;
		if(matchedTransactions.getMatchedTransactionCount() > 0) {
			if(debugLevel == DebugLevel.HIGH) {
				System.out.println("[" + matchedTransactions.getMatchedTransactions().size() + "] matched transactions");
				System.out.println("Retrieving realized P&L for TransactionId [" + transaction.getTransactionId() + "]");
			}
			for(MatchedTransaction matchedTransaction: matchedTransactions.getMatchedTransactions()) {
				if(matchedTransaction.getClosedOutTransaction().getTransactionId().equals(transaction.getTransactionId())) {
					result = result.add(matchedTransaction.getRealizedPnl());
				}
			}
		}
		result = result.setScale(2, RoundingMode.HALF_UP);
		transaction.setRealizedPnl(result);
		return result;
	}

	public MatchedTransactions getMatchedTransactions(TranMatchKeyI tranMatchKeyI) {
		return matchedTransactions.get(tranMatchKeyI);
	}

	private void setMatchedTransactions(TranMatchKeyI tranMatchKey) {
		if(!matchedTransactions.containsKey(tranMatchKey)) {
			matchedTransactions.put(tranMatchKey, new MatchedTransactions(transactions.get(tranMatchKey)));
		}
	}

	//what purpose does this serve?
	public Date getDate() {
		return date;
	}

	/*public MatchedTransactions getMatchedTransactions(String instrumentIdentifier) {
		return new MatchedTransactions(transactionsByInstrumentId.get(instrumentIdentifier), date);
	}*/

	/*
	 * TODO: support looking up from tranMatchKey or with only the transactionId
	public String getTransactionLog() {
		return InventoryService.getTransactionLog(transactions);
	}*/

	public String getTransactionLog(String instrumentIdentifier) {
		return InventoryService.getTransactionLog(transactionsByInstrumentId.get(instrumentIdentifier));
	}

	public DebugLevel getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(DebugLevel debugLevel) {
		this.debugLevel = debugLevel;
	}

}
