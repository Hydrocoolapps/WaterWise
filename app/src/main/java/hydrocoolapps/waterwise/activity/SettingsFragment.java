package hydrocoolapps.waterwise.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.SwitchCompat;
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

    private Context context;
    private Bitmap bm;
    private Intent notificationIntent;
    private Button btnSignOut;
    private SwitchCompat btnEnableNotifications;

    private PendingIntent notificationPendingIntent;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private TaskStackBuilder stackBuilder;

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

        context = getActivity().getApplicationContext();

        mKinveyClient = SplashActivity.getClient();
        prefs = getActivity().getSharedPreferences("WaterWise", 0);

        btnEnableNotifications = (SwitchCompat) rootView.findViewById(R.id.btn_enable_notifications);
        btnSignOut = (Button) rootView.findViewById(R.id.btn_signout);

        // The large icon for the notification only accepts a bitmap
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);

        // Creating a test notification
        mBuilder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_small_notificaiton)
                        .setLargeIcon(bm)
                        .setContentTitle("Test notification")
                        .setContentText("It works!");

        // Creating the intent to direct the user when the notification is tapped
        notificationIntent = new Intent(context, SystemActivity.class);

        stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SystemActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        notificationPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Setting up the return to the application
        mBuilder.setContentIntent(notificationPendingIntent);
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Setting onclick listeners for buttons
        btnEnableNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { enableNotifications(); }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { logOut(); }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void enableNotifications() {

        //For now, we will just launch a test notification
        mNotificationManager.notify(0, mBuilder.build());

    }


    // Method to save userdata before logging out
    public void logOut() {

        // Store user information
        mKinveyClient.user().put("plantTitle", prefs.getString("plantTitle", getString(R.string.plant_info_heading)));
        mKinveyClient.user().put("plantDescription", prefs.getString("plantDescription", getString(R.string.plant_info_description)));
        mKinveyClient.user().put("plantImageId", prefs.getInt("plantImageId", R.drawable.ic_placeholder2_img));

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
