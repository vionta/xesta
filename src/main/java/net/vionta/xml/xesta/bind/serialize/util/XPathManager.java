package net.vionta.xml.xesta.bind.serialize.util;

import javax.xml.xpath.XPath;

/**
 * XPath management utils. It allows to prepare for configuration
 * the XPath version, that is expected to grow in the near future.
 */
public class XPathManager {

	/**
	 * @return XPath implementation. Current version is 3.1 and 
	 * expected to advance in next releases.
	 */
	protected static XPath buildXPath()  {
		XPath xPath = (new net.sf.saxon.xpath.XPathFactoryImpl()).newXPath();
		return xPath;
	}
	
}
	
	
	
