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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.rakoon.restaurant.model.ExtraStuff;
import com.rakoon.restaurant.model.Order;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderDetailActivity extends AppCompatActivity {

    @BindView(R.id.mainlay)
    ConstraintLayout mainlay;

    @BindView(R.id.orderid)
    TextView orderid;
    @BindView(R.id.orderidvalue)
    TextView orderidvalue;
    @BindView(R.id.totalprice)
    TextView totalprice;
    @BindView(R.id.totalpricevalue)
    TextView totalpricevalue;
    @BindView(R.id.branch)
    TextView branch;
    @BindView(R.id.branchvalue)
    TextView branchvalue;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.datevalue)
    TextView datevalue;
    @BindView(R.id.subtotal)
    TextView subtotal;
    @BindView(R.id.subtotalvalue)
    TextView subtotalvalue;
    @BindView(R.id.servicetax)
    TextView servicetax;
    @BindView(R.id.servicetaxvalue)
    TextView servicetaxvalue;
    @BindView(R.id.delivery)
    TextView delivery;
    @BindView(R.id.deliveryvalue)
    TextView deliveryvalue;
    @BindView(R.id.discount)
    TextView discount;
    @BindView(R.id.discountvalue)
    TextView discountvalue;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.discountlay)
    LinearLayout discountlay;
    @BindView(R.id.deliverylay)
    LinearLayout deliverylay;

    Unbinder unbinder;
    AllPath apath;
    Order order;
    AlertDialog alertDialog;
    ArrayList<ExtraStuff> itemlist;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        order = (Order) getIntent().getSerializableExtra("order");
        itemlist = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle("Order Detail");
            orderid.setText(getResources().getString(R.string.order_id_en));
            totalprice.setText(getResources().getString(R.string.totalprice_en));
            branch.setText(getResources().getString(R.string.branch_en));
            date.setText(getResources().getString(R.string.date_en));
            subtotal.setText(getResources().getString(R.string.subtotal_en));
            servicetax.setText(getResources().getString(R.string.tax_en));
            delivery.setText(getResources().getString(R.string.deliveryfee_en));
            discount.setText(getResources().getString(R.string.discount_en));
        } else if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle("Order Detail");
            orderid.setText(getResources().getString(R.string.order_id_ar));
            totalprice.setText(getResources().getString(R.string.totalprice_ar));
            branch.setText(getResources().getString(R.string.branch_ar));
            date.setText(getResources().getString(R.string.date_ar));
            subtotal.setText(getResources().getString(R.string.subtotal_ar));
            servicetax.setText(getResources().getString(R.string.tax_ar));
            delivery.setText(getResources().getString(R.string.deliveryfee_ar));
            discount.setText(getResources().getString(R.string.discount_ar));
        }

        if (apath.isInternetOn(OrderDetailActivity.this)) {
            if (order != null)
                getOrderDetail();
            else
                finish();
        } else {
            String msg = "", str_action = "";
            if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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

        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        Context mContext;

        public RecyclerViewAdapter(Context context) {

            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView, price, count, modification;
            ExtraStuff item;

            public ViewHolder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.textView);
                count = v.findViewById(R.id.count);
                price = v.findViewById(R.id.price);
                modification = v.findViewById(R.id.modification);
            }

            public void setData(ExtraStuff item, View view) {
                this.item = item;
                textView.setText(item.getName());
               // if (!item.getModification().equalsIgnoreCase("--"))
                    modification.setText(item.getModification().trim());
//                else
//                    modification.setVisibility(View.GONE);

                if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    price.setText(getResources().getString(R.string.itemprice_en) + ": " + item.getPrice() + " JD");
                    count.setText(getResources().getString(R.string.quantity_en) + ": " + item.getQuantity());
                } else if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    price.setText(getResources().getString(R.string.itemprice_ar) + ": " + item.getPrice() + " JD");
                    count.setText(getResources().getString(R.string.quantity_ar) + ": " + item.getQuantity());
                }

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.order_detail_adap, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            ExtraStuff es = itemlist.get(position);
            Vholder.setData(es, Vholder.itemView);
        }

        @Override
        public int getItemCount() {

            return itemlist.size();
        }
    }

    void getOrderDetail() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(OrderDetailActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.BOOKORDER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        Log.e("response", response + "  order detail");
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            JSONArray jarr = jobj.optJSONArray("data");
                            if (jarr.length() > 0) {
                                JSONObject job = jarr.optJSONObject(0);
                                orderidvalue.setText(job.optString("order_id"));
                                branchvalue.setText(job.optString("branch"));
                                datevalue.setText(job.optString("date"));
                                totalpricevalue.setText(job.optString("total"));
                                subtotalvalue.setText(job.optString("sub-total"));
                                servicetaxvalue.setText(job.optString("service-tax"));

                                String deli = job.optString("delivery-charges");
                                if (!deli.equalsIgnoreCase("") && deli != null)
                                    deliveryvalue.setText(deli);
                                else
                                    deliverylay.setVisibility(View.GONE);

                                String dis = job.optString("discount");
                                if (!dis.equalsIgnoreCase("") && dis != null)
                                    discountvalue.setText(dis);
                                else
                                    discountlay.setVisibility(View.GONE);

                                JSONArray jr = job.optJSONArray("orderDetail");
                                for (int i = 0; i < jr.length(); i++) {
                                    JSONObject jo = jr.optJSONObject(i);
                                    itemlist.add(new ExtraStuff("", jo.optString("item_name"), jo.optString("item_price"), "", jo.optString("details"), jo.optString("quantity"), false));
                                }
                                RecyclerViewAdapter adapter = new RecyclerViewAdapter(OrderDetailActivity.this);
                                recyclerView.setAdapter(adapter);
                                Log.e("Recy width", recyclerView.getWidth() + "  www");
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int width = displayMetrics.widthPixels - 100;
                                AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(OrderDetailActivity.this, 0);
                                recyclerView.setLayoutManager(layoutManager);
                            }
                        } else {
                            Snackbar snackbar = Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG);
                            snackbar.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    finish();
                                }
                            });
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(OrderDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(OrderDetailActivity.this));
                    params.put("action", "order_info");
                    params.put("order_id", order.getOrder_id());

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
            Singleton.getIntance(OrderDetailActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
