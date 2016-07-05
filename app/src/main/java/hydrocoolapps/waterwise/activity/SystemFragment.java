package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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

import java.util.Arrays;

import hydrocoolapps.waterwise.R;
import hydrocoolapps.waterwise.adapter.HttpRequestAsyncTask;

public class SystemFragment extends Fragment{

    private Bundle bundle;
    private Context context;
    private SharedPreferences prefs;
    private SystemFragment current;
    private FragmentManager fm;
    private PowerDialogFragment powerDialog;
    private FloatingActionButton powerBtn;

    private Boolean sendToSystem;
    private HttpRequestAsyncTask request;
    private String reply;
    private String[] parsedReply;

    private int colorAccent, colorDefault;
    private String ipAddress;
    private String[] currentPowerStatus;

    private final String PIN_PARAMETER = "pin";

    private final String PORT_NUMBER = "80";
    private final String LIGHTING_POWER_ON = "9";
    private final String LIGHTING_POWER_OFF = "10";
    private final String AIR_PUMP_POWER_ON = "13";
    private final String AIR_PUMP_POWER_OFF = "14";
    private final String WATER_PUMP_POWER_ON = "11";
    private final String WATER_PUMP_POWER_OFF = "12";
    private final String SYSTEM_STATUS = "15";

    private TextView systemStatusValue, lightingStatusValue, airPumpStatusValue, waterPumpStatusValue;
    private TextView systemStatusHeading, lightingStatusHeading, airPumpStatusHeading, waterPumpStatusHeading;
/*
    private Bitmap bm;
    private Intent notificationIntent;

    private PendingIntent notificationPendingIntent;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private TaskStackBuilder stackBuilder;

*/

    private Bundle args;

    private static final int POWER_DIALOG_FRAGMENT = 1;

    public SystemFragment() {} // Constructor for the fragment class

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_system, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Set the current fragment so we can return later
        current = this;

        // Get application context
        context = getActivity().getApplicationContext();

        // Get the fragment manager
        fm = getActivity().getFragmentManager();

        // Need to access application data storage
        prefs = getActivity().getSharedPreferences("WaterWise", 0);

        powerBtn = (FloatingActionButton) view.findViewById(R.id.system_fab);

        systemStatusHeading = (TextView) view.findViewById(R.id.system_status_heading);
        lightingStatusHeading = (TextView) view.findViewById(R.id.lighting_status_heading);
        airPumpStatusHeading = (TextView) view.findViewById(R.id.air_pump_status_heading);
        waterPumpStatusHeading = (TextView) view.findViewById(R.id.water_pump_status_heading);

        systemStatusValue = (TextView) view.findViewById(R.id.system_status_value);
        lightingStatusValue = (TextView) view.findViewById(R.id.lighting_status_value);
        airPumpStatusValue = (TextView) view.findViewById(R.id.air_pump_status_value);
        waterPumpStatusValue = (TextView) view.findViewById(R.id.water_pump_status_value);

        // Getting the default text color, and the accented text color
        colorDefault = systemStatusValue.getTextColors().getDefaultColor();
        colorAccent = getActivity().getResources().getColor(R.color.colorAccent);

        currentPowerStatus = new String[4];

        // Checking the app data storage for an ip address that was set by the user
        ipAddress = prefs.getString("ip", "0.0.0.0");

        // If the IP Address is not set, we can't get the current status
        if (ipAddress.equals("0.0.0.0")) {
            System.out.println("no ip found");

            // Editing the view to display instructions to the user
            systemStatusHeading.setText("Please set the system ip address in account settings.");
            lightingStatusHeading.setVisibility(View.GONE);
            airPumpStatusHeading.setVisibility(View.GONE);
            waterPumpStatusHeading.setVisibility(View.GONE);

            // There are no power options to toggle if the system is not connected
            powerBtn.setEnabled(false);
        }

        else if(ipAddress.equals("1.1.1.1")) {

            for (int i = 0; i < 4; i++)
                currentPowerStatus[i] = "OFF";

            sendToSystem = false;
            updateStatus();
        }

        else {

            // Request the system status
            request = new HttpRequestAsyncTask(context, SYSTEM_STATUS, ipAddress, PORT_NUMBER, PIN_PARAMETER);
            request.execute();

            // Get the system status as a string
            reply = request.getReply();

            // The string is delimited by commas
            parsedReply = reply.split(",");

            if(Arrays.asList(parsedReply).contains("1"))
                currentPowerStatus[0] = "ON";

            else
                currentPowerStatus[0] = "OFF";

            // Set the power status from the string
            for (int i = 1; i < 4; i++) {

                if (parsedReply[i-1].equals("1"))
                    currentPowerStatus[i] = "ON";

                else
                    currentPowerStatus[i] = "OFF";
            }

            // Bring back the headings
            systemStatusHeading.setText(getString(R.string.system_power));
            lightingStatusHeading.setVisibility(View.VISIBLE);
            airPumpStatusHeading.setVisibility(View.VISIBLE);
            waterPumpStatusHeading.setVisibility(View.VISIBLE);

            // Update the system status without sending it to the system
            sendToSystem = false;
            updateStatus();

            // Re-enable the power button
            powerBtn.setEnabled(true);

            System.out.println(reply);
        }

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

            if (currentPowerStatus == null)
                return;

            // Going to update the system status and send it to the system
            sendToSystem = true;
            updateStatus();

            // Saving results to local storage
            prefs.edit().putString("systemPowerStatus", currentPowerStatus[0]).apply();
            prefs.edit().putString("lightingPowerStatus", currentPowerStatus[1]).apply();
            prefs.edit().putString("airPumpPowerStatus", currentPowerStatus[2]).apply();
            prefs.edit().putString("waterPumpPowerStatus", currentPowerStatus[3]).apply();
        }

    }

    // Method to update the current system status
    public void updateStatus() {

        // Set the status for each component
        systemStatusValue.setText(currentPowerStatus[0]);
        lightingStatusValue.setText(currentPowerStatus[1]);
        airPumpStatusValue.setText(currentPowerStatus[2]);
        waterPumpStatusValue.setText(currentPowerStatus[3]);

        // Series of if else statements to handle the color of the text
        // If the value is ON use the accent color, if the value is OFF use the default color
        // If sendToSystem is true send an HTTP request. If false, do nothing.

        if (currentPowerStatus[0].equals("ON"))
            systemStatusValue.setTextColor(colorAccent);

        else
            systemStatusValue.setTextColor(colorDefault);

        if (currentPowerStatus[1].equals("ON")) {
            lightingStatusValue.setTextColor(colorAccent);

            if (sendToSystem)
                new HttpRequestAsyncTask(
                        context, LIGHTING_POWER_ON, ipAddress, PORT_NUMBER, PIN_PARAMETER
                ).execute();
        }

        else {
            lightingStatusValue.setTextColor(colorDefault);

            if (sendToSystem)
                new HttpRequestAsyncTask(
                        context, LIGHTING_POWER_OFF, ipAddress, PORT_NUMBER, PIN_PARAMETER
                ).execute();
        }

        if(currentPowerStatus[2].equals("ON")) {
            airPumpStatusValue.setTextColor(colorAccent);

            if (sendToSystem)
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                new HttpRequestAsyncTask(
                                        context, AIR_PUMP_POWER_ON, ipAddress, PORT_NUMBER, PIN_PARAMETER
                                ).execute();

                            }
                        }, 5000);
        }

        else {
            airPumpStatusValue.setTextColor(colorDefault);

            if (sendToSystem)
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                new HttpRequestAsyncTask(
                                        context, AIR_PUMP_POWER_OFF, ipAddress, PORT_NUMBER, PIN_PARAMETER
                                ).execute();

                            }
                        }, 5000);
        }

        if(currentPowerStatus[3].equals("ON")) {
            waterPumpStatusValue.setTextColor(colorAccent);

            if (sendToSystem)
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                new HttpRequestAsyncTask(
                                        context, WATER_PUMP_POWER_ON, ipAddress, PORT_NUMBER, PIN_PARAMETER
                                ).execute();

                            }
                        }, 10000);
        }

        else {
            waterPumpStatusValue.setTextColor(colorDefault);

            if (sendToSystem)
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                new HttpRequestAsyncTask(
                                        context, WATER_PUMP_POWER_OFF, ipAddress, PORT_NUMBER, PIN_PARAMETER
                                ).execute();

                            }
                        }, 10000);
        }
    }
}