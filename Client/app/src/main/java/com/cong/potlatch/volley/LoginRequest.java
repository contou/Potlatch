package com.cong.potlatch.volley;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    //
    private String mUsername = "guest";
    private String mPassword = "pass";
    private String mClientId = "guest";
    private String mClientSecret = "";
    public LoginRequest(int method, String url, Response.Listener<String> listener,
                        Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username",mUsername);
        params.put("password", mPassword);
        params.put("client_id", mClientId);
        params.put("client_secret", mClientSecret);
        params.put("grant_type", "password");

        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        String base64Auth = Base64.encodeToString(new String(mClientId + ":" + mClientSecret).getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", "Basic " + base64Auth);
        return headers;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public void setmClientId(String mClientId) {
        this.mClientId = mClientId;
    }

    public void setmClientSecret(String mClientSecret) {
        this.mClientSecret = mClientSecret;
    }
}