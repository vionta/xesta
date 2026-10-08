package net.vionta.xml.xesta.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception creation helper 
 */
public class ExceptionHelper {

	private static Logger log = LoggerFactory.getLogger(ExceptionHelper.class);

	/**
	 * Creates and launches the mapping exception. 
	 *
	 * @param mainClassName
	 * @param targetClassName
	 * @param targetPropertyName
	 * @param mappingExpression
	 * @param e
	 * @param logMessage
	 * @throws MappingException
	 */
	public static void treatMappingException(String mainClassName, String targetClassName, String targetPropertyName,
			String mappingExpression, 
			Exception e, String logMessage) throws MappingException {
		MappingException me = new MappingException();
		me.setException(e);
		me.setSourceClassName(mainClassName);
		me.setMappingExpression(mappingExpression);
		me.setTargetPropertyName("classNames");
		me.setTargetClassName(targetClassName);
		if(e!=null)e.printStackTrace();
		log.error(logMessage);
		throw me;
	}
	
}
