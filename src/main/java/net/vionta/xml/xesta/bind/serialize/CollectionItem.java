package net.vionta.xml.xesta.bind.serialize;

import org.w3c.dom.Node;

/**
 * Utility object to hold a relation between a object 
 * and a node from a collection item.
 */
public class CollectionItem {
	
	/**
	 * Key string, identifying the node.
	 */
	public String key = "";
	
	/**
	 * A integer variable to kept track of the 
	 * item position.
	 */
	public int position = -1;
	/**
	 * The java, object part of the item.
	 */
	public Object object;
	/**
	 * The Xml node element.
	 */
	public Node node;
	

	public String keyNodeExpression;
	public Class classNode;

	public CollectionItem() {	}

	public CollectionItem(Object object) {
		this.object = object;
	}

	public CollectionItem(Object object, String key) {
		this.object = object;
		this.key = key;
	}

	public CollectionItem(Node node) {
		this.node = node;
	}
	
}
