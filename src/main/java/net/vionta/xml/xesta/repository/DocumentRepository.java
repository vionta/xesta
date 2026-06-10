package net.vionta.xml.xesta.repository;

import java.io.Serializable;

import org.w3c.dom.Document;

import net.vionta.xml.xesta.repository.exception.PersistException;
import net.vionta.xml.xesta.repository.exception.RetrieveException;

public interface DocumentRepository  {
	
	<T extends Serializable>  T load(T object) throws RetrieveException;
	<T extends Serializable> void persist(T object) throws PersistException;
	<T extends Serializable> void persist(T object, Document document) throws PersistException;
	<T extends Serializable> void remove(T object) throws RetrieveException;
	Document template();

}
