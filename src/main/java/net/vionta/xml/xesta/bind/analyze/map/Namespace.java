package net.vionta.xml.xesta.bind.analyze.map;

public class Namespace {

	String alias;
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
