package nestoria;

import com.esotericsoftware.minlog.Log;
import org.apache.beam.sdk.transforms.DoFn;
import common.Helper;
import common.Elasticsearch.ESDoc.Location;
import nestoria.ListingResponse.Listing;

public class MapToHomeRental extends DoFn<Listing, RentalHome> {
    private static final long serialVersionUID = 1L;

    @ProcessElement
    public void processElement(@Element Listing listing, OutputReceiver<RentalHome> output) {
        RentalHome rentalHome = new RentalHome();

        rentalHome.bathroomNumber =
                Integer.parseInt(Helper.checkString(listing.bathroomNumber, "-1"));
        rentalHome.bedroomNumber = Integer.parseInt(Helper.checkString(listing.bedroomNumber, "-1"));
        rentalHome.carSpaces = Integer.parseInt(Helper.checkString(listing.carSpaces, "-1"));
        rentalHome.commission = Integer.parseInt(Helper.checkString(listing.commission, "-1"));
        rentalHome.constructionYear = listing.constructionYear;
        rentalHome.location = new Location(listing.latitude, listing.longitude);
        rentalHome.propertyType = listing.propertyType;
        rentalHome.roomNumber = Integer.parseInt(Helper.checkString(listing.roomNumber, "-1"));
        rentalHome.size = Double.parseDouble(Helper.checkString(listing.size, "-1"));
        double price = Double.parseDouble(Helper.checkString(listing.price, "-1"));
        if (!listing.priceCurrency.equals("£")) {
            Log.warn(String.format("Non GBP price detected: %s", listing.priceCurrency));
        }
        switch (listing.priceType) {
            case "monthly":
                break;
            case "weekly":
                price *= 4;
                break;
            default:
                Log.warn(String.format("Unsupported price type detected: %s", listing.priceType));
                break;
        }
        rentalHome.pricePerMonth = price;

        output.output(rentalHome);
    }
}
