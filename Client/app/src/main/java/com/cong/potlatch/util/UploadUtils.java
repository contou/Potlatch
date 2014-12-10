package com.cong.potlatch.util;


import android.content.Context;
import android.content.Intent;

import com.cong.potlatch.ui.LoginActivity;
import com.cong.potlatch.volley.toolbox.unsafe.EasyHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.entity.mime.content.StringBody;

/**
 * Created by cong on 11/5/14.
 */
public class UploadUtils {
    public static String upload(Context context,String uri,String filePath){

        if(!AccountUtils.hasActiveAccount(context)) {
            Intent i = new Intent(context,LoginActivity.class);
            context.startActivity(i);
            return "";
        }
        String accessToken = AccountUtils.getAuthToken(context);
        HttpPost req = new HttpPost(uri);
        req.setHeader("Authorization","Bearer "+ accessToken);


        MultipartEntity entity = new MultipartEntity();
        File toUpload = new File(filePath);
        entity.addPart("image", new FileBody(toUpload));
        try {
            entity.addPart("token", new StringBody("12123214"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        req.setEntity(entity);
        HttpResponse response;
        try {
            response =  new EasyHttpClient().execute(req);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
