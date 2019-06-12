package com.rakoon.restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.rakoon.restaurant.datasource.DataBaseHelper;
import com.rakoon.restaurant.model.Branch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PickupBranchActivity extends AppCompatActivity {
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.areaspinner)
    Spinner areaspinner;
    @BindView(R.id.listv)
    ListView listv;
    @BindView(R.id.mainlay)
    LinearLayout mainlay;
    @BindView(R.id.rakoonlocation)
    TextView rakoonlocation;
    @BindView(R.id.rakoonservice)
    TextView rakoonservice;

    Unbinder unbinder;
    AllPath apath;
    AlertDialog alertDialog;
    ArrayList<Branch> branchlist;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_branch);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        branchlist = new ArrayList<>();
        if (apath.isInternetOn(PickupBranchActivity.this))
            getBranchLocation();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
        if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            rakoonlocation.setText(getString(R.string.rakoonlocation_en));
            rakoonlocation.setGravity(Gravity.CENTER);
            rakoonservice.setText(getString(R.string.rakoonservice_en));
            rakoonservice.setGravity(Gravity.CENTER);
        } else if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            rakoonlocation.setText(getString(R.string.rakoonlocation_pickup_ar));
            rakoonlocation.setGravity(Gravity.RIGHT);
            rakoonservice.setText(getString(R.string.rakoonservice_pickup_ar));
            rakoonservice.setGravity(Gravity.RIGHT);
        }
    }

    void getBranchLocation() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(PickupBranchActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(PickupBranchActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.ALL_BRANCHES), new Response.Listener<String>() {
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
                                JSONArray arrJson = job.getJSONArray("payment_type");
                                String[] payment_type = new String[arrJson.length()];
                                for (int j = 0; j < arrJson.length(); j++) {
                                    payment_type[j] = arrJson.getString(j);
                                    Log.e("string arra ", payment_type[j] + " value");
                                }
                                branchlist.add(new Branch(job.optString("id"), job.optString("name"),
                                        job.optString("open_validation"), job.optString("close_validation"), job.optString("logo"), job.optString("address"), payment_type, job.optString("phone"),
                                        job.optString("open"), job.optString("close")));                            }
                            areaspinner.setAdapter(new BranchAdap());
                            listv.setAdapter(new BranchAdap());
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(PickupBranchActivity.this));
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
            Singleton.getIntance(PickupBranchActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class BranchAdap extends BaseAdapter {

        @Override
        public int getCount() {
            return branchlist.size();
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
            View vv = getLayoutInflater().inflate(R.layout.pickup_adap, null);
            Branch branch = branchlist.get(i);
            TextView name = vv.findViewById(R.id.name);
            TextView address = vv.findViewById(R.id.address);
            TextView open = vv.findViewById(R.id.open);
            TextView close = vv.findViewById(R.id.close);
            TextView opentext = vv.findViewById(R.id.opentext);
            TextView closetext = vv.findViewById(R.id.closetext);
            ImageView logo = vv.findViewById(R.id.logo);

            if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                name.setGravity(Gravity.LEFT);
                address.setGravity(Gravity.LEFT);
                open.setGravity(Gravity.LEFT);
                close.setGravity(Gravity.LEFT);
                opentext.setGravity(Gravity.LEFT);
                closetext.setGravity(Gravity.LEFT);
                opentext.setText(getString(R.string.open_en));
                closetext.setText(getString(R.string.close_en));
            } else if (apath.getLanguage(PickupBranchActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                name.setGravity(Gravity.RIGHT);
                address.setGravity(Gravity.RIGHT);
                open.setGravity(Gravity.RIGHT);
                close.setGravity(Gravity.RIGHT);
                opentext.setGravity(Gravity.RIGHT);
                closetext.setGravity(Gravity.RIGHT);
                opentext.setText(getString(R.string.open_ar));
                closetext.setText(getString(R.string.close_ar));
            }

            name.setText(branch.getName());
            address.setText(branch.getAddress() + "\n" + branch.getPhone());
            open.setText(branch.getOpen_lang());
            close.setText(branch.getClose_lang());
            ImageRequest request = new ImageRequest(branch.getLogo(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            logo.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            logo.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                    });
            Singleton.getIntance(PickupBranchActivity.this).addToRequestQueue(request);
            vv.setOnClickListener((v) -> {
                DataBaseHelper dbh = new DataBaseHelper(PickupBranchActivity.this);
                boolean isinserted = dbh.insertBranch(branch, apath.getUserid(PickupBranchActivity.this));
                Log.e("br from db", isinserted + " insert status " + branch.getArr());
                Branch baa = dbh.getBranch(apath.getUserid(PickupBranchActivity.this));
                SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("branch_id", branch.getId());
                editor.putString("branch_name", branch.getName());
                editor.putString("branch_open", branch.getOpen());
                editor.putString("branch_close", branch.getClose());
                editor.putString("branch_address", branch.getAddress());
                editor.putString("branch_phone", branch.getPhone());
                editor.putString("picker", apath.getName(PickupBranchActivity.this));
                editor.putString("branch_open_lang", branch.getOpen_lang());
                editor.putString("branch_close_lang", branch.getClose_lang());
                editor.commit();

                if (baa != null)
                    Log.e("br from db", baa.getName() + " ,   ==  " + TextUtils.join(",", baa.getArr()));
                else
                    Log.e("br from db", "Null");
                Intent in = new Intent(PickupBranchActivity.this, CategoryActivity.class);
                in.putExtra("branch_id", branch.getId());
                startActivity(in);
                finish();
            });
            return vv;
        }
    }

}
