package net.vionta.xml.xesta.bind.serialize;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.annotation.Bind;
import net.vionta.xml.xesta.exception.MappingException;

/**
 * Convenience methods for mapping calculations. 
 */
public class MappingHelper {

	/**
	 * @param mappingExpression
	 * @return
	 */
	public static boolean isAttributeMapping( String mappingExpression) {
		if(mappingExpression == null ||  mappingExpression.isEmpty()) return false;
//		Pattern pattern = Pattern.compile("\\/@[\\w-]+$|\\/@\\w+:[\\w-]+$");
//		Pattern pattern = Pattern.compile("@[\\w-]:[\\w-]+\\/@[\\w-]+$|\\/@\\w+:[\\w-]+$");
        Pattern pattern = Pattern.compile("([\\/\\w\\*\\:-]*\\/)?(@[\\w\\*][\\w-]*:?[\\w][\\w-]*[\\w])+$");
		Matcher matcher = pattern.matcher(mappingExpression);
		return matcher.find();	
	}

	/**
	 * @param parentObject
	 * @param propertyName
	 * @return
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public static boolean isMapped(Serializable parentObject, String propertyName, Mapping mapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
		Bind annotation = parentObject.getClass().getDeclaredField(propertyName).getAnnotation(Bind.class); 
		// Check the collection mapping
		boolean collectionAnnotation = (mapping!=null && mapping.getMappingExpression()!= "" );
		if(collectionAnnotation) return true;
		// Check if the mapping is on the collection elements.
		boolean elementAnnotation = mapping.getMappings().get(0) != null && mapping.getMappings().get(0).getMappingExpression() != null && mapping.getMappings().get(0).getMappingExpression() != "";
		return elementAnnotation ; 
	}

	/**
	 * @param parentObject
	 * @param propertyName
	 * @return
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws MappingException 
	 */
	public static boolean isMappedClass(Serializable parentObject) 
			throws MappingException {
		
		try {
			Class<? extends Serializable> testedClass = parentObject.getClass();
			Bind mainAnnotation = testedClass.getAnnotation(Bind.class); 
			return (mainAnnotation != null);
		} catch (Exception e) {
			MappingException mappingException = new MappingException();
			mappingException.setException(e);
			e.printStackTrace();
			throw mappingException;
		} 
		
	}

//	/**
//	 * Returns true if the property is an instance of a considered collection node.
//	 * @param parentObject
//	 * @return
//	 */
//	public static boolean isSingleCollection(Serializable parentObject, String propertyName) 
//			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
//		if(!isCollection(parentObject, propertyName)  || !isMapped(parentObject, propertyName)) return false;
//		Bind annotation = parentObject.getClass().getDeclaredField(propertyName).getAnnotation(Bind.class); 
//		if(annotation.classNames() == null || annotation.classNames().length <= 1) return true;
//		return false;
//	}

//	/**
//	 * Returns true if the collection is mapped with the fento annotation. 
//	 * 
//	 * @param parentObject
//	 * @param parentNode
//	 * @param mappings
//	 * @param propertyName
//	 * @return
//	 * @throws XPathExpressionException
//	 * @throws InstantiationException
//	 * @throws IllegalAccessException
//	 * @throws InvocationTargetException
//	 * @throws NoSuchMethodException
//	 * @throws NoSuchFieldException
//	 * @throws SecurityException
//	 */
//	public static boolean isMappedCollection(Serializable parentObject, String propertyName) 
//			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
//		return  isCollection(parentObject, propertyName)  && isMapped(parentObject, propertyName);
//	}

	/**
	 * Returns true if the property is an instance of a considered collection node.
	 * @param parentObject
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static boolean isCollection(Serializable parentObject, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException  {
		Field declaredField = parentObject.getClass().getDeclaredField(propertyName);
		return (declaredField.getType().equals(Vector.class) || 
				declaredField.getType().equals(ArrayList.class) ||
				declaredField.getType().equals(List.class));
	}

	
	public static void main(String[] args) {
//		String mappingExpression = "@office:value-type";
		String mappingExpression = "@offijce:value-type";
//		if(mappingExpression == null ||  mappingExpression.isEmpty()) return false;
//		Pattern pattern = Pattern.compile("(@[\\w-]:[\\w-])+(@[\\w]:[\\w-])+(@[\\w-]:[\\w])");
		Pattern pattern = Pattern.compile("@[\\w-*]:?[\\w-*]");
//		Pattern pattern = Pattern.compile("@[\\w-]:[\\w-]+\\/@[\\w-]+$|\\/@\\w+:[\\w-]+$");
		Matcher matcher = pattern.matcher(mappingExpression);
		System.out.println( "Ok: "+matcher.find());	
	}
}
