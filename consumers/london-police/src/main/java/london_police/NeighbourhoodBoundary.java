package london_police;

import java.io.Serializable;
import java.util.Arrays;
import com.google.gson.annotations.SerializedName;
import common.Helper;
import common.Elasticsearch.ESDoc;

public class NeighbourhoodBoundary extends ESDoc {
    private static final long serialVersionUID = 1L;

    public NeighbourhoodBoundary() {
        super("neighbourhood_boundaries");
    }

    public static class Point implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("lat")
        double latitude;

        @SerializedName("lon")
        double longitude;

        public Point() {
        }

        public Point(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public boolean equals(Object obj) {
            Point other = (Point) obj;
            return other != null && Helper.objectEquals(this.latitude, other.latitude)
                    && Helper.objectEquals(this.longitude, other.longitude);
        }
    }

    public static class Shape implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("type")
        private final String type = "linestring";

        @SerializedName("coordinates")
        double[][] coordinates;

        public Shape() {
        }

        public Shape(Point[] coordinates) {
            this.coordinates = new double[coordinates.length][];
            for (int i = 0; i < coordinates.length; i++) {
                this.coordinates[i] =
                        new double[] {coordinates[i].longitude, coordinates[i].latitude};
            }
        }

        @Override
        public boolean equals(Object obj) {
            Shape other = (Shape) obj;
            return other != null && Arrays.deepEquals(this.coordinates, other.coordinates);
        }
    }

    @SerializedName("neighbourhood")
    Neighbourhood neighbourhood;

    @SerializedName("center")
    Point center;

    @SerializedName("geoShape")
    Shape geoShape;

    @SerializedName("points")
    Point[] points;

    @Override
    public boolean equals(Object obj) {
        NeighbourhoodBoundary other = (NeighbourhoodBoundary) obj;

        return other != null && Arrays.deepEquals(this.points, other.points)
                && Helper.objectEquals(this.center, other.center)
                && Helper.objectEquals(this.geoShape, other.geoShape)
                && Helper.objectEquals(this.neighbourhood, other.neighbourhood);

        // if (other == null || this.points == null || this.points.length != other.points.length) {
        // return false;
        // }

        // for (int i = 0; i < this.points.length; i++) {
        // if (!this.points[i].equals(other.points[i])) {
        // return false;
        // }
        // }

        // return Helper.objectEquals(this.neighbourhood, other.neighbourhood);
    }
}
