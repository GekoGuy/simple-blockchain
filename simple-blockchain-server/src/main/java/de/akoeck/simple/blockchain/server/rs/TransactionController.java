package de.akoeck.simple.blockchain.server.rs;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.akoeck.simple.blockchain.common.domain.Transaction;
import de.akoeck.simple.blockchain.server.service.TransactionService;

/**
 * 
 * @author Alexander Köck
 * 
 *         Rest Service for controlling transactions.
 *
 */
@RestController
@RequestMapping(TransactionController.ENDPOINT)
public class TransactionController {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(TransactionController.class);

	/**
	 * Endpoint.
	 */
	public final static String ENDPOINT = "transaction";

	/**
	 * Transaction service.
	 */
	private final TransactionService transactionService;

	@Autowired
	public TransactionController(final TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * Adds a new transaction to the transaction pool.
	 * 
	 * @param transaction the new transaction to add in the pool.
	 * @param response    202 if the transaction is valid and could be added,
	 *                    otherwise false
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void addTransaction(@RequestBody final Transaction transaction, final HttpServletResponse response) {
		LOG.info("Add transaction {}", Base64.encodeBase64(transaction.getHash()));
		boolean success = transactionService.addTransaction(transaction);
		if (success) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		}
	}

}
