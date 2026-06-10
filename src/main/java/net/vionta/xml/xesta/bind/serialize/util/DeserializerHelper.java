package net.vionta.xml.xesta.bind.serialize.util;

import static net.vionta.xml.xesta.bind.serialize.util.XPathHelper.getXPath;

import java.io.Serializable;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.xesta.bind.serialize.Deserializer;
import net.vionta.xml.xesta.exception.BindingException;

/**
 * Convenience methods for deserializing documents into 
 * objects. 
 */
public class DeserializerHelper {

	private static Logger log = LoggerFactory.getLogger(Deserializer.class);

	/**
	 * Creates an object instance from a certain class. 
	 * 
	 * @param clazz The class used for reference.
	 * @return The class instance object. 
	 * @throws BindingException Unexpected situations 
	 * that prevents the object creation.
	 */
	public static Serializable getObjectInstance(Class clazz)
			throws BindingException {
		try {
		if(clazz == null ||  clazz.equals(java.lang.Class.class) ) return null;
		log.debug("Getting instance of "+clazz.getName());
		 if(clazz.getSuperclass().equals(Number.class)) {
			if(clazz.equals(Integer.class)) return   Integer.parseInt("0");
			else if(clazz.equals(Short.class)) return   Short.parseShort("0");
			else if(clazz.equals(Float.class)) return   Float.parseFloat("0");
			else if(clazz.equals(Double.class)) return   Double.parseDouble("0");
			else if(clazz.equals(Long.class)) return   Long.parseLong("0");
			else if(clazz.equals(Byte.class)) return   Byte.parseByte("0");
		 } 
			Serializable mainObject =(Serializable) clazz.getDeclaredConstructor().newInstance();
			return mainObject;
		 	
		} catch (Exception e) {
			e.printStackTrace();
			BindingException bindException = new BindingException();
			log.error("Error getting class instance: "+e);
			if(clazz != null) {
				log.error("Getting instance of "+clazz.getName());
				bindException.setTargetClassName(clazz.getName());
			}
			throw bindException;
		}
	}
	
	/**
	 * Creates an object instance from a certain class. 
	 * 
	 * @param clazz The class used for reference.
	 * @return The class instance object. 
	 * @throws BindingException Unexpected situations 
	 * that prevents the object creation.
	 */
	public static Serializable getObjectInstance(Class clazz, String value)
			throws BindingException {
		if(clazz == null ||  clazz.equals(java.lang.Class.class) || value == null ) return null;
		log.debug("Getting instance of "+clazz.getName() + " with instance value "+value);
		try {
			if(clazz.getSuperclass().equals(Number.class)) {
				if(value=="") return null ;
				else if(clazz.equals(Integer.class)) return   Integer.parseInt(value);
				else if(clazz.equals(Short.class)) return   Short.parseShort(value);
				else if(clazz.equals(Float.class)) return   Float.parseFloat(value);
				else if(clazz.equals(Double.class)) return   Double.parseDouble(value);
				else if(clazz.equals(Long.class)) return   Long.parseLong(value);
				else if(clazz.equals(Byte.class)) return   Byte.parseByte(value);
			} 
			Serializable mainObject =(Serializable) clazz.getDeclaredConstructor().newInstance();
			return mainObject;
			
		} catch (Exception e) {
			BindingException bindException = new BindingException();
			log.error("Error getting class instance: "+e);
			if(clazz != null) {
				log.error("Getting instance of "+clazz.getName());
				bindException.setTargetClassName(clazz.getName());
			}
			throw bindException;
		}
	}
	

	/**
	 * Get the main node of the base class. Deppends on the mainMapping, that may 
	 * exist or not.
	 * It does not accept nodecollections as a result, since a single class should map to
	 *  a unique node. 
	 *  
	 * @param document
	 * @param mainMappingExpression
	 * @return
	 * @throws XPathExpressionException
	 */
	public static Node getClassNode(Document document,  String mainMappingExpression, Map<String, String> namespaces)
		throws XPathExpressionException {
		if(document==null) return null;
		NodeList mainNodeset;
		Node mainNode = null; 
		if (mainMappingExpression != null && mainMappingExpression != "" ) {
			log.debug("Getting main Nodeset at :"+mainMappingExpression);
//			XPath xPath = getXPath(namespaces);
//			if(namespaces != null ) 
//				xPath.setNamespaceContext(extractNamespacesContext(namespaces) );
			mainNodeset= (NodeList)  getXPath(namespaces).evaluate(mainMappingExpression, document , XPathConstants.NODESET);
			if(mainNodeset!=null && mainNodeset.getLength()==1) {
			  mainNode = mainNodeset.item(0 );
			} else if (mainNodeset!=null && mainNodeset.getLength()>1 ) {
				throw new IllegalStateException("More than one node expected to map to one class, refine mappingExpression");
			}
		}
		return mainNode;
	}


	/**
	 * Returns a namespace context object with the provided namespaces on 
	 * the mapping or by hand.
	 * @param namespaces
	 * @return namespace context object
	 */
	public static XPathQueryNSContext extractNamespacesContext(Map<String, String> namespaces) {
		XPathQueryNSContext xpathQueryNSContext= new XPathQueryNSContext ();
		xpathQueryNSContext.setNamespaces(namespaces);
		return xpathQueryNSContext;
	}
	
}
