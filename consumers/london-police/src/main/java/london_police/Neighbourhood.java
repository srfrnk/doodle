package london_police;

import java.io.Serializable;
import common.Helper;

public class Neighbourhood implements Serializable {
    private static final long serialVersionUID = 1L;

    String id;
    String name;
    ForceResponse force;

    @Override
    public boolean equals(Object obj) {
        Neighbourhood other = (Neighbourhood) obj;
        return other != null && Helper.objectEquals(this.id, other.id)
                && Helper.objectEquals(this.name, other.name)
                && Helper.objectEquals(this.force, other.force);
    }
}
