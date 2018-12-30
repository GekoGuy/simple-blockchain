/**
 * 
 */
package de.akoeck.simple.blockchain.common.domain;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Longs;

import de.akoeck.simple.blockchain.common.HashUtil;

/**
 * @author Alexander Köck
 * 
 *         Represents a transaction in the block. A transaction is the transfer
 *         of coins between two wallet (addresses).
 *
 */
public final class Transaction {

	/**
	 * Unique identifier of the transaction.
	 */
	private final byte[] hash;

	/**
	 * The amount which should be transferred.
	 */
	private final long value;

	/**
	 * The address of the sender in bytes.
	 */
	private final byte[] sender;

	/**
	 * The address of the recipient in bytes.
	 */
	private final byte[] recipient;

	/**
	 * The signature of the transaction. The transaction should be signed with the
	 * private key of the sender.
	 */
	private final byte[] signature;

	/**
	 * 
	 */
	private final long timestamp;

	/**
	 * 
	 * @param sender
	 * @param recipient
	 * @param coins
	 * @param signature
	 */
	public Transaction(final byte[] sender, final byte[] recipient, final long value, final byte[] signature) {
		this.sender = ArrayUtils.clone(sender);
		this.recipient = ArrayUtils.clone(recipient);
		this.value = value;
		this.signature = ArrayUtils.clone(signature);
		this.timestamp = System.currentTimeMillis();
		this.hash = calculateHash();
	}

	public byte[] getHash() {
		return ArrayUtils.clone(hash);
	}

	public byte[] getSender() {
		return ArrayUtils.clone(sender);
	}

	public byte[] getRecipient() {
		return ArrayUtils.clone(recipient);
	}

	public byte[] getSiganture() {
		return ArrayUtils.clone(signature);
	}

	public long getValue() {
		return value;
	}

	/**
	 * Calculate the hash of the transaction with the relevant data. For calculating
	 * the hash following data is used: sender, recipient, amount of coins,
	 * signature and the timestamp.
	 * 
	 * @return SHA-256 hash of the transaction as bytes
	 */
	public byte[] calculateHash() {
		byte[] hashData = ArrayUtils.addAll(sender, recipient);
		hashData = ArrayUtils.addAll(hashData, Longs.toByteArray(value));
		hashData = ArrayUtils.addAll(hashData, signature);
		hashData = ArrayUtils.addAll(hashData, Longs.toByteArray(timestamp));
		return HashUtil.applySha256(hashData);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(hash);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Transaction other = (Transaction) obj;
		return Arrays.equals(hash, other.hash);
	}

}
