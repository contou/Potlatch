package com.cong.potlatch.volley.ResponseListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cong.potlatch.ui.LoginActivity;
import com.cong.potlatch.ui.RegisterActivity;
import com.solo.cong.potlatch.R;

import static com.cong.potlatch.util.LogUtils.LOGD;

/**
 * Created by cong on 11/10/14.
 */
public class ToastResponseListener implements Response.ErrorListener,Response.Listener {
    private static final String TAG = ToastResponseListener.class.getCanonicalName();
    Context mContext;

    public ToastResponseListener(Context mContext) {
        this.mContext = mContext;
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

    @Override
    public void onResponse(Object o) {
        String message = mContext.getResources().getString(R.string.success);
        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
    }
}
