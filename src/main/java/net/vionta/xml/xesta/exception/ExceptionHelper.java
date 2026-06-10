package net.vionta.xml.xesta.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vionta.xml.xesta.bind.analyze.BindMapExtractor;
import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.annotation.Bind;

/**
 * Exception creation helper 
 */
public class ExceptionHelper {

	private static Logger log = LoggerFactory.getLogger(ExceptionHelper.class);

	/**
	 * Creates and launches the mapping exception. 
	 * @param mainClazz
	 * @param mainBindAnnotation
	 * @param mapping
	 * @param z
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
		e.printStackTrace();
		log.error(logMessage);
		throw me;
	}
	
}
