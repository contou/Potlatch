package com.cong.potlatch.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.cong.potlatch.Config;
import com.cong.potlatch.data.model.Image;
import com.cong.potlatch.util.AccountUtils;
import com.cong.potlatch.util.UploadUtils;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 11/8/14.
 */
public class UploadImageRequest<T extends Image> extends JsonRequest<T> {
    private static final String TAG = makeLogTag(UploadImageRequest.class);
    private final Class<T> mResponseType;
    private final String mFile;
    private final Context mContext;

    public UploadImageRequest(Context mContext, String url, String mFile, Class<T> responseType, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, requestBody, listener, errorListener);
        this.mResponseType = responseType;
        this.mContext = mContext;
        this.mFile = mFile;
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String accessToken = AccountUtils.getAuthToken(mContext);
        if(accessToken == null) {
            throw new AuthFailureError("Don't have accessToken! Please login!");
        }
        HashMap<String,String> header = new HashMap<String, String>();
        header.put("Authorization","Bearer "+ accessToken);
        LOGD(TAG,accessToken);
        return header;
    }
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Gson gson = new Gson();
            T t = gson.fromJson(jsonString,mResponseType);
            UploadUtils.upload(mContext, Config.BASE_URL+t.getImage(), mFile);
            LOGD(TAG, String.valueOf(t.getImage()));

            return  Response.success(t,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}