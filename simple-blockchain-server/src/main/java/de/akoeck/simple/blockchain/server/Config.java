package de.akoeck.simple.blockchain.server;

/**
 * 
 * @author Alexander Köck
 * 
 * Configuration for a server node.
 *
 */
public final class Config {

	/**
	 * The master node is ruinning under localhost:8080.
	 */
	public final static String MASTER_NODE_ADDRESS = "http://localhost:8080";

	/**
	 * The maximum number of transactions which should be in a block.
	 */
	public final static int TRANSACTION_LIMIT = 10;
	
	/**
	 * The current difficulty for the proof of work.
	 */
	public static int DIFFICULTY = 1;
	
	/**
	 * Make the constructor private so this class cannot be instantiated.
	 */
	private Config() {
		
	}
}
