package net.vionta.xml.xesta.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE})
public @interface Bind {

	/**
	 * The XPath mapping expression that points 
	 * to the selected document nodes.
	 * 
	 * @return XPath query expression
	 */
	public String expression() default "";

	/**
	 * Indicates if the attribute identifies the node element. 
	 * 
	 * @return boolean indicating that the attribute 
	 * can be used to identify the node element.
	 */
	public boolean key() default false; 
	
	public int serializingMode() default SerializingMode.CREATE_ON_NOT_EXISTING;
	public int deserializingMode() default DeserializingMode.AVOID_ON_NOT_EXISTING;
	
	public int collectionBindStrategy() default SerializingMode.BIND_COLLECTION_BY_KEY;
	public int collectionDeleteUnmatched() default SerializingMode.COLLECTION_DELETE_UNMATCHED;

	/**
	 * A Java array with the class names of a 
	 * collection contents.
	 * 
	 * @return Collection elements class names.
	 */
	public String[] classNames()  default {};
	
	

	/**
	 * @return a list of the name space alias.  
	 */
 	public String[] namespaceAlias()  default {};

 	public namespace[] namespaces() default {};
 	
 	 /**
 	 * @return a list of the name space uris.  
 	 * use Q{"alias",
 	 */
	public String[] namespaceUris()  default {};

	/**
	 * Default namespace for the main document or element.
	 * @return
	 */
//	public String namespace()  default "";

	
	public boolean auto() default false;
	
	/**
	 * Fail when a defined bind can not be found 
	 * on the document
	 */
	public static final int FAIL_ON_NOT_EXISTING = 1;
	/**
	 * Create the node when a defined bind can not 
	 * be found.
	 */
	public static final int CREATE_ON_NOT_EXISTING = 2;
	/**
	 * Skip the bind when the defined bind can not
	 * be found.
	 */
	public static final int SKIP_ON_NOT_EXISTING = 3;
	
	//Object Property Mapping.
	public static final int MODE_UPDATE_NEW = 11;
	public static final int MODE_NEW = 12;
	public static final int MODE_SET = 13;
	
}
