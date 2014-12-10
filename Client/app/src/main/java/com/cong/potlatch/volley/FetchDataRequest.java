package com.cong.potlatch.volley;

import android.content.Context;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.cong.potlatch.Config;
import com.cong.potlatch.data.DataHandler;
import com.cong.potlatch.data.JSONHandler;
import com.cong.potlatch.provider.GiftContract;
import com.cong.potlatch.util.AccountUtils;
import com.cong.potlatch.util.PrefUtils;
import com.cong.potlatch.util.TimeUtils;
import com.cong.potlatch.util.UIUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 11/9/14.
 */
public class FetchDataRequest extends StringRequest {


    private static final String TAG = makeLogTag(FetchDataRequest.class);
    Context mContext;
    DataHandler mDataHandler;
    JSONHandler mJsonHandler;

    public FetchDataRequest(Context mContext, JSONHandler mJsonHandler, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        this.mContext = mContext;
        this.mDataHandler = new DataHandler(mContext,mJsonHandler);
        this.mJsonHandler = mJsonHandler;
    }

    public FetchDataRequest(Context mContext, JSONHandler mJsonHandler, int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.mContext = mContext;
        this.mDataHandler = new DataHandler(mContext,mJsonHandler);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String accessToken = AccountUtils.getAuthToken(mContext);
        if(accessToken == null) {
            throw new AuthFailureError("Don't have accessToken!");
        }
        HashMap<String,String> header = new HashMap<String, String>();
        header.put("Authorization","Bearer "+ accessToken);
        LOGD(TAG,accessToken);
        return header;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//            LOGD(TAG,jsonString);
            mDataHandler.applyData(jsonString, TimeUtils.formatHumanFriendlyShortDate(mContext, UIUtils.getCurrentTime(mContext)),false);
            PrefUtils.markSyncSucceededNow(mContext,mJsonHandler.getClass().toString());
            mContext.getContentResolver().notifyChange(Uri.parse(GiftContract.CONTENT_AUTHORITY),
                    null, false);
            LOGD(TAG, "data bootstrap process done!");
            return  Response.success(jsonString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }
    }

}
