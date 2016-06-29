package hydrocoolapps.waterwise.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import hydrocoolapps.waterwise.R;
public class SystemFragment extends Fragment{

    private Context context;
    private SystemFragment current;
    private FragmentManager fm;
    private PowerDialogFragment powerDialog;
    private static final int POWER_DIALOG_FRAGMENT = 1;

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
        context = getActivity().getApplicationContext();

        current = this;
        fm = getActivity().getFragmentManager();

        powerDialog = new PowerDialogFragment();

        // Instantiate the FAB
        powerBtn = (FloatingActionButton) rootView.findViewById(R.id.system_fab);

        // OnClickListener for FAB
        powerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Launching the power options dialog
                powerDialog.setTargetFragment(current, POWER_DIALOG_FRAGMENT);
                powerDialog.show(fm, "fragment_power_dialog");
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
