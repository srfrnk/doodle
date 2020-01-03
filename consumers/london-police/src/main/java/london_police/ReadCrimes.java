package london_police;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.net.MediaType;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WebClient.WebResponseException;
import london_police.NeighbourhoodBoundry.Point;
import london_police.Crime.Location;

public class ReadCrimes extends PTransform<PCollection<NeighbourhoodBoundry>, PCollection<Crime>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadCrimes.class);
    private String apiPoliceUrl;

    public ReadCrimes(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<Crime> expand(PCollection<NeighbourhoodBoundry> input) {
        return input.apply("Read Crimes", ParDo.of(new ReadCrimesDoFn(this.apiPoliceUrl)));
    }

    private static class ReadCrimesDoFn extends DoFn<NeighbourhoodBoundry, Crime> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadCrimesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element NeighbourhoodBoundry boundry,
                OutputReceiver<Crime> output)
                throws IOException, InterruptedException, WebResponseException {
            List<Point> points = Arrays.asList(boundry.points);
            String boundryString = String.join(":", points.stream()
                    .map((Point point) -> String.format("%s,%s", point.latitude, point.longitude))
                    .collect(Collectors.toList()));
            CrimeResponse[] crimes = ApiReader.postJson(
                    String.format("%s/crimes-street/all-crime", this.apiPoliceUrl),
                    String.format("poly=%s", boundryString), MediaType.FORM_DATA,
                    CrimeResponse[].class);
            for (CrimeResponse crimeResponse : crimes) {
                Crime crime = new Crime();
                crime.location = new Location();
                crime.location.latitude = crimeResponse.location.latitude;
                crime.location.longitude = crimeResponse.location.longitude;
                output.output(crime);
            }
        }
    }
}
