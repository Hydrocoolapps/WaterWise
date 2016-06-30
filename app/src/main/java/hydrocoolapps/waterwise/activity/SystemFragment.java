package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import hydrocoolapps.waterwise.R;
import hydrocoolapps.waterwise.adapter.HttpRequestAsyncTask;

public class SystemFragment extends Fragment{

    private Context context;
    private Bundle bundle;
    private SharedPreferences prefs;
    private SystemFragment current;
    private FragmentManager fm;
    private PowerDialogFragment powerDialog;
    private FloatingActionButton powerBtn;

    private String ipAddress;
    private String[] currentPowerStatus;
    private Set<String> lastPowerStatus;

    private Bundle args;

    private HttpRequestAsyncTask httpRequestAsyncTask;

    private static final int POWER_DIALOG_FRAGMENT = 1;
    private static final int DEFAULT_PORT_NUMBER = 80;

    public SystemFragment() {} // Constructor for the fragment class

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_system, container, false);
        context = getActivity().getApplicationContext();

        current = this;
        fm = getActivity().getFragmentManager();
        powerDialog = new PowerDialogFragment();

        // Instantiate the FAB
        powerBtn = (FloatingActionButton) rootView.findViewById(R.id.system_fab);

        prefs = getActivity().getSharedPreferences("WaterWise", 0);
        ipAddress = prefs.getString("ip", null);

        // Getting last saved power config, will overwrite with http result
        lastPowerStatus = prefs.getStringSet("currentPowerStatus", null);

        if (currentPowerStatus == null && lastPowerStatus == null) {
            currentPowerStatus = new String[4];

            // For testing, make all power statuses on
            for (int i = 0; i < 4; i++)
                currentPowerStatus[i] = "on";
        }

        else if (currentPowerStatus == null && lastPowerStatus!= null)
            currentPowerStatus = lastPowerStatus.toArray(new String[4]);

        // Http request and overwrite currentPowerStatus


        // OnClickListener for FAB
        powerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Packaging power options to be sent to the power dialog
                args = new Bundle();
                args.putStringArray("currentPowerStatus", currentPowerStatus);

                powerDialog = new PowerDialogFragment();

                // Sending the package to the power dialog
                powerDialog.setArguments(args);

                // Launching the power options dialog
                powerDialog.setTargetFragment(current, POWER_DIALOG_FRAGMENT);
                powerDialog.show(fm, "fragment_power_dialog");
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() { super.onDetach(); }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // If the power dialog returned I will get the results from it
        if (resultCode == Activity.RESULT_OK && requestCode == POWER_DIALOG_FRAGMENT) {

            bundle = data.getExtras();
            currentPowerStatus = bundle.getStringArray("newPowerStatus");

            if (currentPowerStatus == null)
                return;

            // Saving results to local storage
            prefs.edit().putStringSet("currentPowerStatus", new HashSet<String>(Arrays.asList(currentPowerStatus))).commit();
        }

    }

}
