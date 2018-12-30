package de.akoeck.simple.blockchain.server.service.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyUtil {

	private KeyUtil() {
	}

	public static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024, new SecureRandom());
			return keyGen.generateKeyPair();

		} catch (NoSuchAlgorithmException nse) {
			throw new RuntimeException(nse);
		}
	}

}
