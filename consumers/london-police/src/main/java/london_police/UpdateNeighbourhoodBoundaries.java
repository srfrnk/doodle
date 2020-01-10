package london_police;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import common.ApiReader;
import common.WriteToES;

public class UpdateNeighbourhoodBoundaries
        extends PTransform<PBegin, PCollection<NeighbourhoodBoundary>> {
    private static final long serialVersionUID = 1L;

    private String apiPoliceUrl;
    private String elasticSearchUrl;

    public UpdateNeighbourhoodBoundaries(String apiPoliceUrl, String elasticSearchUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
        this.elasticSearchUrl = elasticSearchUrl;
    }

    @Override
    public PCollection<NeighbourhoodBoundary> expand(PBegin input) {
        PCollection<ForceResponse> forces =
                input.apply(new ReadForces(this.apiPoliceUrl));
        PCollection<Neighbourhood> neighbourhoods =
                forces.apply(new ReadNeighbourhoods(this.apiPoliceUrl));
        PCollection<NeighbourhoodBoundary> neighbourhoodBoundries = neighbourhoods
                .apply(new ReadNeighbourhoodBoundries(this.apiPoliceUrl));
        neighbourhoodBoundries.apply(new WriteToES<NeighbourhoodBoundary>(this.elasticSearchUrl));
        return neighbourhoodBoundries;
    }
}
