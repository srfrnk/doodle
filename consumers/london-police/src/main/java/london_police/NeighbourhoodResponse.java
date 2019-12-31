package london_police;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class NeighbourhoodResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @Override
    public boolean equals(Object obj) {
        NeighbourhoodResponse other = (NeighbourhoodResponse) obj;
        return other != null && Helper.objectEquals(this.id, other.id)
                && Helper.objectEquals(this.name, other.name);
    }
}
