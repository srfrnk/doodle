package london_police;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.BundledParDo;
import common.PostCodeReader;
import common.Elasticsearch.ESDoc.Location;
import common.WebClient.WebResponseException;
import london_police.NeighbourhoodBoundary.Point;

public class ReadCrimes extends BundledParDo<NeighbourhoodBoundary, Crime> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadCrimes.class);

    public ReadCrimes(String apiPoliceUrl) {
        super(new ReadCrimesBundler(apiPoliceUrl), new Crime[0]);
    }

    private static class ReadCrimesBundler extends ArrayBundler<NeighbourhoodBoundary, Crime> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadCrimesBundler(String apiPoliceUrl) {
            super(10);
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @Override
        public Crime[] getDataArray(NeighbourhoodBoundary boundary) {
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
                return crimes;
            } catch (IOException | InterruptedException | WebResponseException e) {
                LOG.error("Read", e);
                return new Crime[0];
            }
        }
    }
}
