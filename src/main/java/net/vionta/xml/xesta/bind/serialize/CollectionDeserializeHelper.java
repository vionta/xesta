package net.vionta.xml.xesta.bind.serialize;


import static net.vionta.xml.xesta.bind.serialize.MappingHelper.isMapped;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.annotation.Bind;
import net.vionta.xml.xesta.bind.serialize.util.DeserializerHelper;
import net.vionta.xml.xesta.bind.serialize.util.XPathHelper;
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
	 * @param parentObject The parent object that contains the collection.
	 * @return true if the collection has only one type of elements.
	 */
	public static boolean isSingleCollection(Serializable parentObject, String propertyName, Mapping mapping) 
									throws XPathExpressionException, InstantiationException, IllegalAccessException, 
										InvocationTargetException, NoSuchMethodException, NoSuchFieldException, 
										SecurityException {
		
		if(!isCollection(parentObject, propertyName)  || !isMapped(parentObject, propertyName, mapping)) return false;
		Bind annotation = parentObject.getClass().getDeclaredField(propertyName).getAnnotation(Bind.class); 
		if(annotation.classNames() == null || annotation.classNames().length <= 1) return true;
		return false;
	}

	/**
	 * Returns true if the property is an instance of a considered collection node.
	 * @param parentObject The parent object that contains the collection.
	 * @return true if the property of the mapping is a collection object.
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static boolean isCollection(Serializable parentObject, String propertyName) throws IllegalAccessException, 
								InvocationTargetException, NoSuchMethodException, NoSuchFieldException, SecurityException  {
		Field declaredField = parentObject.getClass().getDeclaredField(propertyName);
		return isCollection(declaredField.getType());
	}
	

	/**
	 * Checks if the field belongs to one of the considered collection 
	 * types.
	 * @param clazz the tested class.
	 * @return true if the class implements a list/Collection type.
	 */
	public static boolean isCollection(Class clazz)    {
		return (clazz.equals(Vector.class) || 
			clazz.equals(ArrayList.class) ||
			clazz.equals(List.class));
	}

	/**
	 * Returns true if the property is an instance of a considered collection node.
	 * @param parentObject
	 * @return true if the value type is an atomic type that can be expressed 
	 * 			by a text value.
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static boolean isSimpleValueType(Serializable parentObject, String propertyName, 
									Mapping mapping) throws IllegalAccessException, InvocationTargetException, 
									NoSuchMethodException, NoSuchFieldException, SecurityException  {
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
	protected static Serializable deserializeCollection(Serializable parentObject, Node mainNode, 
																Mapping currentMapping,String propertyName) 
																		throws XPathExpressionException, 
																			InstantiationException, IllegalAccessException,
																			InvocationTargetException, NoSuchMethodException, 
																			NoSuchFieldException, MappingException,
																			BindingException {

		if(isSingleCollection(parentObject, propertyName,currentMapping)) {
			log.debug(" Getting Singe Node: "+ propertyName);
			Serializable deserializeSingleCollection = new CollectionDeserializeHelper().deserializeSingleCollection(parentObject, mainNode, currentMapping);
			return deserializeSingleCollection;
		} else {
			log.debug(" Getting Multiple Node: "+ propertyName);
			Serializable deserializeMultipleCollection = new CollectionDeserializeHelper().deserializeMultipleCollection(parentObject, mainNode, currentMapping);
			return deserializeMultipleCollection;
		}
	}
	
	/**
	 * Deserializes a collection with a single type of nodes. 
	 * 
	 * @param parentObject The parent object.
	 * @param parentNode The parent Xml node.
	 * @param mapping the descriptor mappind of the property.s
	 * @return The object with the deserialized element content.
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
		
		Entry<Class, Mapping> next = mapping.getCollectionMappings().entrySet().iterator().next();
		if (next!=null && next.getValue()!=null) {
			Mapping elementMapping = next.getValue();
			elementMappingExpression = elementMapping.getMappingExpression();
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
			log.debug("Main Mapping expression : "+mainMappingExpression);
			log.debug("Element Mapping expression : "+elementMappingExpression);
			
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
			Serializable collectionElement = getCollectionElementTypeInstance( parentObject, propertyClass, next.getValue());
				Serializable deserializeSingleElement = new Deserializer().deserializeSubproperties(collectionElement , node, next.getValue().getMappings());
				targetCollection.add(deserializeSingleElement);
		}
		return (Serializable) targetCollection;
	}


	/**
	 * Multiple element collection, a collection that has more than one possible sub-element.
	 * 
	 * @param parentObject The parent java object.
	 * @param parentNode The parent Xml node.
	 * @param collectionMapping The mappind descriptor.
	 * @return the list with the deserialized List/Collection.
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
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException, 
			NoSuchMethodException, NoSuchFieldException, SecurityException, MappingException, BindingException {	

		//We get the main mapping node
		Node collectionNode =  (Node) XPathHelper.getXPath(collectionMapping.getNamespaces()).
							evaluate(collectionMapping.getMappingExpression(), parentNode, 
									XPathConstants.NODE);
		
		//The map that should end up as the result of the sorted elements.		
//		Map<Node, Serializable> sortedElements = new LinkedHashMap<Node, Serializable>();
		LinkedNodeObjectList sortedElements = new LinkedNodeObjectList();
		
		//We get the collection classes mappings and iterator.
		Map<Class, Mapping> collectionMappings = collectionMapping.getCollectionMappings(); 
		Iterator<Entry<Class, Mapping>> collectionElementMappingsIterator = collectionMappings.entrySet().iterator();
//		boolean first=true;

		// For each mapping class 
		while(collectionElementMappingsIterator.hasNext()) {
			
			// Start with the current collection element mapping
			Mapping nextMapping = collectionElementMappingsIterator.next().getValue();
			String currentExpression = nextMapping.getMappingExpression();
			Class currentPropertyClass = nextMapping.getPropertyClass();
			
			log.debug(" Looking for elemnets with mapping Expression : "+nextMapping.getPropertyName() + "--> "+ currentExpression);
			checkPossibleLackingMappings(parentObject, currentExpression, currentPropertyClass);
			
			NodeList currentElementNodeList = (NodeList) XPathHelper.getXPath(nextMapping.getNamespaces())
										.evaluate(currentExpression , collectionNode, XPathConstants.NODESET);
		
			log.debug(" Gotten nodelist with "+currentElementNodeList.getLength()+" elements ");
			
			LinkedNodeObject listCounter = sortedElements.getFirst();
			for(int e= 0 ; e < currentElementNodeList.getLength() ; e++) {
				
				Node currentNode = currentElementNodeList.item(e);
				Serializable currentDeserializedElement = new Deserializer().
										deserializeSubproperties((Serializable)currentPropertyClass.newInstance(), 
												currentNode, nextMapping.getMappings()); 
				LinkedNodeObject currentLinkedNodeObject = new  LinkedNodeObject(currentNode, currentDeserializedElement);
		
				// insert the first one
				if(sortedElements.isEmpty()) {
					sortedElements.insertNext(new LinkedNodeObject(currentNode ,currentDeserializedElement ));
					listCounter = sortedElements.getFirst();
				//The element should go before the current one
				} else if(listCounter.isBefore(currentNode) ) {
					//Hay que tener en cuenta los nexts y que pueden haber varios antes
					
					currentLinkedNodeObject.setNext(listCounter);
					listCounter = currentLinkedNodeObject;
					sortedElements.setFirst(currentLinkedNodeObject);
				//The counter goes after the current one
				} else if (listCounter.isAfter(currentNode)  ) {
					listCounter.setNext(currentLinkedNodeObject);
					listCounter=currentLinkedNodeObject;
				} 
			} // For ends
		}//While ends
							
		List targetCollection = wrapTargetCollection(sortedElements);
		return (Serializable) targetCollection;
	}

	/**
	 * Utility method to wrap in java list the selected objects from a custom linked node.
	 * @param sortedElements The custom linked list.
	 * @return the List with the java objects.
	 */
	private List<Serializable> wrapTargetCollection(LinkedNodeObjectList sortedElements) {
		LinkedNodeObject first = sortedElements.getFirst();
		List<Serializable> targetCollection = new ArrayList<Serializable>();
		while(first.getNext()!= null) {
			targetCollection.add(first.getObject());
			first = first.getNext();
		}
		return null;
	}

	/**
	 * An check when an object may lack a mapping in one of the element classes.  
	 * 
	 * @param parentObject The parent object that holds the collection.
	 * @param nextMappingExpression The mapping expression of the element.
	 * @param nextMappingClass The mapping class from the collection.
	 * @throws MappingException When the class has not an appropriated mapping.
	 */
	private void checkPossibleLackingMappings(Serializable parentObject, String nextMappingExpression,
			Class nextMappingClass) throws MappingException {
		if(nextMappingExpression == null ) ExceptionHelper.treatMappingException(
												parentObject.getClass().getName(), nextMappingClass.getName(),
												"collection","", null, 
												"Could not retrieve an expression for "+nextMappingClass
												+" from collection ");
	}


	/**
	 * TODO: review naming and organization.
	 * @param clazz
	 * @return
	 * @throws BindingException 
	 */
	public static Serializable getCollectionTypeInstance(Class clazz) throws BindingException  {
		Type type = clazz.getGenericInterfaces()[0];
		return (Serializable) DeserializerHelper.getObjectInstance(type.getClass());
	}

	/**
	 * TODO: review naming and organization.
	 * @param parentObject
	 * @param elementClass
	 * @param mapping
	 * @return
	 * @throws BindingException
	 */
	public static Serializable getCollectionTypeInstance(Serializable parentObject, Class elementClass, Mapping mapping) throws BindingException  {
		if(mapping.getClass()!=null) return DeserializerHelper.getObjectInstance(mapping.getPropertyClass());
		Type type = elementClass.getGenericInterfaces()[0];
		return (Serializable) DeserializerHelper.getObjectInstance(type.getClass());
	}

	/**
	 * TODO: review naming and organization.
	 * @param parentObject
	 * @param elementClass
	 * @param mapping
	 * @return
	 * @throws BindingException
	 */
	public static Serializable getCollectionElementTypeInstance(Serializable parentObject, Class elementClass, Mapping mapping) throws BindingException  {
		if(mapping.getClass()!=null) return DeserializerHelper.getObjectInstance(mapping.getPropertyClass());
		Type type = elementClass.getGenericInterfaces()[0];
		return (Serializable) DeserializerHelper.getObjectInstance(type.getClass());
	}
	
}
