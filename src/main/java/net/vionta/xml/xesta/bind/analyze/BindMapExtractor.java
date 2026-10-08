package net.vionta.xml.xesta.bind.analyze;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPathConstants;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.vionta.xml.xesta.bind.analyze.map.MapVisualization;
import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.annotation.Bind;
import net.vionta.xml.xesta.bind.serialize.CollectionDeserializeHelper;
import net.vionta.xml.xesta.bind.serialize.Deserializer;
import net.vionta.xml.xesta.bind.serialize.util.XPathHelper;
import net.vionta.xml.xesta.exception.ExceptionHelper;
import net.vionta.xml.xesta.exception.MappingException;

/**
 * Extracts the object structure and associated 
 * mapping for the Xml Binding.
 */
public class BindMapExtractor {

	private static Logger log = LoggerFactory.getLogger(BindMapExtractor.class);

	/**
	 * Extracts the object mapping from 
	 * the object mapping. This is the main 
	 * method that iteratively calls the sub-actions.
	 * 
	 * @param serializable The object that will be analyzed.
	 * @return The object and subobjects structure description to perform the binding.
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	public static Mapping analyze(Serializable serializable) throws ClassNotFoundException, MappingException {
		return analyze(serializable.getClass());
	}
	
	/**
	 * Extracts the object mapping from 
	 * the object mapping. This is the main 
	 * method that iteratively calls the sub-actions.
	 * 
	 * @param clazz The object class to be analyzed.
	 * @return The object and subobjects structure description to perform the binding.
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	public static Mapping analyze(Class clazz) throws MappingException {
		log.debug(" Analyzing Object Mapping "+clazz.getName());
		int recursionFuse = 1; 
		Bind mainBindAnnotation = (Bind) clazz.getAnnotation(Bind.class); 
		if(clazz == null 
				|| mainBindAnnotation == null ) return null;
		//Get the mapping basic info
		Mapping mainMapping = populateMappingFromBindAnnotation(clazz, mainBindAnnotation);
		//Start the iterative call
		mainMapping.setMappings(extractPropertyMappings(mainMapping.getMappings(), clazz, recursionFuse));
		log.debug(" Analyzed Object Mapping: \n"+MapVisualization.printMapping(mainMapping));
		return mainMapping;
	}
	
	/**
	 * Extracts the namespaces map from the annotation. 
	 * @param bindAnnotation the annotation with the mapping parameters.
	 * @return The Map with the keys and uris of the namespaces.
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	private static Map<String, String> extractNamespaces(Bind bindAnnotation) throws MappingException {
		if(bindAnnotation.namespaceAlias()== null || bindAnnotation.namespaceUris() == null 
				||  bindAnnotation.namespaceAlias().length==0) return null;
		if(bindAnnotation.namespaceAlias().length != bindAnnotation.namespaceUris().length) {
			MappingException me = new MappingException(); 
			me.setMappingExpression(bindAnnotation.expression());
			me.setValue("Namespaces for "+bindAnnotation.expression()+" seem to be disaligned");
			throw me;
		}
		Map<String, String> namespaces = new HashMap<String, String>();
		for( int i = 0; i < bindAnnotation.namespaceAlias().length ; i++) {
			namespaces.put(bindAnnotation.namespaceAlias()[i], bindAnnotation.namespaceUris()[i]);
		}
		return namespaces;
	}

	/**
	 * Iterative method to extract child object mappings. 
	 * 
	 * @param prepertiesMappings
	 * @param mainClazz
	 * @param recursionFuse
	 * @return
	 * @throws MappingException Incorrect Mapping definition and problems processing it.
	 */
	private static ArrayList<Mapping> extractPropertyMappings(ArrayList<Mapping> prepertiesMappings, 
											Class mainClazz,  int recursionFuse) throws MappingException  {

		log.debug(" Analyzing "+mainClazz.getName());
		//Breaks a cycle on the classes and annotations, prevents infinite recursion.
		if(recursionFuse> 500) throw new IllegalStateException("Too much recursion, probable mapping cycle");
		
		// Run on object fields.
		Field[] declaredFields = mainClazz.getDeclaredFields();
		for (Field field: declaredFields) {
			log.debug("Mapping Field "+field);
			
			Bind propertyAnnotation = field.getAnnotation(Bind.class);
			Mapping currentMapping = null ;
			
			if(propertyAnnotation != null){ 
				//Generic Field, usually a collection 
				if (field.getGenericType() instanceof ParameterizedType && propertyAnnotation.classNames() == null ) {
						currentMapping = getGenericTypeMapping(prepertiesMappings, mainClazz, recursionFuse, field, propertyAnnotation);
			        } else  if (field.getType() instanceof Class) {
			        	currentMapping = getBaseMappingInformation(field, propertyAnnotation);
						if(CollectionDeserializeHelper.isCollection(field.getType())  ) {
							//TODO: enhance with generic type class names. 
							currentMapping = collectionMappingInformation(propertyAnnotation, currentMapping, field);
						}
						currentMapping.setMappings(extractPropertyMappings(currentMapping.getMappings(), field.getType(), recursionFuse + 1));
				}			 
			}// End of main bind annotation.
			if (currentMapping!=null) prepertiesMappings.add(currentMapping);
		} // For fields
		return prepertiesMappings;
	}

	/**
	 * @param propertyAnnotation
	 * @param mapping
	 * @return
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	private static Mapping collectionMappingInformation(Bind propertyAnnotation,  Mapping mapping, Field field)
			throws MappingException {
		

		log.debug("Mapping collection: "+mapping.getPropertyName());
		if (propertyAnnotation.classNames()!=null) { 
			mapping.setIsCollection(true);
			boolean isMultiple = propertyAnnotation.classNames().length > 1;
			mapping.setIsMultilple(isMultiple);
			if(isMultiple) {
				for(int z = 0 ; z < propertyAnnotation.classNames().length ; z++) {
					try {
						Class<?> collectionClass = Class.forName(propertyAnnotation.classNames()[z]);
						String collectionItemExpression = null ;
						Bind collectionItemAnnotation = collectionClass.getAnnotation(Bind.class);
						if(collectionItemAnnotation != null) {
								collectionItemExpression = collectionItemAnnotation.expression();
							}
						Mapping collectionItemMapping = new Mapping(collectionClass, collectionItemExpression);
						collectionItemMapping.setMappings(extractPropertyMappings(collectionItemMapping.getMappings(), collectionClass, 1));
						
						mapping.getCollectionMappings().put(collectionClass, collectionItemMapping);
					} catch (ClassNotFoundException e) {
						ExceptionHelper.treatMappingException(mapping.getPropertyClass().getName(), mapping.getPropertyClass().getName(), 
								mapping.getPropertyName(), mapping.getMappingExpression(), e,
								"Error trying to get a class by name for the collection Mapping Information ");
					}
				}// Multiple collection for end	
			} else {
				//Single collection mapping ...
				//TODO Single mapping 
				//log.debug("Evaluating single collection: "+propertyAnnotation.classNames()[0]);
//				log.debug("Using mapping"+MapVisualization.printMapping(mapping));
//				log.debug("Using mapping"+MapVisualization.printBindAnnotationDescription(propertyAnnotation));
//
			    Type rawType = null;
		        Type genericType = null;
		    	Type type = field.getGenericType();
			    if (type instanceof ParameterizedType) {
			        ParameterizedType pType = (ParameterizedType)type;
			        rawType = pType.getRawType();
			        genericType = pType.getActualTypeArguments()[0];
			    }
//			    log.debug("Raw type: " + rawType + " - ");
//			    log.debug("Type args: " + genericType);
//			    
//			    
//				log.debug(" Target Class : "+field.getName() );
//				log.debug(" Raw Type : "+field.getAnnotatedType());
//				try {
//				log.debug(" Generic Class: "+Class.forName(genericType.getTypeName()) );
//				Bind annotation = Class.forName(genericType.getTypeName()).getAnnotation(Bind.class);
//				if(annotation == null ) { log.warn(" Expected annotation from generic type "+genericType.getTypeName()+" is null. Please review your mappings.");
//				}
//				log.debug(" Generic Class Bind Annotation: "+annotation);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
					try {
						Class<?> collectionClass = Class.forName(genericType.getTypeName());
						String collectionItemExpression = null ;
						Bind collectionItemAnnotation = collectionClass.getAnnotation(Bind.class);
						if(collectionItemAnnotation != null) {
								collectionItemExpression = collectionItemAnnotation.expression();
							}
						Mapping collectionItemMapping = new Mapping(collectionClass, collectionItemExpression);
						collectionItemMapping.setMappings(extractPropertyMappings(collectionItemMapping.getMappings(), collectionClass, 1));
						
						mapping.getCollectionMappings().put(collectionClass, collectionItemMapping);
					} catch (ClassNotFoundException e) {
						ExceptionHelper.treatMappingException(mapping.getPropertyClass().getName(), mapping.getPropertyClass().getName(), 
								mapping.getPropertyName(), mapping.getMappingExpression(), e,
								"Error trying to get a class by name for the collection Mapping Information ");
					}
			}
			 
		}
		return mapping;
	}
	
	
	/* Chuleta 


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

*/
	/**
	 * @param prepertiesMappings
	 * @param mainClazz
	 * @param recursionFuse
	 * @param field
	 * @param propertyAnnotation
	 * @return
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	private static Mapping getGenericTypeMapping(ArrayList<Mapping> prepertiesMappings, Class mainClazz,
			int recursionFuse, Field field, Bind propertyAnnotation) throws  MappingException {
		Mapping currentMapping = null;
		Class<?> parametrizedClazz;
		parametrizedClazz = (Class<?>) field.getType();
		ParameterizedType pt = (ParameterizedType) field.getGenericType() ;
		Type[] typeArgs = pt.getActualTypeArguments();
		log.debug("Generic Type: " + typeArgs[0]);
		// TODO: Check multiple generalized classes.
		
		if(typeArgs[0] != null   )   {
			log.debug("-Class: " + typeArgs[0].getClass());
			//TODO : Seguir aqui, En caso de super clase o interfaz tratar de inferir la especIfica.
			try {
			parametrizedClazz  = Class.forName(typeArgs[0].getTypeName());
			} catch(ClassNotFoundException e) {
				ExceptionHelper.treatMappingException(mainClazz.getName(), parametrizedClazz.getName() , 
						(currentMapping!=null)? currentMapping.getPropertyName() : null,
						(currentMapping != null)? currentMapping.getMappingExpression(): null,
						e, "Error trying to invoke a class declared in a collection mapping.");
			}
		}
		//TODO: Seguir limpiando
		
		currentMapping = getBaseMappingInformation(field, propertyAnnotation);
		
		//Mapping information for collections.
		if(CollectionDeserializeHelper.isCollection(field.getType())  ) {
			currentMapping = extractCollectionMappings(mainClazz, propertyAnnotation, parametrizedClazz, currentMapping);
		}
		
		currentMapping.setMappings(extractPropertyMappings(currentMapping.getMappings(), parametrizedClazz, recursionFuse + 1));
		prepertiesMappings.add(currentMapping);
		return currentMapping;
	}

	/**
	 * Extract Collection mapping information
	 * 
	 * @param mainClazz
	 * @param mainBindAnnotation
	 * @param parametrizedClazz
	 * @param mapping
	 * @return
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	private static Mapping extractCollectionMappings(Class mainClazz, Bind mainBindAnnotation,
			Class<?> parametrizedClazz, Mapping mapping) throws MappingException {
		
		mapping.setIsCollection(true);
		mapping.setCollectionBindStrategy(mainBindAnnotation.collectionBindStrategy());
		mapping.setCollectionDeleteUnmatched(mainBindAnnotation.collectionDeleteUnmatched());
		
		Bind CollectionClassBindAnnotation = parametrizedClazz.getAnnotation(Bind.class);
		if (CollectionClassBindAnnotation != null && CollectionClassBindAnnotation.expression() != null) {
			Mapping collectionItemMapping = new Mapping(parametrizedClazz ,CollectionClassBindAnnotation.expression() );
			mapping.getCollectionMappings().put(parametrizedClazz, collectionItemMapping);
		}

		// Fill the collection class names.
		if (mainBindAnnotation.classNames() != null) {
			// Define if collection is simple or multiple
			if (mainBindAnnotation.classNames().length <= 1)
				mapping.setIsMultilple(false);

			// extract classes and expressions from the collection elements.
			Class[] collectionClazzes = new Class[mainBindAnnotation.classNames().length];
			for (int z = 0; z < mainBindAnnotation.classNames().length; z++) {
				try {
					collectionClazzes[z] = Class.forName(mainBindAnnotation.classNames()[z]);
					Bind annotation = (Bind) collectionClazzes[z].getAnnotation(Bind.class);
					String collectionClassExpression = null;
					if(annotation!=null) collectionClassExpression = annotation.expression();
					Mapping collectionItemMapping = new Mapping(collectionClazzes[z].getName(), collectionClassExpression);
					mapping.getCollectionMappings().put(collectionClazzes[z], collectionItemMapping);
				} catch (ClassNotFoundException e) {
					ExceptionHelper.treatMappingException(mainClazz.getName(), mainBindAnnotation.classNames()[z] , 
							mapping.getPropertyName(), mapping.getMappingExpression(), e,
							"Error trying to invoke a class declared in a collection mapping.");
				}
			}
		}
		return mapping;
	}

	/**
	 * Extracts the object mapping from 
	 * the object mapping.
	 * @param serializable
	 * @return
	 * @throws MappingException Problems extracting the mapping definition of the 
	 */
	public static Mapping extractCollectionClassMapping(Serializable serializable) throws ClassNotFoundException, MappingException {
		
		log.debug(" Analyzing "+serializable.getClass().getName());
		int recursionFuse = 1; 
		Bind mainBindAnnotation = serializable.getClass().getAnnotation(Bind.class); 
		if(serializable == null 
				|| mainBindAnnotation == null ) return null;
		
		Mapping mainMapping  = populateMappingFromBindAnnotation(serializable, mainBindAnnotation);
		mainMapping.setMappings(extractPropertyMappings(mainMapping.getMappings(), serializable.getClass(), recursionFuse));
		log.debug(" Analyzed "+mainMapping);
		return mainMapping;
		
	}

	/**
	 * @param mainBindAnnotation
	 * @param mainMapping
	 * @return
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	private static Mapping populateMappingProperties(Bind mainBindAnnotation,
			Mapping mainMapping) throws MappingException {
		mainMapping.setMappingExpression(mainBindAnnotation.expression());
		mainMapping.setKey(mainBindAnnotation.key()==true);
		mainMapping.setNamespaces(extractNamespaces(mainBindAnnotation));
		return mainMapping;
	}

	/**
	 * Pass the basic bind annotation information to the mapping class.
	 * 
	 * @param serializable
	 * @param mainBindAnnotation
	 * @return
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	private static Mapping populateMappingFromBindAnnotation(Serializable serializable,
			Bind mainBindAnnotation) throws MappingException {
		return populateMappingFromBindAnnotation(serializable.getClass(), mainBindAnnotation);
	}

	/**
	 * @param clazz
	 * @param mainBindAnnotation
	 * @return
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	private static Mapping populateMappingFromBindAnnotation(Class clazz,
			Bind mainBindAnnotation) throws MappingException {
		Mapping mainMapping = new Mapping(clazz.getName(), mainBindAnnotation.expression());
		mainMapping.setPropertyClass(clazz);
		mainMapping = populateMappingProperties(mainBindAnnotation, mainMapping);
		return mainMapping;
	}

	/**
	 * @param field
	 * @param mainBindAnnotation
	 * @return
	 * @throws MappingException Thrown when an error on the mapping annotations is detected.
	 */
	private static Mapping getBaseMappingInformation(Field field, Bind mainBindAnnotation)
			throws MappingException {
		
		Mapping mapping = new Mapping(field.getName(), mainBindAnnotation.expression());
		mapping.setPropertyName(field.getName());
		mapping.setPropertyClass(field.getType());
		
		mapping.setSerializeMode(mainBindAnnotation.serializingMode());
		mapping.setDeserializeMode(mainBindAnnotation.deserializingMode());
		

		mapping.setCollectionBindStrategy(mainBindAnnotation.collectionBindStrategy());
		mapping.setCollectionDeleteUnmatched(mainBindAnnotation.collectionDeleteUnmatched());
		
		mapping = populateMappingProperties(mainBindAnnotation, mapping); 
		return mapping;
	}
}
