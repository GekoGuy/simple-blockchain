/**
 * 
 */
package de.akoeck.simple.blockchain.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Alexander Köck
 *
 */
@SpringBootApplication
public class BlockchainServerNode {

	public static void main(String[] args) {
		SpringApplication.run(BlockchainServerNode.class, args);
	}

}
