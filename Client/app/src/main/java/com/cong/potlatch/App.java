package com.cong.potlatch;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.volley.VolleyUrlLoader;
import com.cong.potlatch.volley.RequestManager;

import java.io.InputStream;

/**
 * Created by storm on 14-3-24.
 */
public class App extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        Glide.get(this).register(GlideUrl.class, InputStream.class,
                new VolleyUrlLoader.Factory(RequestManager.mRequestQueue));
    }

    public static Context getContext() {
        return sContext;
    }

}
