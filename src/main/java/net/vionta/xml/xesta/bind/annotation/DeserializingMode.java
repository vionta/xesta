package net.vionta.xml.xesta.bind.annotation;

/**
 * The options defined for the deserializing 
 * operation (read Xml to objects).
 * 
 * <b>NOTE: Some of this options have not been implemented yet</b>
 */
public @interface DeserializingMode {

	/**
	 * @return The default deserialize mode.
	 */
	int mode() default WARN_ON_NOT_EXISTING ; 

	/**
	 * Fail when an element that exists on the Xml 
	 * does not exist on the element collection.
	 */
	
	int FAIL_ON_NOT_EXISTING = 1 ;
	
	/**
	 * Warn when an Xml element that exists on the 
	 * Xml does not exists on the Java collection.
	 */
	int WARN_ON_NOT_EXISTING = 2 ;
	
	/**
	 * Do not deserialize elements that 
	 * does not have a matching java propertiy.
	 */
	int AVOID_ON_NOT_EXISTING = 3 ;

}
