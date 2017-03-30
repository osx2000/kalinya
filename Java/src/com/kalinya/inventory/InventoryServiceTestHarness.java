package com.kalinya.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.kalinya.stubs.TransactionStub;
import com.kalinya.stubs.TransactionStubs;

public class InventoryServiceTestHarness {

	/**
	 * Buy 1.0mm MyTicker @ 100.1
	 * Buy 1.2mm MyTicker @ 100.3
	 * Sell 0.8mm MyTicker @ 100.4
	 * ExpectedResults:
	 *  RealizedPnl=80,000 on ref#333
	 *  NetPosition=1,400,000
	 */
	@Test
	public void executeFirstTestSuite() {
		InventoryService is = new InventoryService();
		String instrumentId = "MyTicker";
		TransactionStubs transactionStubs = getFirstTestSuite(instrumentId, 0);
		is.loadTransactions(transactionStubs);

		int expectedTransactionCount = transactionStubs.getCount();

		//Test that all transactions were loaded
		assertEquals(expectedTransactionCount, is.getTransactions().getCount());

		//Test that TransactionsByInstrumentId method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsByInstrumentId(transactionStubs, instrumentId).getCount());

		//Test that getTransactionsSortedByTradeDate method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsSortedByTradeDate(transactionStubs.getTransactions()).size());

		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		//Test getting the P&L on TransactionId=333
		String transactionId = "333";
		BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		BigDecimal expectedRealizedPnlAmount = new BigDecimal("80000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the net position
		BigDecimal netPosition = is.getNetPosition(instrumentId, getToday()).setScale(2);
		BigDecimal expectedNetPosition = new BigDecimal("1400000").setScale(2);
		assertEquals(expectedNetPosition, netPosition);

		//Test getting the position a second time for the same instrument
		assertEquals(is.getNetPosition(instrumentId, getToday()).setScale(2), expectedNetPosition);

		//Test getting P&L on TranId=444 fails
		try {
			realizedPnlAmount = realizedPnl.getAmount("444");
			fail("Failed to hit an exception retrieving P&L for a TranId not in the set");
		} catch (Exception e) {
		}
		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
	}

	/**
	 * Buy 1.0mm MyTicker @ 100.1
	 * Buy 1.2mm MyTicker @ 100.3
	 * Sell 1.4mm MyTicker @ 100.4
	 * ExpectedResults:
	 *  RealizedPnl=180,000 on ref#444
	 *  NetPosition=800,000
	 */
	@Test
	public void executeSecondTestSuite() {
		InventoryService is = new InventoryService();
		String instrumentId = "MyTicker";
		TransactionStubs transactionStubs = getSecondTestSuite(instrumentId, 0);
		is.loadTransactions(transactionStubs);
		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		//Test getting the P&L on TransactionId=444
		String transactionId = "444";
		BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		BigDecimal expectedRealizedPnlAmount = new BigDecimal("180000").setScale(2);
		assertEquals(realizedPnlAmount, expectedRealizedPnlAmount);

		//Test getting the net position
		BigDecimal netPosition = is.getNetPosition(instrumentId, getToday()).setScale(2);
		BigDecimal expectedNetPosition = new BigDecimal("800000").setScale(2);
		assertEquals(netPosition, expectedNetPosition);

		//Test getting the position a second time for the same instrument
		assertEquals(is.getNetPosition(instrumentId, getToday()).setScale(2), expectedNetPosition);

		//Test getting P&L on TranId=333 fails
		try {
			realizedPnlAmount = realizedPnl.getAmount("333");
			fail("Failed to hit an exception retrieving P&L for a TranId not in the set");
		} catch (Exception e) {
		}
		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
	}

	/**
	 * Buy 1.0mm MyTicker @ 100.1
	 * Buy 1.2mm MyTicker @ 100.3
	 * Sell 0.8mm @ 100.4
	 * Sell 1.4mm MyTicker @ 100.4
	 * ExpectedResults:
	 *  RealizedPnl[333]=180,000
	 *  RealizedPnl[444]=180,000
	 *  NetPosition=0
	 */
	@Test
	public void executeThirdTestSuite() {
		InventoryService is = new InventoryService();
		String instrumentId = "MyTicker";
		TransactionStubs transactionStubs = getThirdTestSuite(instrumentId, 0);
		is.loadTransactions(transactionStubs);
		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		//Test getting the P&L on TransactionId=333
		String transactionId = "333";
		BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		BigDecimal expectedRealizedPnlAmount = new BigDecimal("80000").setScale(2);
		assertEquals(realizedPnlAmount, expectedRealizedPnlAmount);

		//Test getting the P&L on TransactionId=444
		transactionId = "444";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("340000").setScale(2);
		assertEquals(realizedPnlAmount, expectedRealizedPnlAmount);

		//Test getting the net position
		BigDecimal netPosition = is.getNetPosition(instrumentId, getToday()).setScale(2);
		BigDecimal expectedNetPosition = new BigDecimal("0").setScale(2);
		assertEquals(netPosition, expectedNetPosition);
		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
	}

	/**
	 * Buy 1.0mm MyTicker on 2/11 @ 100.1
	 * Buy 1.2mm MyTicker on 2/13 @ 100.3
	 * Sell 0.8mm MyTicker on 2/14 @ 100.4
	 * 
	 * Buy 1.0mm MyTicker2 on 2/11 @ 100.1
	 * Buy 1.2mm MyTicker2 on 2/13 @ 100.3
	 * Sell 1.4mm MyTicker2 on 2/15 @ 100.4
	 * 
	 * ExpectedResults (MyTicker):
	 *  RealizedPnl=80,000 on ref#333-1
	 *  NetPosition=1,400,000
	 *  
	 * ExpectedResults (MyTicker2):
	 *  RealizedPnl=180,000 on ref#444-2
	 *  NetPosition=800,000
	 */
	@Test
	public void executeFourthTestSuite() {
		InventoryService is = new InventoryService();
		String firstInstrumentId = "MyTicker";
		String secondInstrumentId = "MyTicker2";
		TransactionStubs transactionStubs = new TransactionStubs();
		transactionStubs.add(getFirstTaxLot(firstInstrumentId, 1));
		transactionStubs.add(getSecondTaxLotAtHigherPrice(firstInstrumentId, 1));
		transactionStubs.add(getPartialCloseoutTrade(firstInstrumentId, 1));
		transactionStubs.addAll(getSecondTestSuite(secondInstrumentId, 2));
		is.loadTransactions(transactionStubs);

		int expectedTransactionCount = transactionStubs.getCount();

		//Test that all transactions were loaded
		assertEquals(expectedTransactionCount, is.getTransactions().getCount());

		//Test that TransactionsByInstrumentId method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsByInstrumentId(transactionStubs, firstInstrumentId).getCount()
				+ InventoryService.getTransactionsByInstrumentId(transactionStubs, secondInstrumentId).getCount());

		//Test that getTransactionsSortedByTradeDate method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsSortedByTradeDate(transactionStubs.getTransactions()).size());

		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		//Test getting the P&L on TransactionId=333
		String transactionId = "333-1";
		BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		BigDecimal expectedRealizedPnlAmount = new BigDecimal("80000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the net position
		BigDecimal netPosition = is.getNetPosition(firstInstrumentId, getToday()).setScale(2);
		BigDecimal expectedNetPosition = new BigDecimal("1400000").setScale(2);
		assertEquals(expectedNetPosition, netPosition);

		//Test getting the position a second time for the same instrument
		assertEquals(is.getNetPosition(firstInstrumentId, getToday()).setScale(2), expectedNetPosition);

		//Test MyTicker2
		transactionId = "444-2";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("180000").setScale(2);
		assertEquals(realizedPnlAmount, expectedRealizedPnlAmount);

		//Test getting the net position
		netPosition = is.getNetPosition(secondInstrumentId, getToday()).setScale(2);
		expectedNetPosition = new BigDecimal("800000").setScale(2);
		assertEquals(netPosition, expectedNetPosition);

		//Test getting P&L on TranId=444-1 fails
		try {
			realizedPnlAmount = realizedPnl.getAmount("444-1");
			fail("Failed to hit an exception retrieving P&L for a TranId not in the set");
		} catch (Exception e) {
		}
		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
	}

	/**
	 * Opens a short position
	 * 
	 * Sell 1.4mm @ 100.4 on 2/11
	 * Sell 1.0mm @ 100.3 on 2/12
	 * Buy 1.2mm @ 100.1 on 2/13
	 * ExpectedResults:
	 *  RealizedPnl=260,000 on ref#777-1
	 *  NetPosition=-1,200,000
	 */
	@Test
	public void executeFifthTestSuite() {
		InventoryService is = new InventoryService();
		String firstInstrumentId = "MyTicker";
		TransactionStubs transactionStubs = getFourthTestSuite(firstInstrumentId, 1);
		is.loadTransactions(transactionStubs);

		int expectedTransactionCount = transactionStubs.getCount();

		//Test that all transactions were loaded
		assertEquals(expectedTransactionCount, is.getTransactions().getCount());

		//Test that TransactionsByInstrumentId method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsByInstrumentId(transactionStubs, firstInstrumentId).getCount());

		//Test that getTransactionsSortedByTradeDate method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsSortedByTradeDate(transactionStubs.getTransactions()).size());

		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		//Test getting the P&L on TransactionId=777
		String transactionId = "777-1";
		BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		BigDecimal expectedRealizedPnlAmount = new BigDecimal("260000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the net position
		BigDecimal netPosition = is.getNetPosition(firstInstrumentId, getToday()).setScale(2);
		BigDecimal expectedNetPosition = new BigDecimal("-1200000").setScale(2);
		assertEquals(expectedNetPosition, netPosition);

		//Test getting the position a second time for the same instrument
		assertEquals(is.getNetPosition(firstInstrumentId, getToday()).setScale(2), expectedNetPosition);

		//Test getting P&L on TranId=444-1 fails
		try {
			realizedPnlAmount = realizedPnl.getAmount("444-1");
			fail("Failed to hit an exception retrieving P&L for a TranId not in the set");
		} catch (Exception e) {
		}
		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
	}

	/**
	 * Crosses zero from a long position to a short
	 * 
	 * Buy 1.0mm @ 100.1 on 2/11
	 * Buy 1.2mm @ 100.3 on 2/13
	 * Sell 2.4mm @ 100.6 on 2/14
	 * ExpectedResults:
	 *  RealizedPnl=500,000 on ref#111-1
	 *  RealizedPnl=360,000 on ref#222-1
	 *  NetPosition=-200,000
	 */
	@Test
	public void executeSixthTestSuite() {
		InventoryService is = new InventoryService();
		String firstInstrumentId = "MyTicker";
		TransactionStubs transactionStubs = getSixthTestSuite(firstInstrumentId, 1);
		is.loadTransactions(transactionStubs);

		int expectedTransactionCount = transactionStubs.getCount();

		//Test that all transactions were loaded
		assertEquals(expectedTransactionCount, is.getTransactions().getCount());

		//Test that TransactionsByInstrumentId method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsByInstrumentId(transactionStubs, firstInstrumentId).getCount());

		//Test that getTransactionsSortedByTradeDate method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsSortedByTradeDate(transactionStubs.getTransactions()).size());

		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		//Test getting the P&L on TransactionId=111-1
		String transactionId = "111-1";
		BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		BigDecimal expectedRealizedPnlAmount = new BigDecimal("500000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the P&L on TransactionId=222-1
		transactionId = "222-1";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("360000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the net position
		BigDecimal netPosition = is.getNetPosition(firstInstrumentId, getToday()).setScale(2);
		BigDecimal expectedNetPosition = new BigDecimal("-200000").setScale(2);
		assertEquals(expectedNetPosition, netPosition);

		//Test getting the position a second time for the same instrument
		assertEquals(is.getNetPosition(firstInstrumentId, getToday()).setScale(2), expectedNetPosition);

		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
	}

	/**
	 * Crosses zero from a long position to a short, closes out
	 * 
	 * Buy 1.0mm @ 100.1 on 2/11
	 * Buy 1.2mm @ 100.3 on 2/13
	 * Sell 2.4mm @ 100.6 on 2/14
	 * Buy 0.2mm @ 100.7 on 2/15
	 * ExpectedResults:
	 *  RealizedPnl=500,000 on ref#111-1
	 *  RealizedPnl=360,000 on ref#222-1
	 *  RealizedPnl=-20,000 on ref#999-1
	 *  NetPosition=0
	 */
	@Test
	public void executeSeventhTestSuite() {
		InventoryService is = new InventoryService();
		String firstInstrumentId = "MyTicker";
		TransactionStubs transactionStubs = getSixthTestSuite(firstInstrumentId, 1);
		transactionStubs.add(coverSmallShort(firstInstrumentId, 1));
		is.loadTransactions(transactionStubs);

		int expectedTransactionCount = transactionStubs.getCount();

		//Test that all transactions were loaded
		assertEquals(expectedTransactionCount, is.getTransactions().getCount());

		//Test that TransactionsByInstrumentId method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsByInstrumentId(transactionStubs, firstInstrumentId).getCount());

		//Test that getTransactionsSortedByTradeDate method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsSortedByTradeDate(transactionStubs.getTransactions()).size());

		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		//Test getting the P&L on TransactionId=111-1
		String transactionId = "111-1";
		BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		BigDecimal expectedRealizedPnlAmount = new BigDecimal("500000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the P&L on TransactionId=222-1
		transactionId = "222-1";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("360000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the P&L on TransactionId=999-1
		transactionId = "999-1";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("-20000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the net position
		BigDecimal netPosition = is.getNetPosition(firstInstrumentId, getToday()).setScale(2);
		BigDecimal expectedNetPosition = new BigDecimal("0").setScale(2);
		assertEquals(expectedNetPosition, netPosition);

		//Test getting the position a second time for the same instrument
		assertEquals(is.getNetPosition(firstInstrumentId, getToday()).setScale(2), expectedNetPosition);

		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
	}

	/**
	 * Crosses zero from a long position to a short, crosses back to long, closes out
	 * 
	 * Buy 1.0mm @ 100.1 on 2/11, ref#111
	 * Buy 1.2mm @ 100.3 on 2/13, ref#222 (cum.pos=2.2mm)
	 * Sell 2.4mm @ 100.6 on 2/14, ref#888 (cum.pos=-0.2mm)
	 * Buy 1.2mm @ 100.2 on 2/16, ref#1111 (cum.pos=1.0mm)
	 * Sell 1.0mm @ 100.7 on 2/17, ref#1112 (cum.pos=0.0mm)
	 * 
	 * ExpectedResults:
	 *  RealizedPnl=500,000 on ref#111-1
	 *  RealizedPnl=360,000 on ref#222-1
	 *  RealizedPnl=80,000 on ref#888-1
	 *  RealizedPnl=0 on ref#1111-1
	 *  RealizedPnl=500,000 on ref#1112-1
	 *  NetPosition=0
	 */
	@Test
	public void executeEighthTestSuite() {
		InventoryService is = new InventoryService();
		String firstInstrumentId = "MyTicker";
		TransactionStubs transactionStubs = new TransactionStubs();
		transactionStubs.add(getFirstTaxLot(firstInstrumentId, 1));
		transactionStubs.add(getSecondTaxLotAtHigherPrice(firstInstrumentId, 1));
		transactionStubs.add(bigSale(firstInstrumentId, 1));
		transactionStubs.add(midMonthPurchase(firstInstrumentId, 1));
		transactionStubs.add(closeoutCrissCross(firstInstrumentId, 1));
		is.loadTransactions(transactionStubs);

		int expectedTransactionCount = transactionStubs.getCount();

		//Test that all transactions were loaded
		assertEquals(expectedTransactionCount, is.getTransactions().getCount());

		//Test that TransactionsByInstrumentId method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsByInstrumentId(transactionStubs, firstInstrumentId).getCount());

		//Test that getTransactionsSortedByTradeDate method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsSortedByTradeDate(transactionStubs.getTransactions()).size());

		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		//Test getting the P&L on TransactionId=111-1
		String transactionId = "111-1";
		BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		BigDecimal expectedRealizedPnlAmount = new BigDecimal("500000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the P&L on TransactionId=222-1
		transactionId = "222-1";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("360000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the P&L on TransactionId=888-1
		transactionId = "888-1";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("80000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the P&L on TransactionId=1111-1
		transactionId = "1111-1";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("0").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the P&L on TransactionId=1112-1
		transactionId = "1112-1";
		realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
		expectedRealizedPnlAmount = new BigDecimal("500000").setScale(2);
		assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

		//Test getting the net position
		BigDecimal netPosition = is.getNetPosition(firstInstrumentId, getToday()).setScale(2);
		BigDecimal expectedNetPosition = new BigDecimal("0").setScale(2);
		assertEquals(expectedNetPosition, netPosition);

		//Test getting the position a second time for the same instrument
		assertEquals(is.getNetPosition(firstInstrumentId, getToday()).setScale(2), expectedNetPosition);

		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
	}

	@Test
	public void executePerformanceTestSuite() {
		InventoryService is = new InventoryService();
		String instrumentId = "MyTicker";
		TransactionStubs transactionStubs = new TransactionStubs();
		int harnessCount=100;
		for(int i = 1; i <= harnessCount; i++) {
			transactionStubs.addAll(getFirstTestSuite(instrumentId + i, i));
		}
		is.loadTransactions(transactionStubs);

		int expectedTransactionCount = transactionStubs.getCount();

		//Test that all transactions were loaded
		assertEquals(expectedTransactionCount, is.getTransactions().getCount());

		//Set to true to test getTransactionsByInstrumentId
		boolean testIt = true;
		if(testIt) {
			int actualTransactionCount = 0;
			for(int i = 1; i <= harnessCount; i++) {
				actualTransactionCount += InventoryService.getTransactionsByInstrumentId(transactionStubs, instrumentId + i).getCount();
			}
			//Test that TransactionsByInstrumentId method returns all transactions loaded
			assertEquals(expectedTransactionCount, actualTransactionCount );
		}

		//Test that getTransactionsSortedByTradeDate method returns all transactions loaded
		assertEquals(expectedTransactionCount,
				InventoryService.getTransactionsSortedByTradeDate(transactionStubs.getTransactions()).size());

		RealizedPnl realizedPnl = is.getRealizedPnl(getToday());

		for(int i = 1; i < harnessCount; i++) {
			//Test getting the P&L on TransactionId=333-i
			String transactionId = "333-" + i;
			BigDecimal realizedPnlAmount = realizedPnl.getAmount(transactionId).setScale(2);
			BigDecimal expectedRealizedPnlAmount = new BigDecimal("80000").setScale(2);
			assertEquals(expectedRealizedPnlAmount, realizedPnlAmount);

			//Test getting the net position
			BigDecimal netPosition = is.getNetPosition(instrumentId + i, getToday()).setScale(2);
			BigDecimal expectedNetPosition = new BigDecimal("1400000").setScale(2);
			assertEquals(expectedNetPosition, netPosition);
		}
		System.out.println("<<JUnit>> Completed [" + new Object(){}.getClass().getEnclosingMethod().getName() + "]");
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

	private static TransactionStubs getThirdTestSuite(String ticker, int i) {
		/*
		 * Identifier [MyTicker], NetPosition [0.00] on Date [02/15/2016]
		 * [3] matched transactions
		 * Retrieving realized P&L for TransactionIdentifier [333]
		 * InstrumentIdentifier [MyTicker], TransactionIdentifier [333], RealizedP&L [80,000.00], RemainingPosition [0.00]
		 * [3] matched transactions
		 * Retrieving realized P&L for TransactionIdentifier [444]
		 * InstrumentIdentifier [MyTicker], TransactionIdentifier [444], RealizedP&L [340,000.00], RemainingPosition [0.00]
		 */
		TransactionStubs transactionStubs = new TransactionStubs();
		transactionStubs.add(getFirstTaxLot(ticker, i));
		transactionStubs.add(getSecondTaxLotAtHigherPrice(ticker, i));
		transactionStubs.add(getPartialCloseoutTrade(ticker, i));
		transactionStubs.add(getLargeCloseoutTrade(ticker, i));

		return transactionStubs;
	}

	private static TransactionStubs getFourthTestSuite(String ticker, int i) {
		TransactionStubs transactionStubs = new TransactionStubs();
		transactionStubs.add(goShort(ticker, i));
		transactionStubs.add(goShorter(ticker, i));
		transactionStubs.add(coverShort(ticker, i));
		return transactionStubs;
	}

	private static TransactionStubs getSixthTestSuite(String ticker, int i) {
		/*
		 * TODO: update details
		 * Identifier [MyTicker], NetPosition [1,400,000.00] on Date [02/14/2016]
		 * Identifier [MyTicker], RealizedP&L [80,000.00], RemainingPosition [1,400,000.00]
		 */
		TransactionStubs transactionStubs = new TransactionStubs();
		transactionStubs.add(getFirstTaxLot(ticker, i));
		transactionStubs.add(getSecondTaxLotAtHigherPrice(ticker, i));
		transactionStubs.add(bigSale(ticker, i));

		return transactionStubs;
	}

	/**
	 * Buy 1.0mm @ 100.1 on 2/11, ref#111
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
	 * Buy 1.2mm @ 100.3 on 2/13, ref#222
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
	 * Sell 0.8mm @ 100.4 on 2/14, ref#333
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
	 * Sell 1.4mm @ 100.4 on 2/15, ref#444
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
	 * Sell 1.4mm @ 100.4 on 2/11, ref#555
	 * @return
	 */
	private static TransactionStub goShort(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"555" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 11).getTime(),
				new BigDecimal(-1.4E6),
				new BigDecimal(100.4));
	}

	/**
	 * Sell 1.0mm @ 100.3 on 2/12, ref#666
	 * @return
	 */
	private static TransactionStub goShorter(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"666" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 11).getTime(),
				new BigDecimal(-1.0E6),
				new BigDecimal(100.3));
	}

	/**
	 * Buy 1.2mm @ 100.1 on 2/13, ref#777
	 * @return
	 */
	private static TransactionStub coverShort(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"777" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 13).getTime(),
				new BigDecimal(1.2E6),
				new BigDecimal(100.1));
	}

	/**
	 * Sell 2.4mm @ 100.6 on 2/14, ref#888
	 * @param ticker
	 * @param i
	 * @return
	 */
	private static TransactionStub bigSale(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"888" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 14).getTime(),
				new BigDecimal(-2.4E6),
				new BigDecimal(100.6));
	}

	/**
	 * Buy 0.2mm @ 100.7 on 2/15, ref#999
	 * @param ticker
	 * @param i
	 * @return
	 */
	private static TransactionStub coverSmallShort(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"999" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 15).getTime(),
				new BigDecimal(0.2E6),
				new BigDecimal(100.7));
	}

	/**
	 * Buy 1.2mm @ 100.2 on 2/16, ref#1111
	 * @param ticker
	 * @param i
	 * @return
	 */
	private static TransactionStub midMonthPurchase(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"1111" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 16).getTime(),
				new BigDecimal(1.2E6),
				new BigDecimal(100.2));
	}

	/**
	 * Sell 1.0mm @ 100.7 on 2/17, ref#1112
	 * @param ticker
	 * @param i
	 * @return
	 */
	private TransactionStub closeoutCrissCross(String ticker, int i) {
		return new TransactionStub(
				ticker,
				"1112" + (i==0 ? "": "-"+i),
				"PortfolioA",
				"A/C 123",
				new GregorianCalendar(2016, Calendar.FEBRUARY, 17).getTime(),
				new BigDecimal(-1.0E6),
				new BigDecimal(100.7));
	}


	private static Date getToday() {
		return new GregorianCalendar(2016, Calendar.FEBRUARY, 15).getTime();
	}
}
