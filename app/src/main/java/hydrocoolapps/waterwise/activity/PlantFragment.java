package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;

import org.w3c.dom.Text;

import hydrocoolapps.waterwise.R;
import hydrocoolapps.waterwise.adapter.PlantEntity;

public class PlantFragment extends Fragment {

    private Bundle bundle;
    private Context context;
    private ImageView plantImage;
    private TextView plantTitle;
    private FloatingActionButton searchBtn;
    private SearchDialogFragment searchDialog;
    private ResultsDialogFragment resultsDialog;
    private FragmentManager fm;


    private Bundle args;
    private String searchInput;
    private String resultInput;
    private int numResults;
    private String[] searchResults;
    private PlantEntity selectedPlant;
    private static Client mKinveyClient;

    private Query query;
    private AsyncAppData<PlantEntity> plantResults;

    private PlantFragment current;

    // Using ints to provide selection of which DialogFragment to inflate
    private static final int SEARCH_DIALOG_FRAGMENT = 1;
    private static final int RESULTS_DIALOG_FRAGMENT = 2;

    public PlantFragment() {
        // Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Using onCreateView for Fragments
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_plant, container, false);
        context = getActivity().getApplicationContext();
        mKinveyClient = SplashActivity.getClient();

        // Instantiate the search dialog, results dialog, and get the fragment manager
        fm = getActivity().getFragmentManager();
        searchDialog = new SearchDialogFragment();
        resultsDialog = new ResultsDialogFragment();

        current = this;


        // Instantiate the title
        plantTitle = (TextView) rootView.findViewById(R.id.plant_info_heading);

        // Instantiate the image view
        plantImage = (ImageView) rootView.findViewById(R.id.plant_info_image);

        // Instantiate the FAB
        searchBtn = (FloatingActionButton) rootView.findViewById(R.id.plant_fab);

        // Attaching the listener to the FAB
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showSearchDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Method to launch search DialogFragment
    private void showSearchDialog() {

        searchDialog.setTargetFragment(this, SEARCH_DIALOG_FRAGMENT);
        searchDialog.show(fm, "fragment_search_dialog");
    }

    // Method to get data from Intent after returning to plant info page
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Switch statement to differentiate between which dialog just returned
        switch(requestCode) {

            // If it was the search dialog, then we want to search the database before launching the results dialog with the results
            case SEARCH_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    bundle = data.getExtras();
                    searchInput = bundle.getString("search", "Failure");

                    // If the input string is valid, search the database and create a dialog holding the results
                    if (!searchInput.equalsIgnoreCase("Failure")) {

                        // Need to query the database here using the search input
                        query = new Query();
                        query.startsWith("plantName", searchInput);

                        // Setting up the Async Data
                        plantResults = mKinveyClient.appData("Plants", PlantEntity.class);

                        plantResults.get(query, new KinveyListCallback<PlantEntity>() {
                            @Override
                            public void onSuccess(PlantEntity[] plantEntities) {

                                numResults = plantEntities.length;

                                System.out.println(numResults);

                                if (numResults > 0) {
                                    searchResults = new String[numResults];

                                    for (int i = 0; i < numResults; i++)
                                        searchResults[i] = plantEntities[i].getPlantName().toString();
                                } else {
                                    searchResults = new String[1];
                                    searchResults[1] = "No Plants Found!";
                                }

                                // Launching the results dialog with the list of results
                                args = new Bundle();
                                args.putStringArray("searchResults", searchResults);

                                resultsDialog.setArguments(args);

                                resultsDialog.setTargetFragment(current, RESULTS_DIALOG_FRAGMENT);
                                resultsDialog.show(fm, "fragment_results_dialog");

                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Toast.makeText(context,"Kinvey failed to search", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }

                break;

            // If it was the result dialog, then I just need to get the selection, then pull the correct object from the database
            case RESULTS_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    bundle = data.getExtras();
                    resultInput = bundle.getString("result", "Failure");

                    // If the selection is valid, get the object from the database
                    if (!resultInput.trim().equalsIgnoreCase("Failure")) {

                        plantTitle.setText(resultInput);

                        if (resultInput.contains("Lettuce")) {
                            plantImage.setImageResource(R.drawable.ic_lettuce_img);

                        }
                        // Need to query the database again for the name that was selected


                        // Once the query is complete, need to set the textview title and the imgview source to the correct title and picture
                    }
                }

                break;
        }
    }

}



