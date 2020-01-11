package zoopla;

import common.Elasticsearch.ESDoc;

public class RentalHome extends ESDoc {
    private static final long serialVersionUID = 1L;

    public boolean sharedOccupancy;
    public double pricePerMonth;
    public double price;
    public int numFloors;
    public int numBedrooms;
    public int numBathrooms;
    public Location location;
    public String furnishedState;
    public String category;
    public String propertyType;
}
