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
import common.PostCodeReader;
import common.Elasticsearch.ESDoc.Location;
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

        return input.apply(ParDo.of(new ReadCrimesDoFn(this.apiPoliceUrl)))
        .setCoder(AvroCoder.of(Crime[].class))
                .apply(ParDo.of(new ArraySplitterDoFn<Crime>()));
    }

    private static class ReadCrimesDoFn extends DoFn<NeighbourhoodBoundary, Crime[]> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadCrimesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void getDataArray(@Element NeighbourhoodBoundary boundary,
                OutputReceiver<Crime[]> output) {
            try {
                LOG.info(String.format("Reading: %s -> %s", boundary.neighbourhood.force.name,
                        boundary.neighbourhood.name));
                List<Point> points = Arrays.asList(boundary.points);
                String boundryString = String.join(":", points.stream().map(
                        (Point point) -> String.format("%s,%s", point.latitude, point.longitude))
                        .collect(Collectors.toList()));
                CrimeResponse[] crimeResponses = App.apiReaderUKPolice.postJson(
                        String.format("%s/crimes-street/all-crime", this.apiPoliceUrl),
                        String.format("poly=%s", boundryString), MediaType.FORM_DATA,
                        CrimeResponse[].class);
                Crime[] crimes = (Arrays.asList(crimeResponses).stream().map(crimeResponse -> {
                    Crime crime = new Crime();
                    crime.neighbourhoodBoundary = boundary;
                    crime.location = new Location(crimeResponse.location.latitude,
                            crimeResponse.location.longitude);
                    try {
                        crime.postcode = PostCodeReader.read(App.apiReaderPostCode,
                                crimeResponse.location.latitude, crimeResponse.location.longitude);
                    } catch (WebResponseException | IOException | InterruptedException e) {
                        LOG.error("Get postcode", e);
                    }
                    crime.month = crimeResponse.month;
                    crime.category = crimeResponse.category;
                    return crime;
                }).collect(Collectors.toList()).toArray(new Crime[0]));
                LOG.debug(String.format("Done: %s -> %s", boundary.neighbourhood.force.name,
                        boundary.neighbourhood.name));
                output.output(crimes);
            } catch (IOException | InterruptedException | WebResponseException e) {
                LOG.error("Read", e);
            }
        }
    }


}
