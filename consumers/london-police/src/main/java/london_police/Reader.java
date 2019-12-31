package london_police;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reader {
    private static final Logger LOG = LoggerFactory.getLogger(Reader.class);
    private static final RateLimiter rateLimiter = RateLimiter.create(15, 0, TimeUnit.SECONDS);
    private static final HttpClient httpClient =
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    public static String requestString(HttpRequest request) {
        try {
            Reader.rateLimiter.acquire();
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

    public static String postString(String url, String data, String contentType) {
        HttpRequest request =
                HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(data))
                        .uri(URI.create(url)).header("Content-Type", contentType).build();
        return requestString(request);
    }

    public static <T> T getJson(String url, Class<T> clazz) {
        String json = Reader.getString(url);
        return Json.parse(json, clazz);
    }

    public static <T> T postJson(String url, String data, String contentType, Class<T> clazz) {
        String json = Reader.postString(url, data, contentType);
        return Json.parse(json, clazz);
    }
}
