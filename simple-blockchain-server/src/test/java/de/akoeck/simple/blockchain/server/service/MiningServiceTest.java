package de.akoeck.simple.blockchain.server.service;

import static org.junit.Assert.assertFalse;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Random;

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
public class MiningServiceTest {

	@Autowired
	private MiningService miningService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private BlockchainService blockchainService;

	private KeyPair sender;
	private KeyPair recipient;

	@Before
	public void setup() {
		sender = KeyUtil.generateKeyPair();
		recipient = KeyUtil.generateKeyPair();
	}

	@Test
	public void test() throws Exception {
		boolean stop = false;
		final int transactionCount = 20;
		int blocks = blockchainService.getBlockchain().size();
		createTransactions(transactionCount);
		miningService.startMiner();
		long start = System.currentTimeMillis();
		while (transactionService.getTransactions().size() == transactionCount && !stop) {
			Thread.sleep(1000);
			if (System.currentTimeMillis() - start > 30000) {
				stop = true;
			}
		}

		miningService.stopMiner();

		assertFalse("No new blocks were mined.", blocks == blockchainService.getBlockchain().size());

	}

	private void createTransactions(final int count) {
		Random r = new Random();

		for (int i = 0; i < count; i++) {
			long value = r.nextInt(100);
			Transaction transaction = new Transaction(sender.getPublic().getEncoded(),
					recipient.getPublic().getEncoded(), r.nextInt(100), HashUtil.createSignature(sender.getPrivate(),
							createData(sender.getPublic(), recipient.getPublic(), value)));
			transactionService.addTransaction(transaction);
		}

	}

	private String createData(final PublicKey sender, final PublicKey recipient, final long value) {
		return Arrays.toString(sender.getEncoded()) + Arrays.toString(recipient.getEncoded()) + value;
	}

}
