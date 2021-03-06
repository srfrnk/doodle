package london_police;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.Sample;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WriteToES;

public class UpdateCrimes extends PTransform<PBegin, PDone> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadNeighbourhoods.class);


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

        PCollection<Crime> crimes =
                neighbourhoodBoundaries.apply(new ReadCrimes(this.apiPoliceUrl));

        return crimes.apply(new WriteToES<Crime>(this.elasticSearchUrl));
    }
}
