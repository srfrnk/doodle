package common;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WebClient.WebResponseException;

public class PostCodeReader {
    private static final Logger LOG = LoggerFactory.getLogger(PostCodeReader.class);

    public static class PostCodeResponse implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("status")
        String status;

        @SerializedName("result")
        PostCode[] results;
    }

    public static class PostCode implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("postcode")
        String postcode;

        @SerializedName("quality")
        int quality;

        @SerializedName("eastings")
        int eastings;

        @SerializedName("northings")
        int northings;

        @SerializedName("country")
        String country;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("nhs_ha")
        String nhsHA;

        @SerializedName("longitude")
        double longitude;

        @SerializedName("latitude")
        double latitude;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("european_electoral_region")
        String europeanElectoralRegion;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("primary_care_trust")
        String primaryCareTrust;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("region")
        String region;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("lsoa")
        String lsoa;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("msoa")
        String msoa;

        @SerializedName("incode")
        String incode;

        @SerializedName("outcode")
        String outcode;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("parliamentary_constituency")
        String parliamentaryConstituency;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("admin_district")
        String adminDistrict;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("parish")
        String parish;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("admin_county")
        String adminCounty;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("admin_ward")
        String adminWard;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("ced")
        String ced;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("ccg")
        String ccg;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("nuts")
        String nuts;

        @SerializedName("codes")
        ONSCodes ONSCodes;

        @SerializedName("distance")
        double distance;

        @Override
        public boolean equals(Object obj) {
            PostCode other = (PostCode) obj;
            return other != null && Helper.objectEquals(this.postcode, other.postcode)
                    && Helper.objectEquals(this.quality, other.quality)
                    && Helper.objectEquals(this.eastings, other.eastings)
                    && Helper.objectEquals(this.northings, other.northings)
                    && Helper.objectEquals(this.country, other.country)
                    && Helper.objectEquals(this.nhsHA, other.nhsHA)
                    && Helper.objectEquals(this.longitude, other.longitude)
                    && Helper.objectEquals(this.latitude, other.latitude)
                    && Helper.objectEquals(this.europeanElectoralRegion,
                            other.europeanElectoralRegion)
                    && Helper.objectEquals(this.primaryCareTrust, other.primaryCareTrust)
                    && Helper.objectEquals(this.region, other.region)
                    && Helper.objectEquals(this.lsoa, other.lsoa)
                    && Helper.objectEquals(this.msoa, other.msoa)
                    && Helper.objectEquals(this.incode, other.incode)
                    && Helper.objectEquals(this.outcode, other.outcode)
                    && Helper.objectEquals(this.parliamentaryConstituency,
                            other.parliamentaryConstituency)
                    && Helper.objectEquals(this.adminDistrict, other.adminDistrict)
                    && Helper.objectEquals(this.parish, other.parish)
                    && Helper.objectEquals(this.adminCounty, other.adminCounty)
                    && Helper.objectEquals(this.adminWard, other.adminWard)
                    && Helper.objectEquals(this.ced, other.ced)
                    && Helper.objectEquals(this.ccg, other.ccg)
                    && Helper.objectEquals(this.nuts, other.nuts)
                    && Helper.objectEquals(this.ONSCodes, other.ONSCodes)
                    && Helper.objectEquals(this.distance, other.distance);
        }
    }

    public static class ONSCodes implements Serializable {
        private static final long serialVersionUID = 1L;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("admin_district")
        String adminDistrict;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("admin_county")
        String adminCounty;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("admin_ward")
        String adminWard;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("parish")
        String parish;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("parliamentary_constituency")
        String parliamentaryConstituency;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("ccg")
        String ccg;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("ccg_id")
        String ccgId;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("ced")
        String ced;

        @org.apache.avro.reflect.Nullable
        @Nullable
        @SerializedName("nuts")
        String nuts;

        @Override
        public boolean equals(Object obj) {
            ONSCodes other = (ONSCodes) obj;
            return other != null && Helper.objectEquals(this.adminDistrict, other.adminDistrict)
                    && Helper.objectEquals(this.adminCounty, other.adminCounty)
                    && Helper.objectEquals(this.adminWard, other.adminWard)
                    && Helper.objectEquals(this.parish, other.parish)
                    && Helper.objectEquals(this.parliamentaryConstituency,
                            other.parliamentaryConstituency)
                    && Helper.objectEquals(this.ccg, other.ccg)
                    && Helper.objectEquals(this.ccgId, other.ccgId)
                    && Helper.objectEquals(this.ced, other.ced)
                    && Helper.objectEquals(this.nuts, other.nuts);
        }
    }

    public static PostCode read(ApiReader apiReader, String latitude, String longitude)
            throws WebResponseException, IOException, InterruptedException {
        PostCodeResponse postCodeResponse = null;
        try {
            postCodeResponse = apiReader.getJson(String.format(
                    "https://api.postcodes.io/postcodes?lon=%s&lat=%s&limit=1&radius=1000",
                    longitude, latitude), PostCodeResponse.class);
            return postCodeResponse.results[0];
            // return postCodeResponse.results.length == 0 ? null : postCodeResponse.results[0];
        } catch (NullPointerException e) {
            LOG.debug(String.format("lon=%s&lat=%s", longitude, latitude)
                    + Json.format(postCodeResponse));
            return new PostCode();
        }
    }
}
