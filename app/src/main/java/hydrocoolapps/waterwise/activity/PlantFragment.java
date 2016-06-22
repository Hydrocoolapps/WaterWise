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
import android.widget.Toast;

import hydrocoolapps.waterwise.R;

public class PlantFragment extends Fragment {

    private Bundle bundle;
    private Context context;
    private FloatingActionButton searchBtn;
    private SearchDialogFragment searchDialog;
    private FragmentManager fm;
    private String searchInput;

    private static final int SEARCH_DIALOG_FRAGMENT = 1; // Need this to establish a target between this fragment and the search dialog fragment

    public PlantFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_plant, container, false);
        context = getActivity().getApplicationContext();

        // Instantiate the search dialog
        fm = getActivity().getFragmentManager();
        searchDialog = new SearchDialogFragment();

        // I can use a bundle to add any args to this search dialog. I will use this later to add the search results to the results dialog

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

    private void showSearchDialog() {
        searchDialog.setTargetFragment(this, SEARCH_DIALOG_FRAGMENT);
        searchDialog.show(fm, "fragment_search_dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case SEARCH_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    bundle = data.getExtras();
                    searchInput = bundle.getString("search", "Failure");

                    // Now that I have the input string, I have to search the db then create the next dialog and add the results to it using a bundle

                    Toast.makeText(context, "You just searched: " + searchInput, Toast.LENGTH_SHORT).show();

                }

                break;
        }
    }

}



