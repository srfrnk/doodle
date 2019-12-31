package london_police;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class ForceResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @Override
    public boolean equals(Object obj) {
        ForceResponse other = (ForceResponse) obj;
        return this.id.equals(other.id) && this.name.equals(other.name);
    }
}
