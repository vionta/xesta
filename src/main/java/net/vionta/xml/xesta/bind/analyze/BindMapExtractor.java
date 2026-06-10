package net.vionta.xml.xesta.bind.analyze;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vionta.xml.xesta.bind.analyze.map.Mapping;
import net.vionta.xml.xesta.bind.analyze.map.ObjectDocumentMapping;
import net.vionta.xml.xesta.bind.annotation.Bind;
import net.vionta.xml.xesta.bind.serialize.CollectionDeserializeHelper;
import net.vionta.xml.xesta.exception.ExceptionHelper;
import net.vionta.xml.xesta.exception.MappingException;

/**
 * Extracts the object structure and associated 
 * mapping. 
 */
public class BindMapExtractor {

	private static Logger log = LoggerFactory.getLogger(BindMapExtractor.class);

	/**
	 * Extracts the object mapping from 
	 * the object mapping.
	 * @param serializable
	 * @return
	 * @throws MappingException Problems extracting the mapping definition of the 
	 */
	public static ObjectDocumentMapping analyze(Serializable serializable) throws ClassNotFoundException, MappingException {
		
		log.debug(" Analyzing "+serializable.getClass().getName());
		int recursionFuse = 1; 
		Bind mainBindAnnotation = serializable.getClass().getAnnotation(Bind.class); 
		if(serializable == null 
				|| mainBindAnnotation == null ) return null;
		ObjectDocumentMapping mainMapping = new ObjectDocumentMapping();
		mainMapping.setMappingExpression(mainBindAnnotation.expression());
		mainMapping.setPropertyClass(serializable.getClass());
		mainMapping.setKey(mainBindAnnotation.key()==true);
		mainMapping.setPropertyName(serializable.getClass().getName());
		mainMapping.setNamespaces(extractNamespaces(mainBindAnnotation));
		
		mainMapping.setMappings(extractMappings(mainMapping.getMappings(), serializable.getClass(), recursionFuse));
		log.debug(" Analyzed "+mainMapping);
		return mainMapping;
		
	}

	/**
	 * Extracts the namespaces map from the annotation. 
	 * @param bindAnnotation the annotation with the mapping parameters.
	 * @return The Map with the keys and uris of the namespaces.
	 * @throws MappingException
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
	 * @param mappings
	 * @param mainClazz
	 * @param recursionFuse
	 * @return
	 * @throws ClassNotFoundException
	 * @throws MappingException Incorrect Mapping definition and problems processing it.
	 */
	private static ArrayList<Mapping> extractMappings(ArrayList<Mapping> mappings, 
											Class mainClazz,  int recursionFuse) throws ClassNotFoundException, MappingException  {

		log.debug(" Analyzing "+mainClazz.getName());
		if(recursionFuse> 500) throw new IllegalStateException("Too much recursion, probable mapping cycle");
		Field[] declaredFields = mainClazz.getDeclaredFields();
		
		// Run on object fields.
		for (Field field: declaredFields) {
			
			log.debug(" Feld "+field);
			Bind mainBindAnnotation = field.getAnnotation(Bind.class);
			
			if(mainBindAnnotation != null){
				Class<?> parametrizedClazz ;
				//Generic Field, usually a collection 
				 if (field.getGenericType() instanceof ParameterizedType) {
						 parametrizedClazz = (Class<?>) field.getType();
			            ParameterizedType pt = (ParameterizedType) field.getGenericType() ;
			            Type[] typeArgs = pt.getActualTypeArguments();
			            log.debug("Generic Type: " + typeArgs[0]);
			            // TODO: Check multiple generalized classes.
			            if(typeArgs[0] != null)   {
			            	log.debug("-Class: " + typeArgs[0].getClass());
			            	parametrizedClazz  = Class.forName(typeArgs[0].getTypeName());
			            }
			            //TODO: Eliminar duplicidad codigo
			            
			        	Mapping mapping = new Mapping(field.getName(), mainBindAnnotation.expression());
						mapping.setPropertyName(field.getName());
						mapping.setPropertyClass(parametrizedClazz);
						
						//TODO: --
						mapping.setKey(mainBindAnnotation.key());
						mapping.setMappingExpression(mainBindAnnotation.expression());
						
						//Mapping information for collections.
						if(CollectionDeserializeHelper.isCollection(field.getType())  ) {
							mapping = extractCollectionMappings(mainClazz, mainBindAnnotation, parametrizedClazz, mapping);
						}

						mapping.setNamespaces(extractNamespaces(mainBindAnnotation));
						
						mapping.setMappings(extractMappings(mapping.getMappings(), parametrizedClazz, recursionFuse + 1));
						mappings.add(mapping);

						
			        } else  if (field.getType() instanceof Class) {
			        	parametrizedClazz = (Class<?>) field.getType();
			        	//TODO: Revisar esto cuando =class java.lang.Class,
			        	//String.class.getGenericSuperclass() != field.getGenericType()
			        	if(field.getGenericType() != null && 1==2 )   {
			        		parametrizedClazz = field.getGenericType().getClass(); 
			        	}				
						Mapping mapping = new Mapping(field.getName(), mainBindAnnotation.expression());
						mapping.setPropertyName(field.getName());
						mapping.setPropertyClass(parametrizedClazz);
						
						mapping.setKey(mainBindAnnotation.key());
						mapping.setMappingExpression(mainBindAnnotation.expression());
						mapping.setSerializeMode(mainBindAnnotation.serializingMode());
						mapping.setDeserializeMode(mainBindAnnotation.deserializingMode());
						mapping.setNamespaces(extractNamespaces(mainBindAnnotation));
						
						
						if(CollectionDeserializeHelper.isCollection(field.getType())  ) {
							mapping.setCollectionBindStrategy(mainBindAnnotation.collectionBindStrategy());
							mapping.setCollectionDeleteUnmatched(mainBindAnnotation.collectionDeleteUnmatched());
							//TODO: enhance with generic type class names. 
							if (mainBindAnnotation.classNames()!=null) { 
								if(mainBindAnnotation.classNames().length <= 1 ) mapping.setIsMultilple(false);
								Class[] clazzes = new Class[mainBindAnnotation.classNames().length];
								for(int z = 0 ; z <= mainBindAnnotation.classNames().length ; z++) {
								clazzes[z] = Class.forName(mainBindAnnotation.classNames()[z]);
								}
							}
						}
						
						mapping.setMappings(extractMappings(mapping.getMappings(), parametrizedClazz, recursionFuse + 1));
						mappings.add(mapping);
				}
				 
			}// End of main bind annotation.
		} // For fields
		return mappings;
	}

	/**
	 * Extract Collection mapping information
	 * 
	 * @param mainClazz
	 * @param mainBindAnnotation
	 * @param parametrizedClazz
	 * @param mapping
	 * @return
	 * @throws MappingException
	 */
	private static Mapping extractCollectionMappings(Class mainClazz, Bind mainBindAnnotation,
			Class<?> parametrizedClazz, Mapping mapping) throws MappingException {
		

		mapping.setCollectionBindStrategy(mainBindAnnotation.collectionBindStrategy());
		mapping.setCollectionDeleteUnmatched(mainBindAnnotation.collectionDeleteUnmatched());
		Bind CollectionClassBindAnnotation = parametrizedClazz.getAnnotation(Bind.class);
		if (CollectionClassBindAnnotation != null && CollectionClassBindAnnotation.expression() != null) {
			mapping.getCollectionClasses().put(parametrizedClazz, CollectionClassBindAnnotation.expression());
		}

		// Fill the collection class names.
		if (mainBindAnnotation.classNames() != null) {
			// Define if collection is simple or multiple
			if (mainBindAnnotation.classNames().length <= 1)
				mapping.setIsMultilple(false);

			// extract classes and expressions from the collection elements.
			Class[] collectionClazzes = new Class[mainBindAnnotation.classNames().length];
			String[] collectionClassExpressions = new String[mainBindAnnotation.classNames().length];
			for (int z = 0; z < mainBindAnnotation.classNames().length; z++) {
				try {
					collectionClazzes[z] = Class.forName(mainBindAnnotation.classNames()[z]);
					Bind annotation = (Bind) collectionClazzes[z].getAnnotation(Bind.class);
					String collectionClassExpression = null;
					if(annotation!=null) collectionClassExpression = annotation.expression();

					mapping.getCollectionClasses().put(collectionClazzes[z], collectionClassExpression);
				} catch (ClassNotFoundException e) {
					ExceptionHelper.treatMappingException(mainClazz.getName(), mainBindAnnotation.classNames()[z] , 
							mapping.getPropertyName(), mapping.getMappingExpression(), e,
							"Error trying to invoke a class declared in a collection mapping.");
				}
			}

		}
		return mapping;
	}

	
	
}
