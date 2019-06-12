package com.rakoon.restaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.rakoon.restaurant.datasource.DataBaseHelper;
import com.rakoon.restaurant.model.Area;
import com.rakoon.restaurant.model.Branch;
import com.rakoon.restaurant.model.ExtraStuff;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ExtraStuffActivity extends AppCompatActivity {

    @BindView(R.id.mainlay)
    LinearLayout mainlay;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.next)
    TextView next;


    AllPath apath;
    AlertDialog alertDialog;
    Unbinder unbinder;
    ArrayList<ExtraStuff> itemlist;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.extra_stuff, menu);
        final MenuItem myActionMenuItem = menu.findItem(R.id.skip);
        if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            menu.findItem(R.id.skip).setTitle(getResources().getString(R.string.skip_en));
        } else if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            menu.findItem(R.id.skip).setTitle(getResources().getString(R.string.skip_ar));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.skip:
                Intent in=new Intent(ExtraStuffActivity.this,SummaryActivity.class);
                startActivity(in);
                finish();
              //  Toast.makeText(ExtraStuffActivity.this, "skip clicked ", Toast.LENGTH_LONG).show();
                return false;
            case android.R.id.home:
                finish();
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_stuff);
        apath = new AllPath();
        unbinder = ButterKnife.bind(this);
        itemlist = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.extrastuff_en));
            next.setText(getResources().getString(R.string.next_en));
        } else if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.extrastuff_ar));
            next.setText(getResources().getString(R.string.next_ar));
        }

        if (apath.isInternetOn(ExtraStuffActivity.this)) {
            DataBaseHelper dbh = new DataBaseHelper(ExtraStuffActivity.this);
            Area aa = dbh.getArea(apath.getUserid(ExtraStuffActivity.this));
            String cate_id = dbh.getcategoryid(apath.getUserid(ExtraStuffActivity.this));
            if (aa != null) {
                getItem(aa.getBranch_id(), cate_id);
            } else {
                Branch baa = dbh.getBranch(apath.getUserid(ExtraStuffActivity.this));
                if (baa != null) {
                    getItem(baa.getId(), cate_id);
                } else {
                    if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.english)))
                        Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                    else if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                        Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                }
            }

        } else {
            String msg = "", str_action = "";
            if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                msg = getResources().getString(R.string.internet_message_ar);
                str_action = getResources().getString(R.string.enable_ar);
            }

            Snackbar.make(mainlay, msg, Snackbar.LENGTH_LONG)
                    .setAction(str_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // put your logic here
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ExtraStuff> extra_array=new ArrayList<>();
                for (int i=0;i<itemlist.size();i++)
                {
                    ExtraStuff es=itemlist.get(i);
                    int qu=Integer.parseInt(es.getQuantity());
                    if (qu > 0)
                    {
                        extra_array.add(es);
                    }
                }

                DataBaseHelper dbh = new DataBaseHelper(ExtraStuffActivity.this);
                boolean inserted = dbh.insertExtrastuf(extra_array);
                if (inserted)
                {
                    Intent in=new Intent(ExtraStuffActivity.this,SummaryActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        Context mContext;

        public RecyclerViewAdapter(Context context) {

            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView, price, minus, count, plus;
            public ImageView imageView;
            ExtraStuff item;

            public ViewHolder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.textView);
                minus = v.findViewById(R.id.minus);
                count = v.findViewById(R.id.count);
                plus = v.findViewById(R.id.plus);
                price = v.findViewById(R.id.price);
                imageView = (ImageView) v.findViewById(R.id.imageView);

            }

            public void setData(ExtraStuff item, View view) {
                this.item = item;

                textView.setText(item.getName());
                price.setText(item.getPrice() + " JD");
                // imageView.setImageResource(item.drawable);
                count.setText(item.getQuantity());
                Picasso.with(ExtraStuffActivity.this).load(item.getPhoto()).placeholder(R.drawable.dummy).into(imageView);
                int qu = Integer.parseInt(item.getQuantity());
                if (qu > 0)
                    view.setBackgroundColor(getResources().getColor(R.color.shadow));
                else
                    view.setBackgroundColor(getResources().getColor(R.color.white));

               /* imageView.setImageResource(R.drawable.dummy);
                ImageRequest request = new ImageRequest(item.getPhoto(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                imageView.setImageBitmap(bitmap);
                            }
                        }, 0, 0, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                            }
                        });

                Singleton.getIntance(ExtraStuffActivity.this).addToRequestQueue(request);*/
                //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.extra_stuff_adap, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            ExtraStuff es = itemlist.get(position);
            Vholder.setData(es, Vholder.itemView);

            Vholder.plus.setOnClickListener((v) -> {
                int qu = Integer.parseInt(es.getQuantity());
                if (qu < 99) {
                    qu += 1;
                    es.setQuantity(qu + "");
                    itemlist.set(position, es);
                    notifyItemChanged(position);
                } else {
                    Vholder.itemView.setBackgroundColor(getResources().getColor(R.color.shadow));
                }
            });
            Vholder.minus.setOnClickListener((v) -> {
                int qu = Integer.parseInt(es.getQuantity());
                if (qu > 0) {
                    qu -= 1;
                    es.setQuantity(qu + "");
                    itemlist.set(position, es);
//                    if (qu > 0)
//                        Vholder.itemView.setBackgroundColor(getResources().getColor(R.color.shadow));
//                    else
//                        Vholder.itemView.setBackgroundColor(getResources().getColor(R.color.white));
                    notifyItemChanged(position);
                } else {
                    Vholder.itemView.setBackgroundColor(getResources().getColor(R.color.white));
                }
            });
        }

        @Override
        public int getItemCount() {

            return itemlist.size();
        }
    }

    void getItem(String branch_id, String categoryid) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExtraStuffActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(ExtraStuffActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.EXTRA_STUFF), new Response.Listener<String>() {
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
                                itemlist.add(new ExtraStuff(job.optString("item_id"), job.optString("name"),
                                        job.optString("price"), job.optString("photo"), job.optString("Modification_status"),
                                        "0", false));
                            }
                            Log.e("SIze", itemlist.size() + " size");
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(ExtraStuffActivity.this);
                            recyclerView.setAdapter(adapter);
                            Log.e("Recy width", recyclerView.getWidth() + "  www");
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int width = displayMetrics.widthPixels;
                            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(ExtraStuffActivity.this, width / 2);
                            recyclerView.setLayoutManager(layoutManager);

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ExtraStuffActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(ExtraStuffActivity.this));
                    params.put("branchid", branch_id);
                    params.put("categoryid", categoryid);
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
            Singleton.getIntance(ExtraStuffActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String priceCalculator(double price, int quantity) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        double pr = price * quantity;

        return df2.format(pr) + " JD";
    }
}
