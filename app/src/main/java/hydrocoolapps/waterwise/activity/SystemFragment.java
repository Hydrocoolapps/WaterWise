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
public class SystemFragment extends Fragment{

    private Context context; // Need the context of the application for the toast message

    public SystemFragment() {
        // Constructor for the fragment class
    }

    private FloatingActionButton powerBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_system, container, false);

        // Instantiate the FAB and then add an action listener to it

        powerBtn = (FloatingActionButton) rootView.findViewById(R.id.system_fab);

        powerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // What to do on click by default Toast an Okay message

                Toast.makeText(context, "System FAB Okay!", Toast.LENGTH_SHORT).show();
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Get the context from the applicaiton when the fragment gets attached
        context = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
