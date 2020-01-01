package common;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebClient {
    private static final Logger LOG = LoggerFactory.getLogger(WebClient.class);
    private static final HttpClient httpClient =
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    public static String requestString(HttpRequest request) {
        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new Exception(response.toString());
            }
        } catch (Exception e) {
            LOG.error("Error:", e);
        }
        return "";
    }

    public static String getString(String url) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        return requestString(request);
    }

    public static String postString(String url, String data, MediaType contentType,
            MediaType accept) {
        HttpRequest request =
                HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(data))
                        .uri(URI.create(url)).header("Content-Type", contentType.toString())
                        .header("Accept", accept.toString()).build();
        return requestString(request);
    }

    public static <T> T getJson(String url, Class<T> clazz) {
        String json = WebClient.getString(url);
        return Json.parse(json, clazz);
    }

    public static <T> T postJson(String url, String data, MediaType contentType, Class<T> clazz) {
        String json = WebClient.postString(url, data, contentType, MediaType.JSON_UTF_8);
        return Json.parse(json, clazz);
    }
}
