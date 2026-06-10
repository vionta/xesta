package net.vionta.xml.xesta.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializingMode {

	// What should we do with new elements, when serializing.
	public int mode() default CREATE_ON_NOT_EXISTING ; 

	public static final int FAIL_ON_NOT_EXISTING = 1;
	public static final int CREATE_ON_NOT_EXISTING = 2;
	public static final int SKIP_ON_NOT_EXISTING = 3;

	
	// Collection Strategies
	public static final int BIND_COLLECTION_BY_KEY = 1;
	public static final int BIND_COLLECTION_BY_POSITION = 2;
	public static final int BIND_COLLECTION_APPEND_LAST = 3;
	public static final int BIND_COLLECTION_APPEND_FIRST = 4;
	public static final int BIND_COLLECTION_FULL_RESET = 5;
	
	// Should we delete elements or just update/add
	public static final int COLLECTION_DONT_DELETE_UNMATCHED= 1;
	public static final int COLLECTION_DELETE_UNMATCHED = 2;

	
	
}
