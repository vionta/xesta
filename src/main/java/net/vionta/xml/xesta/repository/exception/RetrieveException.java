package net.vionta.xml.xesta.repository.exception;

/**
 * An exception thrown while trying to retrieve a document 
 * and bind it to a java object.
 */
public class RetrieveException extends PersistException {

	public String toString() {
		return " A error has ocurred ["+sourceExpeption+"] while "
				+ "retrieving a document ("+ objectName +") at path=" + path ;
		}
	
}
