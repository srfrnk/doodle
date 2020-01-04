package london_police;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.net.MediaType;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.ArraySplitterDoFn;
import common.WebClient.WebResponseException;
import london_police.NeighbourhoodBoundary.Point;

public class ReadCrimes extends PTransform<PCollection<NeighbourhoodBoundary>, PCollection<Crime>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadCrimes.class);
    private String apiPoliceUrl;

    public ReadCrimes(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<Crime> expand(PCollection<NeighbourhoodBoundary> input) {
        return input.apply("Read Crimes", ParDo.of(new ReadCrimesDoFn(this.apiPoliceUrl)))
                .setCoder(AvroCoder.of(Crime[].class))
                .apply("Split Crimes", ParDo.of(new ArraySplitterDoFn<Crime>()));
    }

    private static class ReadCrimesDoFn extends DoFn<NeighbourhoodBoundary, Crime[]> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadCrimesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element NeighbourhoodBoundary boundry,
                OutputReceiver<Crime[]> output)
                throws IOException, InterruptedException, WebResponseException {
            LOG.info(String.format("Reading: %s -> %s", boundry.neighbourhood.force.name,
                    boundry.neighbourhood.name));
            List<Point> points = Arrays.asList(boundry.points);
            String boundryString = String.join(":", points.stream()
                    .map((Point point) -> String.format("%s,%s", point.latitude, point.longitude))
                    .collect(Collectors.toList()));
            CrimeResponse[] crimes = ApiReader.postJson(
                    String.format("%s/crimes-street/all-crime", this.apiPoliceUrl),
                    String.format("poly=%s", boundryString), MediaType.FORM_DATA,
                    CrimeResponse[].class);
            output.output(Arrays.asList(crimes).stream()
                    .map(crime -> new Crime(crime.location.latitude, crime.location.longitude))
                    .collect(Collectors.toList()).toArray(new Crime[0]));
        }
    }
}
