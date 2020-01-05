package london_police;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.BundledParDo;
import common.WebClient;
import common.WebClient.WebResponseException;

public class ReadNeighbourhoods extends BundledParDo<ForceResponse, Neighbourhood> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReadNeighbourhoods.class);

    public ReadNeighbourhoods(String apiPoliceUrl) {
        super(new ReadNeighbourhoodsBundler(apiPoliceUrl), new Neighbourhood[0]);
    }

    private static class ReadNeighbourhoodsBundler
            extends ArrayBundler<ForceResponse, Neighbourhood> {
        private static final long serialVersionUID = 1L;
        private String apiPoliceUrl;

        public ReadNeighbourhoodsBundler(String apiPoliceUrl) {
            super(10);
            this.apiPoliceUrl = apiPoliceUrl;
        }

        @Override
        public Neighbourhood[] getDataArray(ForceResponse force) {
            try {
                LOG.info(String.format("Reading: %s", force.name));
                NeighbourhoodResponse[] neighbourhoodRespones =
                        ApiReader.getJson(
                                String.format("%s/%s/neighbourhoods", this.apiPoliceUrl,
                                        WebClient.urlEncode(force.id)),
                                NeighbourhoodResponse[].class);
                Neighbourhood[] neighbourhoods =
                        Arrays.asList(neighbourhoodRespones).stream().map(neighbourhoodResponse -> {
                            Neighbourhood neighbourhood = new Neighbourhood();
                            neighbourhood.id = neighbourhoodResponse.id;
                            neighbourhood.name = neighbourhoodResponse.name;
                            neighbourhood.force = force;
                            return neighbourhood;
                        }).collect(Collectors.toList()).toArray(new Neighbourhood[0]);
                return neighbourhoods;
            } catch (WebResponseException | IOException | InterruptedException e) {
                LOG.error("Reading", e);
                return new Neighbourhood[0];
            }
        }
    }
}
