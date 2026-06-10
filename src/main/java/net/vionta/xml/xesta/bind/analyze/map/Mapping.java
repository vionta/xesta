package net.vionta.xml.xesta.bind.analyze.map;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Subclasses that implements the iterative object 
 * document mapping.
 */
public class Mapping extends BaseMapping {

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

	public Mapping(ArrayList<Mapping> mappings) {
		super();
		this.mappings = mappings;
	}

	@Override
	public String toString() {
		return "Mapping "
				+ "\n [property : " + propertyName + " -> Exp : " + mappingExpression + ", (key: " + key
				+ " - \n namespaces=" + namespaces + " \n  propertyClass=" + propertyClass + ", classes=" + collectionClasses + ", isMultilple="
				+ isMultilple + ", propertyFormatter=" + propertyFormatter +" value="
				+ value + ", Modes ser (" + serializeMode + ")  deser(" + deserializeMode
				+ ")  bind(" + collectionBindStrategy + ") del("
				+ collectionDeleteUnmatched + ") + \n  mappings=" + mappings + ", ]";
	}

}
