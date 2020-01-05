package london_police;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.BoundedSource.BoundedReader;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        return input.apply(Read.from(new NeighbourhoodBoundariesSource(this.elasticSearchUrl)));
    }

    private static class NeighbourhoodBoundariesSource
            extends BoundedSource<NeighbourhoodBoundary> {
        private static final long serialVersionUID = 1L;
        private String elasticSearchUrl;
        private NeighbourhoodBoundary[] neighbourhoodBoundaries;

        public NeighbourhoodBoundariesSource(String elasticSearchUrl) {
            this.elasticSearchUrl = elasticSearchUrl;
        }

        @Override
        public List<? extends BoundedSource<NeighbourhoodBoundary>> split(
                long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
            try {
                LOG.info("Loading...");
                String readResource = Helper.readResource("london_geoshape_query.json");
                this.neighbourhoodBoundaries = Elasticsearch.searchDocs("neighbourhood_boundaries",
                        readResource, NeighbourhoodBoundary.class, this.elasticSearchUrl);
                LOG.info(String.format("Loaded %d", neighbourhoodBoundaries.length));
                ArrayList<NeighbourhoodBoundariesSplitSource> splits = new ArrayList<>();
                int from = 0;
                int splitSize = 10;
                while (from < neighbourhoodBoundaries.length) {
                    splits.add(new NeighbourhoodBoundariesSplitSource(this.neighbourhoodBoundaries,
                            from, Math.min(neighbourhoodBoundaries.length, from + splitSize)));
                    from += splitSize;
                }
                LOG.info(String.format("Split into %d", splits.size()));
                return splits;
            } catch (URISyntaxException | IOException e) {
                LOG.error("Reader", e);
                return Arrays.asList();
            }
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
            return 0L;
        }

        @Override
        public BoundedReader<NeighbourhoodBoundary> createReader(PipelineOptions options)
                throws IOException {
            return null;
        }

        @Override
        public Coder<NeighbourhoodBoundary> getOutputCoder() {
            return SerializableCoder.of(NeighbourhoodBoundary.class);
        }
    }

    private static class NeighbourhoodBoundariesSplitSource
            extends BoundedSource<NeighbourhoodBoundary> {
        private static final long serialVersionUID = 1L;

        private NeighbourhoodBoundariesSplitReader neighbourhoodBoundariesSplitReader;

        public NeighbourhoodBoundariesSplitSource(NeighbourhoodBoundary[] neighbourhoodBoundaries,
                int from, int to) {
            this.neighbourhoodBoundariesSplitReader =
                    new NeighbourhoodBoundariesSplitReader(this, neighbourhoodBoundaries, from, to);
        }

        @Override
        public List<? extends BoundedSource<NeighbourhoodBoundary>> split(
                long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
            return null;
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
            return 0L;
        }

        @Override
        public BoundedReader<NeighbourhoodBoundary> createReader(PipelineOptions options)
                throws IOException {
            return this.neighbourhoodBoundariesSplitReader;
        }

        @Override
        public Coder<NeighbourhoodBoundary> getOutputCoder() {
            return null;
        }
    }

    private static class NeighbourhoodBoundariesSplitReader
            extends BoundedReader<NeighbourhoodBoundary> {

        private NeighbourhoodBoundariesSplitSource source;
        private NeighbourhoodBoundary[] neighbourhoodBoundaries;
        private int from;
        private int to;
        private int current;

        public NeighbourhoodBoundariesSplitReader(NeighbourhoodBoundariesSplitSource source,
                NeighbourhoodBoundary[] neighbourhoodBoundaries, int from, int to) {
            this.source = source;
            this.neighbourhoodBoundaries = neighbourhoodBoundaries;
            this.from = from;
            this.to = to;
        }

        @Override
        public BoundedSource<NeighbourhoodBoundary> getCurrentSource() {
            return this.source;
        }

        @Override
        public boolean start() throws IOException {
            this.current = from;
            return true;
        }

        @Override
        public boolean advance() throws IOException {
            this.current++;
            return this.current < this.to;
        }

        @Override
        public NeighbourhoodBoundary getCurrent() throws NoSuchElementException {
            return this.neighbourhoodBoundaries[this.current];
        }

        @Override
        public void close() throws IOException {
            this.current = 0;
            this.neighbourhoodBoundaries = null;
        }
    }
}
