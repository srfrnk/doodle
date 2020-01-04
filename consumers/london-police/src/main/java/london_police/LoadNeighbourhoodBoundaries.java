package london_police;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.ArraySplitterDoFn;
import common.Elasticsearch;
import common.Helper;

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
        return input
                .apply(Create.ofProvider(
                        new LoadNeighbourhoodBoundariesProvider(this.elasticSearchUrl),
                        AvroCoder.of(NeighbourhoodBoundary[].class)))
                .apply("Split Neighbourhood Boundaries",
                        ParDo.of(new ArraySplitterDoFn<NeighbourhoodBoundary>()));
    }

    private static class LoadNeighbourhoodBoundariesProvider
            implements ValueProvider<NeighbourhoodBoundary[]>, Serializable {
        private static final long serialVersionUID = 1L;
        private String elasticSearchUrl;

        public LoadNeighbourhoodBoundariesProvider(String elasticSearchUrl) {
            this.elasticSearchUrl = elasticSearchUrl;
        }

        @Override
        public NeighbourhoodBoundary[] get() {
            try {
                LOG.info("Loading...");
                String readResource = Helper.readResource("london_geoshape_query.json");
                NeighbourhoodBoundary[] neighbourhoodBoundries =
                        Elasticsearch.searchDocs("neighbourhood_boundaries", readResource,
                                NeighbourhoodBoundary.class, this.elasticSearchUrl);
                LOG.info(String.format("Loaded %d", neighbourhoodBoundries.length));
                return neighbourhoodBoundries;
            } catch (URISyntaxException | IOException e) {
                LOG.error("", e);
            }
            return null;
        }

        @Override
        public boolean isAccessible() {
            return true;
        }

    }
}
