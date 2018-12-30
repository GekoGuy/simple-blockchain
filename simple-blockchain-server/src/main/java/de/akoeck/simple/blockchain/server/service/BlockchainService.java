/**
 * 
 */
package de.akoeck.simple.blockchain.server.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.akoeck.simple.blockchain.common.domain.Block;
import de.akoeck.simple.blockchain.common.domain.Node;

/**
 * @author Alexander Köck
 * 
 *         Service for working on the blockchain.
 *
 */
@Service
public class BlockchainService {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BlockchainService.class);

	/**
	 * Transaction service.
	 */
	private final TransactionService transactionService;

	/**
	 * The blockchain.
	 */
	private final List<Block> blockchain = new ArrayList<>();

	@Autowired
	public BlockchainService(final TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * Gets the HEAD of the blockchain.
	 * 
	 * @return Last block in the chain.
	 */
	public Block getHead() {
		if (blockchain.isEmpty()) {
			return null;
		}
		return blockchain.get(blockchain.size() - 1);
	}

	public List<Block> getBlockchain() {
		return new ArrayList<>(blockchain);
	}

	public synchronized boolean addBlock(final Block block) {
		if (verify(block)) {
			blockchain.add(block);
			block.getTransactions().forEach(transactionService::remove);
			return true;
		}
		return false;
	}

	/**
	 * Fetches the blocks from another node.
	 * 
	 * @param node         the node where the blockchain should be fetched from
	 * @param restTemplate the rest template for calling the rest service
	 */
	public void retrieveBlockchain(Node node, RestTemplate restTemplate) {
		LOG.info("Retrieve blocks from node {}", node.getAddress());
		Block[] blocks = restTemplate.getForObject(node.getAddress() + "/block", Block[].class);
		Collections.addAll(blockchain, blocks);
		LOG.info("Retrieved {} blocks from node {}", blocks.length, node.getAddress());
	}

	/**
	 * Verifies that the block is valid. A block is valid if the previous hash is
	 * really the has of the previous block.
	 * 
	 * @param block the block to verify
	 * @return true if the block is valid, otherwise false
	 */
	private boolean verify(final Block block) {
		if (!blockchain.isEmpty()) {
			byte[] lastHash = getHead().getHash();
			if (!Arrays.equals(lastHash, block.getPreviousHash())) {
				return false;
			}
		} else {
			if (block.getPreviousHash() != null) {
				return false;
			}
		}
		return Arrays.equals(block.getHash(), block.calculateHash());
	}
}
