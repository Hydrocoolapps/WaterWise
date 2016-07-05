package hydrocoolapps.waterwise.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hydrocoolapps.waterwise.R;
import hydrocoolapps.waterwise.adapter.HttpRequestAsyncTask;

public class SensorsFragment extends Fragment {

    private Context context;

    private String time;

    private HttpRequestAsyncTask request;
    private String reply;
    private String ipAddress;
    private String[] parsedReply;

    private SharedPreferences prefs;

    private Handler handler;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView instructions;
    private TextView phReading, ecReading,waterTempReading, waterLevelReading, airTempReading, humidityReading;
    private TextView phTimestamp, ecTimestamp, waterTempTimestamp, waterLevelTimestamp, airTempTimestamp, humidityTimestamp;

    private final String PIN_PARAMETER = "pin";
    private final String PORT_NUMBER = "80";
    private final String SENSOR_REQUEST = "1";

    public SensorsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();
        handler = new Handler();

        // Attaching elements
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sensor_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        instructions = (TextView) view.findViewById(R.id.instructions);

        prefs = getActivity().getSharedPreferences("WaterWise", 0);
        ipAddress = prefs.getString("ip", "0.0.0.0");

        phReading = (TextView) view.findViewById(R.id.ph_sensor_value);
        ecReading = (TextView) view.findViewById(R.id.ec_sensor_value);
        waterTempReading = (TextView) view.findViewById(R.id.h20_sensor_value);
        waterLevelReading = (TextView) view.findViewById(R.id.water_level_sensor_value);
        airTempReading = (TextView) view.findViewById(R.id.air_temp_sensor_value);
        humidityReading = (TextView) view.findViewById(R.id.humidity_sensor_value);

        phTimestamp = (TextView) view.findViewById(R.id.ph_sensor_timestamp);
        ecTimestamp = (TextView) view.findViewById(R.id.ec_sensor_timestamp);
        waterTempTimestamp = (TextView) view.findViewById(R.id.h20_sensor_timestamp);
        waterLevelTimestamp = (TextView) view.findViewById(R.id.water_level_sensor_timestamp);
        airTempTimestamp = (TextView) view.findViewById(R.id.air_temp_sensor_timestamp);
        humidityTimestamp = (TextView) view.findViewById(R.id.humidity_sensor_timestamp);

        parsedReply = new String[6];

        // If this isn't the first launch of the app on this device remove the hint
        if (!SplashActivity.firstStart)
            instructions.setVisibility(View.GONE);

        // Adding on refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // If the hint was there before remove it now
                if (instructions.isEnabled())
                    instructions.setVisibility(View.GONE);

                // Make sure the user has entered an ip address
                if (ipAddress.equals("0.0.0.0")) {

                    mSwipeRefreshLayout.setRefreshing(false);

                    // If the ip address has not been set, print instructions
                    instructions.setText("Please set the system ip address in account settings.");
                    instructions.setVisibility(View.VISIBLE);


                }

                else if (ipAddress.equals("1.1.1.1")) {
                    for (int i = 0; i < 6; i++)
                        parsedReply[i] = "testValue";

                    time = getFormattedTime();

                    update();
                }


                else {

                    // Request sensor data from the system as a string
                    request = new HttpRequestAsyncTask(context, SENSOR_REQUEST, ipAddress, PORT_NUMBER, PIN_PARAMETER);
                    request.execute();

                    // Get the string
                    reply = request.getReply();

                    // Error handling
                    if (reply == null || reply.contains("ERROR"))
                        System.out.println(reply);

                    else {

                        // String will be delimited by commas
                        parsedReply = reply.split(",");

                        // Get the timestamp for the update
                        time = getFormattedTime();

                        // Update the fields
                        update();
                    }
                }

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // This method will update the fields on the screen
    private void update() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(false);

                // Use the server reply to set the sensor values
                phReading.setText(parsedReply[0] + "ph");
                ecReading.setText(parsedReply[1] + "mS/cm");
                waterTempReading.setText(parsedReply[2] + "F");
                waterLevelReading.setText(parsedReply[3]);
                airTempReading.setText(parsedReply[4] + "F");
                humidityReading.setText(parsedReply[5] + "%");

                // Use the timestamp to set the timestamp values
                phTimestamp.setText(time);
                ecTimestamp.setText(time);
                waterTempTimestamp.setText(time);
                waterLevelTimestamp.setText(time);
                airTempTimestamp.setText(time);
                humidityTimestamp.setText(time);

            }
        }, 1000);
    }

    // Method to create a timestamp
    public static String getFormattedTime() {
        Calendar cal = Calendar.getInstance();

        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("hh:mm a");
        String localTime = date.format(currentLocalTime);

        return localTime;
    }

}
