package zoopla;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class PropertyListingsResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("listing")
    public Listing[] listings;

    public static class Listing implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("rental_prices")
        public RentalPrices rentalPrices;

        @SerializedName("num_floors")
        public String numFloors;

        @SerializedName("num_bedrooms")
        public String numBedrooms;

        @SerializedName("latitude")
        public double latitude;

        @SerializedName("furnished_state")
        public String furnishedState;

        @SerializedName("category")
        public String category;

        @SerializedName("property_type")
        public String propertyType;

        @SerializedName("letting_fees")
        public String lettingFees;

        @SerializedName("longitude")
        public double longitude;

        @SerializedName("price")
        public String price;

        @SerializedName("listing_id")
        public String listingId;

        @SerializedName("num_bathrooms")
        public String numBathrooms;
    }

    public static class RentalPrices implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("shared_occupancy")
        public String sharedOccupancy;

        @SerializedName("per_week")
        public double perWeek;

        @SerializedName("accurate")
        public String accurate;

        @SerializedName("per_month")
        public double perMonth;
    }
}
