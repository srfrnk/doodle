package zoopla;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Sample;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PDone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.Helper;
import common.Json;

public class UpdateZooplaRentals extends PTransform<PBegin, PDone> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(UpdateZooplaRentals.class);
    private String apiZooplaUrl;
    private String elasticSearchUrl;
    private String keyZooplaApi;

    public UpdateZooplaRentals(String apiZooplaUrl, String elasticSearchUrl, String keyZooplaApi) {
        this.apiZooplaUrl = apiZooplaUrl;
        this.elasticSearchUrl = elasticSearchUrl;
        this.keyZooplaApi = keyZooplaApi;
    }

    @Override
    public PDone expand(PBegin input) {
        String[] londonPostcodes;
        try {
            londonPostcodes =
                    Json.parse(Helper.readResource("london-postcodes.json"), String[].class);
            input.apply("From Postcode List", Create.of(Arrays.asList(londonPostcodes)))
                    .apply(Sample.any(1))
                    .apply("Find Properties", new FindProperties(this.apiZooplaUrl, keyZooplaApi))
                    // .apply(Sample.any(1))
                    .apply(ParDo.of(new DoFn<RentalHome, Void>() {
                        private static final long serialVersionUID = 1L;

                        @ProcessElement
                        public void processElement(@Element RentalHome s) {
                            LOG.info(Json.format(s));
                        }
                    }));
        } catch (URISyntaxException | IOException e) {
            LOG.error("", e);
        }
        return PDone.in(input.getPipeline());
    }


}
