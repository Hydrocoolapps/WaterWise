package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import hydrocoolapps.waterwise.R;

public class PlantFragment extends Fragment {

    private Context context;
    private FloatingActionButton searchBtn;

    public PlantFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_plant, container, false);

        // Instantiate the FAB
        searchBtn = (FloatingActionButton) rootView.findViewById(R.id.plant_fab);

        // Attaching the listener to the FAB
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // What to do on click by default Toast an Okay message

                Toast.makeText(context, "Plant FAB Okay!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
