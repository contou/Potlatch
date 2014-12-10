package com.cong.potlatch.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cong.potlatch.App;
import com.cong.potlatch.volley.toolbox.ExtHttpClientStack;
import com.cong.potlatch.volley.toolbox.unsafe.EasyHttpClient;
import com.solo.cong.potlatch.R;

import java.io.InputStream;


/**
 * Created by storm on 14-3-25.
 */
public class RequestManager {
    public static final InputStream keyStore = App.getContext().getResources().openRawResource(R.raw.test);

    public static RequestQueue mRequestQueue = Volley.newRequestQueue(App.getContext(),
            new ExtHttpClientStack(new EasyHttpClient())
    );

    private RequestManager() {
        // no instances
    }

    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}
