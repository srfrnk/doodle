/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package nestoria;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.beam.runners.direct.DirectOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.ApiReader;
import common.Elasticsearch;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static String apiNestoriaUrl = "https://api.nestoria.co.uk/api?encoding=json&pretty=0&action=search_listings&country=uk&place_name=london";
    // public static String apiNestoriaUrl = "https://api.nestoria.co.uk/api?encoding=json&pretty=0&action=search_listings&country=uk&place_name=london&number_of_results=50&listing_type=rent&page=2";
    public static String elasticSearchUrl = "http://localhost:9200";
    public static ApiReader apiReader = new ApiReader(1); // This will break idempotency of
                                                          // transforms but since running within
                                                          // same process it can still be safely
                                                          // used.

    public static void main(String[] args) throws URISyntaxException, IOException {

        Elasticsearch.deleteIndex("home_rentals", elasticSearchUrl);
        Elasticsearch.mapIndex("home_rentals", Map.ofEntries(Map.entry("location", "geo_point")),
                elasticSearchUrl);

        DirectOptions options = PipelineOptionsFactory.create().as(DirectOptions.class);
        options.setRunner(DirectRunner.class);
        options.setTargetParallelism(20);
        Pipeline p = Pipeline.create(options);
        p.apply(new UpdateHomeRentals(App.apiNestoriaUrl, elasticSearchUrl));
        p.run().waitUntilFinish();
    }
}