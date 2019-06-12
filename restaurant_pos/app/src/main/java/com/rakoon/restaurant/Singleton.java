package com.rakoon.restaurant;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Singleton {
    private static Singleton myintance;
    private RequestQueue requestQueue;
    private static Context mCtx;


    private Singleton(Context context)
    {
        mCtx=context;
        requestQueue=getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
        {
            requestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized  Singleton getIntance(Context context)
    {
        if (myintance == null)
        {
            myintance=new Singleton(context);
        }
        return  myintance;
    }
    public <T> void addToRequestQueue(Request<T> request)
    {
        requestQueue.add(request);
    }
}
