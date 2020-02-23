package common;

import java.io.IOException;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WebClient.WebResponseException;

public class ApiReader {
    private static final Logger LOG = LoggerFactory.getLogger(ApiReader.class);
    private RateLimiter rateLimiter;

    public ApiReader(double qps) {
        this.rateLimiter = RateLimiter.create(qps);
    }

    public <T> T getJson(String url, Class<T> clazz)
            throws WebResponseException, IOException, InterruptedException {
        int retryCount = 0;
        while (true) {
            try {
                this.rateLimiter.acquire();
                return WebClient.getJson(url, clazz);
            } catch (WebResponseException e) {
                if (e.statusCode >= 500) {
                    LOG.debug(String.format("%s", url));
                }
                // if (e.statusCode != 429) {
                // throw e;
                // }
            } catch (IOException e) {
                LOG.debug(Json.format(e));
            }
            retryCount++;
            LOG.debug(String.format("Retry %d: %s", retryCount, url));
        }
    }

    public <T> T postJson(String url, String data, MediaType contentType, Class<T> clazz)
            throws IOException, InterruptedException, WebResponseException {
        int retryCount = 0;
        while (true) {
            try {
                this.rateLimiter.acquire();
                return WebClient.postJson(url, data, contentType, clazz);
            } catch (WebResponseException e) {
                if (e.statusCode >= 500) {
                    LOG.debug(String.format("%s %s", url, data));
                }
                // if (e.statusCode != 429) {
                // throw e;
                // }
            } catch (IOException e) {
                LOG.debug(Json.format(e));
            }
            retryCount++;
            LOG.debug(String.format("Retry %d: %s %s", retryCount, url, data));
        }
    }
}
