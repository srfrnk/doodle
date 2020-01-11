package zoopla;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.BundledParDo;
import common.Helper;
import common.Elasticsearch.ESDoc.Location;
import common.WebClient.WebResponseException;

public class FindProperties extends BundledParDo<String, RentalHome> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(UpdateZooplaRentals.class);

    public FindProperties(String apiZooplaUrl, String keyZooplaApi) {
        super(new FindPropertiesBundler(apiZooplaUrl, keyZooplaApi), new RentalHome[0]);
    }

    private static final class FindPropertiesBundler extends ArrayBundler<String, RentalHome> {
        private static final long serialVersionUID = 1L;
        private String apiZooplaUrl;
        private String keyZooplaApi;

        private FindPropertiesBundler(String apiZooplaUrl, String keyZooplaApi) {
            super(10);
            this.apiZooplaUrl = apiZooplaUrl;
            this.keyZooplaApi = keyZooplaApi;
        }

        @Override
        public RentalHome[] getDataArray(String input) {
            try {
                PropertyListingsResponse response = App.apiReader.getJson(String.format(
                        "%s/property_listings.json?postcode=%s&api_key=%s&listing_status=rent",
                        this.apiZooplaUrl, input, this.keyZooplaApi),
                        PropertyListingsResponse.class);
                return Arrays.asList(response.listings).stream().map(listing -> {
                    RentalHome rentalHome = new RentalHome();
                    rentalHome.sharedOccupancy =
                            Helper.checkValue(listing.rentalPrices.sharedOccupancy, "").equals("Y");
                    rentalHome.pricePerMonth = listing.rentalPrices.perMonth;
                    rentalHome.numFloors =
                            Integer.parseInt(Helper.checkValue(listing.numFloors, "-1"));
                    rentalHome.numBedrooms =
                            Integer.parseInt(Helper.checkValue(listing.numBedrooms, "-1"));
                    rentalHome.numBathrooms =
                            Integer.parseInt(Helper.checkValue(listing.numBathrooms, "-1"));
                    rentalHome.location = new Location(listing.latitude, listing.longitude);
                    rentalHome.furnishedState = Helper.checkValue(listing.furnishedState, "");
                    rentalHome.category = Helper.checkValue(listing.category, "");
                    rentalHome.propertyType = Helper.checkValue(listing.propertyType, "");
                    rentalHome.price = Double.parseDouble(Helper.checkValue(listing.price, "-1"));
                    return rentalHome;
                }).collect(Collectors.toList()).toArray(new RentalHome[0]);
            } catch (WebResponseException | IOException | InterruptedException e) {
                LOG.error("", e);
                return new RentalHome[0];
            }
        }
    }
}
