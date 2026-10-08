package net.vionta.xml.xesta.bind.serialize;


import static net.vionta.xml.xesta.bind.serialize.MappingHelper.isAttributeMapping;
import static net.vionta.xml.xesta.bind.serialize.util.XPathHelper.getXPath;

import java.io.Serializable;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.vionta.xml.xesta.bind.analyze.BindMapExtractor;
import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.serialize.util.DeserializerHelper;
import net.vionta.xml.xesta.exception.BindingException;
import net.vionta.xml.xesta.exception.MappingException;

/**
 * Main class that takes the document information and 
 * populates the java beans.
 */
public class Deserializer {
	
	private static Logger log = LoggerFactory.getLogger(Deserializer.class);
	
	/**
	 * Deserialzes an Xml document and returns it as a java object. The mapping 
	 * information is taken from the pojos.
	 * 
	 * @param mainObject The java object that will be populated with the info
	 * 					 and used as reference for the mapping information.
	 * @param document The document, existing or a supplied template.
	 * @return The object with the populated information.
	 * 
	 * @throws MappingException
	 * @throws BindingException
	 * @throws ClassNotFoundException 
	 * @throws XPathExpressionException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public <T extends Serializable> T deserialize(T mainObject, Document document) throws MappingException, BindingException, ClassNotFoundException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, InstantiationException {
		Mapping mapping = BindMapExtractor.analyze(mainObject);
		return (T) deserialize(mapping, document);
	}
	
	/**
	 * Deserialzes an Xml document and returns it as a java object. The mapping 
	 * information is taken from the pojos.
	 * 
	 * @param mainObject The java object that will be populated with the info
	 * 					 and used as reference for the mapping information.
	 * @param document The document, existing or a supplied template.
	 * @return The object with the populated information.
	 * 
	 * @throws MappingException
	 * @throws BindingException
	 * @throws ClassNotFoundException 
	 * @throws XPathExpressionException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public Object deserialize(Mapping mapping, Document document) throws  MappingException, BindingException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, InstantiationException {

		log.info("Deserialzing Document  "+document);
		log.info("With Mapping:  "+mapping);
		
		Serializable mainObject = DeserializerHelper.getObjectInstance(mapping.getPropertyClass());
		log.info("Main Object:  "+mainObject);
	
		// Getting main nodeset (if provided)h
		String mainMappingExpression = mapping.getMappingExpression();
		log.info(" Mapping Expresion "+mainMappingExpression);
		Node mainNode =	DeserializerHelper.getClassNode(document,  mainMappingExpression, mapping.getNamespaces());
		log.info(" Main Node  : "+mainNode);
		//We start with the iterative exploraton.
		ArrayList<Mapping> mappings = mapping.getMappings();
		log.info("Main Mappings : "+mappings);
		mainObject =(Serializable) deserializeSubproperties(mainObject, mainNode, mappings);
		return mainObject;
	}

	
	/**
	 * Iterative method to deserialize object subproperties with the information from the document node. 
	 * @param <T> The object type.
	 * @param parentObject The parent object.
	 * @param mainNode The main node that we will take as root for the collection.
	 * @param mapping The mapping information.
	 * @return The serialized object. 
	 * @throws MappingException
	 * @throws BindingException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 */
	protected <T extends Serializable> T deserializeSubproperties(T parentObject, Node mainNode, ArrayList<Mapping> mapping) throws  MappingException, 
		BindingException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, XPathExpressionException, InstantiationException {
			
		log.info(" Subproperties : "+ mainNode+ " Into "+parentObject);
		if(parentObject!=null)
		log.debug(" Parent Object Class: "+ parentObject.getClass().getName());
		log.debug(" Iterating overr subproperties : ------------------------------------- ");
		
		for(Mapping currentMapping : mapping) {
			 
			log.info(" Subproperty evaluated to : "+ currentMapping.getMappingExpression() +" -> "+currentMapping.getPropertyName());
			String mappingExpression = currentMapping.getMappingExpression();
			String propertyName = currentMapping.getPropertyName();
			Field declaredField = parentObject.getClass().getDeclaredField(propertyName);
			AnnotatedType annotatedType = declaredField.getAnnotatedType();
			log.debug(" Subproperty Class : "+currentMapping.getPropertyClass());
			
			// ... Deserialize collection .................
			if (CollectionDeserializeHelper.isCollection(parentObject, propertyName))  {
					Serializable deserializedCollection = CollectionDeserializeHelper.deserializeCollection(parentObject, mainNode, currentMapping, propertyName);
					PropertyUtils.setNestedProperty(parentObject, propertyName, deserializedCollection);	
			} 
			// ... Deserialize Attribute .............
			else if(isAttributeMapping(mappingExpression) || (parentObject.getClass().getDeclaredField(propertyName).getClass().equals(String.class)))  {
					deserializeAttribute(parentObject, mainNode, mappingExpression, propertyName, currentMapping);

			// ... Basic numeric types  .................
			}  else if( isBasicNumericType(annotatedType)) {
					deserializeNumericType(parentObject, mainNode, currentMapping, mappingExpression, propertyName);
			// ... Rest  ......................
			} else {
					// Nos queda el nodo single
					log.debug(" Getting Single Node for: "+ propertyName);
					
					XPath xPath = getXPath(currentMapping.getNamespaces());
					
					Node currentNode = (Node) xPath.evaluate(mappingExpression, mainNode,XPathConstants.NODE);
					Serializable singleObject ;
					try {
						singleObject = (Serializable) PropertyUtils.getNestedProperty( parentObject, propertyName);
						log.debug(" Candidate Object: "+singleObject);
						if(singleObject==null ) {
							log.debug(" Candidate Object is null, getting instance of   "+currentMapping.getPropertyClass());
							singleObject = (Serializable) DeserializerHelper.getObjectInstance( currentMapping.getPropertyClass());
						}
						if(currentMapping.getPropertyClass()!=null && !currentMapping.getPropertyClass().equals(String.class)) {
							Serializable deserializeSubproperties = (Serializable) deserializeSubproperties(singleObject, currentNode, currentMapping.getMappings());
							log.debug(" Candidate Object is null, getting instance of   "+currentMapping.getPropertyClass());
							PropertyUtils.setNestedProperty(parentObject, propertyName, deserializeSubproperties);
						} else if(currentNode!=null && currentNode.getTextContent()!=null) PropertyUtils.setNestedProperty(parentObject, propertyName,  currentNode.getTextContent());
						
					} catch (Exception e) {
						log.error("Could not get  "+ propertyName+" property from "+parentObject );
						MappingException mappingException = new MappingException();
						mappingException.setSourceClassName((parentObject!= null) ? parentObject.getClass().getName(): null);
						mappingException.setTargetPropertyName(propertyName);
						mappingException.setException(e);
						log.error(mappingExpression);
						log.error(e.getMessage());	
						throw mappingException;
					}
			}
		}
		return parentObject;
	}

	/**
	 * Utility method to deserialize numeric nodes.
	 * @param <T> The object type.
	 * @param parentObject The java object that contains the actual property.
	 * @param mainNode The main node to be taken as root for the operation.
	 * 
	 * @param currentMapping
	 * @param mappingExpression
	 * @param propertyName
	 * @throws XPathExpressionException
	 * @throws BindingException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private <T extends Serializable> void deserializeNumericType(T parentObject, Node mainNode, Mapping currentMapping,
			String mappingExpression, String propertyName) throws XPathExpressionException, BindingException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		log.debug(" Extracting numeric field ");
		String nodeValue = (String) getXPath(currentMapping.getNamespaces())
												.evaluate(mappingExpression, mainNode,XPathConstants.STRING);
		log.debug(" Obtained value :"+nodeValue);
		Serializable singleObject = (Serializable) DeserializerHelper.getObjectInstance( currentMapping.getPropertyClass(), nodeValue);
		PropertyUtils.setNestedProperty(parentObject, propertyName, singleObject);
	}

	/**
	 * Checks if the type is a basic numeric type. 
	 * @param annotatedType 
	 * @return True when the type is an instance 
	 * of the number class.
	 */
	private boolean isBasicNumericType(AnnotatedType annotatedType) {
		return annotatedType != null && (
					annotatedType.toString().equals("java.lang.Short")   || 
					annotatedType.toString().equals("java.lang.Integer") ||
					annotatedType.toString().equals("java.lang.Double") ||
					annotatedType.toString().equals("java.lang.Long")    ||
					annotatedType.toString().equals("java.lang.Float")    ||
					annotatedType.toString().equals("java.lang.Byte"));
	}

	/**
	 * Deserializes a Xml attribute into a java property.
	 * 
	 * @param parentObject The parent object that will hold the property.
	 * @param mainNode The root node from the xml file to be taken as reference.
	 * @param mappingExpression The XPath mapping expression.
	 * @param propertyName name of the java property.
	 * @param mapping The mapping information object.
	 * 
	 * @throws XPathExpressionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private void deserializeAttribute(Serializable parentObject, Node mainNode, String mappingExpression,
			String propertyName, Mapping mapping)
			throws XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		log.info(" Getting attribute: "+ propertyName);
		String attributeValue = extractLiteralValue(mainNode, mappingExpression, mapping);
		if(attributeValue != null ) {
			setValue(parentObject, propertyName, attributeValue);
		}
	}

	/**
	 * Utility method to populate the value of the java node from a text representation.
	 * @param parentObject The java object that holds the property being treated.
	 * @param propertyName The name of the java property.
	 * @param attributeValue The text value of the Xml attribute.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private void setValue(Serializable parentObject, String propertyName, String attributeValue)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		try {
		log.info(" Setting attribute value "+propertyName+" to : "+ attributeValue);
			Class<?> type = parentObject.getClass().getDeclaredField(propertyName).getType();
			if(type.equals(Float.class)) {
				Float f  = Float.parseFloat(attributeValue);
				PropertyUtils.setNestedProperty(parentObject,propertyName,f);
			} else if (type.equals(Integer.class)) {
				Integer  i = Integer.parseInt(attributeValue);
				PropertyUtils.setNestedProperty(parentObject,propertyName,i);
			} else if (type.equals(Double.class)) {
				Double d = Double.parseDouble(attributeValue);
				PropertyUtils.setNestedProperty(parentObject,propertyName,d);
			} else 
				PropertyUtils.setNestedProperty(parentObject,propertyName,attributeValue);
			
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Gets the text literal value from the xpath query on the supplied node.
	 * @param mainNode 
	 * @param mappingExpression
	 * @param mapping
	 * @return The text value of the queried text node.
	 * @throws XPathExpressionException
	 */
	private String extractLiteralValue(Node mainNode, String mappingExpression, Mapping mapping)
			throws XPathExpressionException {
		XPath xPath = getXPath(mapping.getNamespaces());
		String attributeValue= (String) xPath.evaluate(mappingExpression, mainNode,XPathConstants.STRING);
		return attributeValue;
	}

}
