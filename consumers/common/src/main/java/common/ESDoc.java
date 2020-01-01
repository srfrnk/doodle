package common;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class ESDoc<T extends Serializable> implements Serializable {
  private static final long serialVersionUID = 1L;

  public ESDoc(String index, int userId, T data) {
    this.timestamp = DateTime.now(DateTimeZone.UTC);
    this.id = null;
    this.index = index;
    this.type = "_doc";
    this.userId = userId;
    this.data = data;
  }

  @SerializedName("@timestamp")
  public DateTime timestamp;

  public String index;
  public String type;
  public String id;
  public int userId;
  public T data;
}