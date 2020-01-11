package nestoria;

import common.Elasticsearch.ESDoc;

public class RentalHome extends ESDoc {
    private static final long serialVersionUID = 1L;

    public RentalHome() {
        super("home_rentals");
    }

    int roomNumber;
    int bathroomNumber;
    int bedroomNumber;

    int carSpaces;
    double commission;
    String constructionYear;
    Location location;
    double pricePerMonth;
    String propertyType;
    double size;
}
