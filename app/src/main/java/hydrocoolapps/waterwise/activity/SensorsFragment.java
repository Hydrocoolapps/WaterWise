package hydrocoolapps.waterwise.activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import hydrocoolapps.waterwise.R;

public class SensorsFragment extends Fragment {

    private Context context;

    private Handler handler;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public SensorsFragment() {
        // Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_sensors, container, false);
        context = getActivity().getApplicationContext();
        handler = new Handler();

        // Attaching elements
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.sensor_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        // Adding on refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });


        return rootView;
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
                Toast.makeText(context, "Refresh Okay!", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
