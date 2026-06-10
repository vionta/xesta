package net.vionta.xml.xesta.repository.impl.util;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * An Http REST send/retrieve implementation.
 */
public class HttpUtil {

	/**
	 * Retrieve the content from an http server. 
	 * 
	 * @param path The resource path.
	 * @return The content retrieved from the service.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String readHttpContent(String path) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(path))
				.GET()
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		response.statusCode();
		String body = response.body();
		return body;
	}


	/**
	 * Wriites the content to an http service. 
	 * @param path
	 * @param content 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static String sendHttpContent(String path, String content) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(path))
				.POST(HttpRequest.BodyPublishers.ofString(content))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return response.body();
	}


}
