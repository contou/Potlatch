package com.cong.potlatch.volley;

import android.content.Context;

import com.android.volley.Response;

public class GsonRequestBuilder<T> {
    private Context context;
    private int method;
    private String url;
    private Class<T> responseType;
    private String requestBody;
    private Response.Listener<T> listener;
    private Response.ErrorListener errorListener;

    public GsonRequestBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public GsonRequestBuilder setMethod(int method) {
        this.method = method;
        return this;
    }

    public GsonRequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public GsonRequestBuilder setResponseType(Class<T> responseType) {
        this.responseType = responseType;
        return this;
    }

    public GsonRequestBuilder setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public GsonRequestBuilder setListener(Response.Listener<T> listener) {
        this.listener = listener;
        return this;
    }

    public GsonRequestBuilder setErrorListener(Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    public GsonRequest build() {
        return new GsonRequest(context, method, url, responseType, requestBody, listener, errorListener);
    }
}