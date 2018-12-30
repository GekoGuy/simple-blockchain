/**
 * 
 */
package de.akoeck.simple.blockchain.server.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.akoeck.simple.blockchain.common.domain.Transaction;

/**
 * @author Alexander Köck
 * 
 *         Service for adding and removing transaction to/from the transaction
 *         pool.
 *
 */
@Service
public class TransactionService {

	/**
	 * Pool of transactions which should be added to the next mined block.
	 */
	private final Set<Transaction> transactionPool = new HashSet<>();

	/**
	 * Add a new Transaction to the transaction pool.
	 * 
	 * @param transaction
	 * @return
	 */
	public synchronized boolean addTransaction(final Transaction transaction) {
		if (verify(transaction)) {
			return transactionPool.add(transaction);
		}
		return false;
	}

	/**
	 * Removes a transaction from the transaction pool.
	 * 
	 * @param transaction
	 */
	public synchronized void remove(final Transaction transaction) {
		transactionPool.remove(transaction);
	}

	public Set<Transaction> getTransactions() {
		return new HashSet<>(transactionPool);
	}

	private boolean verify(final Transaction transaction) {
		return Arrays.equals(transaction.getHash(),transaction.calculateHash());
	}

}
