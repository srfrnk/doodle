/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package common;

import org.junit.Test;
import static org.junit.Assert.*;

public class WebClientTest {
    @Test
    public void testAppHasAGreeting() {
        assertEquals(WebClient.urlEncode("Swindon South"), "Swindon%20South");
    }
}
