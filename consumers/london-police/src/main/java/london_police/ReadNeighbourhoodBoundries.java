package london_police;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WebClient;
import common.WebClient.WebResponseException;
import london_police.NeighbourhoodBoundry.Point;
import london_police.NeighbourhoodBoundry.Shape;

public class ReadNeighbourhoodBoundries
        extends PTransform<PCollection<Neighbourhood>, PCollection<NeighbourhoodBoundry>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadNeighbourhoodBoundries.class);

    private static final double MIN_LAT = 51.2;
    private static final double MAX_LAT = 51.8;
    private static final double MAX_LON = -0.6207;
    private static final double MIN_LON = 0.4;

    private String apiPoliceUrl;

    public ReadNeighbourhoodBoundries(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<NeighbourhoodBoundry> expand(PCollection<Neighbourhood> input) {
        return input.apply("Read Boundries", ParDo.of(new ReadBoundriesDoFn(this.apiPoliceUrl)));
    }

    private static class ReadBoundriesDoFn extends DoFn<Neighbourhood, NeighbourhoodBoundry> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadBoundriesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element Neighbourhood neighbourhood,
                OutputReceiver<NeighbourhoodBoundry> output)
                throws WebResponseException, IOException, InterruptedException {
            LOG.info(String.format("Reading: %s -> %s", neighbourhood.force.id, neighbourhood.id));
            NeighbourhoodBoundryResponse.Point[] points = ApiReader.getJson(
                    String.format("%s/%s/%s/boundary", this.apiPoliceUrl,
                            WebClient.urlEncode(neighbourhood.force.id),
                            WebClient.urlEncode(neighbourhood.id)),
                    NeighbourhoodBoundryResponse.Point[].class);
            NeighbourhoodBoundry boundary = new NeighbourhoodBoundry();
            boundary.neighbourhood = neighbourhood;
            if (points != null && points.length > 0) {
                boundary.points = Arrays.asList(points).stream()
                        .map(point -> new Point(point.latitude, point.longitude))
                        .collect(Collectors.toList()).toArray(new Point[0]);
                boundary.geoShape = new Shape(boundary.points);
                boundary.center = new Point(0.0, 0.0);
                for (Point point : boundary.points) {
                    boundary.center.latitude += point.latitude;
                    boundary.center.longitude += point.longitude;
                }
                boundary.center.latitude /= boundary.points.length;
                boundary.center.longitude /= boundary.points.length;
                output.output(boundary);
            }
        }
    }
}
