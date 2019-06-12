package com.rakoon.restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rakoon.restaurant.datasource.DataBaseHelper;
import com.rakoon.restaurant.model.Area;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DeliveryLocationActivity extends AppCompatActivity {
    @BindView(R.id.close)
    ImageView close;

    @BindView(R.id.mainlay)
    LinearLayout mainlay;
    @BindView(R.id.findlay)
    LinearLayout findlay;
    @BindView(R.id.find)
    TextView find;
    @BindView(R.id.rakoonlocation)
    TextView rakoonlocation;
    @BindView(R.id.rakoonservice)
    TextView rakoonservice;
    @BindView(R.id.area)
    TextView area;

    Unbinder unbinder;
    AllPath apath;
    AlertDialog alertDialog;
    ArrayList<Area> arealist, searchlist;
    String areaid = "";
    long last_text_edit = 0;
    AlertDialog alert = null;
    Area selectedarea = null;
    AreaAdap adap;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_location);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        arealist = new ArrayList<>();

        if (apath.isInternetOn(DeliveryLocationActivity.this))
            getAreaLocation();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                msg = getResources().getString(R.string.internet_message_ar);
                str_action = getResources().getString(R.string.enable_ar);
            }

            Snackbar snackbar = Snackbar.make(mainlay, msg, Snackbar.LENGTH_LONG)
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
        close.setOnClickListener((v) -> {
            finish();
        });
        if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            find.setText(getString(R.string.find_en));
            findlay.setGravity(Gravity.CENTER);
            rakoonlocation.setText(getString(R.string.rakoonlocation_en));
            rakoonlocation.setGravity(Gravity.CENTER);
            rakoonservice.setText(getString(R.string.rakoonservice_en));
            rakoonservice.setGravity(Gravity.CENTER);
            area.setGravity(Gravity.CENTER);
            area.setHint(getString(R.string.search_en));
        } else if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            find.setText(getString(R.string.find_ar));
            findlay.setGravity(Gravity.RIGHT);
            rakoonlocation.setText(getString(R.string.rakoonlocation_delivery_ar));
            rakoonlocation.setGravity(Gravity.RIGHT);
            rakoonservice.setText(getString(R.string.rakoonservice_delivery_ar));
            rakoonservice.setGravity(Gravity.RIGHT);
            area.setGravity(Gravity.RIGHT);
            area.setHint(getString(R.string.search_ar));
        }

        findlay.setOnClickListener((v) -> {
            if (selectedarea != null) {
                DataBaseHelper dbh = new DataBaseHelper(DeliveryLocationActivity.this);
                boolean isinserted = dbh.insertArea(selectedarea.getId(), selectedarea.getName(), selectedarea.getDelivery(), selectedarea.getBranch_id(), apath.getUserid(DeliveryLocationActivity.this));
                Log.e("area from db",isinserted+" insert status");
                Area aa = dbh.getArea(apath.getUserid(DeliveryLocationActivity.this));
                if (aa != null)
                    Log.e("area from db", aa.getName());
                else
                    Log.e("area from db", "Null");
                SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("area_id", selectedarea.getId());
                editor.putString("area_name", selectedarea.getName());
                editor.putString("area_delivery", selectedarea.getDelivery());
                editor.putString("area_branchid", selectedarea.getBranch_id());
                editor.commit();

                Intent in = new Intent(DeliveryLocationActivity.this, CategoryActivity.class);
                in.putExtra("branch_id", selectedarea.getBranch_id());
                startActivity(in);
                finish();
            } else {
                if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.selectarea_msg_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.selectarea_msg_ar), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        area.setOnClickListener((v) -> {
            long delay = 200; // 1 seconds after user stops typing
            Handler handler = new Handler();
            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryLocationActivity.this);
            View av = getLayoutInflater().inflate(R.layout.delivery_alert, null);
            EditText areaedit = av.findViewById(R.id.areaedit);
            TextView textarea = av.findViewById(R.id.textarea);
            ImageView close = av.findViewById(R.id.close);
            ListView listv = av.findViewById(R.id.listv);

            builder.setView(av);
            builder.setCancelable(false);
            alert = builder.create();
            close.setOnClickListener((cl) -> {
                alert.cancel();
            });
            searchlist = arealist;
            adap = new AreaAdap();
            listv.setAdapter(adap);


            if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                areaedit.setGravity(Gravity.LEFT);
                areaedit.setHint(getString(R.string.search_en));
                textarea.setGravity(Gravity.LEFT);
                textarea.setText(getString(R.string.select_search_en));
            } else if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                areaedit.setGravity(Gravity.RIGHT);
                areaedit.setHint(getString(R.string.search_ar));
                textarea.setGravity(Gravity.RIGHT);
                textarea.setText(getString(R.string.select_search_ar));
            }

            Runnable input_finish_checker = new Runnable() {
                public void run() {
                    if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                        String searchtext = areaedit.getText().toString();
                        searchlist = new ArrayList<>();
                        for (int i = 0; i < arealist.size(); i++) {
                            Area ar = arealist.get(i);
                            if (ar.getName().contains(searchtext)) {
                                searchlist.add(ar);
                            }
                        }
                        adap.notifyDataSetChanged();
                        if (searchtext.length() == 0) {
                            searchlist = arealist;
                            adap.notifyDataSetChanged();
                        }
                    }
                }
            };

            areaedit.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count,
                                                                              int after) {
                                                }

                                                @Override
                                                public void onTextChanged(final CharSequence s, int start, int before,
                                                                          int count) {
                                                    //You need to remove this to run only once
                                                    handler.removeCallbacks(input_finish_checker);

                                                }

                                                @Override
                                                public void afterTextChanged(final Editable s) {
                                                    //avoid triggering event when text is empty
                                                    last_text_edit = System.currentTimeMillis();
                                                    handler.postDelayed(input_finish_checker, delay);
                                                }
                                            }

            );
            //  alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alert.show();
        });

    }

    void getAreaLocation() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryLocationActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(DeliveryLocationActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.AREA), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            Log.e("response", response + "  forgot ");
                            JSONArray jarr = jobj.optJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = jarr.optJSONObject(i);
                                arealist.add(new Area(job.optString("id"), job.optString("name"),
                                        job.optString("delivery"), job.optString("branch_id")));
                            }

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(DeliveryLocationActivity.this));
                    //  params.put("branch_id", "35");
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
            Singleton.getIntance(DeliveryLocationActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class AreaAdap extends BaseAdapter {

        @Override
        public int getCount() {
            return searchlist.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vv = getLayoutInflater().inflate(R.layout.textview, null);
            TextView text1 = vv.findViewById(R.id.text1);
            text1.setText(searchlist.get(i).getName());
            if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                text1.setGravity(Gravity.LEFT);
            } else if (apath.getLanguage(DeliveryLocationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                text1.setGravity(Gravity.RIGHT);
            }
            vv.setOnClickListener((vi) -> {
                selectedarea = searchlist.get(i);
                if (alert != null)
                    alert.cancel();
                area.setText(selectedarea.getName());
            });
            return vv;
        }
    }

}
