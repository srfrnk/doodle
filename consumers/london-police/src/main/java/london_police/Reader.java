package london_police;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reader {
    private static final Logger LOG = LoggerFactory.getLogger(Reader.class);

    public String getString(String url) {
        try {
            HttpClient httpClient =
                    HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                    /* .setHeader("User-Agent", "Java 11 HttpClient Bot") */.build();
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Reader error:", e);
        }
        return "";
    }

    public <T> T getJson(String url, Class<T> clazz) {
        String json = this.getString(url);
        return Json.parse(json, clazz);
    }
}
