package london_police;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

public class ReadForces extends PTransform<PCollection<String>, PCollection<ForceResponse>> {
    private static final long serialVersionUID = 1L;
    private String apiPoliceUrl;

    public ReadForces(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<ForceResponse> expand(PCollection<String> input) {
        return input.apply("Read Forces", ParDo.of(new ReadForcesDoFn(this.apiPoliceUrl)));
    }

    private static class ReadForcesDoFn extends DoFn<String, ForceResponse> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadForcesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(OutputReceiver<ForceResponse> output) {
            ForceResponse[] forces = ApiReader.getJson(String.format("%s/forces", this.apiPoliceUrl),
            ForceResponse[].class);
            for (ForceResponse force : forces) {
                output.output(force);
            }
        }
    }
}
