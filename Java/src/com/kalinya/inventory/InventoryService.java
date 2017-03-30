package com.kalinya.inventory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.kalinya.inventory.closeoutdefinitions.TranMatchKeyI;
import com.kalinya.inventory.closeoutdefinitions.TranMatchKeyIP;
import com.kalinya.inventory.closeoutdefinitions.TranMatchKeyIPA;
import com.kalinya.inventory.comparators.FifoComparator;
import com.kalinya.stubs.TransactionStub;
import com.kalinya.stubs.TransactionStubs;

public class InventoryService {
	private TransactionStubs transactions;
	private Map<TranMatchKeyI, Set<TransactionStub>> categorizedTransactions;
	
	public InventoryService() {
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() + ">" 
				+ (transactions != null? ", TransactionCount [" + transactions.getTransactions().size() + "]" : null);
	}

	public void loadTransactions(TransactionStubs transactions) {
		this.transactions = transactions;
	}
	
	public void prepareForMatching() {
		if(categorizedTransactions == null) {
			loadTransactionsForMatching(transactions);
		}
	}
	
	public void loadTransactionsForMatching(TransactionStubs transactions) {
		this.transactions = transactions;
		categorizedTransactions = new HashMap<>();
		for(TransactionStub transaction: transactions.getTransactions()) {
			TranMatchKeyI tranLookupIndex = getTranLookupIndex(transaction);
			if(!categorizedTransactions.containsKey(tranLookupIndex)) {
				categorizedTransactions.put(tranLookupIndex, new TreeSet<TransactionStub>());
			}
			categorizedTransactions.get(tranLookupIndex).add(transaction);
		}
	}

	public TransactionStubs getTransactions() {
		return transactions;
	}

	public BigDecimal getNetPosition(String instrumentIdentifier, Date date) {
		BigDecimal netPosition = BigDecimal.ZERO;
		for(TransactionStub transaction: transactions.getTransactions()) {
			if(transaction.getInstrumentId().equalsIgnoreCase(instrumentIdentifier)
					&& transaction.getTradeDate().compareTo(date) <= 0) {
				netPosition = netPosition.add(transaction.getRemainingPosition());
			}
		}
		return netPosition;
	}

	/**
	 * Returns the RealizedPnl object for the selected instrument life-to-date to the parameter date
	 * 
	 * @param instrumentIdentifier
	 * @param date
	 * @return
	 */
	/*@Deprecated
	public RealizedPnl getRealizedPnl(String instrumentIdentifier, Date date) {
		return new RealizedPnl(getTransactionsByInstrumentId(transactions, instrumentIdentifier), date);
	}*/

	public RealizedPnl getRealizedPnl(Date date) {
		/*transactionsByInstrumentId = new HashMap<>();
		for(TransactionStub transaction: transactions.getTransactions()) {
			String instrumentIdentifier = transaction.getInstrumentId();
			if(!transactionsByInstrumentId.containsKey(instrumentIdentifier)) {
				transactionsByInstrumentId.put(instrumentIdentifier, new TransactionStubs());
			}
			transactionsByInstrumentId.get(instrumentIdentifier).add(transaction);
		}*/
		if(categorizedTransactions == null) {
			prepareForMatching();
		}
		return new RealizedPnl(categorizedTransactions, date);
	}

	public static TransactionStubs getTransactionsByInstrumentId(TransactionStubs transactionStubs, String instrumentIdentifier) {
		TransactionStubs transactionSubset = new TransactionStubs();
		for(TransactionStub transactionStub: transactionStubs.getTransactions()) {
			if(transactionStub.getInstrumentId().equalsIgnoreCase(instrumentIdentifier)) {
				transactionSubset.add(transactionStub);
			}
		}
		return transactionSubset;
	}

	public static String getTransactionLog(TransactionStubs transactions) {
		StringBuilder sb = new StringBuilder();
		Set<TransactionStub> transactionsByTradeDate = InventoryService.getTransactionsSortedByTradeDate(transactions);
		for(TransactionStub transactionStub: transactionsByTradeDate) {
			if(sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(transactionStub.toString());
		}
		return sb.toString();
	}

	/**
	 * @param transactions
	 * @return
	 * @deprecated Use {@link #getTransactionsSortedByTradeDate(Set)}
	 */
	public static Set<TransactionStub> getTransactionsSortedByTradeDate(TransactionStubs transactions) {
		return getTransactionsSortedByTradeDate(transactions.getTransactions());
	}
	
	public static Set<TransactionStub> getTransactionsSortedByTradeDate(Set<TransactionStub> transactions) {
		if(transactions == null || transactions.size() == 0) {
			return new HashSet<TransactionStub>();
		}
		Set<TransactionStub> transactionsByTradeDate = new TreeSet<>(new FifoComparator());
		for(TransactionStub transactionStub: transactions) {
			transactionsByTradeDate.add(transactionStub);
		}
		return transactionsByTradeDate;
	}
	
	public static TransactionStub getTransaction(Map<TranMatchKeyI, Set<TransactionStub>> transactions, String transactionId) {
		for(TranMatchKeyI tranMatchKey: transactions.keySet()) {
			for(TransactionStub transaction: transactions.get(tranMatchKey)) {
				if(transaction.getTransactionId().equalsIgnoreCase(transactionId)) {
					return transaction;
				}
			}
		}
		throw new IllegalArgumentException("Failed to find TransactionId [" + transactionId + "] in map");
	}

	public static TranMatchKeyI getTranLookupIndex(TransactionStub transaction) {
		if(transaction.isFutures()) {
			return new TranMatchKeyIPA(transaction);
		}
		return new TranMatchKeyIP(transaction);
	}
}
