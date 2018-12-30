/**
 * 
 */
package de.akoeck.simple.blockchain.server.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import de.akoeck.simple.blockchain.common.domain.Block;
import de.akoeck.simple.blockchain.common.domain.Transaction;
import de.akoeck.simple.blockchain.server.Config;
import de.akoeck.simple.blockchain.server.rs.BlockController;

/**
 * @author Alexander Köck
 * 
 *         Service for Mining a block in the blockchain.
 *
 */
@Service
public class MiningService implements Runnable {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MiningService.class);

	/**
	 * Blockchain service.
	 */
	private final BlockchainService blockchainService;

	/**
	 * Transaction service.
	 */
	private final TransactionService transactionService;

	/**
	 * Node service.
	 */
	private final NodeService nodeService;

	@Autowired
	public MiningService(final BlockchainService blockchainService, final TransactionService transactionService,
			final NodeService nodeService) {
		this.blockchainService = blockchainService;
		this.transactionService = transactionService;
		this.nodeService = nodeService;
	}

	/**
	 * Miner should not run at start.
	 */
	private AtomicBoolean run = new AtomicBoolean(false);

	/**
	 * Start the miner in a new Thread.
	 */
	public void startMiner() {
		if (run.compareAndSet(false, true)) {
			LOG.info("Start mining...");
			Thread thread = new Thread(this);
			thread.start();
			LOG.info("Mining started.");
		}
	}

	/**
	 * Stops the miner.
	 */
	public void stopMiner() {
		LOG.info("Stop mining...");
		run.set(false);
	}

	/**
	 * Mine a new block.
	 */
	@Override
	public void run() {
		while (run.get()) {
			Block block = mineBlock();
			if (block != null) {
				blockchainService.addBlock(block);
				nodeService.broadcast(RequestMethod.PUT, BlockController.ENDPOINT, block);
			}
		}
	}

	/**
	 * Mine a new block.
	 * 
	 * @return the found block.
	 */
	private Block mineBlock() {
		// Collect data for the new block
		byte[] previousHash = blockchainService.getHead() != null ? blockchainService.getHead().getHash() : null;
		List<Transaction> transactions = transactionService.getTransactions().stream().limit(Config.TRANSACTION_LIMIT)
				.collect(Collectors.toList());

		// If there are no transaction pause for 5 seconds and then try again
		if (transactions.isEmpty()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOG.error("Thread interrupted", e);
			}
			return null;
		}

		// all data is available - try to mine the new block
		Block block = new Block(previousHash, transactions);
		while (!targetReached(block, Config.DIFFICULTY)) {
			block.incrementNonceAndRecalculateHash();
		}
		return block;
	}

	private boolean targetReached(final Block block, final int difficulty) {
		for (int i = 0; i < block.getHash().length; i++) {
			if (block.getHash()[i] != 0) {
				return i >= difficulty;
			}
		}
		return true;
	}
}
