
package london_police;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json {
    public static <T> T parse(String json, Class<T> clazz) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, clazz);
    }

    public static <T> String format(T json) {
        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Gson gson = builder.create();
        return gson.toJson(json);
    }
}