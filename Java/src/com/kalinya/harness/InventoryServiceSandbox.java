package com.kalinya.harness;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.kalinya.inventory.InventoryService;
import com.kalinya.inventory.RealizedPnl;
import com.kalinya.stubs.TransactionStub;
import com.kalinya.stubs.TransactionStubs;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.StringUtil;

public class InventoryServiceSandbox {

	public static void main(String[] args) {
		
		TransactionStubs transactions = null;
		String instrumentId = "MyTicker";
		transactions = getSecondTestSuite(instrumentId, 0);
		InventoryService is = new InventoryService();
		is.loadTransactions(transactions);
		is.prepareForMatching();
		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());
		
		String transactionId = "444";
		if(transactions.contains(transactionId)) {
			printRealizedPnlDetails(instrumentId, transactionId, is, realizedPnl);
		} else {
			System.out.println("No transactions in TransactionId [" + transactionId + "]");
		}
		/*TransactionStubs transactionStubs = null;
		String instrumentId = "MyTicker";
		transactionStubs = getFifthTestSuite(instrumentId, 0);

		is.loadTransactions(transactionStubs);

		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());
		//realizedPnl.setDebugLevel(DebugLevel.HIGH);

		String transactionId = "888";
		if(transactions.contains(transactionId)) {
			printRealizedPnlDetails(instrumentId, transactionId, is, realizedPnl);
		} else {
			System.out.println("No transactions in TransactionId [" + transactionId + "]");
		}*/

		/*transactionId = "333";
		if(transactions.contains(transactionId)) {
			printRealizedPnlDetails(instrumentId, transactionId, is, realizedPnl);
		} else {
			System.out.println("No transactions in TransactionId [" + transactionId + "]");
		}*/

		//System.out.println("Transaction Log for InstrumentIdentifier [" + instrumentId+ "]\n" + realizedPnl.getTransactionLog(instrumentId));
	}

	private static void printRealizedPnlDetails(String instrumentId, String transactionId, InventoryService is,
			RealizedPnl realizedPnl) {
		System.out.println(
				"InstrumentId [" + instrumentId + "], TransactionId [" + transactionId 
				+ "], RealizedP&L [" + StringUtil.formatDouble(realizedPnl.getAmount(transactionId).doubleValue()) 
				+ "], RemainingPosition [" + StringUtil.formatDouble(is.getNetPosition(instrumentId, getToday()).doubleValue()) + "]");
	}

	private static TransactionStubs getFirstTestSuite(String ticker, int i) {
		/*
		 * Identifier [MyTicker], NetPosition [1,400,000.00] on Date [02/14/2016]
		 * Identifier [MyTicker], RealizedP&L [80,000.00], RemainingPosition [1,400,000.00]
		 */
		TransactionStubs transactionStubs = new TransactionStubs();
		transactionStubs.add(getFirstTaxLot(ticker, i));
		transactionStubs.add(getSecondTaxLotAtHigherPrice(ticker, i));
		transactionStubs.add(getPartialCloseoutTrade(ticker, i));
		return transactionStubs;
	}

	private static TransactionStubs getSecondTestSuite(String ticker, int i) {
		TransactionStubs transactionStubs = new TransactionStubs();
		transactionStubs.add(getFirstTaxLot(ticker, i));
		transactionStubs.add(getSecondTaxLotAtHigherPrice(ticker, i));
		transactionStubs.add(getLargeCloseoutTrade(ticker, i));
		return transactionStubs;
	}

	/**
	 * Sell 0.8mm @ 100.5, Id=666
	 * Sell 1.2mm @ 100.3, Id=777
	 * Buy 1.4mm @ 100.1, Id=888
	 */
	private static TransactionStubs getFifthTestSuite(String ticker, int i) {
		///TODO: is this right?  It is using the same high-price comparator
		
		TransactionStubs transactionStubs = new TransactionStubs();
		transactionStubs.add(getShortPosition(ticker, i));
		transactionStubs.add(getSecondShortPosition(ticker, i));
		transactionStubs.add(getShortCover(ticker, i));

		return transactionStubs;
	}

	/**
	 * Buy 1.0mm @ 100.1
	 * @param ticker 
	 * @return
	 */
	private static TransactionStub getFirstTaxLot(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"111" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 11).getTime(),
				new BigDecimal(1E6),
				new BigDecimal(100.1));
	}

	/**
	 * Buy 1.2mm @ 100.3
	 * @return
	 */
	private static TransactionStub getSecondTaxLotAtHigherPrice(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"222" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 13).getTime(),
				new BigDecimal(1.2E6),
				new BigDecimal(100.3));
	}

	/**
	 * Sell 0.8mm @ 100.4
	 * @return
	 */
	private static TransactionStub getPartialCloseoutTrade(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"333" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 14).getTime(),
				new BigDecimal(-0.8E6),
				new BigDecimal(100.4));
	}

	/**
	 * Sell 1.4mm @ 100.4
	 * @return
	 */
	private static TransactionStub getLargeCloseoutTrade(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"444" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 15).getTime(),
				new BigDecimal(-1.4E6),
				new BigDecimal(100.4));
	}

	/**
	 * Sell 0.8mm @ 100.5, Id=666
	 * @return
	 */
	private static TransactionStub getShortPosition(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"666" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 10).getTime(),
				new BigDecimal(-0.8E6),
				new BigDecimal(100.5));
	}
	
	/**
	 * Sell 1.2mm @ 100.3, Id=777
	 */
	private static TransactionStub getSecondShortPosition(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"777" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 11).getTime(),
				new BigDecimal(-1.2E6),
				new BigDecimal(100.3));
	}
	
	/**
	 * Buy 1.4mm @ 100.0, Id=888
	 */
	private static TransactionStub getShortCover(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"888" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 13).getTime(),
				new BigDecimal(1.4E6),
				new BigDecimal(100.0));
	}
	
	private static Date getToday() {
		return new GregorianCalendar(2016, Calendar.FEBRUARY, 15).getTime();
	}



}
