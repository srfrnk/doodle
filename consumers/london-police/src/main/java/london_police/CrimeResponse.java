package london_police;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class CrimeResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    static class Location implements Serializable {
        private static final long serialVersionUID = 1L;

        static class Street implements Serializable {
            private static final long serialVersionUID = 1L;
            @SerializedName("id")
            long id;

            @SerializedName("name")
            String name;

            @Override
            public boolean equals(Object obj) {
                Street other = (Street) obj;
                return other != null && Helper.objectEquals(this.id, other.id)
                        && Helper.objectEquals(this.name, other.name);
            }
        }

        @SerializedName("latitude")
        String latitude;

        @SerializedName("longitude")
        String longitude;

        @SerializedName("street")
        Street street;

        @Override
        public boolean equals(Object obj) {
            Location other = (Location) obj;
            return other != null && Helper.objectEquals(this.latitude, other.latitude)
                    && Helper.objectEquals(this.longitude, other.longitude)
                    && Helper.objectEquals(this.street, other.street);
        }
    }

    static class OutcomeStatus implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("category")
        String category;

        @SerializedName("date")
        String date;

        @Override
        public boolean equals(Object obj) {
            OutcomeStatus other = (OutcomeStatus) obj;
            return other != null && Helper.objectEquals(this.category, other.category)
                    && Helper.objectEquals(this.date, other.date);
        }
    }

    @SerializedName("id")
    long id;

    @SerializedName("category")
    String category;

    @SerializedName("location_type")
    String locationType;

    @SerializedName("location")
    Location location;

    @SerializedName("context")
    String context;

    @SerializedName("outcome_status")
    OutcomeStatus outcomeStatus;

    @SerializedName("persistent_id")
    String persistentId;

    @SerializedName("location_subtype")
    String locationSubtype;

    @SerializedName("month")
    String month;

    @Override
    public boolean equals(Object obj) {
        CrimeResponse other = (CrimeResponse) obj;
        return other != null && Helper.objectEquals(this.id, other.id)
                && Helper.objectEquals(this.category, other.category)
                && Helper.objectEquals(this.locationType, other.locationType)
                && Helper.objectEquals(this.location, other.location)
                && Helper.objectEquals(this.context, other.context)
                && Helper.objectEquals(this.outcomeStatus, other.outcomeStatus)
                && Helper.objectEquals(this.persistentId, other.persistentId)
                && Helper.objectEquals(this.locationSubtype, other.locationSubtype)
                && Helper.objectEquals(this.month, other.month);
    }
}
