package net.vionta.xml.xesta.bind.serialize;


import static net.vionta.xml.xesta.bind.serialize.MappingHelper.isAttributeMapping;
import static net.vionta.xml.xesta.bind.serialize.util.XPathHelper.getXPath;

import java.awt.List;
import java.io.Serializable;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.xesta.bind.analyze.BindMapExtractor;
import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.analyze.map.ObjectDocumentMapping;
import net.vionta.xml.xesta.bind.serialize.util.DeserializerHelper;
import net.vionta.xml.xesta.exception.BindingException;
import net.vionta.xml.xesta.exception.MappingException;
import net.vionta.xml.xesta.repository.impl.util.DocumentUtils;

/**
 * Main class that takes the document information and 
 * populates the java beans.
 */
public class Deserializer {
	
	private static Logger log = LoggerFactory.getLogger(Deserializer.class);
	
	/**
	 * @param mainObject
	 * @param document
	 * @return The object that 
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
		ObjectDocumentMapping mapping = BindMapExtractor.analyze(mainObject);
		return (T) deserialize(mapping, document);
	}
	
	public Object deserialize(ObjectDocumentMapping mapping, Document document) throws  MappingException, BindingException, XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, InstantiationException {

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

	
	protected <T extends Serializable> T deserializeSubproperties(T parentObject, Node mainNode, ArrayList<Mapping> mappings) throws  MappingException, 
		BindingException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, XPathExpressionException, InstantiationException {
			
		log.info(" Subproperties : "+ mainNode+ " Into "+parentObject);
		if(parentObject!=null)
		log.debug(" Parent Object Class: "+ parentObject.getClass().getName());
		log.debug(" Iterating overr subproperties : ------------------------------------- ");
		
		for(Mapping currentMapping : mappings) {
			 
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
						if(currentMapping.getPropertyClass()!=null && !currentMapping.getPropertyClass().equals(java.lang.String.class)) {
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

	private void deserializeAttribute(Serializable parentObject, Node mainNode, String mappingExpression,
			String propertyName, Mapping mapping)
			throws XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		log.info(" Getting attribute: "+ propertyName);
		String attributeValue = extractLiteralValue(mainNode, mappingExpression, mapping);
		if(attributeValue != null ) {
			setValue(parentObject, propertyName, attributeValue);
		}
	}

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

	private String extractLiteralValue(Node mainNode, String mappingExpression, Mapping mapping)
			throws XPathExpressionException {
		XPath xPath = getXPath(mapping.getNamespaces());
//		if(mapping.getNamespaces()!= null ) xPath.setNamespaceContext( DeserializerHelper.extractNamespacesContext(mapping.getNamespaces()));
		String attributeValue= (String) xPath.evaluate(mappingExpression, mainNode,XPathConstants.STRING);
		return attributeValue;
	}
	



}
