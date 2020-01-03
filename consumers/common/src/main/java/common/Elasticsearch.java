package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.google.common.net.MediaType;
import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.LoggerFactory;

public class Elasticsearch {
  private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Elasticsearch.class);

  public static class WriteDocResponse implements Serializable {
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

  public static class DeleteIndexResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("acknowledged")
    public boolean acknowledged;
  }

  public static class MapIndexResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("index")
    public boolean index;

    @SerializedName("acknowledged")
    public boolean acknowledged;

    @SerializedName("shards_acknowledged")
    public boolean shardsAcknowledged;
  }

  private static class MapIndexRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private static class Mappings implements Serializable {
      private static final long serialVersionUID = 1L;

      @SerializedName("properties")
      Map<String, Property> properties;
    }

    private static class Property implements Serializable {
      public Property(String type) {
        this.type = type;
      }

      private static final long serialVersionUID = 1L;

      @SerializedName("type")
      String type;
    }

    @SerializedName("mappings")
    Mappings mappings;
  }

  public static WriteDocResponse writeDoc(Elasticsearch.ESDoc doc, String elasticSearchUrl) {
    try {
      String docString = Json.format(doc);
      return WebClient.postJson(String.format("%s/%s/_doc", elasticSearchUrl, doc.index), docString,
          MediaType.JSON_UTF_8, WriteDocResponse.class);
    } catch (Exception e) {
      LOG.error(String.format("ES Write Error: %s", Logging.exception(e)));
      return null;
    }
  }

  public static DeleteIndexResponse deleteIndex(String index, String elasticSearchUrl) {
    try {
      return WebClient.deleteJson(String.format("%s/%s", elasticSearchUrl, index),
          DeleteIndexResponse.class);
    } catch (Exception e) {
      LOG.error(String.format("ES Delete Error: %s", Logging.exception(e)));
      return null;
    }
  }

  public static MapIndexResponse mapIndex(String index, Map<String, String> properties,
      String elasticSearchUrl) {
    try {
      MapIndexRequest mapIndexRequest = new MapIndexRequest();
      mapIndexRequest.mappings = new MapIndexRequest.Mappings();
      mapIndexRequest.mappings.properties = new HashMap<>();
      for (Entry<String, String> property : properties.entrySet()) {
        mapIndexRequest.mappings.properties.put(property.getKey(),
            new MapIndexRequest.Property(property.getValue()));
      }
      return WebClient.putJson(String.format("%s/%s", elasticSearchUrl, index),
          Json.format(mapIndexRequest), MediaType.JSON_UTF_8, MapIndexResponse.class);
    } catch (Exception e) {
      LOG.error(String.format("ES Delete Error: %s", Logging.exception(e)));
      return null;
    }
  }

  public static class ESDoc implements Serializable {
    private static final long serialVersionUID = 1L;

    public ESDoc(String index) {
      this.timestamp = DateTime.now(DateTimeZone.UTC);
      this.index = index;
    }

    @SerializedName("@timestamp")
    public DateTime timestamp;

    public String index;

    @Override
    public boolean equals(Object obj) {
      ESDoc other = (ESDoc) obj;
      return other != null && Helper.objectEquals(this.index, other.index)
          && Helper.objectEquals(this.timestamp, other.timestamp);
    }
  }
}
