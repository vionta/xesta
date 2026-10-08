package net.vionta.xml.xesta.bind.serialize;

import java.io.Serializable;

import org.w3c.dom.Node;

/**
 * A custom linked list representation to hold references 
 * and both the nodes and objects.
 */
public class LinkedNodeObject {

	/**
	 * The reference to the next LinkedNodeObject.
	 */
	private LinkedNodeObject next;
	
	/**
	 * The xml node.
	 */
	private Node node;
	
	/**
	 * The java object.
	 */
	private Serializable object;
	
	public LinkedNodeObject(Node node, Serializable object) {
		super();
		this.node = node;
		this.object = object;
	}

	public LinkedNodeObject getNext() {
		return next;
	}

	public void setNext(LinkedNodeObject next) {
		this.next = next;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Serializable getObject() {
		return object;
	}

	public void setObject(Serializable object) {
		this.object = object;
	}
	
	public boolean isBefore(Node node) { 
		return this.node.compareDocumentPosition(node) == 2;
	}
	
	public boolean isNext(Node node) {
		return this.node.compareDocumentPosition(node) == 4 && (
				this.next == null ||
				this.next.getNode().compareDocumentPosition(node) ==2);
	}
	
	public boolean isAfter(Node node) {
		return this.node.compareDocumentPosition(node) == 4; 
	}

}