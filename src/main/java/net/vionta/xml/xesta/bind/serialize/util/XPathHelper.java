package net.vionta.xml.xesta.bind.serialize.util;

import java.util.Map;

import javax.xml.xpath.XPath;

public class XPathHelper {

	/**
	 * Returns the default XPath interpreter, a Saxonica 
	 * www.saxonica.com, based XPath 3.1 implementation.
	 * @return
	 */
	private static XPath getXPath() {
		return XPathManager.buildXPath();
	}

	/**
	 * Returns the default XPath interpreter, a Saxonica 
	 * www.saxonica.com, based XPath 3.1 implementation.
	 * @return A XPath 3.1 implementation based on Saxonica.
	 */
	public static XPath getXPath(Map<String, String> namespaceList) {
		XPath xPath = XPathManager.buildXPath();
		if(namespaceList!= null ) xPath.setNamespaceContext( 
				DeserializerHelper.extractNamespacesContext(namespaceList));
		return xPath;
	}
	
}
