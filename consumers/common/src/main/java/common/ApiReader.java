package common;

import java.io.IOException;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WebClient;
import common.WebClient.WebResponseException;

public class ApiReader {
    private static final Logger LOG = LoggerFactory.getLogger(ApiReader.class);
    private RateLimiter rateLimiter;

    public ApiReader(int qps) {
        this.rateLimiter = RateLimiter.create(qps);
    }

    public <T> T getJson(String url, Class<T> clazz)
            throws WebResponseException, IOException, InterruptedException {
        while (true) {
            try {
                this.rateLimiter.acquire();
                return WebClient.getJson(url, clazz);
            } catch (WebResponseException e) {
                if (e.statusCode != 429) {
                    throw e;
                }
            }
        }
    }

    public <T> T postJson(String url, String data, MediaType contentType, Class<T> clazz)
            throws IOException, InterruptedException, WebResponseException {
        while (true) {
            try {
                this.rateLimiter.acquire();
                return WebClient.postJson(url, data, contentType, clazz);
            } catch (WebResponseException e) {
                if (e.statusCode != 429) {
                    throw e;
                }
            }
        }
    }
}
