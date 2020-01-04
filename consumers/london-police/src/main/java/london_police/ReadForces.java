package london_police;

import java.io.IOException;
import java.io.Serializable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.ArraySplitterDoFn;
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
        return input
                .apply("Read Forces",
                        Create.ofProvider(new ReadForcesProvider(this.apiPoliceUrl),
                                AvroCoder.of(ForceResponse[].class)))
                .apply("Split Forces", ParDo.of(new ArraySplitterDoFn<ForceResponse>()));
    }

    private static class ReadForcesProvider
            implements ValueProvider<ForceResponse[]>, Serializable {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadForcesProvider(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @Override
        public ForceResponse[] get() {
            LOG.info(String.format("Reading..."));
            ForceResponse[] forces;
            try {
                forces = ApiReader.getJson(String.format("%s/forces", this.apiPoliceUrl),
                        ForceResponse[].class);
                return forces;
            } catch (WebResponseException | IOException | InterruptedException e) {
                LOG.error("Error reading forces:", e);
                return null;
            }
        }

        @Override
        public boolean isAccessible() {
            return true;
        }
    }
}
