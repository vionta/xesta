package net.vionta.xml.xesta.bind.serialize.datamerge;

import java.util.Collection;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.annotation.SerializingMode;
import net.vionta.xml.xesta.bind.serialize.CollectionItem;
import net.vionta.xml.xesta.bind.serialize.Serializer;
import net.vionta.xml.xesta.bind.serialize.util.XPathHelper;
import net.vionta.xml.xesta.bind.serialize.util.XPathManager;
import net.vionta.xml.xesta.exception.ExceptionHelper;
import net.vionta.xml.xesta.exception.MappingException;
import net.vionta.xml.xesta.repository.exception.PersistException;



public class DataMergeBuilder {

	private static Logger log = LoggerFactory.getLogger(DataMergeBuilder.class);

	/**
	 * Convenience method for populating dataMerges objects.
	 * 
	 * @param currentMapping
	 * @param propertyName
	 * @param mainClass
	 * @return
	 * @throws MappingException 
	 */
	public static CollectionDataMerge buildDataMerge(Mapping currentMapping) throws MappingException {

		CollectionDataMerge dataMerge = new CollectionDataMerge();
		dataMerge.setMapping(currentMapping);
		
		//Main collection merge options.
		dataMerge.setMappingEstrategy(currentMapping.getCollectionBindStrategy());
		dataMerge.setIsDeleteAllowed(
				SerializingMode.COLLECTION_DELETE_UNMATCHED == currentMapping.getCollectionDeleteUnmatched());

		boolean multipleCollection = (currentMapping.getCollectionClasses().size() > 1);

		// There is no collection classes there is a failure.
		if (currentMapping.getCollectionClasses().size() == 0)
			ExceptionHelper.treatMappingException(currentMapping.getPropertyClass().getName(), null,
					currentMapping.getPropertyName(), currentMapping.getMappingExpression(),
					new ArrayIndexOutOfBoundsException(), "Could not find any element classes for collection ");

		if (multipleCollection) {

			//	private ArrayList<CollectionItem> objectItems = new ArrayList<CollectionItem>();
			//	private ArrayList<CollectionItem> nodeItems = new ArrayList<CollectionItem>();
			
			//Single collection (main create mappings at collection level).
		} else {

			if(dataMerge.getMappingEstrategy() == SerializingMode.BIND_COLLECTION_BY_KEY) {
				Object[] classesArray = currentMapping.getCollectionClasses().keySet().toArray();
				if (classesArray.length == 1) {
					Object firstClass = classesArray[0];
					dataMerge.setClassNode((Class)firstClass);
					String elementMappingExpression = (String) currentMapping.getCollectionClasses().get(firstClass);
					dataMerge.setCrateNodeExpression(elementMappingExpression);
					dataMerge.setCreatePossible(Serializer.isMappingSimple( elementMappingExpression));  
					//Add node elements and objects expressions
					String[] keyParameters =  findKeyNodeExpression(currentMapping);
					dataMerge.setKeyNodeExpression(keyParameters[0]);
					dataMerge.setKeyNodeParameter(keyParameters[1]);
				}
			} else if (dataMerge.getMappingEstrategy() == SerializingMode.BIND_COLLECTION_BY_POSITION) {
				
				Object[] classesArray = currentMapping.getCollectionClasses().keySet().toArray();
				if (classesArray.length == 1) {
					Object firstClass = classesArray[0];
					dataMerge.setClassNode((Class)firstClass);
					String elementMappingExpression = (String) currentMapping.getCollectionClasses().get(firstClass);
					dataMerge.setCrateNodeExpression(elementMappingExpression);
					dataMerge.setCreatePossible(Serializer.isMappingSimple( elementMappingExpression));  
					
				}
			
			} else if (dataMerge.getMappingEstrategy() == SerializingMode.BIND_COLLECTION_FULL_RESET) {
			
			
		} 
			 
		}
		return dataMerge;
	}

	/**
	 * Look for the first property marked as key. Only suitable for single 
	 * collections (in other case .
	 * @param currentMapping
	 * @return
	 */
	private static String[] findKeyNodeExpression(Mapping currentMapping)  {
		String[] keyNodeExpression = new String[2]; 
		keyNodeExpression[0] = null;
		keyNodeExpression[1] = null;
		for(Mapping nestedMappings : currentMapping.getMappings()) {
			if(nestedMappings.isKey()) {
				keyNodeExpression[0] = nestedMappings.getMappingExpression();
				keyNodeExpression[1] = nestedMappings.getPropertyName();
				return keyNodeExpression;
			}
		}
		//change to mapping exception
		log.warn("A key property was not defined for object " +currentMapping.getPropertyName());
		return keyNodeExpression;
	}

	public static CollectionDataMerge fillNodes(CollectionDataMerge dataMerge, Mapping currentMapping, Node mainNode) throws PersistException {
		
		String collectionMappingExpression = currentMapping.getMappingExpression();
		
		boolean collectionExpression = (collectionMappingExpression != null && !"".equals(collectionMappingExpression));
		
		try {
		
		// Simple collection
		if (!dataMerge.isMultipleCollection()) {
			String elementMappingExpression = dataMerge.getCrateNodeExpression();
			boolean elementExpression = (elementMappingExpression != null && elementMappingExpression != "");
			NodeList nodeList = null;
			XPath buildXPath = XPathHelper.getXPath(currentMapping.getNamespaces());
			
			
			if( collectionExpression && !elementExpression) {
				nodeList = (NodeList) buildXPath.evaluate(collectionMappingExpression, mainNode, XPathConstants.NODESET);
			} else if (collectionExpression && elementExpression) {
				Node  newMainNode = (Node) buildXPath.evaluate(collectionMappingExpression, mainNode, XPathConstants.NODE);
				nodeList = (NodeList) buildXPath.evaluate(elementMappingExpression, newMainNode, XPathConstants.NODESET);
			} else if (!collectionExpression && elementExpression) {
				nodeList = (NodeList) buildXPath.evaluate(elementMappingExpression, mainNode, XPathConstants.NODESET);				
			}
			
			for(int i = 0 ; i < nodeList.getLength() ; i++) {
				CollectionItem item = new CollectionItem();
				item.node=nodeList.item(i);
				if(SerializingMode.BIND_COLLECTION_BY_KEY== dataMerge.getMappingEstrategy()) {
					String nodeKey = (String) buildXPath.evaluate(dataMerge.getKeyNodeExpression(), item.node, XPathConstants.STRING);
					item.key= nodeKey;
				} // Pendiente por posicion 
				item.position=i;
				dataMerge.getNodeItems().add(item);
				
			}
			
			
		
		//Multiple collection
		} else {
			
		}
		
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error("Error retrieving node for collection serialization");
			log.debug(" Stack : "+e.getStackTrace());
			PersistException persistException = new PersistException();
			persistException.setObjectName(currentMapping.getPropertyName());
			persistException.setPath(currentMapping.getMappingExpression());
			persistException.setSourceExpeption(e);
			throw persistException;
		}
		
		return dataMerge;
	}

}
