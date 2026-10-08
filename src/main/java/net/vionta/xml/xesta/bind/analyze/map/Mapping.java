package net.vionta.xml.xesta.bind.analyze.map;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Subclasses that implements the iterative object 
 * document mapping.
 */
public class Mapping extends BaseMapping {

	//Should be used only on root classes
	/**
	 * Default constructor with an xpath parameter 
	 * expression.
	 * @param mappingExpression
	 */
	public Mapping(String mappingExpression) {
		this.mappingExpression = mappingExpression;
	}
	
	/**
	 * A constructor with a Java property name and 
	 * an Xpath expression.
	 * @param propertyName The name of the java property.
	 * @param mappingExpression The Xpath expression of the mapping
	 * to the element/s.
	 */ 
	public Mapping(String propertyName, String mappingExpression) {
		super();
		this.propertyName = propertyName;
		this.mappingExpression = mappingExpression;
	}

	public Mapping(String propertyName, String mappingExpression, ArrayList<Mapping> mappings) {
		super();
		this.propertyName = propertyName;
		this.mappingExpression = mappingExpression;
		this.mappings = mappings;
	}

	public Mapping(Class elementClass, String mappingExpression) {
		super();
		this.propertyClass = elementClass;
		this.mappingExpression = mappingExpression;
	}
	
	public Mapping(ArrayList<Mapping> mappings) {
		super();
		this.mappings = mappings;
	}

	@Override
	public String toString() {
		return "Mapping "
				+ "\n [property : " + propertyName + " -> Exp : " + mappingExpression + ", \n (key: " + key
				+ " - namespaces=" + namespaces + " \n  propertyClass=" + propertyClass + ", classes=" + collectionClasses + ", isMultilple="
				+ isMultilple + ", propertyFormatter=" + propertyFormatter +" value="
				+ value + ", \n :: Mode ser (" + serializeMode + ")  deser(" + deserializeMode
				+ ")  bind(" + collectionBindStrategy + ") del("
				+ collectionDeleteUnmatched + ") + \n  - - mappings=" + mappings + ", ]";
	}

}
