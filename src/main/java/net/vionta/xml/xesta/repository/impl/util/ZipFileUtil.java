package net.vionta.xml.xesta.repository.impl.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * An utility class to edit files inside zip file sets.
 */
public class ZipFileUtil {

	/**
	 * Reads the content form a zip file. 
	 * @param zipPath
	 * @param filePath
	 * @param content
	 * @throws IOException
	 */
	public static String readTextFileInZip(String zipPath, String filePath) throws IOException {
		Path zipFilePath = Paths.get(zipPath);
		FileSystem fs = FileSystems.newFileSystem(zipFilePath,  ZipFileUtil.class.getClassLoader());
		Path fileName = fs.getPath("/"+filePath);
		return readZipFileContent(fileName);
	}

	/**
	 * Writes content to a zip file.
	 * @param fileName
	 * @param content
	 * @throws IOException 
	 */
	private static String readZipFileContent( Path fileName) throws IOException {
		String content ="";
		InputStream newInputStream = Files.newInputStream(fileName);
		InputStreamReader inputStreamReader = new InputStreamReader(newInputStream);
		String line;
		BufferedReader br = new BufferedReader(inputStreamReader) ;
		while ((line = br.readLine()) != null ) {
			content += line;
		}
		br.close();
		inputStreamReader.close();
		newInputStream.close();
		return content;
	}
	
	/**
	 * Writes the content to a zip file. 
	 * @param zipPath
	 * @param filePath
	 * @param content
	 * @throws IOException
	 */
	public static void writeTextFileInZip(String zipPath, String filePath, String content) throws IOException {
		Path zipFilePath = Paths.get(zipPath);
		try (FileSystem fs = FileSystems.newFileSystem(zipFilePath,  ZipFileUtil.class.getClassLoader())) {
			Path fileName = fs.getPath("/"+filePath);
			writeZipFileContent(fileName, content);
		}
	}

	/**
	 * Writes content to a zip file.
	 * @param fileName
	 * @param content
	 * @throws IOException 
	 */
	private static void writeZipFileContent( Path fileName, String content) throws IOException {
		BufferedWriter bw = 
				new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(fileName))) ;
		bw.write(content);
		bw.close();
	}

}
