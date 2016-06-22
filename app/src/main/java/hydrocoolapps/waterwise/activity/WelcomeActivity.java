package hydrocoolapps.waterwise.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

    private static final int G_SIGN_IN_KEY = 1001;
    private static final String G_PLUS_PASS = "ntg;biunwertSPH;IJ01";

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

                    mKinveyClient.user().create(email, pass,
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
                                            CharSequence text = "User creation failed, please try again";
                                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onSuccess(User u) {
                                    CharSequence text = u.getUsername() + "\nyour account has been created.";
                                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(context, SystemActivity.class);
                                    startActivity(intent);
                                    finish();
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
                    mKinveyClient.user().login(email, pass,
                            new KinveyUserCallback() {
                                @Override
                                public void onFailure(Throwable t) {
                                    CharSequence text = "Wrong username or password.";
                                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(User u) {
                                    CharSequence text = "Welcome back!\n" + u.getUsername();
                                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(context, SystemActivity.class);
                                    startActivity(intent);
                                    finish();
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
                                            CharSequence text = u.getUsername() + "\nyour account has been created.";
                                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(context, SystemActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }

                        @Override
                        public void onSuccess(User u) {
                            CharSequence text = "Welcome back!\n" + u.getUsername();
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(context, SystemActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }

        else {
            CharSequence text = "G+ failed to authenticate";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

}
