package london_police;

import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.Sample;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.Json;
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

        neighbourhoodBoundaries = neighbourhoodBoundaries.apply(Sample.any(10));

        PCollection<Crime> crimes =
                neighbourhoodBoundaries.apply(new ReadCrimes(this.apiPoliceUrl));

        return crimes.apply(MapElements.into(TypeDescriptors.strings()).via((c) -> Json.format(c)))
                .apply(TextIO.write().to("./data/d.json").withWindowedWrites());
        // return crimes.apply(new WriteToES<Crime>(this.elasticSearchUrl));
    }
}
