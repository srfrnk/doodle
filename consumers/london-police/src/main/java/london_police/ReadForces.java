package london_police;

import java.io.IOException;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.ArraySplitterDoFn;
import common.WebClient.WebResponseException;

public class ReadForces extends PTransform<PCollection<String>, PCollection<ForceResponse>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadForces.class);
    private String apiPoliceUrl;

    public ReadForces(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<ForceResponse> expand(PCollection<String> input) {
        return input.apply("Read Forces", ParDo.of(new ReadForcesDoFn(this.apiPoliceUrl)))
                .setCoder(AvroCoder.of(ForceResponse[].class))
                .apply("Split Forces", ParDo.of(new ArraySplitterDoFn<ForceResponse>()));
    }

    private static class ReadForcesDoFn extends DoFn<String, ForceResponse[]> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadForcesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(OutputReceiver<ForceResponse[]> output)
                throws WebResponseException, IOException, InterruptedException {
            LOG.info(String.format("Reading..."));
            ForceResponse[] forces = ApiReader
                    .getJson(String.format("%s/forces", this.apiPoliceUrl), ForceResponse[].class);
            output.output(forces);
        }
    }
}
