package net.vionta.xml.xesta.bind.analyze.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	
	/**
	 * The mappings information of a collection, that can be multiple 
	 * and be related to several class names. You should use the 
	 * collectionMappings accessor methods (getter and setter) as the 
	 * internal field is expected to be renamed.
	 */
	protected Map collectionClasses = new HashMap<Class, Mapping>();
	
	/**
	 * Identifies if a collection may have more than one 
	 * type of elements.
	 */
	protected Boolean isCollection = Boolean.FALSE;
	
	/**
	 * Identifies if a collection may have more than one 
	 * type of elements.
	 */
	protected Boolean isMultilple = Boolean.FALSE;
	
	/**
	 * The formatter class for the field, like a date format 
	 * for specific field types.
	 */
	protected Class propertyFormatter;
	
	/**
	 * Sub mappings of the current instance object, 
	 * represent the subelements from the current 
	 * element.
	 */
	protected ArrayList<Mapping> mappings;
	
	/**
	 * A value field for internal management purposes.
	 */
	protected Object value;
	
	/**
	 * Configuration of the specific serialization operation. 
	 */
	protected int serializeMode  = SerializingMode.CREATE_ON_NOT_EXISTING;
	
	/**
	 * Configuration of the specific deserialization operation. 
	 */
	protected int deserializeMode  = DeserializingMode.AVOID_ON_NOT_EXISTING;
	
	/**
	 * A collection item identification configuration. Defines if the items
	 * are identified by key, possition, or the collection should be binded 
	 * using the traditional drop/rebuild approach. 
	 */
	protected int collectionBindStrategy = SerializingMode.BIND_COLLECTION_BY_KEY;

	/**
	 * This option defines if elements from a collection should be deleted 
	 * on the original document if not provided from the collection. For 
	 * example, if a element has 7 subelements, we mapped to a collection and 
	 * provide only 6 elements back to serialization, should the unpaired 
	 * element or elements be removed.
	 */
	protected int collectionDeleteUnmatched = SerializingMode.COLLECTION_DELETE_UNMATCHED;

	/**
	 * A list of the namespaces of the current mapping
	 * expression.  
	 * Namespaces can be added using Q{alias,uri} 
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

	@Deprecated
	public Map<Class, Mapping> getCollectionClasses() {
		return collectionClasses;
	}

	public Map<Class, Mapping> getCollectionMappings() {
		return collectionClasses;
	}

	public Mapping getCollectionClassMapping() {
		if (this.isMultilple) throw new IllegalStateException("Collection is multiple, you must Iterate over collection classes");
		Set entrySet = collectionClasses.entrySet();
		Entry next2 = (Entry)entrySet.iterator().next();
		return (Mapping) next2.getValue();
	}
	
	@Deprecated	
	public void setCollectionClasses(Map<Class, Mapping> collectionClasses) {
		this.collectionClasses = collectionClasses;
	}

	public void setCollectionMappings(Map<Class, Mapping> collectionMapping) {
		this.collectionClasses = collectionMapping;
	}
	
	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}

	public Boolean getIsCollection() {
		return isCollection;
	}

	public void setIsCollection(Boolean isCollection) {
		this.isCollection = isCollection;
	}

}
