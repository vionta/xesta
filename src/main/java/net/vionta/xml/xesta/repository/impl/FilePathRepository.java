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
import net.vionta.xml.xesta.repository.impl.util.FileManager;
import net.vionta.xml.xesta.repository.impl.util.PathAdjust;

public class FilePathRepository implements DocumentRepository {

	static Logger log  = getLogger(FilePathRepository.class);

	private String path = "" ; 
	private String basePath = "" ; 
	
	private Document template; 

	public FilePathRepository(String path) {
		super();
		if (path == null) throw new IllegalStateException("The paths can not be null");
		this.path = path;
	}

	@Override
	public void persist(Serializable object, Document document) throws PersistException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getFullPath(), object);
			// Load 
			Document serializedDocument = new Serializer().serialize(object, document);
			FileManager.writeFile(adjustedPath,DocumentUtils.documentToString(serializedDocument));
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(basePath+path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from File repository");
			log .error(re.toString());
			throw re;
		}
	}

	@Override
	public <T extends Serializable>  T load(T object) throws RetrieveException  {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getFullPath(), object);

			// Load 
			Document documentContents = FileManager.readDocument(adjustedPath);
			return  (T) new Deserializer().deserialize(object, documentContents);
		} catch (Exception e) {
			log .error(e.toString());
			RetrieveException re = new RetrieveException();
			re.setPath( basePath + path);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from File repository");
			log .error(re.toString());
			throw re;
		}
	}
	

	@Override
	public Document template() {
		return null;
	}

	@Override
	public void persist(Serializable object) throws PersistException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getFullPath(), object);
			// Load 
			log .debug(" Adjusted Path"+adjustedPath );

			String fileContent = FileManager.readFile(adjustedPath);
			log .debug("File Content:"+fileContent);
			Document documentContents = DocumentUtils.stringToDocument(fileContent);
			Document serializedDocument = new Serializer().serialize(object, documentContents);
			FileManager.writeFile(adjustedPath, DocumentUtils.documentToString(serializedDocument));
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

	public void persistFromTemplate(Serializable object) throws PersistException {
		try {
			// Calculate Path.
			String adjustedPath = PathAdjust.adjustedPath(getFullPath(), object);
			// Load 
			log .debug(" Adjusted Path"+adjustedPath );

//			String fileContent = FileManager.readFile(adjustedPath);
//			log .debug("File Content:"+fileContent);
//			Document documentContents = DocumentUtils.stringToDocument(fileContent);
			Document serializedDocument = new Serializer().serialize(object, template);
			FileManager.writeFile(adjustedPath, DocumentUtils.documentToString(serializedDocument));
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

	@Override
	public void remove(Serializable object) throws RetrieveException {
		throw new IllegalStateException("This method has not yet been implemented.");
	}


	public String getFullPath() {
		return basePath + path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public Document getTemplate() {
		return template;
	}

	public void setTemplate(Document template) {
		this.template = template;
	}




}
