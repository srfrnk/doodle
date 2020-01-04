package common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Helper {
    public static boolean objectEquals(Object obj1, Object obj2) {
        return (obj1 == null && obj2 == null)
                || ((obj1 != null && obj2 != null) && obj1.equals(obj2));
    }

    public static String readResource(String name) throws URISyntaxException, IOException {
        var uri = Helper.class.getResource("/" + name).toURI();
        var path = Paths.get(uri);
        return Files.readString(path);
    }
}
