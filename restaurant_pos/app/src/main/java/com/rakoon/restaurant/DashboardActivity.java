package com.rakoon.restaurant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DashboardActivity extends AppCompatActivity {
    @BindView(R.id.deliveryimg)
    ImageView deliveryimg;
    @BindView(R.id.pickupimg)
    ImageView pickupimg;
    @BindView(R.id.reservationimg)
    ImageView reservationimg;
    @BindView(R.id.discountimg)
    ImageView discountimg;

    @BindView(R.id.deliverytext)
    TextView deliverytext;
    @BindView(R.id.pickuptext)
    TextView pickuptext;
    @BindView(R.id.reservationtext)
    TextView reservationtext;
    @BindView(R.id.discounttext)
    TextView discounttext;
    @BindView(R.id.mainlay)
    LinearLayout mainlay;
    @BindView(R.id.homelay)
    LinearLayout homelay;
    @BindView(R.id.settinglay)
    LinearLayout settinglay;
    @BindView(R.id.adduserlay)
    LinearLayout adduserlay;
    @BindView(R.id.calllay)
    LinearLayout calllay;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.whatisplan)
    TextView whatisplan;

    Unbinder unbinder;
    AlertDialog alertDialog;
    AllPath apath;
    long back_pressed;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + getResources().getInteger(R.integer.delay) > System.currentTimeMillis()) {
            finish();
        } else {
            String msgg = "";
            if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.english)))
                msgg = getString(R.string.backmessage_en);
            else if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                msgg = getString(R.string.backmessage_ar);


            Toast.makeText(getBaseContext(), msgg,
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        unbinder = ButterKnife.bind(this);

        apath = new AllPath();

        if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            whatisplan.setText(getString(R.string.whatisplan_en));
        } else if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            whatisplan.setText(getString(R.string.whatisplan_ar));
        }


        if (apath.isInternetOn(DashboardActivity.this))
            getDashboard();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                msg = getResources().getString(R.string.internet_message_ar);
                str_action = getResources().getString(R.string.enable_ar);
            }
            Snackbar snackbar=Snackbar.make(mainlay, msg, Snackbar.LENGTH_LONG)
                    .setAction(str_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // put your logic here
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    });

            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            });
        }
        deliveryimg.setOnClickListener((v) -> {
            onMenuClick(0,deliverytext.getText().toString());
        });
        deliverytext.setOnClickListener((v) -> {
            onMenuClick(0,deliverytext.getText().toString());
        });

        pickupimg.setOnClickListener((v) -> {
            onMenuClick(1,pickuptext.getText().toString());
        });
        pickuptext.setOnClickListener((v) -> {
            onMenuClick(1,pickuptext.getText().toString());
        });

        reservationimg.setOnClickListener((v) -> {
            onMenuClick(2,reservationtext.getText().toString());
        });
        reservationtext.setOnClickListener((v) -> {
            onMenuClick(2,reservationtext.getText().toString());
        });

        discountimg.setOnClickListener((v) -> {
            onMenuClick(3,discounttext.getText().toString());
        });
        discounttext.setOnClickListener((v) -> {
            onMenuClick(3,discounttext.getText().toString());
        });
        calllay.setOnClickListener((v) ->
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0123456789"));
            startActivity(intent);
        });
        adduserlay.setOnClickListener((v) ->
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=com.abacus.ontrack");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
        settinglay.setOnClickListener((v) ->
        {

        });
        homelay.setOnClickListener((v) ->
        {

        });
        menu.setOnClickListener((v) ->
        {
            //onMenuClick(10);
        });

    }

    void getDashboard() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(DashboardActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.DASHBOARD), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            Log.e("response", response + "  das ");
                            JSONArray jarr = jobj.optJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = jarr.optJSONObject(i);
                                if (i == 0) {
                                    deliverytext.setTag(job.opt("id"));
                                    deliverytext.setText(job.optString("name"));
                                    ImageRequest request = new ImageRequest(job.optString("photo"),
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    deliveryimg.setImageBitmap(bitmap);
                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                public void onErrorResponse(VolleyError error) {
                                                    deliveryimg.setImageResource(R.drawable.ic_launcher_foreground);
                                                }
                                            });
                                    Singleton.getIntance(DashboardActivity.this).addToRequestQueue(request);
                                }
                                if (i == 1) {
                                    pickuptext.setTag(job.opt("id"));
                                    pickuptext.setText(job.optString("name"));
                                    ImageRequest request = new ImageRequest(job.optString("photo"),
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    pickupimg.setImageBitmap(bitmap);
                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                public void onErrorResponse(VolleyError error) {
                                                    pickupimg.setImageResource(R.drawable.ic_launcher_foreground);
                                                }
                                            });
                                    Singleton.getIntance(DashboardActivity.this).addToRequestQueue(request);
                                }
                                if (i == 2) {
                                    reservationtext.setTag(job.opt("id"));
                                    reservationtext.setText(job.optString("name"));
                                    ImageRequest request = new ImageRequest(job.optString("photo"),
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    reservationimg.setImageBitmap(bitmap);
                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                public void onErrorResponse(VolleyError error) {
                                                    reservationimg.setImageResource(R.drawable.ic_launcher_foreground);
                                                }
                                            });
                                    Singleton.getIntance(DashboardActivity.this).addToRequestQueue(request);
                                }
                                if (i == 3) {
                                    discounttext.setTag(job.opt("id"));
                                    discounttext.setText(job.optString("name"));
                                    ImageRequest request = new ImageRequest(job.optString("photo"),
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    discountimg.setImageBitmap(bitmap);
                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                public void onErrorResponse(VolleyError error) {
                                                    discountimg.setImageResource(R.drawable.ic_launcher_foreground);
                                                }
                                            });
                                    Singleton.getIntance(DashboardActivity.this).addToRequestQueue(request);
                                }
                            }

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    alertDialog.cancel();
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        Log.e("res data", new String(response.data));
                        try {
                            JSONObject jobj = new JSONObject(new String(response.data));
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {
                        if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(DashboardActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(DashboardActivity.this));


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
            Singleton.getIntance(DashboardActivity.this).addToRequestQueue(sr);

        /*    sr.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            queue.add(sr);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void onMenuClick(int position,String message) {
        switch (position) {
            case 0:
                Intent indelivery = new Intent(DashboardActivity.this, DeliveryLocationActivity.class);
                indelivery.putExtra("message",message);
                startActivity(indelivery);
                break;
            case 1:
                Intent inpickup = new Intent(DashboardActivity.this, PickupBranchActivity.class);
                inpickup.putExtra("message",message);
                startActivity(inpickup);
                break;
            case 2:
                Intent inreserv = new Intent(DashboardActivity.this, ReservationActivity.class);
                inreserv.putExtra("message",message);
                startActivity(inreserv);
                break;
            case 3:
                Intent indiscount= new Intent(DashboardActivity.this, DiscountActivity.class);
                indiscount.putExtra("message",message);
                startActivity(indiscount);
                break;

            default:
                Toast.makeText(DashboardActivity.this, "clicked", Toast.LENGTH_LONG).show();
        }
    }

}
