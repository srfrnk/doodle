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
import london_police.NeighbourhoodBoundary.Point;
import london_police.NeighbourhoodBoundary.Shape;

public class ReadNeighbourhoodBoundries
        extends PTransform<PCollection<Neighbourhood>, PCollection<NeighbourhoodBoundary>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadNeighbourhoodBoundries.class);

    private String apiPoliceUrl;

    public ReadNeighbourhoodBoundries(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<NeighbourhoodBoundary> expand(PCollection<Neighbourhood> input) {
        return input.apply("Read Boundries", ParDo.of(new ReadBoundriesDoFn(this.apiPoliceUrl)));
    }

    private static class ReadBoundriesDoFn extends DoFn<Neighbourhood, NeighbourhoodBoundary> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadBoundriesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element Neighbourhood neighbourhood,
                OutputReceiver<NeighbourhoodBoundary> output)
                throws WebResponseException, IOException, InterruptedException {
            LOG.info(String.format("Reading: %s -> %s", neighbourhood.force.name, neighbourhood.name));
            NeighbourhoodBoundryResponse.Point[] points = ApiReader.getJson(
                    String.format("%s/%s/%s/boundary", this.apiPoliceUrl,
                            WebClient.urlEncode(neighbourhood.force.id),
                            WebClient.urlEncode(neighbourhood.id)),
                    NeighbourhoodBoundryResponse.Point[].class);
            NeighbourhoodBoundary boundary = new NeighbourhoodBoundary();
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
