package hydrocoolapps.waterwise.activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hydrocoolapps.waterwise.R;

public class SensorsFragment extends Fragment {

    private Context context;

    private String time;

    private Handler handler;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView phReading, ecReading,waterTempReading, waterLevelReading, humidityReading;
    private TextView phTimestamp, ecTimestamp, waterTempTimestamp, waterLevelTimestamp, humidityTimestamp;

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

        phReading = (TextView) view.findViewById(R.id.ph_sensor_value);
        ecReading = (TextView) view.findViewById(R.id.ec_sensor_value);
        waterTempReading = (TextView) view.findViewById(R.id.h20_sensor_value);
        waterLevelReading = (TextView) view.findViewById(R.id.water_level_sensor_value);
        humidityReading = (TextView) view.findViewById(R.id.humidity_sensor_value);

        phTimestamp = (TextView) view.findViewById(R.id.ph_sensor_timestamp);
        ecTimestamp = (TextView) view.findViewById(R.id.ec_sensor_timestamp);
        waterTempTimestamp = (TextView) view.findViewById(R.id.h20_sensor_timestamp);
        waterLevelTimestamp = (TextView) view.findViewById(R.id.water_level_sensor_timestamp);
        humidityTimestamp = (TextView) view.findViewById(R.id.humidity_sensor_timestamp);

        // Adding on refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Do some stuff, then update.
                time = getFormattedTime();

                update();
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // This method will do some jazz while we're refreshing
    private void update() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(false);

                phTimestamp.setText(time);
                ecTimestamp.setText(time);
                waterTempTimestamp.setText(time);
                waterLevelTimestamp.setText(time);
                humidityTimestamp.setText(time);

            }
        }, 2000);
    }

    // Method to create a timestamp
    public static String getFormattedTime() {
        Calendar cal = Calendar.getInstance();

        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("KK:mm:ss a");
        String localTime = date.format(currentLocalTime);

        return localTime;
    }



}
