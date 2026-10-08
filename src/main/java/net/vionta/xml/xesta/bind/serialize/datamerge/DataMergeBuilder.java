package net.vionta.xml.xesta.bind.serialize.datamerge;

import java.util.Map;

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
import net.vionta.xml.xesta.exception.ExceptionHelper;
import net.vionta.xml.xesta.exception.MappingException;
import net.vionta.xml.xesta.repository.exception.PersistException;

/**
 * Utility class to manage the merge of data between the 
 * received java information and the pre-existent xml content.
 */
public class DataMergeBuilder {

	private static Logger log = LoggerFactory.getLogger(DataMergeBuilder.class);

	/**
	 * Convenience method for populating dataMerges objects.
	 * 
	 * @param currentMapping The mapping considered for this operation
	 * @return The wrapper object with the data merged
	 * @throws MappingException An error was detected on the mapping of the object.
	 */
	public static CollectionDataMerge buildDataMerge(Mapping currentMapping) throws MappingException {
		CollectionDataMerge dataMerge = new CollectionDataMerge();
		dataMerge.setMapping(currentMapping);
		//Main collection merge options.
		dataMerge.setMappingEstrategy(currentMapping.getCollectionBindStrategy());
		dataMerge.setIsDeleteAllowed(
				SerializingMode.COLLECTION_DELETE_UNMATCHED == currentMapping.getCollectionDeleteUnmatched());
		boolean multipleCollection = (currentMapping.getCollectionMappings().size() > 1);
		// There is no collection classes there is a failure.
		if (currentMapping.getCollectionMappings().size() == 0)
			ExceptionHelper.treatMappingException(currentMapping.getPropertyClass().getName(), null,
					currentMapping.getPropertyName(), currentMapping.getMappingExpression(),
					new ArrayIndexOutOfBoundsException(), "Could not find any element classes for collection ");
		if (multipleCollection) {
			//TODO:Pendiente implementar. *** 
			//TODO:Pendiente implementar. *** 
			//TODO:Pendiente implementar. *** 
			//TODO:Pendiente implementar. *** 
			//TODO:Pendiente implementar. *** 
			//	private ArrayList<CollectionItem> objectItems = new ArrayList<CollectionItem>();
			//	private ArrayList<CollectionItem> nodeItems = new ArrayList<CollectionItem>();
			
			//Single collection (main create mappings at collection level).
		} else {
			if(dataMerge.getMappingEstrategy() == SerializingMode.BIND_COLLECTION_BY_KEY) {
				Object[] classesArray = currentMapping.getCollectionMappings().keySet().toArray();
				if (classesArray.length == 1) {
					Object firstClass = classesArray[0];
					dataMerge.setClassNode((Class)firstClass);
					String elementMappingExpression = (String) currentMapping.getCollectionMappings().get(firstClass).getMappingExpression();
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
					String elementMappingExpression = (String) currentMapping.getCollectionClasses().get(firstClass).getMappingExpression();
					dataMerge.setCrateNodeExpression(elementMappingExpression);
					dataMerge.setCreatePossible(Serializer.isMappingSimple( elementMappingExpression));  
				}
			} else if (dataMerge.getMappingEstrategy() == SerializingMode.BIND_COLLECTION_FULL_RESET) {
				//TODO:Pendiente implementar. *** 
				//TODO:Pendiente implementar. *** 
				//TODO:Pendiente implementar. *** 
				//TODO:Pendiente implementar. *** 
				//TODO:Pendiente implementar. *** 
			} 
		}
		return dataMerge;
	}

	/**
	 * Look for the first property marked as key. Only suitable for single 
	 * collections (in other case.
	 * @param currentMapping The mapping description object.
	 * @return A two element string array with the mapping expression and 
	 * 				  the java property name of the node key.
	 */
	private static String[] findKeyNodeExpression(Mapping currentMapping)  {
		String[] keyNodeExpression = new String[2]; 
		keyNodeExpression[0] = null;
		keyNodeExpression[1] = null;
		Map<Class, Mapping> collectionClasses = currentMapping.getCollectionClasses();
		Mapping value = collectionClasses.entrySet().iterator().next().getValue();
		for(Mapping nestedMappings:  value.getMappings()) {
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

	/**
	 * Fill the data nodes on the document element with the 
	 * data merge information.
	 * 
	 * @param dataMerge The data prepared for the merge 
	 * 					operation.
	 * @param currentMapping The mapping description of 
	 * 					the collection.
	 * @param mainNode The main node that we take as 
	 * 					reference to fill the nodes.
	 * @return The Collection data merge object.
	 * @throws PersistException Anything that may happen 
	 * 					during the operation.
	 */
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
