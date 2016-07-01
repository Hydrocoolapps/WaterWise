package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hydrocoolapps.waterwise.R;
import hydrocoolapps.waterwise.adapter.HttpRequestAsyncTask;

public class SystemFragment extends Fragment{

    private Bundle bundle;
    private SharedPreferences prefs;
    private SystemFragment current;
    private FragmentManager fm;
    private PowerDialogFragment powerDialog;
    private FloatingActionButton powerBtn;

    private int colorAccent, colorDefault;
    private String ipAddress;
    private String[] currentPowerStatus;

    private TextView systemStatusValue, lightingStatusValue, airPumpStatusValue, waterPumpStatusValue;
/*
    private Bitmap bm;
    private Intent notificationIntent;

    private PendingIntent notificationPendingIntent;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private TaskStackBuilder stackBuilder;

*/

    private Bundle args;

    private HttpRequestAsyncTask httpRequestAsyncTask;

    private static final int POWER_DIALOG_FRAGMENT = 1;

    public SystemFragment() {} // Constructor for the fragment class

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_system, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        current = this;
        fm = getActivity().getFragmentManager();

        prefs = getActivity().getSharedPreferences("WaterWise", 0);
        ipAddress = prefs.getString("ip", null);

        powerBtn = (FloatingActionButton) view.findViewById(R.id.system_fab);
        systemStatusValue = (TextView) view.findViewById(R.id.system_status_value);
        lightingStatusValue = (TextView) view.findViewById(R.id.lighting_status_value);
        airPumpStatusValue = (TextView) view.findViewById(R.id.air_pump_status_value);
        waterPumpStatusValue = (TextView) view.findViewById(R.id.water_pump_status_value);

        colorDefault = systemStatusValue.getTextColors().getDefaultColor();
        colorAccent = getActivity().getResources().getColor(R.color.colorAccent);

        // Getting last saved power config, will overwrite with http result
        currentPowerStatus = new String[4];
        currentPowerStatus[0] = prefs.getString("systemPowerStatus", "off");
        currentPowerStatus[1] = prefs.getString("lightingPowerStatus", "off");
        currentPowerStatus[2] = prefs.getString("airPumpPowerStatus", "off");
        currentPowerStatus[3] = prefs.getString("waterPumpPowerStatus", "off");

        // Http request and overwrite currentPowerStatus




        // Update system status with corrected power status
        updateStatus();


        // notification code
/*
        if (prefs.getBoolean("enableNotifications", false)) {

            // The large icon for the notification only accepts a bitmap
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);

            // Creating a test notification
            mBuilder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_small_notificaiton)
                    .setLargeIcon(bm)
                    .setContentTitle("Come back!")
                    .setContentText("Click this notification to return to the System Fragment");

            // Creating the intent to direct the user when the notification is tapped
            notificationIntent = new Intent(context, SplashActivity.class);

            stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(SystemActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Setting up the return to the application
            mBuilder.setContentIntent(notificationPendingIntent);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Signaling the notification
            mNotificationManager.notify(0, mBuilder.build());
        }
*/

        // OnClickListener for FAB to launch power options dialog
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

    }

    @Override
    public void onDetach() { super.onDetach(); }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // If the power dialog returned I will get the results from it
        if (resultCode == Activity.RESULT_OK && requestCode == POWER_DIALOG_FRAGMENT) {

            bundle = data.getExtras();
            currentPowerStatus = bundle.getStringArray("newPowerStatus");

            updateStatus();

            if (currentPowerStatus == null)
                return;

            // Saving results to local storage
            prefs.edit().putString("systemPowerStatus", currentPowerStatus[0]).apply();
            prefs.edit().putString("lightingPowerStatus", currentPowerStatus[1]).apply();
            prefs.edit().putString("airPumpPowerStatus", currentPowerStatus[2]).apply();
            prefs.edit().putString("waterPumpPowerStatus", currentPowerStatus[3]).apply();
        }

    }

    // Method to update the current system status
    public void updateStatus() {

        systemStatusValue.setText(currentPowerStatus[0].toUpperCase());
        lightingStatusValue.setText(currentPowerStatus[1].toUpperCase());
        airPumpStatusValue.setText(currentPowerStatus[2].toUpperCase());
        waterPumpStatusValue.setText(currentPowerStatus[3].toUpperCase());

        if (systemStatusValue.getText().equals("ON"))
            systemStatusValue.setTextColor(colorAccent);
        else
            systemStatusValue.setTextColor(colorDefault);

        if (lightingStatusValue.getText().equals("ON"))
            lightingStatusValue.setTextColor(colorAccent);
        else
            lightingStatusValue.setTextColor(colorDefault);

        if(airPumpStatusValue.getText().equals("ON"))
            airPumpStatusValue.setTextColor(colorAccent);
        else
            airPumpStatusValue.setTextColor(colorDefault);

        if(waterPumpStatusValue.getText().equals("ON"))
            waterPumpStatusValue.setTextColor(colorAccent);
        else
            waterPumpStatusValue.setTextColor(colorDefault);
    }

}