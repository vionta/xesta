package net.vionta.xml.xesta.bind.serialize.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;

import net.vionta.xml.xesta.bind.serialize.CollectionItem;

/**
 * Namespace interface implementation to pass namespaces and 
 * alias to Xpath queries. Use the 
 */
public class XPathQueryNSContext implements NamespaceContext {

    /**
     * The set of namespaces alias (keys) and uris (values).
     */
    private Map<String, String> namespaces = new HashMap<String, String>();

	@Override
	public String getNamespaceURI(String prefix) {
		return namespaces.get(prefix);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		for(Entry<String, String> entry : namespaces.entrySet()) {
			if(entry.getKey() !=null && entry.getKey().equals(namespaceURI)) return entry.getValue();
		}
		return null;
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		return namespaces.keySet().iterator();
	}

	/**
	 * @return the namespace map, composed of 
	 * keys (namespace aliases) and values (namespace 
	 * values).
	 */
	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}
	
}
