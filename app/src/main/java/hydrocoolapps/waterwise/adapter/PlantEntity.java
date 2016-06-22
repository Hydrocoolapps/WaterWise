package hydrocoolapps.waterwise.adapter;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

// Class to wrap around plant object in Kinvey server
public class PlantEntity extends GenericJson {

    @Key("plantName") // Holds the name of the plant object
    private String plantName;

    @Key("plantDescription") // Holds the description of the plant object
    private String plantDescription;

    @Key("nutrientAmount") // Holds the amount of nutrients to be fed
    private double nutrientAmount;

    @Key("nutrientFrequency") // Not sure what is being done with this, maybe how frequently the water must be changed?
    private int nutrientFrequency;

    public PlantEntity() {}

}


