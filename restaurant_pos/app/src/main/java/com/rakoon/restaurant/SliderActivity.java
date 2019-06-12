package com.rakoon.restaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

public class SliderActivity extends AppCompatActivity {

    ViewPager brandPager;
    CircleIndicator indicator;
    AlertDialog alertDialog;
    AllPath apath;
    ArrayList<String> images;
    private long back_pressed;
    ConstraintLayout imageslay;
    TextView skip;

    @Override
    public void onBackPressed() {
        if (back_pressed + getResources().getInteger(R.integer.delay) > System.currentTimeMillis()) {
            finish();
        } else {
            String msgg = "";
            if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.english)))
                msgg = getString(R.string.backmessage_en);
            else if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                msgg = getString(R.string.backmessage_ar);


            Toast.makeText(getBaseContext(), msgg,
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        apath = new AllPath();
        images = new ArrayList<>();

        imageslay = findViewById(R.id.mainlay);
        brandPager = (ViewPager) findViewById(R.id.brandpager);
        indicator = (CircleIndicator) findViewById(R.id.brandindicator);
        skip = findViewById(R.id.skip);

        if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            skip.setText(getString(R.string.skip_en));
            skip.setGravity(Gravity.CENTER);
        } else if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            skip.setText(getString(R.string.skip_ar));

        }

        skip.setOnClickListener((v) -> {
            Intent in = new Intent(SliderActivity.this, DashBoardActivity2.class);
            startActivity(in);
            finish();
        });


        if (apath.isInternetOn(SliderActivity.this))
            getImages();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                msg = getResources().getString(R.string.internet_message_ar);
                str_action = getResources().getString(R.string.enable_ar);
            }

            Snackbar snackbar = Snackbar.make(imageslay, msg, Snackbar.LENGTH_LONG)
                    .setAction(str_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // put your logic here
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    });

            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            });
        }
        SharedPreferences pref = getApplicationContext().getSharedPreferences("rakoontoken", 0);
        Log.e("tokenid", pref.getString("tokenid", ""));

    }

    class CustomPageAdapter extends PagerAdapter {
        private Context context;
        private LayoutInflater layoutInflater;

        public CustomPageAdapter(Context context) {
            this.context = context;
            this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = this.layoutInflater.inflate(R.layout.imageview, container, false);
            ImageView imagev = (ImageView) view.findViewById(R.id.image);

            ImageRequest request = new ImageRequest(images.get(position),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            imagev.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            imagev.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                    });
            Singleton.getIntance(SliderActivity.this).addToRequestQueue(request);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    void getImages() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SliderActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(SliderActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.WELCOMEIMAGES), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        Log.e("response... ", response);
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("phone_num", jobj.optString("phone_num"));
                        editor.commit();

                        JSONArray jarr = jobj.optJSONArray("dummay_image");
                        for (int i = 0; i < jarr.length(); i++) {
                            images.add(jarr.optString(i));
                        }
                        brandPager.setAdapter(new CustomPageAdapter(SliderActivity.this));
                        indicator.setViewPager(brandPager);
                    } catch (Exception e) {
                        if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(imageslay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(imageslay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

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
                            Snackbar.make(imageslay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {
                        if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(imageslay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SliderActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(imageslay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    //  params.put("lang", apath.getLang(SliderActivity.this));
                    // params.put("branch_id", "");


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
            Singleton.getIntance(SliderActivity.this).addToRequestQueue(sr);

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
}
