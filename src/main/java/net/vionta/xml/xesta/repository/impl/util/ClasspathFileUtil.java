package net.vionta.xml.xesta.repository.impl.util;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.xml.sax.SAXException;

import net.sf.saxon.resource.ResourceLoader;


/**
 * A utility class that reads and writes content to classpath files. 
 * Classpath files must exist in order to be writen. 
 */
public class ClasspathFileUtil {

	
	static Logger log = getLogger(ClasspathFileUtil.class);
	
	 /**
     * Checks if a file exists in the defined path.
     *
     * @param path
     * @return
	 * @throws URISyntaxException 
     */
    public static boolean fileExists(String path)  {
    	log.debug("Checking Path "+path);
    	try {
    	URL url = ResourceLoader.class.getClassLoader().getResource(path); 
    	File file = new File(url.getPath());
    	return new File(path).exists();
    	} catch (Exception e) {
    		log.error("File  "+path+ " does not exist in classpath ");
    		log.debug(e.getCause() +" - "+ e.getMessage() );
    		return false;
		}
    }

    /**
     * Reads a file from the classpath entries
     * 
     * @param path
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws URISyntaxException
     */
    public static String readFile(String path) throws IOException, SAXException, ParserConfigurationException, URISyntaxException  {
    	log.debug("Reading file "+path);
    	ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);
        
        byte[] resourceBytes = inputStream.readAllBytes();
        inputStream.close();
        // Convert the byte array to a String and print it
        String resourceString = new String(resourceBytes, StandardCharsets.UTF_8);
//        log.debug(" Returning:"+resourceString);
//        log.debug(" ? > "+resourceString.indexOf("?"));        	
        if(resourceString.indexOf("<")==1) {
//        	log.debug(" ? TRUE ");        	
        	resourceString = resourceString.substring(resourceString.indexOf("<"));
        	log.debug(" TRUE : "+resourceString.substring(0,20));        	
        }
        log.debug(" Returning:"+resourceString);
        return resourceString;
	}        
	
	
    /**
     * Writes a file, from within the classpath entries. 
     * Ths method can not write if the file does not exist 
     * previously on the classpath.
     *
     * @param path file relative path.
     * @param contents File contents.
     * @throws IOException Write file exception (permissions, etc.).
     */
    public static void writeFile(String path, String contents) throws IOException, URISyntaxException  {
    	log.debug("Writng File "+path);
    	
    	URL url = ResourceLoader.class.getClassLoader().getResource(path);
    	log.debug("To  "+url);
    	String filePath = url.getPath();
    	log.debug("Final Path "+filePath);
    	filePath = saniticeFileName(filePath);
		PrintWriter writer = new PrintWriter(new File(filePath));
    	writer.println(contents);
    	writer.close();
    }

	public static String saniticeFileName(String fileName) {
		String sanitizedString  =fileName;
		if(fileName.contains(".jar!\\") && fileName.split(".jar!").length>1) {
			sanitizedString  = fileName.split(".jar!")[1].substring(1);
		}
		return sanitizedString ;
	}
    
}
