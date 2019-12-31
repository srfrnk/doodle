package london_police;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import london_police.Boundry.Point;

public class ReadBoundries extends PTransform<PCollection<Neighbourhood>, PCollection<Boundry>> {
    private static final long serialVersionUID = 1L;
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
            Point[] points = Reader.getJson(String.format("%s/%s/%s/boundary", this.apiPoliceUrl,
                    neighbourhood.force.id, neighbourhood.id), Point[].class);
            Boundry boundary = new Boundry();
            boundary.points = points;
            output.output(boundary);
        }
    }
}
