package london_police;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import london_police.Boundry.Point;

public class ReadBoundries extends PTransform<PCollection<Neighbourhood>, PCollection<Boundry>> {
    private static final long serialVersionUID = 1L;
    private static final double MIN_LAT = 51.2016;
    private static final double MAX_LAT = 51.2016;
    private static final double MAX_LON = -0.61207;
    private static final double MIN_LON = 0.43735;

    private String apiPoliceUrl;

    public ReadBoundries(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<Boundry> expand(PCollection<Neighbourhood> input) {
        return input.apply("Read Boundries", ParDo.of(new ReadBoundriesDoFn(this.apiPoliceUrl)));
    }

    private static class ReadBoundriesDoFn extends DoFn<Neighbourhood, Boundry> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadBoundriesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element Neighbourhood neighbourhood,
                OutputReceiver<Boundry> output) {
            Point[] points = ApiReader.getJson(String.format("%s/%s/%s/boundary", this.apiPoliceUrl,
                    neighbourhood.force.id, neighbourhood.id), Point[].class);
            Boundry boundary = new Boundry();
            boundary.points = Arrays.asList(points).stream().filter(point -> {
                return point.latitude >= MIN_LAT && point.latitude <= MAX_LAT
                        && point.longitude >= MIN_LON && point.longitude <= MAX_LON;
            }).collect(Collectors.toList()).toArray(new Point[0]);
            if (boundary.points.length > 0) {
                output.output(boundary);
            }
        }
    }
}
