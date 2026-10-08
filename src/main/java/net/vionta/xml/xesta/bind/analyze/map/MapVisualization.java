package net.vionta.xml.xesta.bind.analyze.map;

import java.util.Map.Entry;

import net.vionta.xml.xesta.bind.annotation.Bind;

/**
 * An utility class to ease the review and visualization of the 
 * extracted mappings. The mapping objects are the representation 
 * of the structure identified by the class structure and 
 * annotations.
 */
public class MapVisualization {

	/**
	 * This class returns the mapping visualization of the
	 * mapping element.  
	 * 
	 * @param mapping The mapping as extracted by the application.
	 * @return A text based representation of the extracted 
	 * 		   mapping objects.
	 */
	public static String printMapping(Mapping mapping) {
		return
				"\n...Mapping................"  
				+ getMappingDescription(mapping, "\n")
				+ "\n....................";
	}
	
	/**
	 * Recursive method that elaborates the mapping description. 
	 * 
	 * @param mapping The provided mapping.
	 * @param path An utility param to increase iteratively the 
	 * 				padding of the subfields.
	 * @return The String of the provided mapping and subfields.
	 */
	private static String getMappingDescription(Mapping mapping, String path) {
		String item = "";
		String multiplicity = "";
		if (mapping.getIsCollection() && mapping.getIsMultilple()) multiplicity += "[*]";
		else if(mapping.getIsCollection())  {
			multiplicity += "[]";
		}
		if(mapping.isKey())   item += "#";
		else item += "-";
		String line1 = path + item+multiplicity+ mapping.getPropertyName() + " : " + mapping.getMappingExpression() +" -> ";
		//\\+	mapping.getPropertyClass();
		for(Mapping submapping : mapping.getMappings() ) {
			line1 += getMappingDescription(submapping, path+"  ") ;
		}
		if(mapping.getIsCollection()) {
			for(Entry<Class, Mapping> submapping : mapping.getCollectionMappings().entrySet()) {
				line1 += path+" " + submapping.getKey() ;
				line1 += getMappingDescription(submapping.getValue(), path+"  ") ;
			}
		}
		return line1;
	}
	
	
	/**
	 * An utility method to print a view of the provided Bind annotation 
	 * information.
	 * @param annotation A Bind annotation.
	 * @return The text representation of the annotation.
	 */
	public static String printBindAnnotationDescription(Bind annotation) {
		String description = "\n....Bind................"  ;
		description += "\n"  + annotation.expression() +" "+ ((annotation.key() ) ? "#" : ""  );
		if(annotation.classNames()  != null && annotation.classNames()  != null ) {
		 description += " [";	
		 	for (int i = 0; i < annotation.classNames()  .length; i++) {
		 		description += " "+annotation.classNames()[i];			
		 		}
				description += " ]";
			}
		return description;
	}
	
}
