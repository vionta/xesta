package net.vionta.xml.xesta.repository;

import java.io.Serializable;

import org.w3c.dom.Document;

import net.vionta.xml.xesta.repository.exception.PersistException;
import net.vionta.xml.xesta.repository.exception.RetrieveException;

/**
 * Xesta uses the repository abstraction to ease the file 
 * access process.
 */
public interface DocumentRepository  {
	
	/**
	 * Loads the document into the provided object.
	 * 
	 * @param <T> The object type.
	 * @param object The java object used to hold the value.
	 * @return The loaded object with the document contents.
	 * @throws RetrieveException
	 */
	<T extends Serializable> T load(T object) throws RetrieveException;
	
	/**
	 * Persist the information from the object to the document. 
	 * @param <T> The object type.
	 * @param object The java object used to hold the value.
	 * @throws PersistException
	 */
	<T extends Serializable> void persist(T object) throws PersistException;
	
	/**
	 * Persist the information from the object to the document. 
	 * @param <T> The object type.
	 * @param object The java object used to hold the value.
	 * @param document The xml document to be updated.
	 * @throws PersistException
	 */
	<T extends Serializable> void persist(T object, Document document) throws PersistException;
	
	/**
	 * A remove operation of the file based on the repository configuration 
	 * and the object data.
	 * @param <T> The object type.
	 * @param object The java object used to hold the value.
	 * @throws RetrieveException
	 */
	<T extends Serializable> void remove(T object) throws RetrieveException;
	Document template();

}
