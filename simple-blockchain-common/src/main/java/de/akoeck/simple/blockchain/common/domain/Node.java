/**
 * 
 */
package de.akoeck.simple.blockchain.common.domain;

import java.net.URL;

/**
 * @author Alexander Köck
 * 
 *         A server node of the blockchain.
 *
 */
public class Node {

	/**
	 * The address (host and port) of the node.
	 */
	private URL address;

	/**
	 * Since another constructor is implemented, this one mus also be provided.
	 * 
	 */
	public Node() {

	}

	/**
	 * Creates a new node for a given address.
	 * 
	 * @param address the Address this node is running on
	 */
	public Node(final URL address) {
		this.address = address;
	}

	public URL getAddress() {
		return address;
	}

	public void setAddress(final URL address) {
		this.address = address;
	}

	@Override
	public int hashCode() {
		return address != null ? address.hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;

		return address != null ? address.equals(other.getAddress()) : other.address == null;
	}

}
