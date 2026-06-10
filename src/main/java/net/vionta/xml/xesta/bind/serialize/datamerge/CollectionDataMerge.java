package net.vionta.xml.xesta.bind.serialize.datamerge;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.serialize.CollectionItem;
import net.vionta.xml.xesta.bind.serialize.CollectionSerializerHelper;
import net.vionta.xml.xesta.bind.serialize.Serializer;
import net.vionta.xml.xesta.exception.ExceptionHelper;
import net.vionta.xml.xesta.exception.MappingException;

public class CollectionDataMerge {
	
	private static Logger log  = LoggerFactory.getLogger(Serializer.class);
	
	private Mapping mapping ; 
	private boolean isMultipleCollection = false; 
	private int mappingEstrategy; 
	private boolean isDeleteAllowed ;
	
	private Class classNode;
	/**
	 * Expression that points to the key node or attribute of the 
	 * element, Used for sorting and comparing elements.
	 */
	private String keyNodeExpression;
	private String keyNodeParameter;
	
	private boolean isCreatePossible = false; 
	private String crateNodeExpression ;

	private ArrayList<CollectionItem> objectItems = new ArrayList<CollectionItem>();
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
		};
	}
	
	public void setObjectItemsWithoutKey(ArrayList<Object> objectItems)   {
		for(Object object : objectItems) {
			CollectionItem cItem = new CollectionItem(object);
			this.objectItems.add(cItem);
		};
	}
	
	public ArrayList<CollectionItem> getNodeItems() {
		return nodeItems;
	}
	public void setNodeItems(ArrayList<CollectionItem> nodeItems) {
		this.nodeItems = nodeItems;
	}
//	public void setNodeItems(NodeList nodes) throws XPathExpressionException {
//		for (int i = 0; i <= nodes.getLength(); i++) {
//			Node node = nodes.item(i);
//			CollectionItem cItem = new CollectionItem();
//			cItem.position = i; 
//			cItem.key = CollectionSerializerHelper.getKeyValue(node, this.keyNodeExpression ); 
//			cItem.keyNodeExpression = this.keyNodeExpression;
//			cItem.node=node;
//			this.nodeItems.add(cItem);
//		}
//	}
	
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
