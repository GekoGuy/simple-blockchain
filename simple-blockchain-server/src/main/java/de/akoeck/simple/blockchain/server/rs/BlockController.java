/**
 * 
 */
package de.akoeck.simple.blockchain.server.rs;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.akoeck.simple.blockchain.common.domain.Block;
import de.akoeck.simple.blockchain.server.service.BlockchainService;
import de.akoeck.simple.blockchain.server.service.MiningService;
import de.akoeck.simple.blockchain.server.service.NodeService;

/**
 * @author Alexander Köck
 *
 *         Rest Endpoint for managing the blocks in the block chain.
 */
@RestController
@RequestMapping(BlockController.ENDPOINT)
public class BlockController {

	/**
	 * Endpoint.
	 */
	public final static String ENDPOINT = "block";

	/**
	 * Blockchain service.
	 */
	private final BlockchainService blockchainService;

	/**
	 * Node service.
	 */
	private final NodeService nodeService;
	
	/**
	 * Mining service.
	 */
	private final MiningService miningsService;

	@Autowired
	public BlockController(final BlockchainService blockchainService, final NodeService nodeService, final MiningService miningService) {
		this.blockchainService = blockchainService;
		this.nodeService = nodeService;
		this.miningsService = miningService;
	}

	@RequestMapping
	public List<Block> getBlockchain() {
		return blockchainService.getBlockchain();
	}

	/**
	 * Adds a block to the blockchain and publishes the change to the other nodes.
	 * 
	 * @param block	the block which should be added
	 * @param response If the block was added, 202 will be returned otherwise the returncode is 406.
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void addBlock(@RequestBody final Block block, final HttpServletResponse response) {
		boolean success = blockchainService.addBlock(block);
		if (success) {
			// return status code 202 and publish the change
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			nodeService.broadcast(RequestMethod.PUT, BlockController.ENDPOINT, block);
		} else {
			// return 406
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		}
	}

	/**
	 * Starts mining on blocks on this node.
	 */
	@RequestMapping(path = "start")
	public void startMining() {
		miningsService.startMiner();
	}

	/**
	 * Stops mining on blocks on this node.
	 */
	@RequestMapping(path = "stop")
	public void stopMining() {
		miningsService.stopMiner();
	}
}
