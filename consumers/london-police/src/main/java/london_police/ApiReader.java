package london_police;

import java.util.concurrent.TimeUnit;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WebClient;

public class ApiReader {
    private static final Logger LOG = LoggerFactory.getLogger(ApiReader.class);
    private static final RateLimiter rateLimiter = RateLimiter.create(15, 0, TimeUnit.SECONDS);

    public static <T> T getJson(String url, Class<T> clazz) {
        ApiReader.rateLimiter.acquire();
        return WebClient.getJson(url, clazz);
    }

    public static <T> T postJson(String url, String data, MediaType contentType, Class<T> clazz) {
        ApiReader.rateLimiter.acquire();
        return WebClient.postJson(url, data, contentType, clazz);
    }
}
