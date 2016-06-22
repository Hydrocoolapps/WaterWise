package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hydrocoolapps.waterwise.R;

public class SettingsFragment extends Fragment {

    private Button btnSignOut;

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

        btnSignOut = (Button) rootView.findViewById(R.id.btn_signout);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SplashActivity.getClient().user().logout();
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        //inflating the view for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
