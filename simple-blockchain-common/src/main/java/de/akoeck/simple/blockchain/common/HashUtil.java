package de.akoeck.simple.blockchain.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;


/**
 * @author Alexander Köck
 * 
 * Utility Class for creating hashes.
 *
 */
public final class HashUtil {

	/**
	 * Hash Algortihm.
	 */
	public static final String SHA_256 = "SHA-256";

	/**
	 * 
	 */
	public static final String SHA_256_WITH_RSA = "SHA256withRSA";

	/**
	 * Private default constructor. So that no constructor is generated.
	 */
	private HashUtil() {
		// Nothing to do here.
	}

	/**
	 * Create a SHA-256 hash on the given String. 
	 * 
	 * @param input String to hash
	 * @return SHA-256 hash in bytes
	 */
	public static byte[] applySha256(final String input) {
		if(input == null) {
			throw new IllegalArgumentException("Input for Hashing cannot be null.");
		}
		
		return HashUtil.applySha256(input.getBytes());
	}
	
	/**
	 * Create a SHA-256 hash on the given bytes. 
	 * 
	 * @param input bytes to hash
	 * @return SHA-256 hash in bytes
	 */
	public static byte[] applySha256(final byte[] input) {
		if(input == null) {
			throw new IllegalArgumentException("Input for Hashing cannot be null.");
		}
		
		try {
			// Apply SHA256 on the input
			MessageDigest digest = MessageDigest.getInstance(SHA_256);
			return digest.digest(input);
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Signs the given data with a given private key.
	 * 
	 * @param privateKey private Key for signing the data
	 * @param data the data which should be signed 
	 * @return signed data as raw bytes
	 */
	public static byte[] createSignature(final PrivateKey privateKey, final String data) {
		byte[] signature = null;
		try {
			Signature rsa = Signature.getInstance(HashUtil.SHA_256_WITH_RSA);
			rsa.initSign(privateKey);
			rsa.update(data.getBytes());
			signature = rsa.sign();
			return signature;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Verifies if the signature corresponds to the public key and data.
	 * @param publicKey
	 * @param data
	 * @param signature
	 * @return true if the signature is valid, otherwise false
	 */
	public static boolean verfifySignature(final PublicKey publicKey, final String data, final byte[] signature) {
		Signature rsa;
		try {
			rsa = Signature.getInstance(HashUtil.SHA_256_WITH_RSA);
			rsa.initVerify(publicKey);
			rsa.update(data.getBytes());
			return rsa.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
