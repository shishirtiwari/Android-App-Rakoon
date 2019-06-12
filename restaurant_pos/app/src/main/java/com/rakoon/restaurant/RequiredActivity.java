package com.rakoon.restaurant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.rakoon.restaurant.datasource.DataBaseHelper;
import com.rakoon.restaurant.model.Area;
import com.rakoon.restaurant.model.Branch;
import com.rakoon.restaurant.model.Cart;
import com.rakoon.restaurant.model.ItemDetail;
import com.rakoon.restaurant.model.Optional_Modication;
import com.rakoon.restaurant.model.Upselling;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RequiredActivity extends AppCompatActivity {

    ArrayList<Optional_Modication> requiredlist, optionallist;
    TextView plus, minus;
    TextView count, price;
    ItemDetail itemDetail = null;
    Upselling upselling;
    String type, cat_name;
    RecyclerView recyclerView, recyclerViewoptional;
    RecyclerViewAdapter adapter;
    RecyclerViewAdapterOptional adapoptional;
    Unbinder unbinder;
    @BindView(R.id.addtocart)
    TextView addtocart;
    @BindView(R.id.required)
    TextView required;
    @BindView(R.id.optional)
    TextView optional;
    @BindView(R.id.mainlay)
    ConstraintLayout mainlay;
    AllPath apath;
    AlertDialog alertDialog;
    private SimpleDateFormat inputParser;
    private Date date;
    private Date dateCompareOne;
    private Date dateCompareTwo;

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            intent.putExtra("message", "");
        } else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            intent.putExtra("message", "");
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = getIntent();
            if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                intent.putExtra("message", "");
            } else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                intent.putExtra("message", "");
            }
            setResult(RESULT_OK, intent);
            finish(); // close this activity and return to preview activity (if there is any)
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
        setContentView(R.layout.activity_required);
        apath = new AllPath();
        unbinder = ButterKnife.bind(this);
        optionallist = new ArrayList<>();
        requiredlist = new ArrayList<>();
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        optionallist = (ArrayList<Optional_Modication>) getIntent().getSerializableExtra("optional");
        requiredlist = (ArrayList<Optional_Modication>) getIntent().getSerializableExtra("required");
        type = getIntent().getStringExtra("type");
        if (type.equalsIgnoreCase("main")) {
            itemDetail = (ItemDetail) getIntent().getSerializableExtra("itemdetail");
        }

        if (type.equalsIgnoreCase("related")) {
            upselling = (Upselling) getIntent().getSerializableExtra("upselling");
            cat_name = getIntent().getStringExtra("cat_name");
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewoptional = findViewById(R.id.recyclerViewoptional);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        count = findViewById(R.id.count);
        price = findViewById(R.id.price);
        count.setText("1");
        if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            addtocart.setText(getResources().getString(R.string.addtocart_en));
            required.setText(getResources().getString(R.string.required_modification_en));
            optional.setText(getResources().getString(R.string.optional_modification_en));
        } else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            addtocart.setText(getResources().getString(R.string.addtocart_ar));
            required.setText(getResources().getString(R.string.required_modification_ar));
            optional.setText(getResources().getString(R.string.optional_modification_ar));
        }

        showVisibility();
        adapter = new RecyclerViewAdapter(RequiredActivity.this);
        recyclerView.setAdapter(adapter);
        Log.e("Recy width", recyclerView.getWidth() + "  www");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(RequiredActivity.this, width / 3);
        recyclerView.setLayoutManager(layoutManager);

        adapoptional = new RecyclerViewAdapterOptional(RequiredActivity.this);
        recyclerViewoptional.setAdapter(adapoptional);
        AutoFitGridLayoutManager layoutManager1 = new AutoFitGridLayoutManager(RequiredActivity.this, width / 2);
        recyclerViewoptional.setLayoutManager(layoutManager1);

        minus.setOnClickListener((v) -> {
            int qu = Integer.parseInt(count.getText().toString());
            if (qu > 1) {
                qu -= 1;
                count.setText(qu + "");
                if (upselling != null)
                    price.setText(priceCalculator(upselling.getPrice(), qu));
                else if (itemDetail != null)
                    price.setText(priceCalculator(itemDetail.getPrice(), qu));
                // setTotalprice();
            } else {

            }
        });
        plus.setOnClickListener((v) -> {
            int qu = Integer.parseInt(count.getText().toString());
            if (qu < 99) {
                qu += 1;
                count.setText(qu + "");
                if (upselling != null)
                    price.setText(priceCalculator(upselling.getPrice(), qu));
                else if (itemDetail != null)
                    price.setText(priceCalculator(itemDetail.getPrice(), qu));
                //   setTotalprice();
            } else {

            }
        });
        if (upselling != null)
            price.setText(priceCalculator(upselling.getPrice(), Integer.parseInt(count.getText().toString())));
        else if (itemDetail != null)
            price.setText(priceCalculator(itemDetail.getPrice(), Integer.parseInt(count.getText().toString())));

        addtocart.setOnClickListener((v) -> {
            SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
            String open_time = sp.getString("open_time", "");
            String close_time = sp.getString("close_time", "");
            String inputFormat = "hh:mm a";
            Calendar now = Calendar.getInstance();
            inputParser = new SimpleDateFormat(inputFormat, Locale.US);
            String formattedDate = inputParser.format(now.getTime());
          /*  if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
            {
                Locale locale = new Locale("ar");
                inputParser = new SimpleDateFormat(inputFormat, locale);
                Date currDate = new Date();
                formattedDate = inputParser.format(currDate);
                Log.e("ARABIC..",formattedDate);
            }*/
            date = parseDate(formattedDate);
            dateCompareOne = parseDate(open_time);
            dateCompareTwo = parseDate(close_time);
            Log.e("open and close time", open_time + "  ,  , " + close_time + " , " + formattedDate);
            Log.e("milli..", date.getTime() + "  ,,  " + dateCompareOne.getTime() + "  ,,  " + dateCompareTwo.getTime());
            if (date.after(dateCompareOne) && date.before(dateCompareTwo)) {
                //Toast.makeText(RequiredActivity.this, "you can purchage now rastaurent have closed", Toast.LENGTH_LONG).show();
                addItemInCart();
            } else {
                String msg = "Now restaurent have closed. Timing is " + open_time + " - " + close_time, actionp = "Shift For Next Day", actionn = "Cancel";
                AlertDialog.Builder builder = new AlertDialog.Builder(RequiredActivity.this);
                if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    actionp = getResources().getString(R.string.shift_en);
                    actionn = getResources().getString(R.string.close_en);
                    msg = getResources().getString(R.string.close_msg_en) + " " + open_time + " - " + close_time;
                } else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    actionp = getResources().getString(R.string.shift_ar);
                    actionn = getResources().getString(R.string.close_ar);
                    msg = getResources().getString(R.string.close_msg_ar) + " " + open_time + " - " + close_time;
                }
                builder.setMessage(msg);
                builder.setPositiveButton(actionp, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addItemInCart();
                    }
                });
                builder.setNegativeButton(actionn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                            intent.putExtra("message", "");
                        } else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                            intent.putExtra("message", "");
                        }
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                builder.show();
                //Toast.makeText(RequiredActivity.this, "you can't purchage now rastaurent have closed", Toast.LENGTH_LONG).show();
            }

        });

        DataBaseHelper dbh = new DataBaseHelper(RequiredActivity.this);
        Area aa = dbh.getArea(apath.getUserid(RequiredActivity.this));
        String cate_id = dbh.getcategoryid(apath.getUserid(RequiredActivity.this));
        if (aa != null) {
            if (apath.isInternetOn(RequiredActivity.this)) {
                getBranchByID(aa.getBranch_id());
            } else {
                String msg = "", str_action = "";
                if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    msg = getResources().getString(R.string.internet_message_en);
                    str_action = getResources().getString(R.string.enable_en);
                } else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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

        } else {
            Branch baa = dbh.getBranch(apath.getUserid(RequiredActivity.this));
            if (baa != null) {
                SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("open_time", baa.getOpen());
                editor.putString("close_time", baa.getClose());
                editor.commit();
            } else {
                Toast.makeText(RequiredActivity.this, "Area or branch not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    void addItemInCart() {
        if (apath.isUserLogedin(RequiredActivity.this)) {
            boolean required = false;
            Optional_Modication selected = null;
            for (int i = 0; i < requiredlist.size(); i++) {
                Optional_Modication om = requiredlist.get(i);
                if (om.isReruired()) {
                    selected = om;
                    required = true;
                    break;
                }
            }
            if (requiredlist.size() == 0) {
                required = true;
            }
            if (required) {
                Cart cart = new Cart();
                cart.setQuantity(count.getText().toString());
                cart.setUserid(apath.getUserid(RequiredActivity.this));
                if (selected != null) {
                    cart.setModification(selected.getModification_status());
                    cart.setRequired_id(selected.getGroup()+"-"+selected.getId());
                    cart.setRequired_name(selected.getName());
                    cart.setRequired_price(selected.getPrice() + "");
                }
                String ids = "", name = "", price = "";
                for (int i = 0; i < optionallist.size(); i++) {
                    Optional_Modication om = optionallist.get(i);
                    if (om.isReruired()) {
                        if (ids.equalsIgnoreCase("")) {
                            ids = om.getGroup()+"-"+om.getId();
                            name = om.getName();
                            price = om.getPrice() + "";
                        } else {
                            ids = ids + "," + om.getGroup()+"-"+om.getId();
                            name = name + "," + om.getName();
                            price = price + "," + om.getPrice() + "";
                        }
                    } else
                        Log.e("Data ..", om.getId() + "  @  " + om.getName() + "  @  " + om.isReruired());

                }
                Log.e("Data ..", ids + "  @  " + name + "  @  " + price);
                cart.setOptional_id(ids);
                cart.setOptional_name(name);
                cart.setOptional_price(price);

                if (upselling != null) {
                    cart.setId(upselling.getId());
                    cart.setCat_id(upselling.getCaregoryid());
                    cart.setCat_name(cat_name);
                    cart.setName(upselling.getName());
                    cart.setPrice(upselling.getPrice() + "");
                    cart.setTax(upselling.getTax() + "");
                    cart.setPhoto(upselling.getPhoto());
                    cart.setDescription(upselling.getDesc());

                } else if (itemDetail != null) {
                    cart.setId(itemDetail.getItem_id());
                    cart.setCat_id(itemDetail.getCategoryid());
                    cart.setCat_name("");
                    cart.setName(itemDetail.getName());
                    cart.setPrice(itemDetail.getPrice() + "");
                    cart.setTax(itemDetail.getTax() + "");
                    cart.setPhoto(itemDetail.getPhoto());
                    cart.setDescription(itemDetail.getDesc());
                }
                DataBaseHelper dbh = new DataBaseHelper(RequiredActivity.this);
                boolean isinserted = dbh.insertItem(cart);
                if (isinserted) {
                    Log.e("Item added", "Item inserted");
                    Intent intent = getIntent();
                    if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                        intent.putExtra("message", getResources().getString(R.string.itemaddtedincart_en));
                    } else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                        intent.putExtra("message", getResources().getString(R.string.itemaddtedincart_ar));
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                } else
                    Log.e("Error", "Item not inserted");

            } else {
                if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getResources().getString(R.string.reqiredmsg_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getResources().getString(R.string.reqiredmsg_ar), Snackbar.LENGTH_LONG).show();
                }
            }
        } else {
            Intent in = new Intent(RequiredActivity.this, LoginActivity.class);
            in.putExtra("message", "rquired");
            startActivity(in);
        }
    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    String priceCalculator(double price, int quantity) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        double pr = price * quantity;

        return df2.format(pr) + " JD";
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        Context mContext;

        public RecyclerViewAdapter(Context context) {

            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView, price;
            public ImageView imageView;
            Optional_Modication item;

            public ViewHolder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.textView);
                price = v.findViewById(R.id.price);
                imageView = (ImageView) v.findViewById(R.id.imageView);

            }

            public void setData(Optional_Modication item, View view) {
                this.item = item;
                if (item.isReruired())
                    view.setBackgroundColor(getResources().getColor(R.color.shadow));
                else
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                textView.setText(item.getName());
                price.setText(item.getPrice() + " JD");
                // imageView.setImageResource(item.drawable);
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
                Singleton.getIntance(RequiredActivity.this).addToRequestQueue(request);
                //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.reduired_adap, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            Optional_Modication om = requiredlist.get(position);
            Vholder.setData(om, Vholder.itemView);
            Vholder.itemView.setOnClickListener((v) -> {
                for (int i = 0; i < requiredlist.size(); i++) {
                    Optional_Modication om1 = requiredlist.get(i);
                    if (om1.isReruired()) {
                        om1.setReruired(false);
                        requiredlist.set(i, om1);
                        // notifyItemChanged(i);
                    }
                    if (i == position) {
                        om1.setReruired(true);
                        requiredlist.set(i, om1);
                        // notifyItemChanged(i);
                    }
                    notifyDataSetChanged();
                    /*if (i == position) {
                        om1.setReruired(true);
                    } else {
                        om1.setReruired(false);
                    }
                    optionallist.set(position,om1);*/
                }
                // adapter.notifyDataSetChanged();

            });
        }

        @Override
        public int getItemCount() {

            return requiredlist.size();
        }
    }

    class RecyclerViewAdapterOptional extends RecyclerView.Adapter<RecyclerViewAdapterOptional.ViewHolder> {

        Context mContext;

        public RecyclerViewAdapterOptional(Context context) {

            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public CheckBox checkbox;
            Optional_Modication item;

            public ViewHolder(View v) {
                super(v);
                checkbox = v.findViewById(R.id.checkbox);

            }

            public void setData(Optional_Modication item, View view) {
                this.item = item;
                if (item.isReruired()) {
                    checkbox.setChecked(true);
                } else {
                    checkbox.setChecked(false);
                }

                checkbox.setText(item.getName() + "\n(" + item.getPrice() + " JD)");
                //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.optional_adap, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            Optional_Modication om = optionallist.get(position);
            Vholder.setData(om, Vholder.itemView);
            Vholder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        om.setReruired(true);
                        optionallist.set(position, om);
                    } else {
                        om.setReruired(false);
                        optionallist.set(position, om);
                    }
                    notifyDataSetChanged();
                }
            });
            /*Vholder.itemView.setOnClickListener((v) -> {
                if (om.isReruired()) {
                    om.setReruired(false);
                    optionallist.set(position, om);
                    notifyDataSetChanged();
                } else {
                    om.setReruired(true);
                    optionallist.set(position, om);
                    notifyDataSetChanged();
                }

            });*/
        }

        @Override
        public int getItemCount() {

            return optionallist.size();
        }
    }

    void showVisibility() {
        if (requiredlist.size() == 0) {
            required.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            required.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (optionallist.size() == 0) {
            optional.setVisibility(View.GONE);
            recyclerViewoptional.setVisibility(View.GONE);
        } else {
            optional.setVisibility(View.VISIBLE);
            recyclerViewoptional.setVisibility(View.VISIBLE);
        }
    }

    void getBranchByID(String branchid) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(RequiredActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(RequiredActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.ALL_BRANCHES), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        Log.e("response", response + "  branch detail ");
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            JSONArray jarr = jobj.optJSONArray("data");
                            if (jarr.length() > 0) {
                                JSONObject job = jarr.optJSONObject(0);
                                SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("open_time", job.optString("open_validation"));
                                editor.putString("close_time", job.optString("close_validation"));
                                editor.commit();
                            }

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(RequiredActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(RequiredActivity.this));
                    params.put("branch_id", branchid);
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
            Singleton.getIntance(RequiredActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
