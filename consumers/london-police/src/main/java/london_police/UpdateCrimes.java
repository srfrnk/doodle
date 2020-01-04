package london_police;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.Sample;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import common.WriteToES;

public class UpdateCrimes extends PTransform<PBegin, PDone> {
    private static final long serialVersionUID = 1L;
    private String apiPoliceUrl;
    private String elasticSearchUrl;

    public UpdateCrimes(String apiPoliceUrl, String elasticSearchUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
        this.elasticSearchUrl = elasticSearchUrl;
    }

    @Override
    public PDone expand(PBegin input) {
        PCollection<NeighbourhoodBoundary> neighbourhoodBoundaries =
                input.apply(new LoadNeighbourhoodBoundaries(this.elasticSearchUrl));
        neighbourhoodBoundaries = neighbourhoodBoundaries.apply(Sample.any(1));
        PCollection<Crime> crimes =
                neighbourhoodBoundaries.apply(new ReadCrimes(this.apiPoliceUrl));
        return crimes.apply(new WriteToES<Crime>(this.elasticSearchUrl));
    }
}
