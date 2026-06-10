package net.vionta.xml.xesta.bind.annotation;

public @interface DeserializingMode {

	public int mode() default WARN_ON_NOT_EXISTING ; 

	public static final int FAIL_ON_NOT_EXISTING = 1 ;
	public static final int WARN_ON_NOT_EXISTING = 2 ;
	public static final int AVOID_ON_NOT_EXISTING = 3 ;

}
