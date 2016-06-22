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
    private Bundle bundle;
    private String[] searchResults;

    public ResultsDialogFragment() {
        //Empty
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_results_dialog, container);

        searchResults = getArguments().getStringArray("searchResults");

        // Creating the listview and setting an on item click listener to get which result the user clicks on
        resultsList = (ListView) view.findViewById(R.id.list_results);
        resultsList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, searchResults));

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Overriding onItemClick, to add the selected item to an intent and trigger the onActivityResult() method of the plant info fragment
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent()
                        .putExtra("result", searchResults[position]);

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
            }
        });

        return view;

    }

}
