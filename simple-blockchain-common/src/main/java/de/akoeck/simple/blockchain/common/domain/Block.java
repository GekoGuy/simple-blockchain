/**
 * 
 */
package de.akoeck.simple.blockchain.common.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import de.akoeck.simple.blockchain.common.HashUtil;

/**
 * @author ALexander Köck
 * 
 *         Represents an element in the blockchain.
 *
 */
public final class Block {

	/**
	 * Unique identifier for this block. The hash is calculated with all relevant
	 * fields.
	 */
	private byte[] hash;

	/**
	 * The hash of the previous block in the blockchain.
	 */
	private final byte[] previousHash;

	/**
	 * List with all transaction which belong to this block.
	 */
	private final List<Transaction> transactions = new ArrayList<>();

	/**
	 * Hash of all transaction hashes.
	 */
	private byte[] merkleRoot;

	/**
	 * Number for generating a new hash.
	 */
	private int nonce = 0;

	/**
	 * Time of creation of this block.
	 */
	private final long timestamp;

	/**
	 * @param previousHash
	 * @param transactions
	 */
	public Block(final byte[] previousHash, final List<Transaction> transactions) {
		this.previousHash = ArrayUtils.clone(previousHash);
		Optional.ofNullable(transactions).ifPresent(this.transactions::addAll);
		this.timestamp = System.currentTimeMillis();
		this.merkleRoot = calculateMerkleRoot();
		this.hash = calculateHash();
	}

	public byte[] getHash() {
		return ArrayUtils.clone(hash);
	}

	public byte[] getPreviousHash() {
		return ArrayUtils.clone(previousHash);
	}

	public List<Transaction> getTransactions() {
		return new ArrayList<>(transactions);
	}

	public void incrementNonceAndRecalculateHash() {
		nonce += 1;
		hash = calculateHash();
	}

	/**
	 * Calculates the hash of this block with all relevant data.
	 * 
	 * @return SHA-256 hash as bytes
	 */
	public byte[] calculateHash() {
		byte[] hashData = ArrayUtils.addAll(merkleRoot, previousHash);
		hashData = ArrayUtils.addAll(hashData, Longs.toByteArray(timestamp));
		hashData = ArrayUtils.addAll(hashData, Ints.toByteArray(nonce));
		return HashUtil.applySha256(hashData);
	}

	/**
	 * Calculates the hash of all transaction hashes.
	 * 
	 * @see {@link https://en.wikipedia.org/wiki/Merkle_tree | Merkle Tree}
	 * @return SHA-256 hash as bytes
	 */
	private byte[] calculateMerkleRoot() {
		List<byte[]> hashes = transactions.stream().map(t -> t.getHash()).collect(Collectors.toList());

		// if amount is odd. Add the last element to the last hash.
		if (hashes.size() % 2 == 1) {
			hashes.add(hashes.get(hashes.size() - 1));
		}

		Queue<byte[]> queue = new LinkedList<>(hashes);

		while (queue.size() > 1) {
			// take 2 from the queue and add the new calculated hash to the queue
			byte[] hashData = ArrayUtils.addAll(queue.poll(), queue.poll());
			queue.add(HashUtil.applySha256(hashData));
		}

		// return the last remaining hash (root) from the queue
		return queue.poll();
	}

}
