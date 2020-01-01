/* package common;

import com.google.common.net.MediaType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

public class Elasticsearch {
  private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Elasticsearch.class);

  public class WriteResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("_index")
    public String index;

    @SerializedName("_type")
    public String type;

    @SerializedName("_id")
    public String id;

    @SerializedName("_version")
    public String version;

    @SerializedName("created")
    public String created;
  }

  public class SearchResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("took")
    public int took;

    @SerializedName("timed_out")
    public boolean timed_out;

    @SerializedName("hits")
    public HitsResponse hits;

    public class HitsResponse implements Serializable {
      private static final long serialVersionUID = 1L;

      @SerializedName("hits")
      public Hit[] hits;
    }

    public class Hit implements Serializable {
      private static final long serialVersionUID = 1L;

      @SerializedName("_index")
      public String index;

      @SerializedName("_type")
      public String type;

      @SerializedName("_id")
      public String id;

      @SerializedName("_source")
      public JsonObject source;
    }
  }

  public static WriteResponse write(ESDoc<?> doc, String elasticsearchUrl) {
    try {

      String json =
          ClientBuilder.newClient()
              .target(
                  String.format(
                      "%s%s/%s%s",
                      elasticsearchUrl,
                      doc.index,
                      doc.type,
                      doc.id == null ? "" : (String.format("/%s", doc.id))))
              .request()
              .accept(MediaType.JSON_UTF_8)
              .post(Entity.entity(Json.format(doc), MediaType.JSON_UTF_8), String.class);
      return Json.parse(json, WriteResponse.class);
    } catch (BadRequestException e) {
      LOG.error(
          String.format(
              "ES Write Error %s: %s",
              e.getResponse().readEntity(String.class), Logging.exception(e)));
      return null;
    } catch (Exception e) {
      LOG.error(String.format("ES Write Error: %s", Logging.exception(e)));
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends Serializable> List<ESDoc<T>> search(
      String index, String query, String elasticsearchUrl, Class<T> clazz) {
    try {
      String json =
          ClientBuilder.newClient()
              .target(String.format("%s%s/_search", elasticsearchUrl, index))
              .request()
              .accept(MediaType.APPLICATION_JSON)
              .post(Entity.entity(query, MediaType.APPLICATION_JSON), String.class);

      SearchResponse<T> res = Json.parse(json, SearchResponse.class);

      ArrayList<ESDoc<T>> docs = new ArrayList<>();
      for (SearchResponse<T>.Hit hit : res.hits.hits) {
        JsonElement source = hit.source;
        JsonElement sourceData = hit.source.get("data");
        ESDoc<T> doc = Json.parse(source.toString(), ESDoc.class);
        T docData = (T) Json.parse(sourceData.toString(), clazz);

        doc.id = hit.id;
        doc.data = docData;

        docs.add(doc);
      }

      return docs;
    } catch (BadRequestException e) {
      LOG.error(
          String.format(
              "ES Search Error %s: %s",
              e.getResponse().readEntity(String.class), Logging.exception(e)));
      return new ArrayList<>();
    } catch (Exception e) {
      LOG.error(String.format("ES Search Error: %s", Logging.exception(e)));
      return new ArrayList<>();
    }
  }

  private static final String MODEL_EVALUATION_QUERY =
      "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"userId\":%d}},{\"match\":{\"data.appId\":\"%d\"}}]}},\"sort\":[{\"@timestamp\":{\"order\":\"desc\"}}],\"size\":1}";

} */