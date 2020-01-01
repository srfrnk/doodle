package london_police;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;
import common.Helper;
import common.Elasticsearch.ESDoc;

public class Crime extends ESDoc {
    private static final long serialVersionUID = 1L;

    public Crime() {
        super("crimes");
    }

    public static class Location implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("lat")
        String latitude;

        @SerializedName("lon")
        String longitude;

        @Override
        public boolean equals(Object obj) {
            Location other = (Location) obj;
            return other != null && Helper.objectEquals(this.latitude, other.latitude)
                    && Helper.objectEquals(this.longitude, other.longitude);
        }
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
