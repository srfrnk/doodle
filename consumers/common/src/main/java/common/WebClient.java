package common;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebClient {
    public static class WebResponseException extends Exception {
        private static final long serialVersionUID = 1L;
        public int statusCode;
        public String method;
        public URI uri;

        public WebResponseException(int statusCode, String method, URI uri) {
            super(String.format("%d %s %s", statusCode, method, uri.toString()));
            this.statusCode = statusCode;
            this.method = method;
            this.uri = uri;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(WebClient.class);
    private static final HttpClient httpClient =
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    public static String requestString(HttpRequest request)
            throws IOException, InterruptedException, WebResponseException {
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 == 2) {
            return response.body();
        } else {
            throw new WebResponseException(response.statusCode(), response.request().method(),
                    response.request().uri());
        }
    }

    public static String getString(String url)
            throws IOException, InterruptedException, WebResponseException {
        LOG.debug(String.format("GET %s", url));
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
        return requestString(request);
    }

    public static String postString(String url, String data, MediaType contentType,
            MediaType accept) throws IOException, InterruptedException, WebResponseException {
        LOG.debug(String.format("POST %s: %s", url, data));
        HttpRequest request =
                HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(data))
                        .uri(URI.create(url)).header("Content-Type", contentType.toString())
                        .header("Accept", accept.toString()).build();
        return requestString(request);
    }

    public static String putString(String url, String data, MediaType contentType, MediaType accept)
            throws IOException, InterruptedException, WebResponseException {
        LOG.debug(String.format("PUT %s: %s", url, data));
        HttpRequest request =
                HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(data))
                        .uri(URI.create(url)).header("Content-Type", contentType.toString())
                        .header("Accept", accept.toString()).build();
        return requestString(request);
    }

    private static String deleteString(String url, MediaType accept)
            throws IOException, InterruptedException, WebResponseException {
        LOG.debug(String.format("DELETE %s", url));
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(url))
                .header("Accept", accept.toString()).build();
        return requestString(request);
    }

    public static <T> T getJson(String url, Class<T> clazz)
            throws IOException, InterruptedException, WebResponseException {
        String json = WebClient.getString(url);
        return Json.parse(json, clazz);
    }

    public static <T> T postJson(String url, String data, MediaType contentType, Class<T> clazz)
            throws IOException, InterruptedException, WebResponseException {
        String json = WebClient.postString(url, data, contentType, MediaType.JSON_UTF_8);
        return Json.parse(json, clazz);
    }

    public static <T> T putJson(String url, String data, MediaType contentType, Class<T> clazz)
            throws IOException, InterruptedException, WebResponseException {
        String json = WebClient.putString(url, data, contentType, MediaType.JSON_UTF_8);
        return Json.parse(json, clazz);
    }

    public static <T> T deleteJson(String url, Class<T> clazz)
            throws IOException, InterruptedException, WebResponseException {
        String json = WebClient.deleteString(url, MediaType.JSON_UTF_8);
        return Json.parse(json, clazz);
    }

    public static String urlEncode(Object value) {
        return URLEncoder.encode(value.toString(), Charset.forName("UTF-8")).replace("+", "%20");
    }
}
