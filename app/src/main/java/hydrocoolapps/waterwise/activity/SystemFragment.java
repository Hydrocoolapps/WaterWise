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


    private Bitmap bm;
    private Intent notificationIntent;

    private PendingIntent notificationPendingIntent;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private TaskStackBuilder stackBuilder;


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

        // Instantiate the FAB
        powerBtn = (FloatingActionButton) rootView.findViewById(R.id.system_fab);

        prefs = getActivity().getSharedPreferences("WaterWise", 0);
        ipAddress = prefs.getString("ip", null);

        // Getting last saved power config, will overwrite with http result
        currentPowerStatus = new String[4];
        currentPowerStatus[0] = prefs.getString("systemPowerStatus", "off");
        currentPowerStatus[1] = prefs.getString("lightingPowerStatus", "off");
        currentPowerStatus[2] = prefs.getString("airPumpPowerStatus", "off");
        currentPowerStatus[3] = prefs.getString("waterPumpPowerStatus", "off");

        // Http request and overwrite currentPowerStatus




        // notification code
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
            prefs.edit().putString("systemPowerStatus", currentPowerStatus[0]).apply();
            prefs.edit().putString("lightingPowerStatus", currentPowerStatus[1]).apply();
            prefs.edit().putString("airPumpPowerStatus", currentPowerStatus[2]).apply();
            prefs.edit().putString("waterPumpPowerStatus", currentPowerStatus[3]).apply();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        // For testing purposes, thow a notification anytime the system fragment is paused
        System.out.println("onPause() called");

        // Signaling the notification
        if (prefs.getBoolean("enableNotifications", false))
            mNotificationManager.notify(0, mBuilder.build());

        else
            System.out.println("Notifications not enabled");
    }

}
