package hydrocoolapps.waterwise.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kinvey.android.AsyncUserDiscovery;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.callback.KinveyUserListCallback;
import com.kinvey.java.User;
import com.kinvey.java.model.UserLookup;

import java.util.regex.Pattern;

import hydrocoolapps.waterwise.R;


public class AccountFragment extends Fragment {

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutIp;
    private EditText updateEmail;
    private EditText updateIP;
    private Button btnAccountUpdate;
    private Button btnSystemUpdate;

    private String email;
    private String ip;

    private SharedPreferences prefs;

    // Regular expression for IP format
    private static final Pattern IP_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private Context context;
    private Client mKinveyClient;
    private AsyncUserDiscovery users;
    private User currentUser;
    private UserLookup criteria;

    public AccountFragment() {
        // Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        context = getActivity().getApplicationContext();

        prefs = getActivity().getSharedPreferences("WaterWise", 0);

        // Setting up some Kinvey variables so that I can update the account info and search for conflicts
        mKinveyClient = SplashActivity.getClient();
        currentUser = mKinveyClient.user();
        users = mKinveyClient.userDiscovery();
        criteria = users.userLookup();

        // Connecting elements
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.input_layout_email);
        inputLayoutIp = (TextInputLayout) rootView.findViewById(R.id.input_layout_ip);

        updateEmail = (EditText) rootView.findViewById(R.id.input_email);
        updateIP = (EditText) rootView.findViewById(R.id.input_ip);

        updateEmail.setTypeface(Typeface.DEFAULT);

        btnAccountUpdate = (Button) rootView.findViewById(R.id.btn_update_account);
        btnSystemUpdate = (Button) rootView.findViewById(R.id.btn_update_system);

        // Setting up the account update button
        btnAccountUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Getting the entered email
                email = updateEmail.getText().toString();

                // Only attempt to update if the user did in fact enter a valid email
                if (!email.equals(getString(R.string.email_unchanged)) && validateEmail(email)) {

                    // Update the username
                    currentUser.setUsername(email);

                    // Update the Kinvey db with the changes
                    mKinveyClient.user().update(
                            new KinveyUserCallback() {
                                @Override
                                public void onFailure(Throwable t) {

                                    // Going to check to see if the e-mail already exists
                                    criteria.setEmail(email);

                                    // Looking up users on the server
                                    users.lookup(criteria, new KinveyUserListCallback() {
                                        @Override
                                        public void onSuccess(User[] result) {
                                            CharSequence text = "That e-mail is already in use";
                                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Throwable error) {
                                            CharSequence text = "Email update failed, please try again";
                                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onSuccess(User u) {
                                    CharSequence text = "Your account information has been updated";
                                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            }
        });


        btnSystemUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ip = updateIP.getText().toString();

                if(!ip.equals(getString(R.string.ip_unchanged)) && isValidIP(ip)) {

                    CharSequence text = "IP Address updated";
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                    prefs.edit().putString("ip", ip).commit();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Method to check if the email is valid and set the error message if not
    private boolean validateEmail(String email) {

        inputLayoutEmail.setErrorEnabled(false);

        if(email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            inputLayoutEmail.setErrorEnabled(true);
            requestFocus(updateEmail);
            return false;
        }

        return true;
    }

    private boolean validateIP(String ip) {

        inputLayoutIp.setErrorEnabled(false);

        if(ip.isEmpty() || !isValidIP(ip)) {

            inputLayoutIp.setError(getString(R.string.err_msg_ip));
            inputLayoutEmail.setErrorEnabled(true);
            requestFocus(updateIP);
            return false;
        }

        return true;
    }

    // Method to check for proper email format
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to check for valid IP format
    private static boolean isValidIP(final String ip) { return IP_PATTERN.matcher(ip).matches(); }

    // Method to request focus
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
