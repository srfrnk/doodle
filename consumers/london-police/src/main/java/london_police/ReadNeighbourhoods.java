package london_police;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

public class ReadNeighbourhoods
        extends PTransform<PCollection<ForceResponse>, PCollection<NeighbourhoodResponse>> {
    private static final long serialVersionUID = 1L;
    private String apiPoliceUrl;

    public ReadNeighbourhoods(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<NeighbourhoodResponse> expand(PCollection<ForceResponse> input) {
        return input.apply("Read Forces", ParDo.of(new ReadNeighbourhoodsDoFn(this.apiPoliceUrl)));
    }

    private static class ReadNeighbourhoodsDoFn extends DoFn<ForceResponse, NeighbourhoodResponse> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadNeighbourhoodsDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element ForceResponse force,
                OutputReceiver<NeighbourhoodResponse> output) {
            Reader reader = new Reader();
            NeighbourhoodResponse[] neighbourhoods = reader.getJson(
                    String.format("%s/%s/neighbourhoods", this.apiPoliceUrl, force.id),
                    NeighbourhoodResponse[].class);
            for (NeighbourhoodResponse neighbourhood : neighbourhoods) {
                output.output(neighbourhood);
            }
        }
    }
}
