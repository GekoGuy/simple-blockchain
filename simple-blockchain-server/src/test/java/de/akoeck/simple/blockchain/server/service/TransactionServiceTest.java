package de.akoeck.simple.blockchain.server.service;

import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.akoeck.simple.blockchain.common.HashUtil;
import de.akoeck.simple.blockchain.common.domain.Transaction;
import de.akoeck.simple.blockchain.server.service.util.KeyUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {
	
	@Autowired
	private TransactionService transactionService;
	
	private KeyPair sender;
	private KeyPair recipient;
	
	@Before
	public void setup() {
		sender = KeyUtil.generateKeyPair();
		recipient = KeyUtil.generateKeyPair();
	}
	
	/**
	 * Test if Transaction are added.
	 */
	@Test
	public void addTransaction() {
		Transaction transaction = createTransaction();
		int size = transactionService.getTransactions().size();
		transactionService.addTransaction(transaction);
		assertTrue("No transaction was added.", size < transactionService.getTransactions().size());
	}
	
	private Transaction createTransaction() {
		Random r = new Random();
		long value = r.nextInt(100);
		return new Transaction(sender.getPublic().getEncoded(), recipient.getPublic().getEncoded(), value, createSiganture(value));
	}
	
	private byte[] createSiganture(long value) {
		String data = Arrays.toString(ArrayUtils.addAll(sender.getPublic().getEncoded(), recipient.getPublic().getEncoded())) + value;
		return HashUtil.createSignature(sender.getPrivate(), data);
	}
	
	

}
