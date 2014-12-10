package com.cong.potlatch.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cong.potlatch.Config;
import com.cong.potlatch.data.model.AuthInfo;
import com.cong.potlatch.util.AccountUtils;
import com.cong.potlatch.volley.GsonRequestBuilder;
import com.cong.potlatch.volley.LoginRequest;
import com.cong.potlatch.volley.RequestManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

public class RegisterActivity extends BaseActivity {
    private String TAG = makeLogTag(RegisterActivity.class);
    private static final  String TOKEN_URL = Config.BASE_URL + "/oauth/token";
    // UI references.
    private TextView mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar mActionBarToolbar = getActionBarToolbar();
        mActionBarToolbar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_up);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateUpToFromChild(RegisterActivity.this,
                        IntentCompat.makeMainActivity(new ComponentName(RegisterActivity.this,
                                BrowseGiftsActivity.class)));
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Register");
        mProgressDialog.setMessage("Creating user account for you!");
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
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                showProgress(false);
            }
        });

        overridePendingTransition(0, 0);
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
        String email = mEmailView.getText().toString();
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
            focusView.requestFocus();
            showProgress(cancel);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            String uri = Config.BASE_URL + "/auth/register";
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, uri,
//                    toastResponseListener,
//                    toastResponseListener);
            AuthInfo authInfo = new AuthInfo(email,password,new String[]{"ADMIN","USER"});
            GsonRequestBuilder<AuthInfo> builder = new GsonRequestBuilder<AuthInfo>();
            builder.setContext(this)
            .setMethod(Request.Method.POST)
            .setUrl(uri)
            .setResponseType(AuthInfo.class)
            .setRequestBody(new Gson().toJson(authInfo));

            builder.setErrorListener(new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                  showProgress(false);
                  mPasswordView.setError(getString(R.string.error_invalid_password));
                }
            });

            builder.setListener(new Response.Listener<AuthInfo>() {
                @Override
                public void onResponse(final AuthInfo authInfo) {
                    LoginRequest loginRequest = new LoginRequest(Request.Method.POST,TOKEN_URL,new Response.Listener<String>() {
                        @Override
                        public void onResponse(String body) {
                            LOGD(TAG,body);

                            //Login success
                            String accessToken = new Gson().fromJson(body, JsonObject.class).get("access_token").getAsString();
                            AccountUtils.setActiveAccount(getApplicationContext(), authInfo.getUsername());
                            AccountUtils.setAuthToken(getApplicationContext(), authInfo.getUsername(), accessToken);
                            finish();
                        }
                    },new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                        }
                    });
                    loginRequest.setmClientId("mobile");
                    loginRequest.setmUsername(authInfo.getUsername());
                    loginRequest.setmPassword(authInfo.getPassword());
                    RequestManager.addRequest(loginRequest, this);
                }
            });

            RequestManager.addRequest(builder.build(), this);

        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
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
