package london_police;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.ArraySplitterDoFn;
import common.WebClient;
import common.WebClient.WebResponseException;

public class ReadNeighbourhoods
        extends PTransform<PCollection<ForceResponse>, PCollection<Neighbourhood>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadNeighbourhoods.class);
    private String apiPoliceUrl;

    public ReadNeighbourhoods(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<Neighbourhood> expand(PCollection<ForceResponse> input) {
        return input
                .apply("Read Neighbourhoods",
                        ParDo.of(new ReadNeighbourhoodsDoFn(this.apiPoliceUrl)))
                .setCoder(AvroCoder.of(Neighbourhood[].class))
                .apply("Split Forces", ParDo.of(new ArraySplitterDoFn<Neighbourhood>()));
    }

    private static class ReadNeighbourhoodsDoFn extends DoFn<ForceResponse, Neighbourhood[]> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadNeighbourhoodsDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element ForceResponse force,
                OutputReceiver<Neighbourhood[]> output)
                throws WebResponseException, IOException, InterruptedException {
            LOG.info(String.format("Reading: %s", force.name));
            NeighbourhoodResponse[] neighbourhoodRespones =
                    ApiReader.getJson(String.format("%s/%s/neighbourhoods", this.apiPoliceUrl,
                            WebClient.urlEncode(force.id)), NeighbourhoodResponse[].class);

            Neighbourhood[] neighbourhoods =
                    Arrays.asList(neighbourhoodRespones).stream().map(neighbourhoodResponse -> {
                        Neighbourhood neighbourhood = new Neighbourhood();
                        neighbourhood.id = neighbourhoodResponse.id;
                        neighbourhood.name = neighbourhoodResponse.name;
                        neighbourhood.force = force;
                        return neighbourhood;
                    }).collect(Collectors.toList()).toArray(new Neighbourhood[0]);

            output.output(neighbourhoods);
        }
    }
}
