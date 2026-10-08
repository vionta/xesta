package net.vionta.xml.xesta.repository.impl.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adjusts the path (text file) based on the object properties 
 * and the marker patterns { }
 */
public class PathAdjust {

	private static Logger log = LoggerFactory.getLogger(PathAdjust.class);
	
	/**
	 * Marks the start of a property.
	 */
	public static final String START_PROPERTY_MARKER="{";
	/**
	 * Marks the end of a property.
	 */
	public static final String END_PROPERTY_MARKER="}";
	
	/**
	 * Adjusts a path with the properties from the internal 
	 * objects.
	 * @param path the provided file path.
	 * @param vo The object from where the properties are 
	 * 			 taken. 
	 * @return the adjusted path.
	 */
	public static String adjustedPath(String path, Serializable vo) {
		String workPath = path;
		while(isAdjustable(workPath)) {
			workPath = adjustPath(workPath, vo);
		}
		return workPath;
	}

	/**
	 * Checks if the path have place holders for adjustment.
	 * @param path the file path.
	 * @return true if the path can/should be adjusted.
	 */
	private static boolean isAdjustable(String path) {
		if(path == null) return false; 
		int paramStart = path.indexOf(START_PROPERTY_MARKER);
		if(paramStart == -1) return false;
		int paramEnd = path.substring((paramStart+1)).indexOf(END_PROPERTY_MARKER);
		if(paramEnd == -1) return false;		
		return true;
	}

	/**
	 * Adjusts the path. Takes the object properties and updates the marked 
	 * placeholders to adjust the path.
	 * 
	 * @param path The file path.
	 * @param vo The pojo or value object.
	 * @return The adjusted file path.
	 * @throws IllegalStateException
	 */
	public static String adjustPath(final String path, final Serializable vo) throws IllegalStateException {
		int paramNameStartPosition = path.indexOf(START_PROPERTY_MARKER);
		String paramName = path.substring(paramNameStartPosition + 1, +paramNameStartPosition + 
				path.substring(paramNameStartPosition).indexOf(END_PROPERTY_MARKER) );
		String paramValue;
		try {
			paramValue = BeanUtils.getProperty(vo, paramName);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
			log.debug("Problems accessing {} property", paramName);
			throw new IllegalStateException("Configuration property could not be accessed");
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
			log.debug("Problems accessing {} property", paramName);
			throw new IllegalStateException("Problems ocurred during the property Configuration");
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
			log.debug("Problems accessing {} property", paramName);
			throw new IllegalStateException("Configuration property could not be found");
		}
		String resultingPath = path.replaceAll("\\"+START_PROPERTY_MARKER+paramName+"\\"+END_PROPERTY_MARKER, paramValue );
		return resultingPath ;
	}

}
