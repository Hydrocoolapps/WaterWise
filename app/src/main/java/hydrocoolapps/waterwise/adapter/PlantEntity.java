package hydrocoolapps.waterwise.adapter;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

// Class to wrap around plant object in Kinvey server
public class PlantEntity extends GenericJson {

    @Key("plantName") // Holds the name of the plant object
    private String plantName;

    @Key("plantDescription") // Holds the description of the plant object
    private String plantDescription;

    @Key("ec") // Holds the EC level for the current plant
    private String ec;

    public PlantEntity() {}

    public String getPlantName() { return this.plantName; }

    public String getPlantDescription() { return this.plantDescription; }

    public String getEC() { return this.ec; }

}


