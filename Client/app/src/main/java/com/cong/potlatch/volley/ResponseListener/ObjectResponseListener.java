package com.cong.potlatch.volley.ResponseListener;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cong.potlatch.data.DataHandler;
import com.cong.potlatch.data.JSONHandler;
import com.cong.potlatch.ui.LoginActivity;
import com.cong.potlatch.ui.RegisterActivity;
import com.solo.cong.potlatch.R;

import java.util.ArrayList;

import static com.cong.potlatch.util.LogUtils.LOGD;

/**
 * Created by cong on 11/19/14.
 */
public class ObjectResponseListener<T>  implements Response.ErrorListener,Response.Listener<T>{
    private static final String TAG = "Volley error";
    Context mContext;
    JSONHandler mJsonHandler;
    boolean mIsNew;

    public ObjectResponseListener(Context mContext, JSONHandler mJsonHandler,boolean isNew) {
        this.mContext = mContext;
        this.mJsonHandler = mJsonHandler;
        this.mIsNew = isNew;
    }

    @Override
    public void onResponse(T t) {
        ArrayList<ContentProviderOperation> list = new ArrayList<ContentProviderOperation>();
        mJsonHandler.build(mIsNew, t, list);
        DataHandler dataHandler = new DataHandler(mContext, mJsonHandler);
        dataHandler.applyBatch(list);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if(volleyError.networkResponse != null)
        switch (volleyError.networkResponse.statusCode/100) {
            case 4:
                LOGD(TAG, "Complaining about missing account.");
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.account_required_title)
                        .setMessage(R.string.account_required_message)
                        .setNegativeButton(R.string.action_register, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(mContext, RegisterActivity.class);
                                mContext.startActivity(i);
                            }
                        })
                        .setPositiveButton(R.string.action_login, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(mContext, LoginActivity.class);
                                mContext.startActivity(i);
                            }
                        })
                        .setNeutralButton(R.string.not_now, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            default:
                break;

        }

    }
}
