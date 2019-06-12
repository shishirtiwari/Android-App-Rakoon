package com.rakoon.restaurant.fcm;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rakoon.restaurant.AllPath;
import com.rakoon.restaurant.R;
import com.rakoon.restaurant.Singleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AnaadIT on 3/30/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public int i = 0;
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        if ((refreshedToken != null || refreshedToken != "") && i == 0)
            sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        /*SavePref pref1 = new SavePref();
        pref1.SavePref(getApplicationContext());
        pref1.setdToken(token);*/

        SharedPreferences pref = getApplicationContext().getSharedPreferences("rakoontoken", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("tokenid", token);
        editor.commit();
        updateToken(token);
        // Log.e(TAG, "db token token123456: " +pref1.getdToken());
        i = 1;
    }

    void updateToken(String token) {
        AllPath apath = new AllPath();
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.USER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        Log.e("response", response + "  service srrr token ");

                    } catch (Exception e) {

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        Log.e("res data token", new String(response.data));
                        try {

                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
                    String lang = "";
                    if (sp.getString("lang", "").equalsIgnoreCase(getResources().getString(R.string.english)))
                        lang = "en";
                    else if (sp.getString("lang", "").equalsIgnoreCase(getResources().getString(R.string.arabic)))
                        lang = "ar";
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", lang);
                    params.put("action", "user_token");
                    params.put("user_id", sp.getString("userid", ""));
                    params.put("device_token", token);
                    Log.e("params ", params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Singleton.getIntance(getApplicationContext()).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
