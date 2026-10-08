package net.vionta.xml.xesta.repository.impl;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;

import org.slf4j.Logger;
import org.w3c.dom.Document;

import net.vionta.xml.xesta.bind.serialize.Deserializer;
import net.vionta.xml.xesta.bind.serialize.Serializer;
import net.vionta.xml.xesta.repository.DocumentRepository;
import net.vionta.xml.xesta.repository.exception.PersistException;
import net.vionta.xml.xesta.repository.exception.RetrieveException;
import net.vionta.xml.xesta.repository.impl.util.ClasspathFileUtil;
import net.vionta.xml.xesta.repository.impl.util.DocumentUtils;
import net.vionta.xml.xesta.repository.impl.util.PathAdjust;

/**
 * A class path file repository. An object that acts as a 
 * tool for accessing files based on the configuration.
 */
public class ClassPathRepository implements DocumentRepository  {
	
	/**
	 * The file path.
	 */
	private String path ;
	
	static Logger log  = getLogger(ClassPathRepository.class);

	/**
	 * Loads the object, from the provided path.
	 */
	@Override
	public <T extends Serializable> T load(T object) throws RetrieveException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getPath(), object);
			log .debug(" Adjusted Path"+adjustedPath );
			String fileContent = ClasspathFileUtil.readFile(adjustedPath);
			log .debug("File Content:"+fileContent);
			Document documentContents = DocumentUtils.stringToDocument(fileContent);
			return  (T) new Deserializer().deserialize(object, documentContents);
		} catch (Exception e) {
			log .error(e.toString());
			RetrieveException re = new RetrieveException();
			re.setPath(path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}
	
	/**
	 * Persist the object from the provided path and document. Consider that 
	 * not all paths will be open/allowed for modification
	 */
	@Override
	public void persist(Serializable object, Document document) throws PersistException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getPath(), object);
			// Load 
			Document serializedDocument = new Serializer().serialize(object, document);
			ClasspathFileUtil.writeFile(adjustedPath, DocumentUtils.documentToString(serializedDocument));
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}

	/**
	 * Persist the object from the provided path. Consider that 
	 * not all paths will be open/allowed for modification
	 */
	public void persist(Serializable object) throws PersistException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getPath(), object);
			// Load 
			log .debug(" Adjusted Path"+adjustedPath );

			String fileContent = ClasspathFileUtil.readFile(adjustedPath);
			log .debug("File Content:"+fileContent);
			Document documentContents = DocumentUtils.stringToDocument(fileContent);
			Document serializedDocument = new Serializer().serialize(object, documentContents);
			ClasspathFileUtil.writeFile(adjustedPath, DocumentUtils.documentToString(serializedDocument));
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}

	public String getPath() {
		return path;
	}

	public ClassPathRepository setPath(String path) {
		this.path = path;
		return this;
	}

	@Override
	public void remove(Serializable object) throws RetrieveException {
		throw new IllegalStateException("Classpath repositories should not remove files.");
	}

	@Override
	public Document template() {
		throw new IllegalStateException("Classpath repositories should not create documents from templates.");
	}
	
}
