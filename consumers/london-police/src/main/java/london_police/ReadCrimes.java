package london_police;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import london_police.Boundry.Point;

public class ReadCrimes extends PTransform<PCollection<Boundry>, PCollection<CrimeResponse>> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadCrimes.class);
    private String apiPoliceUrl;

    public ReadCrimes(String apiPoliceUrl) {
        this.apiPoliceUrl = apiPoliceUrl;
    }

    @Override
    public PCollection<CrimeResponse> expand(PCollection<Boundry> input) {
        return input.apply("Read Crimes", ParDo.of(new ReadBoundriesDoFn(this.apiPoliceUrl)));
    }

    private static class ReadBoundriesDoFn extends DoFn<Boundry, CrimeResponse> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadBoundriesDoFn(String apiPoliceUrl) {
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @ProcessElement
        public void processElement(@Element Boundry boundry, OutputReceiver<CrimeResponse> output) {
            // ArrayList<Point> points = new ArrayList<>(Arrays.asList(boundry.points));
            // if (points.get(0).equals(points.get(points.size() - 1))) {
            // points.remove((points.size() - 1));
            // }
            List<Point> points = Arrays.asList(boundry.points);
            String boundryString = String.join(":", points.stream().map((Point point) -> {
                return String.format("%s,%s", point.latitude, point.longitude);
            }).collect(Collectors.toList()));
            CrimeResponse[] crimes =
                    Reader.postJson(String.format("%s/crimes-street/all-crime", this.apiPoliceUrl),
                            String.format("poly=%s", boundryString),
                            "application/x-www-form-urlencoded", CrimeResponse[].class);
            for (CrimeResponse crimeResponse : crimes) {
                output.output(crimeResponse);
            }
        }
    }
}
