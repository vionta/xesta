package net.vionta.xml.xesta.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The Bind annotation, the main annotation used 
 * to define the bindings.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE})
public @interface Bind {

	/**
	 * The XPath mapping expression that points 
	 * to the selected document nodes.
	 * 
	 * @return XPath query expression
	 */
	String expression() default "";

	/**
	 * Indicates if the attribute identifies the node element. 
	 * 
	 * @return boolean indicating that the attribute 
	 * can be used to identify the node element.
	 */
	boolean key() default false; 
	
	/**
	 * @return this option indicates that a new element should be created if the 
	 * expected is not found.
	 */
	int serializingMode() default SerializingMode.CREATE_ON_NOT_EXISTING;

	/**
	 * @return Option that indicates that new elements should not be creted if they 
	 * previously don't exits.
	 */
	int deserializingMode() default DeserializingMode.AVOID_ON_NOT_EXISTING;
	
	/**
	 * @return This option indicates that a collection element is binded, identified 
	 * by key.
	 */
	int collectionBindStrategy() default SerializingMode.BIND_COLLECTION_BY_KEY;
	
	/**
	 * @return This option indicates that elements that are not updated or maintained 
	 * should be removed when binded.
	 */
	int collectionDeleteUnmatched() default SerializingMode.COLLECTION_DELETE_UNMATCHED;

	/**
	 * A Java array with the class names of a 
	 * collection contents.
	 * 
	 * @return Collection elements class names.
	 */
	String[] classNames()  default {};
	
	

	/**
	 * @return a list of the name space alias.  
	 */
 	String[] namespaceAlias()  default {};

 	/**
 	 * @return A list of namespace alias/url pairs
 	 * Work In Progress, non functional yet.
 	 */
 	namespace[] namespaces() default {};
 	
 	 /**
 	 * @return a list of the name space uris.  
 	 * use Q{"alias",
 	 */
	String[] namespaceUris()  default {};

	/**
	 * Default namespace for the main document or element.
	 * @return
	 */
//	public String namespace()  default "";

	
	boolean auto() default false;
	
	/**
	 * Fail when a defined bind can not be found 
	 * on the document
	 */
	int FAIL_ON_NOT_EXISTING = 1;
	
	/**
	 * Create the node when a defined bind can not 
	 * be found.
	 */
	int CREATE_ON_NOT_EXISTING = 2;
	/**
	 * Skip the bind when the defined bind can not
	 * be found.
	 */
	int SKIP_ON_NOT_EXISTING = 3;
	
	//Object Property Mapping.
	int MODE_UPDATE_NEW = 11;
	int MODE_NEW = 12;
	int MODE_SET = 13;
	
}
