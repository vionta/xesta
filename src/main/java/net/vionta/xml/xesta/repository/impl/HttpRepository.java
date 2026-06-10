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
import net.vionta.xml.xesta.repository.impl.util.DocumentUtils;
import net.vionta.xml.xesta.repository.impl.util.HttpUtil;
import net.vionta.xml.xesta.repository.impl.util.PathAdjust;

public class HttpRepository implements DocumentRepository {

	static Logger log = getLogger(HttpRepository.class);
	
	/**
	 * Specific document path part. It is intended for paths 
	 * that need to be adjusted based on the object/documnet 
	 * properties.  
	 */
	private String documentPath = "" ; 
	/**
	 * The base path,intended for the collection path.
	 */
	private String basePath = "" ; 
	
	@Override
	public <T extends Serializable> T load(T object) throws RetrieveException {
		try {
			String textFromResource = HttpUtil.readHttpContent(PathAdjust.adjustedPath(getPath(), object));
			return  object = (T) new Deserializer().deserialize(object,DocumentUtils.stringToDocument(textFromResource));
		} catch (Exception e) {
			log .error(e.toString());
			RetrieveException re = new RetrieveException();
			re.setPath( getPath());
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	
	}


	@Override
	public void persist(Serializable object, Document document) throws PersistException {
		try {
			Document serializedDocument = new Serializer().serialize(object, document);
			HttpUtil.sendHttpContent(PathAdjust.adjustedPath(getPath(), object), DocumentUtils.documentToString(serializedDocument));
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(getPath());
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
		
	}
	
	@Override
	public void persist(Serializable object) throws PersistException {
		throw new IllegalStateException("This method has not yet been implemented.");
	}

	@Override
	public void remove(Serializable object) throws RetrieveException {
		throw new IllegalStateException("This method has not yet been implemented.");
	}
	
	@Override
	public Document template() {
		throw new IllegalStateException("This method has not yet been implemented.");
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public String getPath() {
		return getBasePath() + getDocumentPath();
	}

}
