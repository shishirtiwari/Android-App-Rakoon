package com.rakoon.restaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.rakoon.restaurant.model.Discount;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DiscountActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.mainlay)
    ConstraintLayout mainlay;
//    @BindView(R.id.title)
//    TextView title;


    AlertDialog alertDialog;
    AllPath apath;
    Unbinder unbinder;
    ArrayList<Discount> discountlist;
    RecyclerViewAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        getSupportActionBar().setTitle(getIntent().getStringExtra("message"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        discountlist = new ArrayList<>();
        int width = recyclerView.getWidth();   //  for always 1 column in grid
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(DiscountActivity.this, width);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(DiscountActivity.this, discountlist);
        recyclerView.setAdapter(adapter);
        //  title.setText(getIntent().getStringExtra("message"));
        if (apath.isInternetOn(DiscountActivity.this))
            getDiscountList();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(DiscountActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(DiscountActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
//        title.setOnClickListener((v) -> {
//            finish();
//        });

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        ArrayList<Discount> mValues;
        Context mContext;

        public RecyclerViewAdapter(Context context, ArrayList<Discount> values) {

            mValues = values;
            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView date, codetext, codevalue, pricetext, pricevalue, fromtext, fromvalue, totext, tovalue, feetext, feevalue;

            Discount item;

            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener((vi) -> {

                });

                date = v.findViewById(R.id.date);
                codetext = v.findViewById(R.id.codetext);
                codevalue = v.findViewById(R.id.codevalue);
                pricetext = v.findViewById(R.id.pricetext);
                pricevalue = v.findViewById(R.id.pricevalue);
                fromtext = v.findViewById(R.id.fromtext);
                fromvalue = v.findViewById(R.id.fromvalue);
                totext = v.findViewById(R.id.totext);
                tovalue = v.findViewById(R.id.tovalue);
                feetext = v.findViewById(R.id.feetext);
                feevalue = v.findViewById(R.id.feevalue);

            }

            public void setData(Discount item) {
                this.item = item;
                if (apath.getLanguage(DiscountActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    codetext.setText(getString(R.string.discountcode_en));
                    pricetext.setText(getString(R.string.discountprice_en));
                    fromtext.setText(getString(R.string.validstartfrom_en));
                    totext.setText(getString(R.string.validendto_en));
                    feetext.setText(getString(R.string.deliveryfee_en));
                } else if (apath.getLanguage(DiscountActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    codetext.setText(getString(R.string.discountcode_ar));
                    pricetext.setText(getString(R.string.discountprice_ar));
                    fromtext.setText(getString(R.string.validstartfrom_ar));
                    totext.setText(getString(R.string.validendto_ar));
                    feetext.setText(getString(R.string.deliveryfee_ar));
                }
                date.setText(item.getStartdate());
                codevalue.setText(item.getCoupon_code());
                pricevalue.setText(item.getFee());
                fromvalue.setText(item.getStartdate());
                tovalue.setText(item.getEnddate());
                feevalue.setText(item.getFee());
                //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.discount_adap, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            Vholder.setData(mValues.get(position));
        }

        @Override
        public int getItemCount() {

            return mValues.size();
        }
    }

    void getDiscountList() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(DiscountActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(DiscountActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.DISCOUNT), new Response.Listener<String>() {
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
                                discountlist.add(new Discount(job.optString("id"), job.optString("name"),
                                        job.optString("startdate"), job.optString("enddate"),
                                        job.optString("coupon_code"), job.optString("branch"), job.optString("fee")
                                        , job.optString("discount_rate"), job.optString("status")));
                            }

                            adapter = new RecyclerViewAdapter(DiscountActivity.this, discountlist);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(DiscountActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(DiscountActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(DiscountActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(DiscountActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(DiscountActivity.this));
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
            Singleton.getIntance(DiscountActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
