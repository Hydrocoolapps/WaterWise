package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import hydrocoolapps.waterwise.R;


public class PowerDialogFragment extends DialogFragment {

    private Context context;
    private SwitchCompat[] arrayBtn;
    private SwitchCompat btnSystemPower, btnLightingPower, btnAirPumpPower, btnWaterPumpPower;
    private Button btnCancel, btnDone;

    private String[] currentPowerStatus, newPowerStatus;

    public PowerDialogFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_power_dialog, container);
        context = getActivity().getApplicationContext();

        btnSystemPower = (SwitchCompat) view.findViewById(R.id.btn_system_power);
        btnLightingPower = (SwitchCompat) view.findViewById(R.id.btn_lighting_power);
        btnAirPumpPower = (SwitchCompat) view.findViewById(R.id.btn_air_pump_power);
        btnWaterPumpPower = (SwitchCompat) view.findViewById(R.id.btn_water_pump_power);

        btnCancel = (Button) view.findViewById(R.id.btn_cancel_power_options);
        btnDone = (Button) view.findViewById(R.id.btn_set_power_options);

        // Easier to hold buttons in an array and create a new string array for the outgoing status
        arrayBtn = new SwitchCompat[4];
        newPowerStatus = new String[4];

        arrayBtn[0] = btnSystemPower;
        arrayBtn[1] = btnLightingPower;
        arrayBtn[2] = btnAirPumpPower;
        arrayBtn[3] = btnWaterPumpPower;

        // Get current status that was passed to this fragment
        currentPowerStatus = getArguments().getStringArray("currentPowerStatus");

        if(currentPowerStatus == null)
            Toast.makeText(context, "Could not get currentPowerStatus", Toast.LENGTH_SHORT);

        // Shut off all the switches if the system power switch is off
        else if (currentPowerStatus[0].equalsIgnoreCase("off")) {

            btnSystemPower.setChecked(false);

            btnLightingPower.setChecked(false);
            btnLightingPower.setEnabled(false);

            btnAirPumpPower.setChecked(false);
            btnAirPumpPower.setEnabled(false);

            btnWaterPumpPower.setChecked(false);
            btnWaterPumpPower.setEnabled(false);
        }

        else {

            for (int i = 0; i < 4; i++) {

                switch (currentPowerStatus[i].toLowerCase()){

                    case "on":
                        arrayBtn[i].setChecked(true);
                        break;

                    case "off":
                        arrayBtn[i].setChecked(false);
                        break;
                }
            }
        }

        // OnclickListener to handle System Power switch setting the others.
        btnSystemPower.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (btnSystemPower.isChecked()) {

                    btnLightingPower.setEnabled(true);
                    btnAirPumpPower.setEnabled(true);
                    btnWaterPumpPower.setEnabled(true);
                }

                else {
                    btnLightingPower.setChecked(false);
                    btnLightingPower.setEnabled(false);

                    btnAirPumpPower.setChecked(false);
                    btnAirPumpPower.setEnabled(false);

                    btnWaterPumpPower.setChecked(false);
                    btnWaterPumpPower.setEnabled(false);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dismiss(); }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Loop to set up the response string
                for (int i = 0; i < 4; i++) {

                    if(arrayBtn[i].isChecked())
                        newPowerStatus[i] = "on";

                    else
                        newPowerStatus[i] = "off";
                }

                //Adding the response string to an intent before returning
                Intent i = new Intent()
                        .putExtra("newPowerStatus", newPowerStatus);

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

                dismiss();
            }
        });
        return view;
    }
}