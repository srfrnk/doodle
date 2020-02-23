package london_police;

import com.google.gson.annotations.SerializedName;
import common.Helper;
import common.Elasticsearch.ESDoc;
import common.PostCodeReader.PostCode;

public class Crime extends ESDoc {
    private static final long serialVersionUID = 1L;

    public Crime() {
        super("crimes");
        this.location = new Location();
    }

    @SerializedName("neighbourhoodBoundary")
    NeighbourhoodBoundary neighbourhoodBoundary;

    @SerializedName("location")
    Location location;

    @SerializedName("month")
    String month;

    @SerializedName("category")
    String category;

    @SerializedName("postcode")
    PostCode postcode;

    @Override
    public boolean equals(Object obj) {
        Crime other = (Crime) obj;
        return other != null && super.equals(obj)
                && Helper.objectEquals(this.neighbourhoodBoundary, other.neighbourhoodBoundary)
                && Helper.objectEquals(this.location, other.location)
                && Helper.objectEquals(this.month, other.month)
                && Helper.objectEquals(this.category, other.category)
                && Helper.objectEquals(this.postcode, other.postcode);
    }
}
