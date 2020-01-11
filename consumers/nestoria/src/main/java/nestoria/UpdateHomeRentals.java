package nestoria;

import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PDone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.WriteToES;

public class UpdateHomeRentals extends PTransform<PBegin, PDone> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(UpdateHomeRentals.class);

    private String apiNestoriaUrl;
    private String elasticSearchUrl;

    public UpdateHomeRentals(String apiNestoriaUrl, String elasticSearchUrl) {
        this.apiNestoriaUrl = apiNestoriaUrl;
        this.elasticSearchUrl = elasticSearchUrl;
    }

    @Override
    public PDone expand(PBegin input) {
        input.apply("Read page count", Read.from(new ApiPagesSource(this.apiNestoriaUrl)))
                .apply("Map to HomeRental", ParDo.of(new MapToHomeRental()))
                .apply("Wrtie to ES", new WriteToES<>(this.elasticSearchUrl));
        return PDone.in(input.getPipeline());
    }
}
