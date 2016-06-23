package hydrocoolapps.waterwise.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.kinvey.android.Client;

// Activity to throw up a splash screen while I do some other stuff in the background
public class SplashActivity extends AppCompatActivity {

    private Context context;
    private Handler handler;
    private static Client mKinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using a handler to delay the activity switch
        handler = new Handler();
        context = this;

        // Starting the Kinvey client
        initKinvey();

        // Checking if the user has already logged in previously
        if (mKinveyClient.user().isUserLoggedIn())
            handler.postDelayed(goSystem, 2000);

        else
            handler.postDelayed(goLogin, 2000);

    }

    private void initKinvey() {
        // Connect the application to Kinvey
        mKinveyClient = new Client.Builder("kid_SJWfzTO4", "c0941b5c6ffe4226810f19bc672a0c23"
                , context).build();
    }

    public static Client getClient() {
        return mKinveyClient;
    }

    private Runnable goLogin = new Runnable() {

        public void run() {
            Intent intent = new Intent(context, WelcomeActivity.class);
            startActivity(intent);
        }
    };

    private Runnable goSystem = new Runnable() {

        public void run() {
            Intent intent = new Intent(context, SystemActivity.class);
            startActivity(intent);
        }
    };

}
