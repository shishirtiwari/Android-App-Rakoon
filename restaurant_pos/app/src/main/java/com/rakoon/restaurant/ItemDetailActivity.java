package com.rakoon.restaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.rakoon.restaurant.datasource.DataBaseHelper;
import com.rakoon.restaurant.model.Area;
import com.rakoon.restaurant.model.Branch;
import com.rakoon.restaurant.model.Cart;
import com.rakoon.restaurant.model.Item;
import com.rakoon.restaurant.model.ItemDetail;
import com.rakoon.restaurant.model.Optional_Modication;
import com.rakoon.restaurant.model.Upselling;
import com.rakoon.restaurant.model.UpsellingCategory;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ItemDetailActivity extends AppCompatActivity {
    Item item_;
    AllPath apath;
    Unbinder unbinder;
    @BindView(R.id.mainlay)
    LinearLayout mainlay;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.addtocart)
    TextView addtocart;
    @BindView(R.id.readaboutdish)
    TextView readaboutdish;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.optional)
    TextView optional;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.totalprice)
    TextView totalprice;
    @BindView(R.id.next)
    TextView next;
    @BindView(R.id.plus)
    ImageView plus;
    @BindView(R.id.description)
    TextView description;
    AlertDialog alertDialog;
    ItemDetail itemDetail = null;
    ArrayList<Optional_Modication> requiredlist, optionallist;
    ArrayList<UpsellingCategory> upsellinglist;
    TextView tv = null;
    FrameLayout cartclick;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bridge_menu, menu);
        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(false);

        MenuItem item = menu.findItem(R.id.cart);
        MenuItemCompat.setActionView(item, R.layout.cart_bridge);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        cartclick = (FrameLayout) notifCount.findViewById(R.id.cartclick);
        tv.setText("0");

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifCount.performClick();
            }
        });
        cartclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifCount.performClick();
            }
        });

        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apath.isUserLogedin(ItemDetailActivity.this)) {
                    Intent in = new Intent(ItemDetailActivity.this, CartActivity.class);
                    startActivity(in);
                } else {
                    Intent in = new Intent(ItemDetailActivity.this, LoginActivity.class);
                    in.putExtra("message", "rquired");
                    startActivity(in);
                }
            }
        });

        showCartCount();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                // Toast.makeText(ItemDetailActivity.this, "cart clicked ", Toast.LENGTH_LONG).show();
                Intent in = new Intent(ItemDetailActivity.this, CartActivity.class);
                startActivity(in);
                finish();
                return false;
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
        setContentView(R.layout.activity_item_detail);
        apath = new AllPath();
        unbinder = ButterKnife.bind(this);
        optionallist = new ArrayList<>();
        requiredlist = new ArrayList<>();
        upsellinglist = new ArrayList<>();
        item_ = (Item) getIntent().getSerializableExtra("item");
        getSupportActionBar().setTitle(item_.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (apath.isInternetOn(ItemDetailActivity.this)) {
            DataBaseHelper dbh = new DataBaseHelper(ItemDetailActivity.this);
            Area aa = dbh.getArea(apath.getUserid(ItemDetailActivity.this));
            if (aa != null) {
                getItemDetail(aa.getBranch_id());
                getItemModification(aa.getBranch_id());
                getUpselling(aa.getBranch_id());
            } else {
                Branch baa = dbh.getBranch(apath.getUserid(ItemDetailActivity.this));
                if (baa != null) {
                    getItemDetail(baa.getId());
                    getItemModification(baa.getId());
                    getUpselling(baa.getId());
                } else {
                    if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                        Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                    else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                        Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                }
            }

        } else {
            String msg = "", str_action = "";
            if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
        if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            addtocart.setText(getResources().getString(R.string.addtocart_en));
            readaboutdish.setText(getResources().getString(R.string.readaboutdish_en));
            optional.setText(getResources().getString(R.string.upselling_en));
            total.setText(getResources().getString(R.string.totalprice_en));
            next.setText(getResources().getString(R.string.next_en));
        } else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            addtocart.setText(getResources().getString(R.string.addtocart_ar));
            readaboutdish.setText(getResources().getString(R.string.readaboutdish_ar));
            optional.setText(getResources().getString(R.string.upselling_ar));
            total.setText(getResources().getString(R.string.totalprice_ar));
            next.setText(getResources().getString(R.string.next_ar));
        }

        readaboutdish.setOnClickListener((v) -> {
            if (readaboutdish.getTag().toString().equalsIgnoreCase("false")) {
                readaboutdish.setTag("true");
                if (itemDetail != null) {
                    if (!itemDetail.getDesc().equalsIgnoreCase("")) {
                        if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                            readaboutdish.setText(getResources().getString(R.string.readaboutdish_en) + "\n" + itemDetail.getDesc());
                        } else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                            readaboutdish.setText(getResources().getString(R.string.readaboutdish_ar) + "\n" + itemDetail.getDesc());
                        }
                    }
                }
            } else {
                readaboutdish.setTag("false");
                if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    readaboutdish.setText(getResources().getString(R.string.readaboutdish_en));
                } else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    readaboutdish.setText(getResources().getString(R.string.readaboutdish_ar));
                }

            }
        });
        next.setOnClickListener((v) -> {
//            Intent in = new Intent(ItemDetailActivity.this, CartActivity.class);
//            startActivity(in);
            finish();
        });
       /* addtocart.setOnClickListener((v) -> {
            if (apath.isUserLogedin(ItemDetailActivity.this)) {
                double tpr = Double.parseDouble(totalprice.getText().toString());
                if (tpr > 0)
                    Toast.makeText(ItemDetailActivity.this, "Under development....", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(ItemDetailActivity.this, "Choose item from optional modification....", Toast.LENGTH_LONG).show();
            } else {
                Intent in = new Intent(ItemDetailActivity.this, LoginActivity.class);
                startActivityForResult(in, 101);
            }
        });*/


        plus.setOnClickListener((v) -> {
            if (itemDetail != null) {
                Intent in = new Intent(ItemDetailActivity.this, RequiredActivity.class);
                in.putExtra("optional", optionallist);
                in.putExtra("required", requiredlist);
                in.putExtra("name", itemDetail.getName());
                in.putExtra("itemdetail", itemDetail);
                in.putExtra("type", "main");
                startActivityForResult(in, 100);
            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        Context mContext;

        public RecyclerViewAdapter(Context context) {
            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            UpsellingCategory item;
            ViewPager brandpager;

            public ViewHolder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.textView);
                brandpager = v.findViewById(R.id.brandpager);

            }

            public void setData(UpsellingCategory item) {
                this.item = item;
                textView.setText(item.getCate_name());

                brandpager.setAdapter(new CustomPageAdapter(ItemDetailActivity.this, item.getItems(), item.getCate_name()));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.upsellingviewpager, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            UpsellingCategory om = upsellinglist.get(position);
            Vholder.setData(om);

        }

        @Override
        public int getItemCount() {

            return upsellinglist.size();
        }
    }

    class CustomPageAdapter extends PagerAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        ArrayList<Upselling> items;
        String cat_name;

        public CustomPageAdapter(Context context, ArrayList<Upselling> items, String cat_name) {
            this.context = context;
            this.items = items;
            this.cat_name = cat_name;
            this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = this.layoutInflater.inflate(R.layout.optional_modification_adap, container, false);
            ImageView imagev = (ImageView) view.findViewById(R.id.imageView);
            TextView textView = view.findViewById(R.id.textView);
            ImageView plus = view.findViewById(R.id.plus);
            TextView price = view.findViewById(R.id.price);
            imagev.setImageResource(R.drawable.logo_signin);
            container.addView(view);

            price.setText(items.get(position).getPrice() + "");
            textView.setText(items.get(position).getName());
            ImageRequest request = new ImageRequest(items.get(position).getPhoto(),
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
            Singleton.getIntance(ItemDetailActivity.this).addToRequestQueue(request);


            plus.setOnClickListener((v) -> {
                Intent in = new Intent(ItemDetailActivity.this, RequiredActivity.class);
                in.putExtra("optional", optionallist);
                in.putExtra("required", requiredlist);
                in.putExtra("name", items.get(position).getName());
                in.putExtra("type", "related");
                in.putExtra("upselling", items.get(position));
                in.putExtra("cat_name", cat_name);
                startActivityForResult(in, 100);
            });

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
  /*  void setTotalprice() {
        double tprice = 0;
        if (itemDetail != null) {
            totalprice.setText(itemDetail.getPrice() + "");
            for (int i = 0; i < upsellinglist.size(); i++) {
                Upselling om = upsellinglist.get(i);
                tprice += (om.getQuantity() * (om.getPrice()));
            }
            totalprice.setText(String.format("%.2f", tprice) + "");
        }
    }*/

    void getItemDetail(String branch_id) {
        try {
            if (alertDialog != null)
                alertDialog.cancel();
            AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(ItemDetailActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.ITEM), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        Log.e("response", response + "  itemdetail ");
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            JSONArray jarr = jobj.optJSONArray("data");
                            if (jarr.length() > 0) {
                                JSONObject jo = jarr.optJSONObject(0);
                                itemDetail = new ItemDetail(jo.optString("item_id"), jo.optString("categoryid"), jo.optString("name"),
                                        jo.optString("desc"), jo.optDouble("price"), jo.optDouble("tax"), jo.optString("photo"));
                                price.setText((itemDetail.getPrice()) + " JD");
                                if (itemDetail.getDesc().length() > 0) {
                                    description.setText(itemDetail.getDesc());
                                    description.setVisibility(View.VISIBLE);
                                } else {
                                    description.setVisibility(View.GONE);
                                }
                                // setTotalprice();
                                ImageRequest request = new ImageRequest(itemDetail.getPhoto(),
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
                                Singleton.getIntance(ItemDetailActivity.this).addToRequestQueue(request);
                            }
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("action", "item_info");
                    params.put("lang", apath.getLang(ItemDetailActivity.this));
                    params.put("item_id", item_.getId());
                    params.put("branch_id", branch_id);

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
            Singleton.getIntance(ItemDetailActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void getItemModification(String branch_id) {
        try {
            if (alertDialog != null)
                alertDialog.cancel();
            AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(ItemDetailActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.ITEM), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        Log.e("response", response + "  modification ");
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            optionallist = new ArrayList<>();
                            requiredlist = new ArrayList<>();
                            JSONArray jarr = jobj.optJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jo = jarr.optJSONObject(i);
                                requiredlist.add(new Optional_Modication(jo.optString("sub_group_id"),
                                        jo.optString("modification_status"), jo.optString("name"), jo.optDouble("price"),
                                        jo.optString("photo"), 0, false, jo.optString("gorup_id")));
                            }

                            JSONArray jarr1 = jobj.optJSONArray("optional");
                            for (int i = 0; i < jarr1.length(); i++) {
                                JSONObject jo = jarr1.optJSONObject(i);
                                optionallist.add(new Optional_Modication(jo.optString("sub_group_id"),
                                        jo.optString("modification_status"), jo.optString("name"), jo.optDouble("price"),
                                        jo.optString("photo"), 0, false, jo.optString("gorup_id")));
                            }
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("action", "modification");
                    params.put("lang", apath.getLang(ItemDetailActivity.this));
                    params.put("item_id", item_.getId());
                    params.put("branch_id", branch_id);

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
            Singleton.getIntance(ItemDetailActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void getUpselling(String branch_id) {
        try {
            if (alertDialog != null)
                alertDialog.cancel();
            AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(ItemDetailActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.ITEM), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        Log.e("response", response + "  upselling ");
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            upsellinglist = new ArrayList<>();
                            JSONArray jarr = jobj.optJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jo = jarr.optJSONObject(i);
                                JSONArray itemjarr = jo.optJSONArray("items");
                                ArrayList<Upselling> itemlist = new ArrayList<>();
                                for (int j = 0; j < itemjarr.length(); j++) {
                                    JSONObject jb = itemjarr.optJSONObject(j);
                                    itemlist.add(new Upselling(jb.optString("id"),
                                            jb.optString("name"), jb.optDouble("price"),
                                            jb.optDouble("tax"), jb.optString("photo"), 0, jb.optBoolean("modification"),
                                            jb.optString("categoryid"), jb.getString("desc")));
                                }
                                upsellinglist.add(new UpsellingCategory(jo.optString("cat_name"), itemlist));
                            }
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(ItemDetailActivity.this);
                            recyclerView.setAdapter(adapter);
                            Log.e("Recy width", recyclerView.getWidth() + "  www");
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int width = displayMetrics.widthPixels - 100;
                            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(ItemDetailActivity.this, width / 2);
                            recyclerView.setLayoutManager(layoutManager);

                            //setTotalprice();
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ItemDetailActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("action", "upselling");
                    params.put("lang", apath.getLang(ItemDetailActivity.this));
                    params.put("item_id", item_.getId());
                    params.put("branch_id", branch_id);

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
            Singleton.getIntance(ItemDetailActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            String requiredValue = data.getStringExtra("status");
            Toast.makeText(ItemDetailActivity.this, requiredValue + " under development...", Toast.LENGTH_LONG).show();
        }
        if (requestCode == 100) {
            String requiredValue = data.getStringExtra("message");
            if (requiredValue.length() > 0)
                try {
                    Snackbar.make(mainlay, requiredValue, Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("Snabar exception", e.toString());
                }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        showCartCount();
    }

    void showCartCount() {
        DataBaseHelper dbh = new DataBaseHelper(ItemDetailActivity.this);
        ArrayList<Cart> cartlist = dbh.getCartlist(apath.getUserid(ItemDetailActivity.this));
        if (cartlist != null) {
            if (tv != null) {
                tv.setText(cartlist.size() + "");
            }
        }
    }

}
