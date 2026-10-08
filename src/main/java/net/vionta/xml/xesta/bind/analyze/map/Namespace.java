package net.vionta.xml.xesta.bind.analyze.map;

/**
 *  Class to be used as an annotation help, to declare the 
 *  namespaces.
 */
public class Namespace {

	/**
	 * The namespace declaration alias.
	 */
	String alias;
	
	/**
	 * The namespace uri or iri to identify the 
	 * namespace.
	 */
	String uri;
	
	public Namespace(String alias, String uri) {
		super();
		this.alias = alias;
		this.uri = uri;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
}
