package london_police;

import java.io.IOException;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WebClient;
import common.WebClient.WebResponseException;

public class ApiReader {
    private static final Logger LOG = LoggerFactory.getLogger(ApiReader.class);
    private static final RateLimiter rateLimiter = RateLimiter.create(15);

    public static <T> T getJson(String url, Class<T> clazz)
            throws WebResponseException, IOException, InterruptedException {
        while (true) {
            try {
                ApiReader.rateLimiter.acquire();
                return WebClient.getJson(url, clazz);
            } catch (WebResponseException e) {
                if (e.statusCode != 429) {
                    throw e;
                }
            }
        }
    }

    public static <T> T postJson(String url, String data, MediaType contentType, Class<T> clazz)
            throws IOException, InterruptedException, WebResponseException {
        while (true) {
            try {
                ApiReader.rateLimiter.acquire();
                return WebClient.postJson(url, data, contentType, clazz);
            } catch (WebResponseException e) {
                if (e.statusCode != 429) {
                    throw e;
                }
            }
        }
    }
}
