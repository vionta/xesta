package net.vionta.xml.xesta.bind.serialize.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;

/**
 * Namespace interface implementation to pass namespaces and 
 * alias to Xpath queries. Use the 
 */
public class XPathQueryNSContext implements NamespaceContext {

    /**
     * The set of namespaces alias (keys) and uris (values).
     */
    private Map<String, String> namespaces = new HashMap<String, String>();

	/**
	 * Returns the namespace related to the 
	 * supplied prefix.
	 */
	@Override
	public String getNamespaceURI(String prefix) {
		return namespaces.get(prefix);
	}

	/**
	 * Returns the prefix related to the namespace 
	 * URI or IRI.
	 */
	@Override
	public String getPrefix(String namespaceURI) {
		for(Entry<String, String> entry : namespaces.entrySet()) {
			if(entry.getKey() !=null && entry.getKey().equals(namespaceURI)) return entry.getValue();
		}
		return null;
	}

	/**
	 * Returns the prefix iterartor related to the namespace 
	 * URI or IRI.
	 */
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

	/**
	 * Sets the namespaces of the context.
	 * @param namespaces
	 */
	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}
	
}
