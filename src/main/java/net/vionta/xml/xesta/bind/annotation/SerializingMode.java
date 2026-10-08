package net.vionta.xml.xesta.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration options for the serialization operation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializingMode {

	// What should we do with new elements, when serializing.
	int mode() default CREATE_ON_NOT_EXISTING ; 

	int FAIL_ON_NOT_EXISTING = 1;
	int CREATE_ON_NOT_EXISTING = 2;
	int SKIP_ON_NOT_EXISTING = 3;

	// Collection Strategies
	/**
	 * Collection option that matches elements by a property used 
	 * as a key. It requires that the considered class or classes
	 * have a mapped property witth the key true option.
	 */
	int BIND_COLLECTION_BY_KEY = 10;
	
	/**
	 * This option considers that elements and collection beans 
	 * will not switch positions. 
	 */
	int BIND_COLLECTION_BY_POSITION = 11;
	
	/**
	 * This option resets the collection contents on every 
	 * serialization operation.
	 */
	int BIND_COLLECTION_FULL_RESET = 12;
	
	/**
	 * Option to append the new elements at the end of the list.
	 */
	int BIND_COLLECTION_APPEND_LAST = 21;

	/**
	 * Option to insert the new elements at the beginning of the list.
	 */
	int BIND_COLLECTION_INSERT_FIRST = 22;
	
	/**
	 * Serialization option to avoid deleting elements on the 
	 * file that does not have a corresponding java element. 
	 * <b>This is the default option</b>
	 */
	int COLLECTION_DONT_DELETE_UNMATCHED = 30 ;
	
	/**
	 * Serialization option to mark for deletion elements on the 
	 * file that does not have a corresponding java element. 
	 */
	int COLLECTION_DELETE_UNMATCHED = 31;

}
