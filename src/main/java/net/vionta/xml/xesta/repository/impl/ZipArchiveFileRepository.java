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
import net.vionta.xml.xesta.repository.impl.util.PathAdjust;
import net.vionta.xml.xesta.repository.impl.util.ZipFileUtil;

public class ZipArchiveFileRepository implements DocumentRepository {

	static Logger log  = getLogger(ZipArchiveFileRepository.class);

	/**
	 * The name pattern for the zip file. The main file that contains the zipped ones.
	 */
	private String zipFilePattern ;
	
	/**
	 * Compressed file pattern inside the zip fileset. 
	 */
	private String fileNamePattern ; 

	public ZipArchiveFileRepository() {}

	public ZipArchiveFileRepository(String zipFilePattern, String fileNamePattern) {
		this.zipFilePattern = zipFilePattern;
		this.fileNamePattern = fileNamePattern;
	}
	

	@Override
	public <T extends Serializable> T load(T object) throws RetrieveException {
		log.debug(" Loading object from zip repository" +object);
		try {
			log.debug(" Zip parameters :" +zipFilePattern +" > "+fileNamePattern);
			String readTextFileInZip = ZipFileUtil.readTextFileInZip(zipFilePattern, fileNamePattern); 
			return  object = (T) new Deserializer().deserialize(object,DocumentUtils.stringToDocument(readTextFileInZip));
		} catch (Exception e) {
			log .error(e.toString());
			RetrieveException re = new RetrieveException();
			re.setPath( fileNamePattern);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Http repository");
			log .error(re.toString());
			throw re;
		}
	}

	@Override
	public void persist(Serializable object, Document document) throws PersistException {
		log.debug(" Persisting object on zip repository" +object );
		try {
			log.debug(" Zip parameters :" +zipFilePattern +" > "+fileNamePattern);
			Document serialize = new Serializer().serialize(object, document);
			ZipFileUtil.writeTextFileInZip(zipFilePattern, PathAdjust.adjustedPath(fileNamePattern, object), DocumentUtils.documentToString(document)); 
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(fileNamePattern);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error retrieving object from Zip repository");
			log .error(re.toString());
			throw re;
		}
	}

	public void persist(Serializable object) throws PersistException {
		try {
			String readTextFileInZip = ZipFileUtil.readTextFileInZip(zipFilePattern, fileNamePattern); 
			
			Document document = new Serializer().serialize(object, DocumentUtils.stringToDocument(  readTextFileInZip));
			ZipFileUtil.writeTextFileInZip(zipFilePattern, PathAdjust.adjustedPath(fileNamePattern, object), DocumentUtils.documentToString(document)); 
		} catch (Exception e) {
			log .error(e.toString());
			PersistException re = new PersistException();
			re.setPath(fileNamePattern);
			re.setSourceExpeption(e);
			e.printStackTrace();
			log .error("Error storing document on Zip repository");
			log .error(re.toString());
			throw re;
		}
	}

	@Override
	public Document template() {
		throw new IllegalStateException("This method has not been developped yet.");
	}

	@Override
	public void remove(Serializable object) throws RetrieveException {
		throw new IllegalStateException("This method has not been developped yet.");
	}
	
	public String getZipFilePattern() {
		return zipFilePattern;
	}

	public void setZipFilePattern(String zipFilePattern) {
		this.zipFilePattern = zipFilePattern;
	}

	public String getFileNamePattern() {
		return fileNamePattern;
	}

	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

}
