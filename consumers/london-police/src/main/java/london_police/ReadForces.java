package london_police;

import java.io.IOException;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.ReadAllAndSplitSource;
import common.WebClient.WebResponseException;

public class ReadForces extends PTransform<PBegin, PCollection<ForceResponse>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadForces.class);
    private String apiPoliceUrl;

    public ReadForces(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<ForceResponse> expand(PBegin input) {
        return input.apply("Read Forces",
                Read.from(new ForcesSource(this.apiPoliceUrl)));
    }

    private static class ForcesSource extends ReadAllAndSplitSource<ForceResponse> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ForcesSource(String apiPoliceUrl) {
            super(10);
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @Override
        public ForceResponse[] getDataArray(PipelineOptions options) {

            LOG.info(String.format("Reading..."));
            ForceResponse[] forces;
            try {
                forces = App.apiReaderUKPolice.getJson(String.format("%s/forces", this.apiPoliceUrl),
                        ForceResponse[].class);
                return forces;
            } catch (WebResponseException | IOException | InterruptedException e) {
                LOG.error("Error reading forces:", e);
                return new ForceResponse[0];
            }
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
            return 0L;
        }

        @Override
        public Coder<ForceResponse> getOutputCoder() {
            return SerializableCoder.of(ForceResponse.class);
        }
    }
}
