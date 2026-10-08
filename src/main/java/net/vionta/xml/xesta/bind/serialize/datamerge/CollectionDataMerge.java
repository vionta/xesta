package net.vionta.xml.xesta.bind.serialize.datamerge;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.serialize.CollectionItem;
import net.vionta.xml.xesta.bind.serialize.Serializer;
import net.vionta.xml.xesta.exception.ExceptionHelper;
import net.vionta.xml.xesta.exception.MappingException; 

/**
 * Utility class to support the implementation of the collection management 
 * algorithms.
 */
public class CollectionDataMerge {
	
//	private static Logger log  = LoggerFactory.getLogger(Serializer.class);
	
	/**
	 * The collection mapping.
	 */
	private Mapping mapping ; 
	
	/**
	 * Use the mapping isCollection isMultiple instead.
	 */
	@Deprecated 
	private boolean isMultipleCollection = false; 
	
	/**
	 * Use the mapping data instead.
	 */
	@Deprecated
	private int mappingEstrategy; 
	
	/**
	 * Use the mapping data instead.
	 */
	@Deprecated
	private boolean isDeleteAllowed ;
	
	/**
	 * The class instance tthat holds the object
	 * part of the data.
	 */
	private Class classNode;
	
	/**
	 * Expression that points to the key node or attribute of the 
	 * element, Used for sorting and comparing elements.
	 */
	private String keyNodeExpression;
	
	/**
	 * Name of the key node property/parameter on the java 
	 * class.
	 */
	private String keyNodeParameter;
	
	/**
	 * A boolean indicating if the node could be created with 
	 * the expression pattern.
	 */
	private boolean isCreatePossible = false; 
	
	/**
	 * The XPath expression to create the node.
	 */
	private String crateNodeExpression ;

	/**
	 * The collection object items.
	 */
	private ArrayList<CollectionItem> objectItems = new ArrayList<CollectionItem>();
	
	/**
	 * The collection Xml node items.
	 */
	private ArrayList<CollectionItem> nodeItems = new ArrayList<CollectionItem>();
	
	public Mapping getMapping() {
		return mapping;
	}
	public void setMapping(Mapping mapping) {	
		this.mapping = mapping;
	}
	public int getMappingEstrategy() {
		return mappingEstrategy;
	}
	public void setMappingEstrategy(int mappingEstrategy) {
		this.mappingEstrategy = mappingEstrategy;
	}
	public boolean getIsDeleteAllowed() {
		return isDeleteAllowed;
	}
	public void setIsDeleteAllowed(boolean isDeleteAllowed) {
		this.isDeleteAllowed = isDeleteAllowed;
	}
	public Class getClassNode() {
		return classNode;
	}
	public void setClassNode(Class classNode) {
		this.classNode = classNode;
	}
	public boolean isMultipleCollection() {
		return isMultipleCollection;
	}
	public void setMultipleCollection(boolean isMultipleCollection) {
		this.isMultipleCollection = isMultipleCollection;
	}
	public String getKeyNodeExpression() {
		return keyNodeExpression;
	}
	public void setKeyNodeExpression(String keyNodeExpression) {
		this.keyNodeExpression = keyNodeExpression;
	}
	public boolean isCreatePossible() {
		return isCreatePossible;
	}
	public void setCreatePossible(boolean isCreatePossible) {
		this.isCreatePossible = isCreatePossible;
	}
	public String getCrateNodeExpression() {
		return crateNodeExpression;
	}
	public void setCrateNodeExpression(String crateNodeExpression) {
		this.crateNodeExpression = crateNodeExpression;
	}

	public ArrayList<CollectionItem> getObjectItems() {
		return objectItems;
	}

	public String getKeyNodeParameter() {
		return keyNodeParameter;
	}
	
	public void setKeyNodeParameter(String keyNodeParameter) {
		this.keyNodeParameter = keyNodeParameter;
	}
	
	/**
	 * Add object items and add the element key.
	 * @param objectItems
	 * @param propertyName
	 * @throws MappingException
	 */
	public void setObjectItems(ArrayList<Object> objectItems, String propertyName) throws MappingException  {
		for(Object object : objectItems) {
			CollectionItem cItem = new CollectionItem(object);
			try {
				cItem.key  = (String) PropertyUtils.getNestedProperty(object, propertyName);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException e) {
				e.printStackTrace();
				ExceptionHelper.treatMappingException("List", object.getClass().getName(), propertyName, crateNodeExpression, e, 
						"A collection element key could not be obtained.");
			}	
			this.objectItems.add(cItem);
		}
	}
	
	public void setObjectItemsWithoutKey(ArrayList<Object> objectItems)   {
		for(Object object : objectItems) {
			CollectionItem cItem = new CollectionItem(object);
			this.objectItems.add(cItem);
		}
	}
	
	public ArrayList<CollectionItem> getNodeItems() {
		return nodeItems;
	}

	public void setNodeItems(ArrayList<CollectionItem> nodeItems) {
		this.nodeItems = nodeItems;
	}
	
	/**
	 * Gets the list of object keys as an array of strings.
	 * @return 
	 */
	public String[] getObjectKeys() {
		String[] objectKeys = {};
		for( int i = 0 ; i < objectItems.size() ; i++) {
			objectKeys[i] = objectItems.get(i).key;
		}
		return objectKeys;
	}

	/**
	 * Gets the list of object element keys as a map.
	 * @return 
	 */
	public Hashtable<String, Object> getObjectKeysAsMap() {
		Hashtable<String, Object> objectKeys = new Hashtable<String, Object>();
		for( int i = 0 ; i < objectItems.size() ; i++) {
			objectKeys.put(objectItems.get(i).key, objectItems.get(i));
		}
		return objectKeys;
	}
	@Override
	public String toString() {
		return "CollectionDataMerge [mapping=" + mapping + ", isMultipleCollection=" + isMultipleCollection
				+ ", mappingEstrategy=" + mappingEstrategy + ", isDeleteAllowed=" + isDeleteAllowed + ", classNode="
				+ classNode + ", keyNodeExpression=" + keyNodeExpression + ", isCreatePossible=" + isCreatePossible
				+ ", crateNodeExpression=" + crateNodeExpression + ", objectItems=" + objectItems + ", nodeItems="
				+ nodeItems + "]";
	}
	
	
}
