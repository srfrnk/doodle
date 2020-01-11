package nestoria;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;
import org.apache.avro.reflect.Nullable;

public class ListingResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("response")
    public Response response;

    public static class Response implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("listings")
        public Listing[] listings;

        @SerializedName("total_results")
        public int totalResults;
    }

    public static class Listing implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("bathroom_number")
        @Nullable
        public String bathroomNumber;

        @SerializedName("bedroom_number")
        @Nullable
        public String bedroomNumber;

        @SerializedName("car_spaces")
        @Nullable
        public String carSpaces;

        @SerializedName("commission")
        @Nullable
        public String commission;

        @SerializedName("construction_year")
        @Nullable
        public String constructionYear;

        @SerializedName("latitude")
        @Nullable
        public String latitude;

        @SerializedName("longitude")
        @Nullable
        public String longitude;

        @SerializedName("price")
        @Nullable
        public String price;

        @SerializedName("price_currency")
        @Nullable
        public String priceCurrency;

        @SerializedName("price_formatted")
        @Nullable
        public String priceFormatted;

        @SerializedName("price_high")
        @Nullable
        public String priceHigh;

        @SerializedName("price_low")
        @Nullable
        public String priceLow;

        @SerializedName("price_type")
        @Nullable
        public String priceType;

        @SerializedName("property_type")
        @Nullable
        public String propertyType;

        @SerializedName("room_number")
        @Nullable
        public String roomNumber;

        @SerializedName("size")
        @Nullable
        public String size;

        @SerializedName("size_type")
        @Nullable
        public String sizeType;
    }
}
