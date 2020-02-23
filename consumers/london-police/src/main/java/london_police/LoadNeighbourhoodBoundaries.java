package london_police;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.Elasticsearch;
import common.Fanout;
import common.Helper;
import common.ReadAllAndSplitSource;

public class LoadNeighbourhoodBoundaries
        extends PTransform<PBegin, PCollection<NeighbourhoodBoundary>> {
    private static final Logger LOG = LoggerFactory.getLogger(LoadNeighbourhoodBoundaries.class);
    private static final long serialVersionUID = 1L;
    private String elasticSearchUrl;

    public LoadNeighbourhoodBoundaries(String elasticSearchUrl) {
        this.elasticSearchUrl = elasticSearchUrl;
    }

    @Override
    public PCollection<NeighbourhoodBoundary> expand(PBegin input) {
        return input.apply(Read.from(new NeighbourhoodBoundariesSource(this.elasticSearchUrl)))
                .apply(new Fanout<NeighbourhoodBoundary>(NeighbourhoodBoundary.class));
    }

    private static class NeighbourhoodBoundariesSource
            extends ReadAllAndSplitSource<NeighbourhoodBoundary> {
        private static final long serialVersionUID = 1L;

        private String elasticSearchUrl;

        public NeighbourhoodBoundariesSource(String elasticSearchUrl) {
            super(10);
            this.elasticSearchUrl = elasticSearchUrl;
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
            return 0L;
        }

        @Override
        public Coder<NeighbourhoodBoundary> getOutputCoder() {
            return AvroCoder.of(NeighbourhoodBoundary.class);
        }

        @Override
        public NeighbourhoodBoundary[] getDataArray(PipelineOptions options) {
            try {
                LOG.debug("Loading...");
                String readResource = Helper.readResource("london_geoshape_query.json");
                NeighbourhoodBoundary[] neighbourhoodBoundaries =
                        Elasticsearch.searchDocs("neighbourhood_boundaries", readResource,
                                NeighbourhoodBoundary.class, this.elasticSearchUrl);
                LOG.info(String.format("Loaded %d", neighbourhoodBoundaries.length));
                return neighbourhoodBoundaries;
            } catch (URISyntaxException | IOException e) {
                LOG.error("Reader", e);
                return new NeighbourhoodBoundary[0];
            }
        }
    }
}
