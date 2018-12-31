/**
 * 
 */
package de.akoeck.simple.blockchain.client.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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

	private PublicKey publicKey;
	private PrivateKey privateKey;

	private RestTemplate restTemplaste;

	public Wallet() {
		restTemplaste = new RestTemplate();
		if (!(importPrivateKey() && importPublicKey())) {
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
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void sendMoney(final byte[] recipient, final long amount) {
		Transaction transaction = new Transaction(publicKey.getEncoded(), recipient, amount,
				createSignature(recipient, amount));
		restTemplaste.put("http://localhost:8080/transaction", transaction);
	}

	private byte[] createSignature(final byte[] recipient, final long amount) {
		final String data = Arrays.toString(publicKey.getEncoded()) + recipient + amount + System.currentTimeMillis();
		return HashUtil.createSignature(privateKey, data);
	}

	public void exportKeys(final File directory) throws IOException {
		exportPublicKey(directory);
		exportPrivateKey(directory);
	}

	private void exportPublicKey(final File directory) throws IOException {
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
		FileOutputStream out = new FileOutputStream(directory.getAbsoluteFile() + "/public.key");
		out.write(x509KeySpec.getEncoded());
		out.close();
	}

	private void exportPrivateKey(final File directory) throws IOException {
		PKCS8EncodedKeySpec pkscKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		FileOutputStream out = new FileOutputStream(directory.getAbsoluteFile() + "/private.key");
		out.write(pkscKeySpec.getEncoded());
		out.close();
	}

	private boolean importPrivateKey() {
		try {
			Path privateKeyPath = Paths.get(System.getProperty("user.home") + "/private.key");
			System.out.println(privateKeyPath.toString());
			FileInputStream in = new FileInputStream(privateKeyPath.toFile());
			byte[] encodedKey = new byte[(int) privateKeyPath.toFile().length()];
			in.read(encodedKey);
			in.close();

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec pkcsKeySpec = new PKCS8EncodedKeySpec(encodedKey);
			privateKey = keyFactory.generatePrivate(pkcsKeySpec);
		} catch (Exception e) {
			
		}
		return privateKey != null;
	}

	private boolean importPublicKey() {
		try {
			Path publicKeyPath = Paths.get(System.getProperty("user.home") + "/public.key");
			System.out.println(publicKeyPath.toString());
			FileInputStream in = new FileInputStream(publicKeyPath.toFile());
			byte[] encodedKey = new byte[(int) publicKeyPath.toFile().length()];
			in.read(encodedKey);
			in.close();

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(encodedKey);
			publicKey = keyFactory.generatePublic(x509KeySpec);
		} catch (Exception e) {
			
		}

		return publicKey != null;
	}

}
