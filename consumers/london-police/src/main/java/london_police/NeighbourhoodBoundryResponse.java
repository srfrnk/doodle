package london_police;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;
import common.Helper;

public class NeighbourhoodBoundryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Point implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("latitude")
        double latitude;

        @SerializedName("longitude")
        double longitude;

        @Override
        public boolean equals(Object obj) {
            Point other = (Point) obj;
            return other != null && Helper.objectEquals(this.latitude, other.latitude)
                    && Helper.objectEquals(this.longitude, other.longitude);
        }
    }

    @Override
    public boolean equals(Object obj) {
        NeighbourhoodBoundryResponse other = (NeighbourhoodBoundryResponse) obj;
        return other != null;
    }
}
