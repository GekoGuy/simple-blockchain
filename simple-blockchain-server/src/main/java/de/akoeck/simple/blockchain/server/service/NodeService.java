/**
 * 
 */
package de.akoeck.simple.blockchain.server.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.checkerframework.checker.initialization.qual.Initialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import de.akoeck.simple.blockchain.common.domain.Node;
import de.akoeck.simple.blockchain.server.Config;
import de.akoeck.simple.blockchain.server.rs.NodeController;

/**
 * @author Alexander Köck
 * 
 *         Service for managing the node.
 */
@Service
public class NodeService implements ApplicationListener<WebServerInitializedEvent> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(NodeService.class);

	/**
	 * Blockchain service.
	 */
	private final BlockchainService blockchainService;

	/**
	 * Set of all nodes in the network.
	 */
	private final Set<Node> nodes = new HashSet<>();

	/**
	 * Rest Template for communicating with the other nodes.
	 */
	private final RestTemplate restTemplate = new RestTemplate();

	/**
	 * Reference on the node itself.
	 */
	private Node self;

	@Autowired
	public NodeService(final BlockchainService blockchainService, final TransactionService transactionService) {
		this.blockchainService = blockchainService;
	}

	public Set<Node> getNodes() {
		return new HashSet<>(nodes);
	}

	/**
	 * Listening on the {@link WebServerInitializedEvent}. When the WebServer is
	 * {@link Initialized} the node retrieves the other nodes and blockchain and
	 * should publish itself.
	 */
	@Override
	public void onApplicationEvent(final WebServerInitializedEvent event) {
		Node master = getMaster();
		String host = retrieveExternalHost(master, restTemplate);
		int port = event.getApplicationContext().getWebServer().getPort();
		self = getSelf(host, port);
		LOG.info("Node is running under {}", self.getAddress());

		if (!self.equals(master)) {
			nodes.add(master);
			retrieveNodes(master, restTemplate);
			blockchainService.retrieveBlockchain(master, restTemplate);
			broadcast(RequestMethod.PUT, NodeController.ENDPOINT, self);
		} else {
			LOG.info("Server is running as master...");
		}
	}

	/**
	 * Adds a new node.
	 * 
	 * @param node the node to add.
	 */
	public synchronized void add(final Node node) {
		nodes.add(node);
	}

	/**
	 * Removes a node.
	 * 
	 * @param node the node to remove.
	 */
	public synchronized void remove(final Node node) {
		nodes.remove(node);
	}

	/**
	 * When the node is shutting down, removes itself from the other nodes.
	 */
	@PreDestroy
	public void shutdown() {
		LOG.info("Shutting node down...");
		broadcast(RequestMethod.DELETE, NodeController.ENDPOINT, self);
		LOG.info("{} nodes informed", nodes.size());
	}

	/**
	 * Retrieve the other nodes from a specific node.
	 * 
	 * @param node
	 * @param restTemplate
	 */
	public void retrieveNodes(final Node node, final RestTemplate restTemplate) {
		LOG.info("Retrieve Nodes from node {}", node.getAddress());
		Node[] nodes = restTemplate.getForObject(node.getAddress() + "/node", Node[].class);
		Collections.addAll(this.nodes, nodes);
		LOG.info("Retrieved {} nodes from node {}", nodes.length, node.getAddress());
	}

	/**
	 * Braodcast a message to all other nodes in the network.
	 * 
	 * @param method
	 * @param endpoint
	 * @param data
	 */
	public void broadcast(final RequestMethod method, final String endpoint, final Object data) {
		switch (method) {
		case DELETE:
			nodes.parallelStream().forEach(node -> restTemplate.delete(node.getAddress() + "/" + endpoint, data));
			break;
		case POST:
			nodes.parallelStream()
					.forEach(node -> restTemplate.postForLocation(node.getAddress() + "/" + endpoint, data));
			break;
		case PUT:
			nodes.parallelStream().forEach(node -> restTemplate.put(node.getAddress() + "/" + endpoint, data));
			break;
		default:
			LOG.error("Not supported method {}", method.name());
		}

	}

	private String retrieveExternalHost(final Node node, RestTemplate restTemplate) {
		return restTemplate.getForObject(node.getAddress() + "/node/ip", String.class);
	}

	private Node getSelf(final String host, final int port) {
		try {
			return new Node(new URL("http", host, port, ""));
		} catch (MalformedURLException e) {

			return new Node();
		}
	}

	private Node getMaster() {
		try {
			return new Node(new URL(Config.MASTER_NODE_ADDRESS));
		} catch (MalformedURLException e) {
			LOG.error("Invalid master node address", e);
			return new Node();
		}
	}

}
