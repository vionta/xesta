package net.vionta.xml.xesta.bind.analyze.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.vionta.xml.xesta.bind.annotation.DeserializingMode;
import net.vionta.xml.xesta.bind.annotation.SerializingMode;

/**
 * Base Mapping class, that provides the list of attributes.
 */
class BaseMapping {

	/**
	 * The name of the java property that the mapping 
	 * points to. It should be a valid existing property of 
	 * a Serializable class.
	 */
	protected String propertyName; 
	/**
	 * The xpath 3.x expression that points to the 
	 * location of the document.
	 */
	protected String mappingExpression;
	/**
	 * Used on lists to identify the property that identifies 
	 * the node. If it is true, a property with a value that
	 * matches the node value will be replaced instead of added.
	 */
	protected boolean key = false;
	
	/**
	 * The class the node will be mapped to. In most cases it could
	 * be inferred and in those cases it is taken directly from the 
	 * mapped java property.
	 */
	protected Class propertyClass;
	
	protected Map collectionClasses = new HashMap<Class, String>();
//	
//	/**
//	 * The described collection classes, taken from the annotation 
//	 * or from the collection generics definition.
//	 * 
//	 */
//	protected List<Class> collectionClasses;
//	
//	/**
//	 * The described collection classes, taken from the annotation 
//	 * or from the collection generics definition.
//	 */
//	protected List<String> collectionClassExpressions;
	
	
	
	/**
	 * Identifies if a collection may have more than one 
	 * type of elements.
	 */
	protected Boolean isMultilple = Boolean.FALSE;
	protected Class propertyFormatter;	
	/**
	 * Sub mappings of the current instance object, 
	 * represent the subelements from the current 
	 * element.
	 */
	protected ArrayList<Mapping> mappings;
	protected Object value;
	
	protected int serializeMode  = SerializingMode.CREATE_ON_NOT_EXISTING;
	protected int deserializeMode  = DeserializingMode.AVOID_ON_NOT_EXISTING;
	
	protected int collectionBindStrategy = SerializingMode.BIND_COLLECTION_BY_KEY;
	protected int collectionDeleteUnmatched = SerializingMode.COLLECTION_DELETE_UNMATCHED;

	/**
	 * A list of the namespaces of the current mapping
	 * expression.  
	 * Namespaces can be added using Q{<alias>,<uri>} 
	 * saxonica syntax.
	 */
	protected Map<String, String> namespaces ;

	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getMappingExpression() {
		return mappingExpression;
	}

	public void setMappingExpression(String mappingExpression) {
		this.mappingExpression = mappingExpression;
	}

	public ArrayList<Mapping> getMappings() {
		if(mappings==null) return new ArrayList<Mapping>();
		return mappings;
	}

	public void setMappings(ArrayList<Mapping> mappings) {
		this.mappings = mappings;
	}

	public Class getPropertyClass() {
		return propertyClass;
	}

	public void setPropertyClass(Class propertyClass) {
		this.propertyClass = propertyClass;
	}

	public Class getPropertyFormatter() {
		return propertyFormatter;
	}

	public void setPropertyFormatter(Class propertyFormatter) {
		this.propertyFormatter = propertyFormatter;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Boolean getIsMultilple() {
		return isMultilple;
	}

	public void setIsMultilple(Boolean isMultilple) {
		this.isMultilple = isMultilple;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}


	public int getSerializeMode() {
		return serializeMode;
	}

	public void setSerializeMode(int serializeMode) {
		this.serializeMode = serializeMode;
	}

	public int getDeserializeMode() {
		return deserializeMode;
	}

	public void setDeserializeMode(int deserializeMode) {
		this.deserializeMode = deserializeMode;
	}

	public int getCollectionBindStrategy() {
		return collectionBindStrategy;
	}

	public void setCollectionBindStrategy(int collectionBindStrategy) {
		this.collectionBindStrategy = collectionBindStrategy;
	}

	public int getCollectionDeleteUnmatched() {
		return collectionDeleteUnmatched;
	}

	public void setCollectionDeleteUnmatched(int collectionDeleteUnmatched) {
		this.collectionDeleteUnmatched = collectionDeleteUnmatched;
	}


	public Map getCollectionClasses() {
		return collectionClasses;
	}

	public void setCollectionClasses(Map collectionClasses) {
		this.collectionClasses = collectionClasses;
	}

	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}
	
}
