package hydrocoolapps.waterwise.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

import hydrocoolapps.waterwise.R;

public class SettingsFragment extends Fragment {

    private Button btnSignOut;

    private SharedPreferences prefs;
    private Client mKinveyClient;

    public SettingsFragment() {
        // Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mKinveyClient = SplashActivity.getClient();
        prefs = getActivity().getSharedPreferences("WaterWise", 0);


        btnSignOut = (Button) rootView.findViewById(R.id.btn_signout);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logOut();
            }
        });


        //inflating the view for this fragment
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Method to save userdata before logging out
    public void logOut() {

        // Store user information
        mKinveyClient.user().put("plantTitle", prefs.getString("plantTitle", getString(R.string.plant_info_heading)));
        mKinveyClient.user().put("plantDescription", prefs.getString("plantDescription", getString(R.string.plant_info_description)));
        mKinveyClient.user().put("plantImageId", prefs.getInt("plantImageId", R.drawable.ic_placeholder_img));

        mKinveyClient.user().update(new KinveyUserCallback() {
            @Override
            public void onSuccess(User user) {
                mKinveyClient.user().logout();
                goHome();
            }

            @Override
            public void onFailure(Throwable throwable) { Toast.makeText(getActivity().getApplicationContext(), "Failed to log out, please try again", Toast.LENGTH_SHORT).show(); }
        });
    }

    public void goHome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(getActivity(), SplashActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        }, 1000);
    }
}
