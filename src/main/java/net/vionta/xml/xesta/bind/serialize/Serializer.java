package net.vionta.xml.xesta.bind.serialize;

import static net.vionta.xml.xesta.bind.serialize.MappingHelper.isAttributeMapping;
import static net.vionta.xml.xesta.bind.serialize.util.XPathHelper.getXPath;

import java.awt.List;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.xesta.bind.analyze.BindMapExtractor;
import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.analyze.map.ObjectDocumentMapping;
import net.vionta.xml.xesta.bind.annotation.SerializingMode;
import net.vionta.xml.xesta.bind.serialize.util.DeserializerHelper;
import net.vionta.xml.xesta.exception.BindingException;
import net.vionta.xml.xesta.exception.MappingException;
import net.vionta.xml.xesta.repository.exception.PersistException;

/**
 * Performs the object to xml data serialization. 
 * 
 */
public class Serializer {
		
		private static Logger log  = LoggerFactory.getLogger(Serializer.class);
		
		/**
		 * Accepts the dto/object, binds the data to the 
		 * @param mainObject The dto/object holding the data that will be serialized.
		 * @param document The document with the previous or template information. 
		 * @return
		 * @throws MappingException
		 * @throws BindingException
		 * @throws XPathExpressionException
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 * @throws InvocationTargetException
		 * @throws NoSuchMethodException
		 * @throws NoSuchFieldException
		 * @throws SecurityException
		 * @throws ClassNotFoundException
		 * @throws PersistException 
		 */
		public Document serialize(Serializable mainObject, Document document) throws MappingException, BindingException, XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, ClassNotFoundException, PersistException {
			log.info("Serialzing Document  "+document);
			ObjectDocumentMapping mapping = BindMapExtractor.analyze(mainObject);
			log.info("With Mapping "+mapping);
			String mainMappingExpression = mapping.getMappingExpression();
			log.debug(" Mapping Expresion "+mainMappingExpression);
			Node mainNode =	DeserializerHelper.getClassNode(document,  mainMappingExpression,  mapping.getNamespaces());
			log.debug(" Main Node  : "+mainNode);
			return serializeSubproperties(mainObject, mainNode,  mapping.getMappings(), document);
		}
		
	/**
	 * Iterative serialization of the object subproperties.
	 * 
	 * @param parentObject The parent object holding the properties that should be serilazed.
	 * @param mainNode The xml context node for related to the object.
	 * @param mappings The mappings of the object properties that should be serialized.
	 * @param document The document being upated.
	 * 
	 * @return
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws MappingException
	 * @throws BindingException
	 * @throws PersistException 
	 */
	protected Document serializeSubproperties(Serializable parentObject, Node mainNode, ArrayList<Mapping> mappings, Document document) throws XPathExpressionException, 
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, 
			BindingException, PersistException {
			
		log.info(" Subproperties. Node  : "+ mainNode+ " Parent Object : "+parentObject);
		if(parentObject!=null)
		
		for(Mapping currentMapping : mappings) {
			log.info(" Subproperty, expression : "+ currentMapping.getMappingExpression() +" ->  property: "+currentMapping.getPropertyName());
			String mappingExpression = currentMapping.getMappingExpression();
			String propertyName = currentMapping.getPropertyName();
			log.debug(" Mapping Property Class : "+currentMapping.getPropertyClass());
			
			Serializable objectInstance = (Serializable) DeserializerHelper.getObjectInstance(currentMapping.getPropertyClass());
			
			log.debug(" Mapping class Instance : "+objectInstance.getClass().getName());
			
			//Collection Serialzing....................................
			if (CollectionDeserializeHelper.isCollection(parentObject, propertyName)) {
						CollectionSerializerHelper.serializeCollection(parentObject, mainNode, document, currentMapping, mappingExpression,
								propertyName, objectInstance);
			
			// Attribute
			}  else if(isAttributeMapping(mappingExpression)) {
						log.debug(" Setting property : "+ mappingExpression);
						Object nestedProperty = PropertyUtils.getNestedProperty(parentObject,propertyName);
						Node attributeNode= (Node) 
								getXPath(currentMapping.getNamespaces()).evaluate(mappingExpression, mainNode,XPathConstants.NODE);
						log.info(" Setting value: "+ nestedProperty.toString());
						if(attributeNode!=null) attributeNode.setNodeValue(nestedProperty.toString());
//						((Attr)attributeNode).setValue(nestedProperty.toString());
						//TODO: Contemplar crear nodos si no existen
					} else if ((parentObject.getClass().getDeclaredField(propertyName).getClass().equals(String.class)))  {
						log.info(" Setting attribute: "+ propertyName);
						log.info(" *** Step review pending : *****" );
						log.info(" *** Step review pending : *****" );
						log.info(" *** Step review pending : *****" );
						//TODO: review if node exists first
						
						
						Object nestedProperty = PropertyUtils.getNestedProperty(parentObject,propertyName);
						Node attributeNode= (Node) 
								getXPath(currentMapping.getNamespaces()).evaluate(mappingExpression, mainNode,XPathConstants.NODE);
						attributeNode.setNodeValue(nestedProperty.toString());
				//TODO:Ver el tipo de nodo y el tipo de resultado. 
				
//			}	else if( parentObject.getClass().getDeclaredField(propertyName).getClass().equals(Vector.class) || 
//					parentObject.getClass().getDeclaredField(propertyName).getClass().equals(ArrayList.class) ||
//					parentObject.getClass().getDeclaredField(propertyName).getClass().equals(List.class)) {
//			
//				throw new IllegalStateException("This should not be used, code duplicate (probably)		");
//				log.info(" Getting List: "+ propertyName);
//		
////				PropertyUtils.setNestedProperty(appender,appenderNameMapping.getPropertyName(), appenderName);
//				NodeList nodeList = (NodeList) getXPath().evaluate(mappingExpression, mainNode,XPathConstants.NODESET);
//				//TODO: Falta por hacer el binding de listas
//				
			} 	else {
				// Nos queda el nodo single
				log.debug(" Trying to serialize single node for "+ propertyName);
				
				Serializable candidateObject ;
				try {
//					Object nestedProperty = PropertyUtils.getNestedProperty(parentObject, propertyName);
					candidateObject = (Serializable) PropertyUtils.getNestedProperty( parentObject,propertyName);
					log.debug(" Gotten value : "+ candidateObject);
					
//					if(singelObject==null ) singelObject = (Serializable) Deserialzer.getObjectInstance( currentMapping.getPropertyClass());
					Node currentNode = (Node) getXPath(currentMapping.getNamespaces()).evaluate(mappingExpression, mainNode, XPathConstants.NODE);
					if(candidateObject!=null) {
						if (currentNode!=null)
//							if ( MappingHelper.isMappedClass(candidateObject)
//							|| ( currentMapping.getMappings() != null
//							l && currentMapping.getMappings().size() > 0)) serializeSubproperties(candidateObject, currentNode, currentMapping.getMappings(), document);
						if ( MappingHelper.isMappedClass(candidateObject)
						|| ( currentMapping.getMappings() != null && currentMapping.getMappings().size() > 0)) serializeSubproperties(candidateObject, currentNode, currentMapping.getMappings(), document);

							else currentNode.setTextContent(candidateObject.toString());
						else {
							// Fail if mapping requires the node to exist
							if(currentMapping.getSerializeMode() == SerializingMode.FAIL_ON_NOT_EXISTING) {
								log.error(" A node could not be found for property : "+currentMapping.getPropertyName() 
								+ " with mapping "+currentMapping.getMappingExpression());	
									throw new PersistException(parentObject.getClass().getName()+"."+propertyName, mappingExpression);
								} else if ( currentMapping.getSerializeMode() == SerializingMode.CREATE_ON_NOT_EXISTING) {
									//if mapping is not simple enought we should raise an exception.
									if(!isMappingSimple(mappingExpression) ) {
										log.error(" A node could not be created for property : "+currentMapping.getPropertyName() 
										+ " with mapping "+currentMapping.getMappingExpression());	
										throw new PersistException(parentObject.getClass().getName() +"."+propertyName,mappingExpression);
									} else {
										//We add nodes and populate them.
										Node baseNode = mainNode; 
										Element createdElement = null;
										String[] nodeListNames = getSubnodeListNames(mappingExpression);
										for (String nodeName : nodeListNames) {
											Node testedNode = (Node) getXPath(currentMapping.getNamespaces()).evaluate(nodeName, baseNode, XPathConstants.NODE);
											if(testedNode != null) {
												baseNode = testedNode;
											} else {
												createdElement = mainNode.getOwnerDocument().
												 							createElement(nodeName);
												baseNode.appendChild(createdElement);
												baseNode=createdElement;
											}
										}
										if(createdElement!=null ) createdElement.setTextContent(
												getTextRepresentation(parentObject, propertyName));
									}
								} 
									
						}
					}
					
				} catch (Exception e) {
					log.error("Could not set "+ propertyName+" property on "+parentObject );
					MappingException mappingException = new MappingException();
					mappingException.setSourceClassName((parentObject!= null) ? parentObject.getClass().getName(): null);
					mappingException.setTargetPropertyName(propertyName);
					mappingException.setException(e);
					log.error(mappingExpression);
					throw mappingException;
				}
				
				
			}
			
		}
		return document;
	}


	protected String getTextRepresentation(Serializable parentObject, String propertyName)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object nestedProperty = PropertyUtils.getNestedProperty( parentObject,propertyName);
		String propertyRepresentation = (nestedProperty!=null) ? nestedProperty.toString() : "" ;  
		if(nestedProperty instanceof java.util.Date && nestedProperty!=null)  {
			//TODO: Add date format to object bind
		       SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		       propertyRepresentation = dateFormatter.format((Date)nestedProperty);
		}
		return propertyRepresentation;
	}

	protected String[] getSubnodeListNames(String mappingExpression) {
		//TODO enforce and review
		String[] nodelistNames = mappingExpression.split("/");
		return nodelistNames;
	}

	public static boolean isMappingSimple(String mappingExpression) {
		//TODO: review, test and expand. 
		if(mappingExpression.indexOf("[")>-1) return false;
		if(mappingExpression.indexOf("//")>-1) return false;
		if(mappingExpression.indexOf("(")>-1) return false;
		if(mappingExpression.indexOf("..")>-1) return false;
		if(mappingExpression.indexOf(".")>-1) return false;
		return true;
	}

}
