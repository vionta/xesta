package net.vionta.xml.xesta.bind.serialize;

import static net.vionta.xml.xesta.bind.serialize.util.XPathHelper.getXPath;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xmlresolver.catalog.entry.Entry;

import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.annotation.Bind;
import net.vionta.xml.xesta.bind.annotation.SerializingMode;
import net.vionta.xml.xesta.bind.serialize.datamerge.CollectionDataMerge;
import net.vionta.xml.xesta.bind.serialize.datamerge.DataMergeBuilder;
import net.vionta.xml.xesta.bind.serialize.util.XPathHelper;
import net.vionta.xml.xesta.bind.serialize.util.XPathManager;
import net.vionta.xml.xesta.exception.BindingException;
import net.vionta.xml.xesta.exception.ExceptionHelper;
import net.vionta.xml.xesta.exception.MappingException;
import net.vionta.xml.xesta.repository.exception.PersistException;

public final class CollectionSerializerHelper {

	private static Logger log = LoggerFactory.getLogger(CollectionSerializerHelper.class);

	/**
	 * @param parentObject
	 * @param mainNode
	 * @param document
	 * @param currentMapping
	 * @param mappingExpression
	 * @param propertyName
	 * @param objectInstance
	 */
	protected static void serializeCollection(Serializable parentObject, Node mainNode, Document document,
			Mapping currentMapping, String mappingExpression, String propertyName, Serializable objectInstance)
			throws XPathExpressionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			InstantiationException, NoSuchFieldException, MappingException, BindingException, PersistException {

			Node baseNode = mainNode; 
			CollectionDataMerge dataMerge = DataMergeBuilder.buildDataMerge( currentMapping );
			// Getting the list objects
			
			dataMerge = DataMergeBuilder.fillNodes(dataMerge, currentMapping, mainNode);
			// TODO: Esto debe ser multiple
			Set entrySet = currentMapping.getCollectionClasses().entrySet();

			// Multiple collection : Pending 
			if(dataMerge.isMultipleCollection()) {
				//TODO: Mulitple collection
				//TODO: Mulitple collection
				
			// Single collection (only one type of elements.	
			} else  {		

				//Bind Collection by key
				if (dataMerge.getMappingEstrategy() == SerializingMode.BIND_COLLECTION_BY_KEY) {
					populateMergeObjects(parentObject, propertyName, dataMerge, dataMerge.getKeyNodeParameter());
					// Removing not existent nodes.
					int nodePosition = 0;
					log.debug(" Getting Objects : " + dataMerge.getObjectItems().size());
					//TODO: For complicated mappings the mainNode may need to be adjusted. 
					for (CollectionItem item : dataMerge.getObjectItems()) {
						String objectKey = item.key;
						log.debug(" Node Items Size " + dataMerge.getNodeItems().size());
						CollectionItem currentNode = dataMerge.getNodeItems().get(nodePosition);
						String nodeKey = currentNode.key;
						// if the current node exists
						log.debug(" Serializing subproperties " + item.key + " - " + item.object);
						log.debug(" Serializing subproperties " + currentNode.key + " - " + currentNode.node);
						
						if (currentNode != null && currentNode.node != null && objectKey.equals(nodeKey)) {
							new Serializer().serializeSubproperties((Serializable) item.object, currentNode.node,
								currentMapping.getMappings(), document);
							nodePosition++;
						} else {
							log.debug(" Node could not be directly seriaziled, checking case ");
							// review this part with multiple collections.
							if (Serializer.isMappingSimple(dataMerge.getCrateNodeExpression())) {
								String elementCreated = dataMerge.getCrateNodeExpression().replace("*:", "");
								log.debug(" Creating node : " + elementCreated);
								Element newElement = document.createElement(elementCreated);
								log.debug(" Creating node : " + elementCreated);
								if (currentNode.node != null)
									mainNode.insertBefore(currentNode.node, newElement);
								// If there is no following node
								else mainNode.appendChild(newElement);
								new Serializer().serializeSubproperties((Serializable) item.object, newElement,
									currentMapping.getMappings(), document);
							} else log.warn("Could not create an element with name :" + dataMerge.getCrateNodeExpression());
						}
					}
					
				}   else  if (dataMerge.getMappingEstrategy() == SerializingMode.BIND_COLLECTION_BY_POSITION) {
//					try {
					populateMergeObjects(parentObject, propertyName, dataMerge);
					// Removing not existent nodes.
					int nodePosition = 0;
					log.debug(" Getting Objects : " + dataMerge.getObjectItems().size());
					//TODO: For complicated mappings the mainNode may need to be adjusted. 
					for (CollectionItem item : dataMerge.getObjectItems()) {
//						String objectKey = item.key;
						log.debug(" Getting Node: " + nodePosition + " From : " + dataMerge.getNodeItems().size());
						if( nodePosition < dataMerge.getNodeItems().size() ) {
							CollectionItem currentNode = dataMerge.getNodeItems().get(nodePosition);
							if (currentNode != null && currentNode.node != null ) {
								log.debug(" Serializing subproperties " + item.object + " - " + currentNode.node);
								new Serializer().serializeSubproperties((Serializable) item.object, currentNode.node,
									currentMapping.getMappings(), document);
								nodePosition++;
							}
						} else {
							log.debug(" Node could not be directly seriaziled, checking case ");
							// review this part with multiple collections.
							if (Serializer.isMappingSimple(dataMerge.getCrateNodeExpression())) {
								String elementCreated = dataMerge.getCrateNodeExpression().replace("*:", "");
								log.debug(" Creating node : " + elementCreated);
								Element newElement = document.createElement(elementCreated);
								log.debug(" Creating node : " + elementCreated);
//								if (currentNode.node != null)
//									mainNode.insertBefore(currentNode.node, newElement);
								// If there is no following node
//								else 
									mainNode.appendChild(newElement);
								new Serializer().serializeSubproperties((Serializable) item.object, newElement,
									currentMapping.getMappings(), document);
							} else log.warn("Could not create an element with name :" + dataMerge.getCrateNodeExpression());
						}
					}
					
//					} catch (Exception e) {
//						e.printStackTrace();
//						
//					}
					
				} //else  if (dataMerge.getMappingEstrategy() == SerializingMode.BIND_COLLECTION_FULL_RESET) {
				 
			//TODO : Full refresh and other strategies.
		
	}
				
		
		

		// ...............................................
		
//		dataMerge.setNodeItems(collectionNodeList);



		
//		dataMerge.setMappingEstrategy(currentMapping.getCollectionBindStrategy());
//		log.debug(" Setting Mapping Expression to: " + currentMapping.getMappingExpression());

		log.debug(" Exiting collection merging. : ");

	}

	/**
	 * Populates the data Merge with the actual collection objects.
	 * @param parentObject The actual java object (dto).
	 * @param propertyName The property name of the key value
	 * @param dataMerge The collection data.
	 * @return Collection Data with the included objects.
	 * @throws MappingException 
	 */
	private static CollectionDataMerge populateMergeObjects(Serializable parentObject, String propertyName,
			CollectionDataMerge dataMerge, String keyPropertyName)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, MappingException {
		ArrayList listObjects = (ArrayList) PropertyUtils.getNestedProperty(parentObject, propertyName);
		dataMerge.setObjectItems(listObjects, keyPropertyName);
		return dataMerge;
	}
	
	/**
	 * Populates the data Merge with the actual collection objects without setting the key
	 * @param parentObject The actual java object (dto).
	 * @param propertyName The property name of the key value
	 * @param dataMerge The collection data.
	 * @return Collection Data with the included objects.
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws MappingException 
	 */
	private static CollectionDataMerge populateMergeObjects(Serializable parentObject, String propertyName,
			CollectionDataMerge dataMerge) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		ArrayList listObjects = (ArrayList) PropertyUtils.getNestedProperty(parentObject, propertyName);
		dataMerge.setObjectItemsWithoutKey(listObjects);
		return dataMerge;
	}

	/**
	 * Extract the noe key
	 * @param nodePosition
	 * @param objectKey
	 * @param currentNode
	 * @return
	 * @throws MappingException
	 */
	private static String extractNodeKey(int nodePosition, String objectKey, CollectionItem currentNode, Map<String, String> namespaces) throws MappingException {
		String nodeValue ="";
		try {
			nodeValue = XPathHelper.getXPath(namespaces).evaluate((String) objectKey, currentNode.node);
			log.debug(" Returning Node key " + nodeValue);
			if (nodeValue == null)
				nodeValue = "";
		} catch (XPathExpressionException e) {
			ExceptionHelper.treatMappingException("", currentNode.classNode.getName(), 
					currentNode.keyNodeExpression, objectKey, e, 
					"Could not exptract the key node value");
		}
		return nodeValue;
	}

	

	/**
	 * Gets the node key value string.
	 * @param node The node being evaluated.
	 * @param expression THe 
	 * @return
	 * @throws XPathExpressionException 
	 */
	public static String getKeyValue(Node node, String expression, Map<String, String> namespaces ) throws XPathExpressionException {
		if(expression == null  || expression == "" || node == null ) return "";
		String nodeKey = (String) 
				XPathHelper.getXPath(namespaces).evaluate(expression, node,XPathConstants.STRING);
		return nodeKey;
	}


	/**
	 * Method to delete the nodes. 
	 * @param mainNode
	 * @param dataMerge
	 * @return
	 */
	private static Node checkAndDeleteNodes(Node mainNode, CollectionDataMerge dataMerge) {
		Hashtable<String, Object> objectKeys = dataMerge.getObjectKeysAsMap();
		for (CollectionItem item : dataMerge.getNodeItems()) {
			if (objectKeys.containsKey(item.key) == false)
				mainNode.removeChild(item.node);
			// TODO Esto lo quita tel
			dataMerge.getNodeItems().remove(item);
		}
		return mainNode;
	}

}
