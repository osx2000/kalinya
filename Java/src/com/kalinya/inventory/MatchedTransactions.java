package com.kalinya.inventory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.kalinya.inventory.enums.InventoryReliefMethod;
import com.kalinya.stubs.TransactionStub;
import com.kalinya.stubs.TransactionStubs;
import com.kalinya.util.StringUtil;

public class MatchedTransactions {
	private TransactionStubs taxLots;
	private Set<MatchedTransaction> matchedTransactions;
	InventoryReliefMethod inventoryReliefMethod;

	public MatchedTransactions(Set<TransactionStub> set) {
		matchedTransactions = new HashSet<>();
		//TODO: make this dynamic
		inventoryReliefMethod = InventoryReliefMethod.MINIMIZE_REALIZED_PNL;
		//Only look for matched trades if there is more than one transaction in the instrument
		if(set != null && set.size() > 1) {
			Set<TransactionStub> transactionsByTradeDate = InventoryService.getTransactionsSortedByTradeDate(set);
			int index = 0;
			for(TransactionStub transactionStub: transactionsByTradeDate) {
				if(index == 0) {
					//The first transaction in the instrument establishes a tax lot
					taxLots = new TransactionStubs(inventoryReliefMethod.getComparatorForPosition(transactionStub.getRemainingPosition()));
					taxLots.add(transactionStub);
				} else {
					//Subsequent transactions either establish another tax lot or match off against an existing tax lot
					if(taxLots.getNetPosition().signum() == transactionStub.getOriginalPosition().signum()) {
						//This trade runs in the same direction to the existing net position
						taxLots.add(transactionStub);
					} else {
						//This trade is a closeout of an existing lot
						updateInventory(transactionStub);
					}
				}
				index++;
			}
		}
	}

	private void updateInventory(TransactionStub closedoutTransaction) {
		/**
		 * The taxLots TreeSet is sorted according to the comparator of the
		 * inventory relief method to ensure that it matches off the tax lots in
		 * the right order
		 */
		if(taxLots.getNetPosition().abs().compareTo(closedoutTransaction.getRemainingPosition().abs()) >= 0) {
			/**
			 * A 'normal' closeout where the (absolute value of the) closeout
			 * position is less than or equal to the sum of the tax lots. We
			 * will loop through each tax lot to bring down the position.
			 */
			for(TransactionStub taxLot: taxLots.getTransactions()) {
				/**
				 * Get the minimum of the remaining position on the closeout and
				 * the tax lot. The minimum amount will be the quantity to
				 * closeout.
				 */
				BigDecimal positionToCloseout = closedoutTransaction.getRemainingPosition().abs().min(taxLot.getRemainingPosition().abs());
				positionToCloseout = positionToCloseout.multiply(new BigDecimal(closedoutTransaction.getRemainingPosition().signum()));
				//Add the pair of {taxLot, closeout} as matched transactions
				matchedTransactions.add(new MatchedTransaction(taxLot, closedoutTransaction, positionToCloseout));
				//Take down the tax lot's remaining position
				taxLot.closeoutPosition(positionToCloseout.negate());
				//Take down the closeout trade's remaining position
				closedoutTransaction.closeoutPosition(positionToCloseout);
			}
		} else {
			/**
			 * Crossing zero! Instead of closing out the 'closeout' trade, we
			 * bring down the position of all of the tax lots, mark the realized
			 * P&L against the tax lots, and setup the 'closeout' trade as the
			 * newly established tax lot
			 */
			System.out.println("Crossing zero on TransactionId [" + closedoutTransaction.getTransactionId() 
			+ "], TaxLotsNetPosition [" + StringUtil.formatDouble(taxLots.getNetPosition().doubleValue()) 
			+ "], NewTradeNetPosition [" + StringUtil.formatDouble(closedoutTransaction.getRemainingPosition().doubleValue()) + "]");
			for(TransactionStub taxLot: taxLots.getTransactions()) {
				/**
				 * Since we are crossing zero,
				 * taxLot.getRemainingPosition().abs() must be less than
				 * closedoutTransaction.getRemainingPosition().abs()
				 */
				BigDecimal positionToCloseout = taxLot.getRemainingPosition().abs();
				positionToCloseout = positionToCloseout.multiply(new BigDecimal(taxLot.getRemainingPosition().signum()));

				//Add the pair of {taxLot, closeout} as matched transactions
				matchedTransactions.add(new MatchedTransaction(closedoutTransaction, taxLot, positionToCloseout));
				//Take down the tax lot's remaining position
				taxLot.closeoutPosition(positionToCloseout);
				//Take down the closeout trade's remaining position
				closedoutTransaction.closeoutPosition(positionToCloseout.negate());
			}
			//TODO: at this point, should we taxLots.add(closedoutTransaction)?
			//TODO: test crossing zero, and crossing zero again.  
			//taxLots.add(closedoutTransaction);  This might require reinitializing the TreeSet to change the comparator
			taxLots = new TransactionStubs(inventoryReliefMethod.getComparatorForPosition(closedoutTransaction.getRemainingPosition()));
			taxLots.add(closedoutTransaction);
		}
	}

	public Set<MatchedTransaction> getMatchedTransactions() {
		return matchedTransactions;
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() 
				+ ">, TaxLotCount [" + taxLots.getCount()
				+ "], MatchedTranCount [" + matchedTransactions.size() + "]";
	}

	public int getMatchedTransactionCount() {
		return getMatchedTransactions().size();
	}
}

