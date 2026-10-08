package net.vionta.xml.xesta.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE})
//@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
/**
 * The namespace list.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface namespaces {
	
	 /**
	 * @return An array of namespaces.
	 */
	namespace[] value() default {};
	
}