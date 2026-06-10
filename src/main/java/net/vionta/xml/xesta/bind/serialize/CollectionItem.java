package net.vionta.xml.xesta.bind.serialize;

import org.w3c.dom.Node;

public class CollectionItem {
	
	public String key = "";
	public int position = -1;
	public Object object;
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
