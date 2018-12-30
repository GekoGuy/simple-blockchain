/**
 * 
 */
package de.akoeck.simple.blockchain.client.domain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.akoeck.simple.blockchain.common.HashUtil;
import de.akoeck.simple.blockchain.common.domain.Transaction;

/**
 * @author Alexander Köck
 *
 */
@Component
public class Wallet {

	private final PublicKey publicKey;
	private final PrivateKey privateKey;

	private RestTemplate restTemplaste;

	public Wallet() {
		restTemplaste = new RestTemplate();
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024, new SecureRandom());
			KeyPair keyPair = keyGen.generateKeyPair();

			this.privateKey = keyPair.getPrivate();
			this.publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException nse) {
			throw new RuntimeException(nse);
		}
	}
	
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void sendMoney(final String recipient, final long amount) {
		Transaction transaction = new Transaction(publicKey.getEncoded(), recipient.getBytes(), amount,
				createSignature(recipient, amount));
		restTemplaste.put("http://localhost:8080/transaction", transaction);
	}

	private byte[] createSignature(final String recipient, final long amount) {
		final String data = Arrays.toString(publicKey.getEncoded()) + recipient + amount + System.currentTimeMillis();
		return HashUtil.createSignature(privateKey, data);
	}
}
