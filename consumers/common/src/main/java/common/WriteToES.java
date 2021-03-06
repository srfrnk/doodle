package common;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.slf4j.LoggerFactory;

public class WriteToES<T extends Elasticsearch.ESDoc> extends PTransform<PCollection<T>, PDone> {
    private static final long serialVersionUID = 1L;
    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Elasticsearch.class);

    private String elasticSearchUrl;

    public WriteToES(String elasticSearchUrl) {
        this.elasticSearchUrl = elasticSearchUrl;
    }

    @Override
    public PDone expand(PCollection<T> input) {
        input.apply("Write to ES", ParDo.of(new WriteToESDoFn<T>(this.elasticSearchUrl)));
        return PDone.in(input.getPipeline());
    }

    static class WriteToESDoFn<T extends Elasticsearch.ESDoc> extends DoFn<T, Void> {
        private static final long serialVersionUID = 1L;
        private String elasticSearchUrl;

        public WriteToESDoFn(String elasticSearchUrl) {
            this.elasticSearchUrl = elasticSearchUrl;
        }

        @ProcessElement
        public void processElement(@Element T doc) {
            try {
                Elasticsearch.writeDoc(doc, this.elasticSearchUrl);
            } catch (Exception e) {
                LOG.error(Json.format(doc), e);
            }
        }
    }
}
