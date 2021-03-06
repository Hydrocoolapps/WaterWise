package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;

import hydrocoolapps.waterwise.R;
import hydrocoolapps.waterwise.adapter.HttpRequestAsyncTask;
import hydrocoolapps.waterwise.adapter.PlantEntity;

public class PlantFragment extends Fragment {

    private Bundle bundle;
    private Context context;
    private Button btnSetPlant;
    private ImageView plantImage;
    private TextView plantTitle;
    private TextView plantDescription;
    private FloatingActionButton searchBtn;
    private SearchDialogFragment searchDialog;
    private ResultsDialogFragment resultsDialog;
    private FragmentManager fm;
    private PlantFragment current;

    private String selectedEC;

    private String ipAddress;
    private String reply;
    private HttpRequestAsyncTask request;

    private Bundle args;
    private String searchInput;
    private static int numResults, currentImageId;
    private String[] searchResults;
    private static Client mKinveyClient;

    private Query query;
    private PlantEntity selectedPlant;
    private static PlantEntity[] plants;
    private AsyncAppData<PlantEntity> plantResults;

    private SharedPreferences prefs;

    private Boolean setPlant;

    // Using ints to provide selection of which DialogFragment to inflate
    private static final int SEARCH_DIALOG_FRAGMENT = 1;
    private static final int RESULTS_DIALOG_FRAGMENT = 2;

    private final String PIN_PARAMETER = "pin";
    private final String PORT_NUMBER = "80";
    private final String SEND_EC_VALUE = "16";

    public PlantFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plant, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        current = this;
        prefs = getActivity().getSharedPreferences("WaterWise", 0);
        context = getActivity().getApplicationContext();
        mKinveyClient = SplashActivity.getClient();

        fm = getActivity().getFragmentManager();
        searchDialog = new SearchDialogFragment();
        resultsDialog = new ResultsDialogFragment();

        // Instantiate the XML elements I will be using
        plantTitle = (TextView) view.findViewById(R.id.plant_info_heading);
        plantImage = (ImageView) view.findViewById(R.id.plant_info_image);
        plantDescription = (TextView) view.findViewById(R.id.plant_description);
        btnSetPlant = (Button) view.findViewById(R.id.btn_set_plant);
        searchBtn = (FloatingActionButton) view.findViewById(R.id.plant_fab);
        currentImageId = -1;

        ipAddress = prefs.getString("ip", "0.0.0.0");
        setPlant = false;

        btnSetPlant.setVisibility(View.GONE);

        btnSetPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlant = true;

                if (!ipAddress.equals("0.0.0.0") && !ipAddress.equals("1.1.1.1")) {
                    // Need to send EC value for this plant
                    request = new HttpRequestAsyncTask(context, SEND_EC_VALUE + "," + selectedEC, ipAddress, PORT_NUMBER, PIN_PARAMETER);
                    request.execute();
                }
            }
        });

        // Attaching the listener to the FAB
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                searchDialog.setTargetFragment(current, SEARCH_DIALOG_FRAGMENT);
                searchDialog.show(fm, "fragment_search_dialog");
            }
        });

    }


    // Method to get data from Intent after returning from either of the dialogs
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Switch statement to differentiate between which dialog just returned
        switch(requestCode) {

            // If it was the search dialog, then we want to search the database before launching the results dialog with the results
            case SEARCH_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {

                    System.out.println("Returned from search dialog");

                    // Get data from the intent sent by the search dialog
                    bundle = data.getExtras();
                    searchInput = bundle.getString("search", "Failure");

                    // If the input string is valid, search the database and create a dialog holding the results
                    if (!searchInput.equals("Failure") && !searchInput.equals("")) {

                        // Need to query the database here using the search input
                        query = mKinveyClient.query().startsWith("plantName", searchInput);

                        System.out.println("Searching for " + searchInput);

                        // Setting up the Async Data holder
                        plantResults = mKinveyClient.appData("Plants", PlantEntity.class);

                        plantResults.get(query, new KinveyListCallback<PlantEntity>() {
                            @Override
                            public void onSuccess(PlantEntity[] plantEntities) {

                                // Get the number of results, and the results
                                numResults = plantEntities.length;
                                plants = plantEntities;

                                if (numResults > 0) {

                                    // Sending the plantName for each result to the result dialog
                                    searchResults = new String[numResults];

                                    for (int i = 0; i < numResults; i++)
                                        searchResults[i] = plantEntities[i].getPlantName().toString();

                                    // Launching the results dialog with the list of results
                                    args = new Bundle();
                                    args.putStringArray("searchResults", searchResults);

                                    resultsDialog.setArguments(args);

                                    resultsDialog.setTargetFragment(current, RESULTS_DIALOG_FRAGMENT);
                                    resultsDialog.show(fm, "fragment_results_dialog");
                                }

                                else
                                    Toast.makeText(context, "No Results Found!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Toast.makeText(context, "Database could not be searched", Toast.LENGTH_SHORT).show();
                                System.out.println(throwable.getMessage());
                            }
                        });
                    }
                }

                break;

            // If it was the result dialog, then I just need to get the selection, then pull the correct object from the database
            case RESULTS_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {

                    // Get data from the intent sent by the results dialog
                    bundle = data.getExtras();
                    selectedPlant = plants[bundle.getInt("position")];

                    // If the selection is valid, change the values in the plant_info xml
                    if (selectedPlant != null) {

                        System.out.println("Selected plant: " + selectedPlant.getPlantName());

                        btnSetPlant.setVisibility(View.VISIBLE);

                        plantTitle.setText(selectedPlant.getPlantName());
                        plantDescription.setText(selectedPlant.getPlantDescription());
                        selectedEC = selectedPlant.getEC();

                        // Deciding which image to use
                        switch(selectedPlant.getPlantName().toLowerCase()) {

                            case "lettuce":
                                currentImageId = R.drawable.ic_lettuce_img;
                                plantImage.setImageResource(currentImageId);
                                break;

                            case "tomatoes":
                                currentImageId = R.drawable.ic_tomato_img;
                                plantImage.setImageResource(currentImageId);
                                break;

                            case "thai basil":
                                currentImageId = R.drawable.ic_thai_basil_img;
                                plantImage.setImageResource(currentImageId);
                                break;

                            default:
                                currentImageId = R.drawable.ic_placeholder2_img;
                                plantImage.setImageResource(currentImageId);
                                break;
                        }
                    }
                }

                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (setPlant) {
            // If the user clicked the set plant button save this plant as the target plant
            // Saving the current plant info selection to sharedprefs
            prefs.edit().putString("plantTitle", plantTitle.getText().toString()).apply();
            prefs.edit().putString("plantDescription", plantDescription.getText().toString()).apply();
            prefs.edit().putString("ec", selectedEC).apply();

            // Had to set if flag to keep the saved image id from being overridden if the user did not search a new plant
            if (currentImageId != -1)
                prefs.edit().putInt("plantImageId", currentImageId).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setPlant = false;

        // Pulling current plant info selection from sharedprefs
        plantTitle.setText(prefs.getString("plantTitle", getString(R.string.plant_info_heading)));
        plantDescription.setText(prefs.getString("plantDescription", getString(R.string.plant_info_description)));

        currentImageId = prefs.getInt("plantImageId", R.drawable.ic_placeholder2_img);
        plantImage.setImageResource(currentImageId);
        
        System.out.printf("Lettuce ID: %d \n Tomato ID: %d \n Thai Basil ID: %d \n Current ID: %d \n", R.drawable.ic_lettuce_img, R.drawable.ic_tomato_img, R.drawable.ic_thai_basil_img, currentImageId);

    }

}
