package com.cong.potlatch.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cong.potlatch.Config;
import com.cong.potlatch.util.AccountUtils;
import com.cong.potlatch.volley.LoginRequest;
import com.cong.potlatch.volley.RequestManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends BaseActivity {


    private static final String TAG = makeLogTag(LoginActivity.class);

    private static final  String TOKEN_URL = Config.BASE_URL + "/oauth/token";
    // UI references.
    private TextView mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar mActionBarToolbar= getActionBarToolbar();
        mActionBarToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_up);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Login");
        mProgressDialog.setMessage("Getting account information for you!");
        mProgressDialog.setCanceledOnTouchOutside(false);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.user_name);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                showProgress(false);
            }
        });

    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            LOGD(TAG, "cancel");
            showProgress(true);
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            LoginRequest loginRequest = new LoginRequest(Request.Method.POST,TOKEN_URL,new Response.Listener<String>() {
                @Override
                public void onResponse(String body) {
                    LOGD(TAG,body);

                    //Login success
                    String accessToken = new Gson().fromJson(body, JsonObject.class).get("access_token").getAsString();
                    AccountUtils.setActiveAccount(getApplicationContext(), email);
                    AccountUtils.setAuthToken(getApplicationContext(), email, accessToken);
                    finish();
                }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    showProgress(false);
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                }
            });
            loginRequest.setmClientId("mobile");
            loginRequest.setmUsername(email);
            loginRequest.setmPassword(password);
            RequestManager.addRequest(loginRequest, this);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        if(email.equals("guest")) {
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean enable) {
        if(enable) {
            mProgressDialog.show();
        }else {
            mProgressDialog.dismiss();
        }
    }

}



