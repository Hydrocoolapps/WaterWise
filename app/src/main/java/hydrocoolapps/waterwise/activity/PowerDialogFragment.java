package hydrocoolapps.waterwise.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hydrocoolapps.waterwise.R;

/**
 * Created by bluef on 6/29/2016.
 */
public class PowerDialogFragment extends DialogFragment {

    public PowerDialogFragment() {

    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_power_dialog, container);


        return view;

    }

}
