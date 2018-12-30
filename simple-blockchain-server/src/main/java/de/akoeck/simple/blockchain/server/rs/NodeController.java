/**
 * 
 */
package de.akoeck.simple.blockchain.server.rs;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.akoeck.simple.blockchain.common.domain.Node;
import de.akoeck.simple.blockchain.server.service.NodeService;

/**
 * @author Geko
 *
 */
@RestController
@RequestMapping(NodeController.ENDPOINT)
public class NodeController {

	private static final Logger LOG = LoggerFactory.getLogger(NodeController.class);
	
	public static final String ENDPOINT = "node";

	private final NodeService nodeService;

	@Autowired
	public NodeController(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public Set<Node> getNodes() {
		return nodeService.getNodes();
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public void addNode(@RequestBody final Node node) {
		LOG.info("Add node {} ", node.getAddress());
		nodeService.add(node);
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public void removeNode(@RequestBody final Node node) {
		LOG.info("Remove node {}", node.getAddress());
		nodeService.remove(node);
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(path = "ip")
	public String getIp(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

}
