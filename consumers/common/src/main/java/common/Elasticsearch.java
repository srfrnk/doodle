package common;

import java.io.Serializable;
import com.google.common.net.MediaType;
import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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


  public static WriteResponse write(Elasticsearch.ESDoc doc, String elasticsearchUrl) {
    try {
      return WebClient.postJson(String.format("%s/%s/_doc", elasticsearchUrl, doc.index),
          Json.format(doc), MediaType.JSON_UTF_8, WriteResponse.class);
    } catch (Exception e) {
      LOG.error(String.format("ES Write Error: %s", Logging.exception(e)));
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
