package hydrocoolapps.waterwise.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hydrocoolapps.waterwise.R;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kinvey.android.AsyncUserDiscovery;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.callback.KinveyUserListCallback;
import com.kinvey.java.User;
import com.kinvey.java.model.UserLookup;


public class WelcomeActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private EditText inputEmail, inputPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private Button btnSignUp, btnSignIn;
    private SignInButton gSignIn;
    private GoogleSignInAccount gAccount;

    private SharedPreferences prefs;
    private String plantTitle, plantDescription;
    private int plantImageId;

    private static final int G_SIGN_IN_KEY = 1001;
    private static final String G_PLUS_PASS = "ntg;biunwertSPH;IJ01";

    private ProgressDialog progressDialog;
    private Client mKinveyClient;
    private AsyncUserDiscovery users;
    private UserLookup criteria;
    private String email;
    private String pass;

    private String gClientID;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInResult result;
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        prefs = getSharedPreferences("WaterWise", 0);
        context = getApplicationContext();

        // Get the Kinvey client
        mKinveyClient = SplashActivity.getClient();

        users = mKinveyClient.userDiscovery();
        criteria = users.userLookup();

        // OAuth 2.0 client ID
        gClientID = "130034760070-gfi6m2u5ie8j5c4oi6f68gsdv51es78m.apps.googleusercontent.com";

        // Set the options for the google client
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(gClientID)
                .build();

        // Set up the api client, this may cause an error, the connectionFailedListener is just a dummy listener
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.title_welcome);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnSignIn = (Button) findViewById(R.id.btn_signin);
        gSignIn = (SignInButton) findViewById(R.id.g_signin);

        // connect click listeners to the buttons
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = inputEmail.getText().toString();
                pass = inputPassword.getText().toString();

                if (validateEmail(email) && validatePassword(pass)) {

                    // Setting up a dialog to show the user while sign up is processing
                    progressDialog =  new ProgressDialog(WelcomeActivity.this,
                            R.style.AppTheme_Dark_Dialog);

                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();

                    mKinveyClient.user().create(email, pass,
                            new KinveyUserCallback() {
                                @Override
                                public void onFailure(Throwable t) {

                                    // Going to check to see if the e-mail already exists
                                    criteria.setEmail(email);

                                    // Dismiss dialog
                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() { progressDialog.dismiss(); }
                                            }, 1000);

                                    // Looking up users on the server
                                    users.lookup(criteria, new KinveyUserListCallback() {
                                        @Override
                                        public void onSuccess(User[] result) {
                                            CharSequence text = "That e-mail is already in use";
                                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Throwable error) {
                                            CharSequence text = "User creation failed, please try again";
                                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onSuccess(User u) {

                                    // Setting default account data
                                    mKinveyClient.user().put("plantTitle", getString(R.string.plant_info_heading));
                                    mKinveyClient.user().put("plantDescription", getString(R.string.plant_info_description));
                                    mKinveyClient.user().put("plantImageId", R.drawable.ic_placeholder2_img);
                                    mKinveyClient.user().put("enableNotifications", false);

                                    mKinveyClient.user().update(new KinveyUserCallback() {
                                        @Override
                                        public void onSuccess(User user) {

                                            getDatabaseInfo(user);

                                            // Dismiss dialog and go to the new activity
                                            new android.os.Handler().postDelayed(
                                                    new Runnable() {
                                                        public void run() {

                                                            progressDialog.dismiss();

                                                            CharSequence text = mKinveyClient.user().getUsername() + "\nYour account has been created.";
                                                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                                                            Intent intent = new Intent(context, SystemActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }, 2000);
                                        }

                                        @Override
                                        public void onFailure(Throwable throwable) { }
                                    });
                                }
                            });
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = inputEmail.getText().toString();
                pass = inputPassword.getText().toString();

                if (validateEmail(email) && validatePassword(pass)) {

                    // Setting up a dialog to show the user while sign up is processing
                    progressDialog =  new ProgressDialog(WelcomeActivity.this,
                            R.style.AppTheme_Dark_Dialog);

                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();

                    mKinveyClient.user().login(email, pass,
                            new KinveyUserCallback() {
                                @Override
                                public void onFailure(Throwable t) {

                                    // Dismiss dialog
                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() { progressDialog.dismiss(); }
                                            }, 1000);

                                    CharSequence text = "Wrong username or password.";
                                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(User u) {

                                    getDatabaseInfo(u);

                                    // Dismiss dialog
                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {

                                                    progressDialog.dismiss();

                                                    Intent intent = new Intent(context, SystemActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            }, 2000);
                                }
                            });
                }
            }
        });

        // Set onClick listener for the google sign in button

        gSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Launch a window to allow the user to choose the account they'd like to use
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, G_SIGN_IN_KEY);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == G_SIGN_IN_KEY) {
            result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGSignInResult(result);
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private boolean validateEmail(String email) {

        inputLayoutEmail.setErrorEnabled(false);

        if(email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            inputLayoutEmail.setErrorEnabled(true);
            requestFocus(inputEmail);
            return false;
        }

        return true;
    }

    private boolean validatePassword(String pass) {

        inputLayoutPassword.setErrorEnabled(false);

        if (pass.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            inputLayoutPassword.setErrorEnabled(true);
            requestFocus(inputPassword);
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void handleGSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            gAccount = result.getSignInAccount();

            // Pass the users auth id token to Kinvey
            mKinveyClient.user().login(gAccount.getDisplayName(), G_PLUS_PASS,
                    new KinveyUserCallback() {
                        @Override
                        public void onFailure(Throwable t) {

                            mKinveyClient.user().create(gAccount.getDisplayName(), G_PLUS_PASS,
                                    new KinveyUserCallback() {
                                        @Override
                                        public void onFailure(Throwable t) {
                                            // Heroes never fail
                                        }

                                        @Override
                                        public void onSuccess(User u) {

                                            // Setting default account data
                                            mKinveyClient.user().put("plantTitle", getString(R.string.plant_info_heading));
                                            mKinveyClient.user().put("plantDescription", getString(R.string.plant_info_description));
                                            mKinveyClient.user().put("plantImageId", R.drawable.ic_placeholder2_img);
                                            mKinveyClient.user().put("enableNotifications", false);

                                            mKinveyClient.user().update(new KinveyUserCallback() {
                                                @Override
                                                public void onSuccess(User user) {

                                                    getDatabaseInfo(user);

                                                    // Dismiss dialog and go to the new activity
                                                    new android.os.Handler().postDelayed(
                                                            new Runnable() {
                                                                public void run() {

                                                                    CharSequence text = mKinveyClient.user().getUsername() + "\nYour account has been created.";
                                                                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent(context, SystemActivity.class);
                                                                    startActivity(intent);
                                                                    finish();

                                                                }
                                                            }, 2000);
                                                }

                                                @Override
                                                public void onFailure(Throwable throwable) { }
                                            });
                                        }
                                    });
                        }

                        @Override
                        public void onSuccess(User u) {
                            getDatabaseInfo(u);

                            Intent intent = new Intent(context, SystemActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }

        else {
            CharSequence text = "G+ failed to authenticate";
            System.out.println(text);
        }
    }

    private void getDatabaseInfo(User u) {
        // Getting account database information
        plantTitle = u.get("plantTitle").toString();
        plantDescription = u.get("plantDescription").toString();

        switch (plantTitle) {

            case "Lettuce":
                plantImageId = R.drawable.ic_lettuce_img;
                break;

            case "Tomatoes":
                plantImageId = R.drawable.ic_tomato_img;
                break;

            case "Thai Basil":
                plantImageId = R.drawable.ic_thai_basil_img;
                break;

            default:
                plantImageId = R.drawable.ic_placeholder2_img;
                break;
        }

        // Saving account database info to application storage
        prefs.edit().putString("plantTitle", plantTitle).commit();
        prefs.edit().putString("plantDescription", plantDescription).commit();
        prefs.edit().putInt("plantImageId", plantImageId).commit();
        prefs.edit().putBoolean("enableNotifications", (Boolean) u.get("enableNotifications")).commit();
    }

}
