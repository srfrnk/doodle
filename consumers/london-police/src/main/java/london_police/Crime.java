package london_police;

import com.google.gson.annotations.SerializedName;
import common.Helper;
import common.Elasticsearch.ESDoc;

public class Crime extends ESDoc {
    private static final long serialVersionUID = 1L;

    public Crime() {
        super("crimes");
        this.location = new Location();
    }

    public Crime(String latitude, String longitude) {
        this();
        this.location =new Location(latitude,longitude);
    }

    @SerializedName("location")
    Location location;

    @Override
    public boolean equals(Object obj) {
        Crime other = (Crime) obj;
        return other != null && super.equals(obj)
                && Helper.objectEquals(this.location, other.location);
    }
}
