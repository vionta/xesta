package net.vionta.xml.xesta.repository.impl.util;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A basic file access implementation.
 */
public class FileManager {
	
	static Logger LOGGER = getLogger(FileManager.class);
	
	 /**
     * Checks if a file exists in the defined path.
     *
     * @param path
     * @return
     */
    public static boolean fileExists(String path) {
    	LOGGER.debug("Checking Path "+path);
    	return new File(path).exists();
    }
	
    /**
     * Writes a file on the host system.
     *
     * @param path file relative path.
     * @param contents File contents.
     * @throws IOException Write file exception (permissions, etc.).
     */
    public static void writeFile(String path, String contents) throws IOException {
    	LOGGER.debug("Writng File "+path);
    	Charset charset = Charset.forName("UTF-8");
    	Path filePath = FileSystems.getDefault().getPath(".", path);
    	BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath,charset);
    	bufferedWriter.write(contents, 0, contents.length());
    	bufferedWriter.close();
    }
    
    /**
     * Writes a file on the host system.
     *
     * @param path file relative path.
     * @param contents File contents.
     * @throws IOException Write file exception (permissions, etc.).
     * @throws TransformerException 
     */
    public static void writeFile(String path, Document contents) throws IOException, TransformerException {
    	LOGGER.info("Writng File "+path);
    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
    	Transformer transformer = transformerFactory.newTransformer();
    	DOMSource source = new DOMSource(contents);
    	FileWriter writer = new FileWriter(new File(path));
    	StreamResult result = new StreamResult(writer);
    	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    	transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    	transformer.transform(source, result);
    }
    
    
    /**
     * Reads a file from a path and returns a Xml document as a result.
     * @param path the file path
     * @return The Xml document object.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Document readDocument(String path) throws IOException, SAXException, ParserConfigurationException {
    	LOGGER.info("Reading documment "+path);
    	File xmlFile = new File(path);
    	if(!xmlFile.exists()) {
    		LOGGER.error("File "+path+" could not be found. It can not be read or parsed.");
        		throw new IOException("File "+path+" could not be found. It can not be read or parsed.");
    	}
	    // Create a DocumentBuilder
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    // Parse the XML file
	    Document document = builder.parse(xmlFile.getPath());
	    return document;
    }
    
    /**
     * Reads a file and returns the text content.
     * 
     * @param path The file path.
     * @return The text content of the file.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static String readFile(String path) throws IOException, SAXException, ParserConfigurationException {
    	LOGGER.debug("Reading file "+path);
	    Path filePath = FileSystems.getDefault().getPath(".", path);	
//    	File newFile = new File(path);
	    LOGGER.debug("Reading file "+filePath );
    	InputStream inputStream;
    	String contents = ""; 
		try {
			inputStream = Files.newInputStream(filePath);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				contents+=line;
				contents+="\n";
			}
			return contents; 
		} catch (IOException e) {
			LOGGER.error("Read file failed with cause:");
			LOGGER.error(""+e.getCause());
			throw e; 
		}
	}                  
    
}
