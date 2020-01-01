package common;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class Json {
  private static Gson gson;
  private static ArrayList<TypeAdapterFactory> factories = new ArrayList<>();

  static {
    initGson();
  }

  private static void initGson() {
    GsonBuilder builder = new GsonBuilder();
    for (TypeAdapterFactory factory : factories) {
      builder.registerTypeAdapterFactory(factory);
    }
    builder.serializeSpecialFloatingPointValues();
    Converters.registerDateTime(builder);
    gson = builder.create();
  }

  public static void addFactory(TypeAdapterFactory factory) {
    factories.add(factory);
    initGson();
  }

  public static <T> T parse(String json, Class<T> clazz) {
    return gson.fromJson(json, clazz);
  }

  public static <T> String format(T object) {
    return gson.toJson(object, new TypeToken<T>() {}.getType());
  }
}