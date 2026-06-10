package net.vionta.xml.xesta.bind.serialize.util;

import javax.xml.xpath.XPath;

/**
 * XPath management utils.
 */
public class XPathManager {

	/**
	 * @return XPath implementation.
	 */
	protected static XPath buildXPath()  {
		XPath xPath = (new net.sf.saxon.xpath.XPathFactoryImpl()).newXPath();
		return xPath;
	}
	
}
	
	
	
