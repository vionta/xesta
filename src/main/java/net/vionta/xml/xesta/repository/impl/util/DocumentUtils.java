package net.vionta.xml.xesta.repository.impl.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * W3C DOM Document utilities.
 */
public class DocumentUtils {
	
	/**
	 * Extracts a W3C document to a String.
	 * 
	 * @param document
	 * @return
	 * @throws TransformerException
	 */
	public static String convert(Document document) throws TransformerException {
		return documentToString(document);
	}
	
	/**
	 * Extracts a W3C document to a String.
	 * 
	 * @param document
	 * @return
	 * @throws TransformerException
	 */
	public static String documentToString(Document document) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    StringWriter stringWriter = new StringWriter();
	    transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
	    String result = stringWriter.toString();
	    return result;
	}
	
	/**
	 * Return a document from the xml contents.
	 * 
	 * @param content
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document stringToDocument(String content) throws ParserConfigurationException, SAXException, IOException {
    // Create a DocumentBuilder
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    // Parse the XML file
    Document document = builder.parse(new InputSource(new StringReader(content)));
    return document;
	}	
	/**
	 * Return a document from the xml contents.
	 * 
	 * @param content
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document convert(String content) throws ParserConfigurationException, SAXException, IOException {
		return stringToDocument(content);
	}

}
