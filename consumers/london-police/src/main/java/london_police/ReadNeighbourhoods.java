package london_police;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

public class ReadNeighbourhoods
        extends PTransform<PCollection<ForceResponse>, PCollection<Neighbourhood>> {
    private static final long serialVersionUID = 1L;
    private String apiPoliceUrl;

    public ReadNeighbourhoods(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<Neighbourhood> expand(PCollection<ForceResponse> input) {
        return input.apply("Read Neighbourhoods", ParDo.of(new ReadNeighbourhoodsDoFn(this.apiPoliceUrl)));
    }

    private static class ReadNeighbourhoodsDoFn extends DoFn<ForceResponse, Neighbourhood> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadNeighbourhoodsDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element ForceResponse force,
                OutputReceiver<Neighbourhood> output) {
            NeighbourhoodResponse[] neighbourhoods = Reader.getJson(
                    String.format("%s/%s/neighbourhoods", this.apiPoliceUrl, force.id),
                    NeighbourhoodResponse[].class);
            for (NeighbourhoodResponse neighbourhoodResponse : neighbourhoods) {
                Neighbourhood neighbourhood = new Neighbourhood();
                neighbourhood.id = neighbourhoodResponse.id;
                neighbourhood.name = neighbourhoodResponse.name;
                neighbourhood.force = force;
                output.output(neighbourhood);
            }
        }
    }
}
