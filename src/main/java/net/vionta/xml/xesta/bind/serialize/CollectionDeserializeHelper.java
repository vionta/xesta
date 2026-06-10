package net.vionta.xml.xesta.bind.serialize;


import static net.vionta.xml.xesta.bind.serialize.MappingHelper.isMapped;

import java.awt.SecondaryLoop;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.analyze.map.ObjectDocumentMapping;
import net.vionta.xml.xesta.bind.annotation.Bind;
import net.vionta.xml.xesta.bind.serialize.util.DeserializerHelper;
import net.vionta.xml.xesta.bind.serialize.util.XPathHelper;
import net.vionta.xml.xesta.bind.serialize.util.XPathManager;
import net.vionta.xml.xesta.exception.BindingException;
import net.vionta.xml.xesta.exception.ExceptionHelper;
import net.vionta.xml.xesta.exception.MappingException;

/**
 * Main deserializer class using Single and 
 * multiple collections. 
 */
public class CollectionDeserializeHelper {

	private static Logger log = LoggerFactory.getLogger(CollectionDeserializeHelper.class);

	/**
	 * Returns true if the property is an instance of a considered collection node.
	 * @param parentObject
	 * @return
	 */
	public static boolean isSingleCollection(Serializable parentObject, String propertyName, Mapping mapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException {
		if(!isCollection(parentObject, propertyName)  || !isMapped(parentObject, propertyName, mapping)) return false;
		Bind annotation = parentObject.getClass().getDeclaredField(propertyName).getAnnotation(Bind.class); 
		if(annotation.classNames() == null || annotation.classNames().length <= 1) return true;
		return false;
	}

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
		return (isCollection(declaredField.getType()));
	}
	

	/**
	 * Checks if the field belongs to one of the considered collection 
	 * types.
	 * @param clazz the tested class.
	 * @return true/false
	 */
	public static boolean isCollection(Class clazz)    {
		return (clazz.equals(Vector.class) || 
			clazz.equals(ArrayList.class) ||
			clazz.equals(List.class));
	}

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
	public static boolean isSimpleValueType(Serializable parentObject, String propertyName, Mapping mapping) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException  {
		Field declaredField = parentObject.getClass().getDeclaredField(propertyName);
		return (declaredField.getType().equals(String.class) || 
				declaredField.getType().equals(Integer.class) ||
				declaredField.getType().equals(Float.class));
	}

	

	/**
	 * Collection deserialization.
	 * 
	 * @param parentObject The main object containing the collection.
	 * @param propertyName The property name where the collection nodes will be stored.
	 * @param mainNode The Main Node that holds the collection data. 
	 * @param currentMapping The mapping object defining the binding.
	 * @return The main object with the updated collection elements. 
	 * 
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws MappingException
	 * @throws BindingException
	 */
	protected static Serializable deserializeCollection(Serializable parentObject, Node mainNode, Mapping currentMapping,
			String propertyName) throws XPathExpressionException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NoSuchFieldException, MappingException, BindingException {
		if(CollectionDeserializeHelper.isSingleCollection(parentObject, propertyName,currentMapping)) {
			log.debug(" Getting Singe Node: "+ propertyName);
			Serializable deserializeSingleCollection = new CollectionDeserializeHelper().deserializeSingleCollection(parentObject, mainNode, currentMapping);
			return deserializeSingleCollection;
		} else {
			log.debug(" Getting Singe Node: "+ propertyName);
			Serializable deserializeMultipleCollection = new CollectionDeserializeHelper().deserializeMultipleCollection(parentObject, mainNode, currentMapping);
			return deserializeMultipleCollection;
		}
	}
	
	/**
	 * Deserializes a collection with a single type of nodes. 
	 * 
	 * @param parentObject
	 * @param parentNode
	 * @param mapping
	 * @return
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws BindingException 
	 * @throws MappingException 
	 */
	protected Serializable deserializeSingleCollection(Serializable parentObject, Node parentNode, Mapping mapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {	
		
		log.debug(" Deserializincing Singe Collection: "+ mapping);
		String propertyName = mapping.getPropertyName();
		Class propertyClass = mapping.getPropertyClass();
		log.debug(" Property : "+propertyName+" - "+propertyClass);
		List<Serializable> targetCollection = (List<Serializable>) PropertyUtils.getNestedProperty( parentObject, propertyName);
		log.debug(" targetCollection : "+targetCollection);
		
		String mainMappingExpression = mapping.getMappingExpression() ;
		boolean  mainMappingExpressionEmpty = false ;
		
		if(mainMappingExpression == null || "".equals(mainMappingExpression))  mainMappingExpressionEmpty =true; 
		
		String elementMappingExpression = "";
		
		 Object[] classesArray = mapping.getCollectionClasses().keySet().toArray();
		if (classesArray.length>=1) {
			Object firstClass = classesArray[0];
			 elementMappingExpression = (String) mapping.getCollectionClasses().get( firstClass) ;
		}
		boolean  elementMappingExpressionEmpty = false ;
		if(elementMappingExpression == null || "".equals(elementMappingExpression))  elementMappingExpressionEmpty =true; 
	
		NodeList targetNodeList  = null; 
		
		//Both main mapping and element mapping are null, not developed for now. 
		if(mainMappingExpressionEmpty && elementMappingExpressionEmpty) {
			log.warn("Both collection and element mappings are not defined. This case has not been developed yet");
			return (Serializable) targetCollection;
		}

		//Collection defined only on the element mapping
		if(!mainMappingExpressionEmpty && elementMappingExpressionEmpty) {
			log.debug("Mapping defined in the collection only");
			targetNodeList = (NodeList) XPathHelper.getXPath(mapping.getNamespaces())
					.evaluate(mapping.getMappingExpression(), parentNode, XPathConstants.NODESET);
		}
		
		//Collection defined on both collection and element
		if(!mainMappingExpressionEmpty && !elementMappingExpressionEmpty) {
			log.debug("Both collection and element mappings are defined. ");
			Node targetCollectionNode = (Node) XPathHelper.getXPath(mapping.getNamespaces())
					.evaluate(mapping.getMappingExpression(), parentNode, XPathConstants.NODE);
			if(targetCollectionNode !=null)  targetNodeList = (NodeList) XPathHelper.getXPath(mapping.getNamespaces())
					.evaluate( elementMappingExpression, targetCollectionNode, XPathConstants.NODESET);
			else {
				log.warn("Collection node is empty. ");
				return (Serializable) targetCollection;
			}
		}
		
		//Collection expression is empty, defined only on the element mapping
		if(mainMappingExpressionEmpty && !elementMappingExpressionEmpty) {
			log.debug("Mapping defined in the element part only");
			targetNodeList = (NodeList) XPathHelper.getXPath(mapping.getNamespaces())
					.evaluate(elementMappingExpression, parentNode, XPathConstants.NODESET);
		}
		
		for(int i =0 ; (targetNodeList !=null ) && i<targetNodeList.getLength() ; i++) {
			Node node = targetNodeList.item(i);
			Serializable collectionElement = getCollectionTypeInstance( parentObject, propertyClass, mapping);
				Serializable deserializeSingleElement = new Deserializer().deserializeSubproperties(collectionElement , node, mapping.getMappings());
				targetCollection.add(deserializeSingleElement);
		}
		return (Serializable) targetCollection;
	}

	private String getMappingExpression(boolean mainMappingExpressionEmpty, String mappingExpression, boolean elementMappingExpressionEmpty, String elementMappingExpression) throws MappingException {
		if(mainMappingExpressionEmpty && !elementMappingExpressionEmpty) return elementMappingExpression;
		else if (!mainMappingExpressionEmpty && elementMappingExpressionEmpty) return mappingExpression;
		else if(!mainMappingExpressionEmpty && !elementMappingExpressionEmpty) return mappingExpression +"/" + elementMappingExpression;
		ExceptionHelper.treatMappingException("", "", "","",null, "Could not retrieve a mapping for collection, both collection and element expressions are empty");
		//Can't touch this
		return null;
	}

	/**
	 * Multiple element collection, a collection that has more than one possible sub-element.
	 * 
	 * @param parentObject
	 * @param parentNode
	 * @param collectionMapping
	 * @return
	 * @throws XPathExpressionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws BindingException 
	 * @throws MappingException 
	 */
	protected Serializable deserializeMultipleCollection(Serializable parentObject, Node parentNode, Mapping collectionMapping) 
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {	
		
		String propertyName = collectionMapping.getPropertyName();
		log.debug(" Deserializincing Multiple Collection: "+ collectionMapping);
		List targetCollection = (List) PropertyUtils.getNestedProperty( parentObject, propertyName);
		NodeList listNodes =  (NodeList) XPathHelper.getXPath(collectionMapping.getNamespaces()).evaluate(collectionMapping.getMappingExpression(), parentNode, XPathConstants.NODESET);
		
		ArrayList<Mapping>  collectionClassesMappings = collectionMapping.getMappings();
		
		for(int n =0 ; n<  listNodes.getLength() ; n++) {
			Node currentNode = listNodes.item(n);
			
			for(int c =0 ; c <  collectionClassesMappings.size() ; c++) {
				Mapping elementMapping = collectionClassesMappings.get(n);
				Class propertyClass = elementMapping.getPropertyClass();
				String elementMappinExpression = elementMapping.getMappingExpression();
				
				NodeList collectionElementNodes =  (NodeList) XPathHelper.getXPath(collectionMapping.getNamespaces())
												.evaluate(elementMappinExpression, currentNode, XPathConstants.NODESET);
				for(int e= 0 ; e< collectionElementNodes.getLength() ; e++) {
					Serializable deserializedSingleElement = new Deserializer().deserializeSubproperties((Serializable)propertyClass.newInstance(), collectionElementNodes.item(e), elementMapping.getMappings());
					targetCollection.add(deserializedSingleElement);
				}
				}
		}
		return (Serializable) targetCollection;
	}

	/**
	 * @param clazz
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws InstantiationException 
	 * @throws BindingException 
	 */
	public static Serializable getCollectionTypeInstance(Class clazz) throws BindingException  {
		Type type = clazz.getGenericInterfaces()[0];
		return (Serializable) DeserializerHelper.getObjectInstance(type.getClass());
	}

	public static Serializable getCollectionTypeInstance(Serializable parentObject, Class elementClass, Mapping mapping) throws BindingException  {
		if(mapping.getClass()!=null) return DeserializerHelper.getObjectInstance(mapping.getPropertyClass());
		Type type = elementClass.getGenericInterfaces()[0];
		return (Serializable) DeserializerHelper.getObjectInstance(type.getClass());
	}

	


	
}
