package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import hydrocoolapps.waterwise.R;

public class ResultsDialogFragment extends DialogFragment {

    private ListView resultsList;
    private String[] searchResults;

    public ResultsDialogFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results_dialog, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get the string array passed to this dialog as an argument
        searchResults = getArguments().getStringArray("searchResults");

        // Creating the listview and setting an on item click listener to get which result the user clicks on
        resultsList = (ListView) view.findViewById(R.id.list_results);
        resultsList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, searchResults));

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Overriding onItemClick, to add the selected item to an intent and trigger the onActivityResult() method of the plant info fragment
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Selected: " + position + " " + searchResults[position]);

                // Adding the selected item to an intent before sending it back to the plant_info fragment
                Intent i = new Intent()
                        .putExtra("searchResults", searchResults[position])
                        .putExtra("position", position);

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

                resultsList.setAdapter(null);

                dismiss();
            }
        });
    }
}
