package london_police;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;
import common.Helper;

public class Boundry implements Serializable {
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

    Point[] points;

    @Override
    public boolean equals(Object obj) {
        Boundry other = (Boundry) obj;

        if (other == null || this.points == null || this.points.length != other.points.length) {
            return false;
        }

        for (int i = 0; i < this.points.length; i++) {
            if (!this.points[i].equals(other.points[i])) {
                return false;
            }
        }

        return true;
    }
}
