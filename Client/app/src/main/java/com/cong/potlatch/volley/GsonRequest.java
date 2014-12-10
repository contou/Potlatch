package com.cong.potlatch.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.cong.potlatch.util.AccountUtils;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cong on 11/8/14.
 */
public class GsonRequest<T> extends JsonRequest<T> {
    private final Class<T> mResponseType;
    private final Map<String, String> mHeaders;
    private final Context mContext;

    public GsonRequest(Context context,int method, String url,Class<T> responseType, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        this.mResponseType = responseType;
        mHeaders = new HashMap<String, String>();
        mContext = context;
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String accessToken = AccountUtils.getAuthToken(mContext);
        if(accessToken == null) {
            throw new AuthFailureError("Don't have accessToken!");
        }
        mHeaders.put("Authorization","Bearer "+ accessToken);
        return mHeaders;
    }
    public void setHeader(String key,String value) {
        mHeaders.put(key,value);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Gson gson = new Gson();
            return  Response.success(gson.fromJson(jsonString,mResponseType),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
