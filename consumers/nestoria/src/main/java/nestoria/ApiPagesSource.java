package nestoria;

import java.io.IOException;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.SplitReadersSource;
import common.WebClient.WebResponseException;
import nestoria.ListingResponse.Listing;

public class ApiPagesSource extends SplitReadersSource<ListingResponse.Listing, String> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ApiPagesSource.class);

    private String apiNestoriaUrl;

    public ApiPagesSource(String apiNestoriaUrl) {
        this.apiNestoriaUrl = apiNestoriaUrl;
    }

    @Override
    public String[] getSplitArray(PipelineOptions options) {
        ListingResponse res;
        try {
            res = App.apiReader.getJson(
                    String.format("%s&number_of_results=1&listing_type=rent", this.apiNestoriaUrl),
                    ListingResponse.class);
            int pageCount = (int) Math.ceil(res.response.totalResults / 50.0);
            String[] splits = new String[pageCount];
            for (int i = 0; i < pageCount; i++) {
                splits[i] = String.format("%s&number_of_results=50&listing_type=rent&page=%d",
                        this.apiNestoriaUrl, i + 1);
            }
            LOG.debug(String.format("Reading %d pages...", pageCount));
            return splits;
        } catch (WebResponseException | IOException | InterruptedException e) {
            LOG.error("", e);
            return new String[0];
        }
    }

    @Override
    public Listing[] readSplit(String splitData) {
        try {
            LOG.debug(String.format("Reading page %s", splitData));
            ListingResponse res = App.apiReader.getJson(splitData, ListingResponse.class);
            return res.response.listings;
        } catch (WebResponseException | IOException | InterruptedException e) {
            LOG.error("", e);
            return new Listing[0];
        }
    }

    @Override
    public long getEstimatedSizeBytes(PipelineOptions options) throws Exception {
        return 0;
    }

    @Override
    public Coder<ListingResponse.Listing> getOutputCoder() {
        return AvroCoder.of(ListingResponse.Listing.class);
    }
}
