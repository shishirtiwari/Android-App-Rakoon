package com.rakoon.restaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.rakoon.restaurant.model.Cart;
import com.rakoon.restaurant.model.Category;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoryActivity extends AppCompatActivity {
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

    AllPath apath;
    AlertDialog alertDialog;
    Unbinder unbinder;
    ArrayList<Category> catelist;
    String branch_id = "";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    SearchView searchView;

    TextView tv = null;
    FrameLayout cartclick;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bridge_menu, menu);
        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(false);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                Toast.makeText(CategoryActivity.this, "text : " + query, Toast.LENGTH_LONG).show();
                /*if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });

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
                if (apath.isUserLogedin(CategoryActivity.this)) {
                    Intent in = new Intent(CategoryActivity.this, CartActivity.class);
                    startActivity(in);
                } else {
                    Intent in = new Intent(CategoryActivity.this, LoginActivity.class);
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
                // Toast.makeText(CategoryActivity.this, "cart clicked ", Toast.LENGTH_LONG).show();
                if (apath.isUserLogedin(CategoryActivity.this)) {
                    Intent in = new Intent(CategoryActivity.this, CartActivity.class);
                    startActivity(in);
                } else {
                    Intent in = new Intent(CategoryActivity.this, LoginActivity.class);
                    in.putExtra("message", "rquired");
                    startActivity(in);
                }
                return false;
            case android.R.id.home:
                finish();
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        apath = new AllPath();
        unbinder = ButterKnife.bind(this);
        catelist = new ArrayList<>();
        branch_id = getIntent().getStringExtra("branch_id");

        if (apath.isInternetOn(CategoryActivity.this))
            getCategory();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(CategoryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(CategoryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
                            finish();
                        }
                    })
                    .show();
        }

        if (apath.getLanguage(CategoryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle(getString(R.string.rakoonmenu_en));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (apath.getLanguage(CategoryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle(getString(R.string.rakoonmenu_ar));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        ArrayList<Category> mValues;
        Context mContext;

        public RecyclerViewAdapter(Context context, ArrayList<Category> values) {

            mValues = values;
            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            public ImageView imageView;
            Category item;

            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener((vi) -> {

                });
                textView = (TextView) v.findViewById(R.id.text);
                imageView = (ImageView) v.findViewById(R.id.image);

            }

            public void setData(Category item) {
                this.item = item;

                textView.setText(item.getName());
                // imageView.setImageResource(item.drawable);
                ImageRequest request = new ImageRequest(item.getLogo(),
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
                Singleton.getIntance(CategoryActivity.this).addToRequestQueue(request);
                //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.category_adap, parent, false);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int heightPixels = displayMetrics.heightPixels-100;
            view.setMinimumHeight(heightPixels/3);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            Vholder.setData(mValues.get(position));
            Vholder.itemView.setOnClickListener((v) -> {
                Intent in = new Intent(CategoryActivity.this, ItemActivity.class);
                in.putExtra("branch_id", branch_id);
                in.putExtra("category", mValues.get(position));
                startActivity(in);
            });
        }

        @Override
        public int getItemCount() {

            return mValues.size();
        }
    }

    void getCategory() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(CategoryActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.BRANCH_CATEGORY), new Response.Listener<String>() {
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
                                catelist.add(new Category(job.optString("id"), job.optString("name"),
                                        job.optString("logo")));
                            }
                            Log.e("SIze", catelist.size() + " size");
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(CategoryActivity.this, catelist);
                            recyclerView.setAdapter(adapter);
                            Log.e("Recy width", recyclerView.getWidth() + "  www");
                            int width = recyclerView.getWidth() / 3;
                            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(CategoryActivity.this, width);
                            recyclerView.setLayoutManager(layoutManager);

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(CategoryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(CategoryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(CategoryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(CategoryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(CategoryActivity.this));
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
            Singleton.getIntance(CategoryActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void setCategoryView(ImageView imageView, TextView textView, String imageurl, String text) {
        textView.setText(text);
        // imageView.setImageResource(item.drawable);
        ImageRequest request = new ImageRequest(imageurl,
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
        Singleton.getIntance(CategoryActivity.this).addToRequestQueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCartCount();
    }

    void showCartCount() {
        DataBaseHelper dbh = new DataBaseHelper(CategoryActivity.this);
        ArrayList<Cart> cartlist = dbh.getCartlist(apath.getUserid(CategoryActivity.this));
        if (cartlist != null) {
            if (tv != null) {
                tv.setText(cartlist.size() + "");
            }
        }
    }
}
