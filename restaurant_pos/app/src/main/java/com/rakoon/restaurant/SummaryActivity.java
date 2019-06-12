package com.rakoon.restaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.rakoon.restaurant.model.ExtraStuff;
import com.rakoon.restaurant.util.DateTimeUtils;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SummaryActivity extends AppCompatActivity {
    private BottomSheetBehavior mBehavior;
    @BindView(R.id.bottomSheet)
    View mBottomSheet;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    AllPath apath;
    ArrayList<Cart> cartlist;
    ArrayList<ExtraStuff> extralist;
    RecyclerViewAdapter adapter;
    LinearLayout tranlay;
    Unbinder unbinder;
    @BindView(R.id.scrl)
    ScrollView sv;
    @BindView(R.id.address)
    TextView myaddress;
    @BindView(R.id.addresstext)
    TextView addresstext;
    @BindView(R.id.edit)
    TextView edit;
    @BindView(R.id.rg_now)
    RadioGroup rg_now;
    @BindView(R.id.now)
    RadioButton now;
    @BindView(R.id.later)
    RadioButton later;

    @BindView(R.id.selectdate)
    TextView selectdate;
    @BindView(R.id.totalprice)
    TextView totalprice;
    @BindView(R.id.totalpricevalue)
    TextView totalpricevalue;

    @BindView(R.id.extraadd)
    TextView extraadd;
    @BindView(R.id.extraaddvalue)
    TextView extraaddvalue;

    @BindView(R.id.subprice)
    TextView subprice;
    @BindView(R.id.subpricevalue)
    TextView subpricevalue;

    @BindView(R.id.tax)
    TextView tax;
    @BindView(R.id.taxvalue)
    TextView taxvalue;

    @BindView(R.id.delivery)
    TextView delivery;
    @BindView(R.id.deliveryvalue)
    TextView deliveryvalue;
    @BindView(R.id.ordernow)
    TextView ordernow;

    @BindView(R.id.discount)
    TextView discount;
    @BindView(R.id.discountvalue)
    TextView discountvalue;

    @BindView(R.id.offercode)
    EditText offercode;
    @BindView(R.id.usecode)
    TextView usecode;

    @BindView(R.id.deliverylay)
    LinearLayout deliverylay;
    @BindView(R.id.discountlay)
    LinearLayout discountlay;
    @BindView(R.id.mainlay)
    LinearLayout mainlay;

    Area aa = null;
    Branch baa = null;
    AlertDialog alertDialog;
    ArrayList<Area> arealist;
    ArrayList<Branch> branchlist;
    String[] payment_type;
    Area selectedarea = null;
    Branch selectedbranch = null;
    String str_phone, str_street, str_building, str_near, str_comments, str_picker;

    double taxpriceJD = 0.16, deliverycharge = 0.0, extraaddJD = 0.0, subtprice = 0.0, offerdis = 0.0, totalJD = 0.0, tax_calculated = 0.0;
    String ordertimingstatus = "2";

    private SimpleDateFormat inputParser;
    private Date date;
    private Date dateCompareOne;
    private Date dateCompareTwo;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = getIntent();
            if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                intent.putExtra("message", "");
            } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                intent.putExtra("message", "");
            }
            setResult(RESULT_OK, intent);
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        getSupportActionBar().setTitle("Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.summary_en));
            myaddress.setText(getResources().getString(R.string.myaddress_en));
            edit.setText(getResources().getString(R.string.edit_en));
            now.setText(getResources().getString(R.string.now_en));
            later.setText(getResources().getString(R.string.later_en));
            totalprice.setText(getResources().getString(R.string.totalprice_en));
            subprice.setText(getResources().getString(R.string.subprice_en));
            tax.setText(getResources().getString(R.string.tax_en));
            delivery.setText(getResources().getString(R.string.delivery_en));
            discount.setText(getResources().getString(R.string.discount_en));
            ordernow.setText(getResources().getString(R.string.ordernow_en));
            offercode.setHint(getResources().getString(R.string.doyouhaveoffer_en));
            usecode.setText(getResources().getString(R.string.usecode_en));
            selectdate.setHint(getResources().getString(R.string.select_later_en));

        } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.summary_ar));
            myaddress.setText(getResources().getString(R.string.myaddress_ar));
            edit.setText(getResources().getString(R.string.edit_ar));
            now.setText(getResources().getString(R.string.now_ar));
            later.setText(getResources().getString(R.string.later_ar));
            totalprice.setText(getResources().getString(R.string.totalprice_ar));
            subprice.setText(getResources().getString(R.string.subprice_ar));
            tax.setText(getResources().getString(R.string.tax_ar));
            delivery.setText(getResources().getString(R.string.delivery_ar));
            discount.setText(getResources().getString(R.string.discount_ar));
            ordernow.setText(getResources().getString(R.string.ordernow_ar));
            offercode.setHint(getResources().getString(R.string.doyouhaveoffer_ar));
            usecode.setText(getResources().getString(R.string.usecode_ar));
            selectdate.setHint(getResources().getString(R.string.select_later_ar));

        }
        usecode.setEnabled(false);
        offercode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    usecode.setEnabled(true);
                    usecode.setTextColor(getResources().getColor(R.color.red));
                    usecode.setFocusable(true);
                    usecode.setClickable(true);
                } else {
                    usecode.setEnabled(false);
                    usecode.setFocusable(false);
                    usecode.setClickable(false);
                    usecode.setTextColor(getResources().getColor(R.color.gray));
                }
            }
        });
        usecode.setOnClickListener((v) -> {
            if (apath.isInternetOn(SummaryActivity.this))
                getDiscount();
            else {
                String msg = "", str_action = "";
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    msg = getResources().getString(R.string.internet_message_en);
                    str_action = getResources().getString(R.string.enable_en);
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
        });
        cartlist = new ArrayList<>();
        extralist = new ArrayList<>();
        DataBaseHelper dbh = new DataBaseHelper(SummaryActivity.this);
        cartlist = dbh.getCartlist(apath.getUserid(SummaryActivity.this));
        priceCalculatorExtra(cartlist);
        extralist = dbh.getextraStuff();
        for (int i = 0; i < extralist.size(); i++) {
            Cart c = new Cart();
            ExtraStuff ex = extralist.get(i);
            c.setQuantity(ex.getQuantity());
            c.setName(ex.getName());
            c.setPhoto(ex.getPhoto());
            c.setPrice(ex.getPrice());
            c.setModification("extra");
            c.setRequired_price("0.0");
            c.setRequired_name("");
            c.setRequired_id("");
            c.setOptional_price("0.0");
            c.setOptional_name("");
            c.setOptional_id("");
            cartlist.add(c);
        }
        aa = dbh.getArea(apath.getUserid(SummaryActivity.this));
        String cate_id = dbh.getcategoryid(apath.getUserid(SummaryActivity.this));
        if (aa != null) {
            deliverylay.setVisibility(View.VISIBLE);
            deliverycharge = Double.parseDouble(aa.getDelivery());
            showAddress("area");
            // getItem(aa.getBranch_id(), cate_id);
        } else {
            deliverylay.setVisibility(View.GONE);
            baa = dbh.getBranch(apath.getUserid(SummaryActivity.this));
            if (baa != null) {
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    myaddress.setText(getResources().getString(R.string.pickup_information_en));
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    myaddress.setText(getResources().getString(R.string.pickup_information_ar));
                }
                showAddress("branch");
            } else {
                Toast.makeText(SummaryActivity.this, "Area or branch not found", Toast.LENGTH_LONG).show();
            }
        }
        priceCalculator();

        int width = recyclerView.getWidth();   //  for always 1 column in grid
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(SummaryActivity.this, width);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(SummaryActivity.this);
        recyclerView.setAdapter(adapter);

        selectdate.setVisibility(View.GONE);
        usecode.setTextColor(getResources().getColor(R.color.gray));

        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBehavior.setPeekHeight(100);
        mBottomSheet.setVisibility(View.VISIBLE);
        showBottomSheetView();
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    findViewById(R.id.arrow).setRotation(-90);
                    sv.post(new Runnable() {
                        public void run() {
                            sv.fullScroll(sv.FOCUS_UP);
                        }
                    });
                    //  Toast.makeText(SummaryActivity.this,"collpse",Toast.LENGTH_LONG).show();
                } else {
                    findViewById(R.id.arrow).setRotation(90);
                    sv.post(new Runnable() {
                        public void run() {
                            sv.fullScroll(sv.FOCUS_DOWN);
                        }
                    });
                    // Toast.makeText(SummaryActivity.this,"show",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        findViewById(R.id.tranlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
      /*  findViewById(R.id.whitelay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });*/
        selectdate.setOnClickListener((v) -> {
            showDateTimePicker();
        });
        rg_now.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) rg_now.findViewById(checkedId);
                String str_type = checkedRadioButton.getText().toString();
                if (checkedId == R.id.now) {
                    ordertimingstatus = "2";
                    selectdate.setText("");
                    selectdate.setVisibility(View.GONE);
                } else {
                    ordertimingstatus = "1";
                    selectdate.setVisibility(View.VISIBLE);
                }
            }
        });
        edit.setOnClickListener((v) -> {

            if (apath.isInternetOn(SummaryActivity.this)) {
                if (aa != null) {
                    getAreaLocation();
                } else {
                    if (baa != null) {
                        getBranchLocation();
                    } else {
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }

            } else {
                String msg = "", str_action = "";
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    msg = getResources().getString(R.string.internet_message_en);
                    str_action = getResources().getString(R.string.enable_en);
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
        });

        ordernow.setOnClickListener((v) -> {
            if (isready()) {
                String str_later = "now";
                if (ordertimingstatus.equalsIgnoreCase("1") && selectdate.getText().toString().equalsIgnoreCase("")) {
                    apath.hideKeyboard(SummaryActivity.this);
                    if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                        Snackbar.make(mainlay, getResources().getString(R.string.select_later_en), Snackbar.LENGTH_LONG).show();
                    else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                        Snackbar.make(mainlay, getResources().getString(R.string.select_later_ar), Snackbar.LENGTH_LONG).show();
                } else {
                    if (ordertimingstatus.equalsIgnoreCase("1"))
                        str_later = "later";
                    else
                        str_later = "now";

                    String inputFormat = "hh:mm a";
                    Calendar now = Calendar.getInstance();
                    inputParser = new SimpleDateFormat(inputFormat, Locale.US);
                    String formattedDate = inputParser.format(now.getTime());
                    if (ordertimingstatus.equalsIgnoreCase("1")) {
                        String sdate = selectdate.getText().toString();
                        sdate = DateTimeUtils.parseDateTime(sdate, AllPath.dformat, inputFormat);
                        Log.e("String time", sdate);
                        formattedDate = sdate;
                    }
                    Area aa = dbh.getArea(apath.getUserid(SummaryActivity.this));
                    if (aa != null) {
                        if (apath.isInternetOn(SummaryActivity.this)) {
                            getBranchByID(aa.getBranch_id(), formattedDate, str_later);
                        } else {
                            String msg = "", str_action = "";
                            if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                                msg = getResources().getString(R.string.internet_message_en);
                                str_action = getResources().getString(R.string.enable_en);
                            } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
                        Branch baa = dbh.getBranch(apath.getUserid(SummaryActivity.this));
                        if (baa != null) {
                            SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("open_time", baa.getOpen());
                            editor.putString("close_time", baa.getClose());
                            editor.commit();
                            showAlert(formattedDate, str_later);
                        } else {
                            Toast.makeText(SummaryActivity.this, "Area or branch not found", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            } else {
                Snackbar snackbar=null;
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    snackbar= Snackbar.make(mainlay, getResources().getString(R.string.edit_address_en), Snackbar.LENGTH_LONG);
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    snackbar=  Snackbar.make(mainlay, getResources().getString(R.string.edit_address_ar), Snackbar.LENGTH_LONG);
                }
                if (snackbar!=null)
                {
                    snackbar.show();
                    snackbar.addCallback(new Snackbar.Callback()
                    {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            edit.performClick();
                        }
                    });

                }

            }
        });
    }

    boolean isready() {
        DataBaseHelper dbh = new DataBaseHelper(SummaryActivity.this);
        Area area = dbh.getArea(apath.getUserid(SummaryActivity.this));
        String cate_id = dbh.getcategoryid(apath.getUserid(SummaryActivity.this));
        if (area != null) {
            SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
            if (sp.getString("street", "").equalsIgnoreCase("") || sp.getString("building", "").equalsIgnoreCase("") || sp.getString("phone", "").equalsIgnoreCase("")) {

                return false;
            } else {
                return true;
            }
            // getItem(aa.getBranch_id(), cate_id);
        } else {
            Branch branch = dbh.getBranch(apath.getUserid(SummaryActivity.this));
            if (branch != null) {
                SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                if (sp.getString("phone", "").equalsIgnoreCase("") || sp.getString("picker", "").equalsIgnoreCase("")) {

                    return false;
                } else {
                    return true;
                }
                // return true;
            }
            else
            {
                return false;
            }
        }
    }
    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    void showAlert(String formattedDate, String later) {
        SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
        String open_time = sp.getString("open_time", "");
        String close_time = sp.getString("close_time", "");
      /*  if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            String inputFormat = "hh:mm a";
            Locale locale = new Locale("ar");
            inputParser = new SimpleDateFormat(inputFormat, locale);
            DateFormat df = new SimpleDateFormat(inputFormat);
            try {
                Date dt = df.parse(formattedDate);
                formattedDate = inputParser.format(dt);
                Log.e("ARABIC..", formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }*/
        date = parseDate(formattedDate);
        dateCompareOne = parseDate(open_time);
        dateCompareTwo = parseDate(close_time);
        Log.e("open and close time", open_time + "  ,  , " + close_time + " , " + formattedDate);
        Log.e("milli..", date.getTime() + "  ,,  " + dateCompareOne.getTime() + "  ,,  " + dateCompareTwo.getTime());
        if (date.after(dateCompareOne) && date.before(dateCompareTwo)) {
            //Toast.makeText(RequiredActivity.this, "you can purchage now rastaurent have closed", Toast.LENGTH_LONG).show();
            orderNow();
        } else {
            String msg = "Now restaurent have closed. Timing is " + open_time + " - " + close_time, actionp = "Shift For Next Day", actionn = "Cancel";
            AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
            if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                actionp = getResources().getString(R.string.shift_en);
                actionn = getResources().getString(R.string.close_en);
                msg = getResources().getString(R.string.close_msg_en) + " " + open_time + " - " + close_time;
            } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                actionp = getResources().getString(R.string.shift_ar);
                actionn = getResources().getString(R.string.close_ar);
                msg = getResources().getString(R.string.close_msg_ar) + " " + open_time + " - " + close_time;
            }
            builder.setMessage(msg);
            if (!later.equalsIgnoreCase("Later")) {
                builder.setPositiveButton(actionp, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        orderNow();
                    }
                });
                builder.setNegativeButton(actionn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                            intent.putExtra("message", "");
                        } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                            intent.putExtra("message", "");
                        }
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            } else {
                builder.setNegativeButton(actionn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }

            builder.show();
            //Toast.makeText(RequiredActivity.this, "you can't purchage now rastaurent have closed", Toast.LENGTH_LONG).show();
        }
    }

    void orderNow() {
        Intent in = new Intent(SummaryActivity.this, PaymentOptionActivity.class);
        in.putExtra("address", addresstext.getText().toString());
        in.putExtra("offercode", offercode.getText().toString());
        in.putExtra("ordertimingstatus", ordertimingstatus);
        in.putExtra("later_date", selectdate.getText().toString());

        in.putExtra("deliverycharge", deliverycharge);
        in.putExtra("extraaddJD", extraaddJD);
        in.putExtra("subtprice", subtprice);
        in.putExtra("offerdis", offerdis);
        //in.putExtra("taxpriceJD", taxpriceJD);
        in.putExtra("taxpriceJD", tax_calculated);
        in.putExtra("totalJD", totalJD);
        startActivity(in);
    }

    void priceCalculator() {
        subtprice = 0.0;
        for (int i = 0; i < cartlist.size(); i++) {
            Cart ct = cartlist.get(i);
            double itemprice = Double.parseDouble(ct.getQuantity()) * Double.parseDouble(ct.getPrice());
            subtprice = subtprice + itemprice;
        }
        DecimalFormat df2 = new DecimalFormat("#.##");
        subpricevalue.setText(df2.format(subtprice) + " JD");
        deliveryvalue.setText(df2.format(deliverycharge) + " JD");
        //double taxpr = (subtprice + extraaddJD + deliverycharge) * taxpriceJD;
        double taxpr = (subtprice + extraaddJD) * taxpriceJD;
        taxvalue.setText(df2.format(taxpr) + " JD");
        tax_calculated = taxpr;
        double totaljd = subtprice + taxpr + deliverycharge + extraaddJD;
        totalJD = totaljd;
        totalpricevalue.setText(df2.format(totaljd) + " JD");
    }

    void priceCalculatorExtra(ArrayList<Cart> cartlist) {
        double extraprice = 0.0;
        for (int i = 0; i < cartlist.size(); i++) {
            Cart ct = cartlist.get(i);
            double reqp = 0.0;
            if (ct.getRequired_price() != null && !ct.getRequired_price().equalsIgnoreCase("") && !ct.getRequired_price().equalsIgnoreCase("null"))
                reqp = Double.parseDouble(ct.getRequired_price());
            double temptotal = 0.0;
            String str = ct.getOptional_price();
            if (str != null && !str.equalsIgnoreCase("") && !str.equalsIgnoreCase("null")) {
                List<String> slist = Arrays.asList(str.split(","));
                for (int j = 0; j < slist.size(); j++) {
                    temptotal = temptotal + Double.parseDouble(slist.get(j));
                }
            }
            extraprice = extraprice + ((temptotal + reqp) * Double.parseDouble(ct.getQuantity()));
        }
        extraaddJD = extraprice;
        DecimalFormat df2 = new DecimalFormat("#.##");
        extraaddvalue.setText(df2.format(extraprice) + " JD");
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        Context mContext;

        public RecyclerViewAdapter(Context context) {

            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name, price, quantity, required, totalprice;
            public ImageView image;
            Cart item;

            public ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.name);
                price = v.findViewById(R.id.price);
                quantity = v.findViewById(R.id.quantity);
                required = v.findViewById(R.id.required);
                totalprice = v.findViewById(R.id.totalprice);
                image = v.findViewById(R.id.image);

            }

            public void setData(Cart item) {
                this.item = item;
                name.setText(item.getName());
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    price.setText(getResources().getString(R.string.itemprice_en) + ": " + item.getPrice() + " JD");
                    quantity.setText(getResources().getString(R.string.quantity_en) + ": " + item.getQuantity());
                    totalprice.setText(getResources().getString(R.string.totalprice_en) + "\n " + priceCalculator(Double.parseDouble(item.getPrice()), Integer.parseInt(item.getQuantity())));
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    price.setText(getResources().getString(R.string.itemprice_ar) + ": " + item.getPrice() + " JD");
                    quantity.setText(getResources().getString(R.string.quantity_ar) + ": " + item.getQuantity());
                    totalprice.setText(getResources().getString(R.string.totalprice_ar) + "\n " + priceCalculator(Double.parseDouble(item.getPrice()), Integer.parseInt(item.getQuantity())));
                }


                if (item.getOptional_name() != null && !item.getRequired_name().equalsIgnoreCase("null"))
                    if (item.getOptional_name().length() > 0)
                        required.setText(item.getRequired_name() + "," + item.getOptional_name());
                    else if (!item.getRequired_name().equalsIgnoreCase("null"))
                        required.setText(item.getRequired_name());
                    else
                        required.setVisibility(View.GONE);

                if (item.getRequired_name().equalsIgnoreCase(""))
                    required.setVisibility(View.GONE);

                ImageRequest request = new ImageRequest(item.getPhoto(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                image.setImageBitmap(bitmap);
                            }
                        }, 0, 0, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                image.setImageResource(R.drawable.ic_launcher_foreground);
                            }
                        });
                Singleton.getIntance(SummaryActivity.this).addToRequestQueue(request);

                //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.summary_adap, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            Cart c = cartlist.get(position);
            Vholder.setData(c);
        }

        @Override
        public int getItemCount() {

            return cartlist.size();
        }
    }

    String priceCalculator(double price, int quantity) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        double pr = price * quantity;

        return df2.format(pr) + " JD";
    }

    private void showBottomSheetView() {
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

      /*if (mBottomSheetDialog != null) {
          mBottomSheetDialog.dismiss();
      }*/
    }

    void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(SummaryActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, final int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                //    SimpleDateFormat formatdate = new SimpleDateFormat("yyyy, MMM-dd hh:mm a");//hh:mm:ss a
                TimePickerDialog tpd = new TimePickerDialog(SummaryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        SimpleDateFormat formatdate = new SimpleDateFormat(AllPath.dformat);
                        Log.e("Date time format", formatdate.format(date.getTime()));
                        Log.e(" Dialog", "The choosen one " + date.getTime() + " , ");

                        selectdate.setText(formatdate.format(date.getTime()));
                        SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("date", selectdate.getText().toString());
                        editor.commit();
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                tpd.setCancelable(false);
                tpd.show();

            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        // dp.getDatePicker().setMaxDate(currentDate.getTimeInMillis() + (1296000000l));
        dp.getDatePicker().setMinDate(currentDate.getTimeInMillis());

        dp.setCancelable(false);
        dp.show();
    }

    void getAreaLocation() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(SummaryActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.AREA), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        arealist = new ArrayList<>();
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            Log.e("response", response + "  forgot ");
                            JSONArray jarr = jobj.optJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = jarr.optJSONObject(i);
                                arealist.add(new Area(job.optString("id"), job.optString("name"),
                                        job.optString("delivery"), job.optString("branch_id")));
                            }
                            showDeliveryAlert();
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(SummaryActivity.this));
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
            Singleton.getIntance(SummaryActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getBranchLocation() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(SummaryActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.ALL_BRANCHES), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        branchlist = new ArrayList<>();
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            Log.e("response", response + "  forgot ");
                            JSONArray jarr = jobj.optJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = jarr.optJSONObject(i);
                                JSONArray arrJson = job.getJSONArray("payment_type");
                                payment_type = new String[arrJson.length()];
                                for (int j = 0; j < arrJson.length(); j++) {
                                    payment_type[j] = arrJson.getString(j);
                                    Log.e("string arra ", payment_type[j] + " value");
                                }
//                                branchlist.add(new Branch(job.optString("id"), job.optString("name"),
//                                        job.optString("open"), job.optString("close"), job.optString("logo"), job.optString("address"), payment_type, job.optString("phone")));
                                branchlist.add(new Branch(job.optString("id"), job.optString("name"),
                                        job.optString("open_validation"), job.optString("close_validation"), job.optString("logo"), job.optString("address"), payment_type, job.optString("phone"),
                                        job.optString("open"), job.optString("close")));
                            }

                            showPickup();

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(SummaryActivity.this));
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
            Singleton.getIntance(SummaryActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class AreaArrayAdapter extends ArrayAdapter<Area> {
        private final Context mContext;
        private final List<Area> mDepartments;
        private final List<Area> mDepartments_All;
        private final List<Area> mDepartments_Suggestion;
        private final int mLayoutResourceId;

        public AreaArrayAdapter(Context context, int resource, List<Area> departments) {
            super(context, resource, departments);
            this.mContext = context;
            this.mLayoutResourceId = resource;
            this.mDepartments = new ArrayList<>(departments);
            this.mDepartments_All = new ArrayList<>(departments);
            this.mDepartments_Suggestion = new ArrayList<>();
        }

        public int getCount() {
            return mDepartments.size();
        }

        public Area getItem(int position) {
            return mDepartments.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    convertView = inflater.inflate(mLayoutResourceId, parent, false);
                }
                final Area department = getItem(position);
                TextView name = (TextView) convertView.findViewById(R.id.text1);
                name.setText(department.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                public String convertResultToString(Object resultValue) {
                    return ((Area) resultValue).getName();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (constraint != null) {
                        mDepartments_Suggestion.clear();
                        for (Area department : mDepartments_All) {
                            if (department.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                mDepartments_Suggestion.add(department);
                            }
                        }
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = mDepartments_Suggestion;
                        filterResults.count = mDepartments_Suggestion.size();
                        return filterResults;
                    } else {
                        return new FilterResults();
                    }
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mDepartments.clear();
                    if (results != null && results.count > 0) {
                        // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                        List<?> result = (List<?>) results.values;
                        for (Object object : result) {
                            if (object instanceof Area) {
                                mDepartments.add((Area) object);
                            }
                        }
                    } else if (constraint == null) {
                        // no filter, add entire original list back in
                        mDepartments.addAll(mDepartments_All);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }

    class BranchArrayAdapter extends ArrayAdapter<Branch> {
        private final Context mContext;
        private final List<Branch> mDepartments;
        private final List<Branch> mDepartments_All;
        private final List<Branch> mDepartments_Suggestion;
        private final int mLayoutResourceId;

        public BranchArrayAdapter(Context context, int resource, List<Branch> departments) {
            super(context, resource, departments);
            this.mContext = context;
            this.mLayoutResourceId = resource;
            this.mDepartments = new ArrayList<>(departments);
            this.mDepartments_All = new ArrayList<>(departments);
            this.mDepartments_Suggestion = new ArrayList<>();
        }

        public int getCount() {
            return mDepartments.size();
        }

        public Branch getItem(int position) {
            return mDepartments.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    convertView = inflater.inflate(mLayoutResourceId, parent, false);
                }
                final Branch department = getItem(position);
                TextView name = (TextView) convertView.findViewById(R.id.text1);
                name.setText(department.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                public String convertResultToString(Object resultValue) {
                    return ((Branch) resultValue).getName();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (constraint != null) {
                        mDepartments_Suggestion.clear();
                        for (Branch department : mDepartments_All) {
                            if (department.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                mDepartments_Suggestion.add(department);
                            }
                        }
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = mDepartments_Suggestion;
                        filterResults.count = mDepartments_Suggestion.size();
                        return filterResults;
                    } else {
                        return new FilterResults();
                    }
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mDepartments.clear();
                    if (results != null && results.count > 0) {
                        // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
                        List<?> result = (List<?>) results.values;
                        for (Object object : result) {
                            if (object instanceof Branch) {
                                mDepartments.add((Branch) object);
                            }
                        }
                    } else if (constraint == null) {
                        // no filter, add entire original list back in
                        mDepartments.addAll(mDepartments_All);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }

    void showDeliveryAlert() {
        SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
        AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
        View vv = getLayoutInflater().inflate(R.layout.edit_address_area, null);
        TextView next = vv.findViewById(R.id.next);
        AutoCompleteTextView autoCompleteArea = vv.findViewById(R.id.autoCompleteArea);
        EditText building = vv.findViewById(R.id.building);
        EditText street = vv.findViewById(R.id.street);
        EditText phone = vv.findViewById(R.id.phonenumber);
        EditText near = vv.findViewById(R.id.near);
        EditText comment = vv.findViewById(R.id.comment);
        builder.setView(vv);

        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            next.setText(getResources().getString(R.string.next_en));
            autoCompleteArea.setHint(getResources().getString(R.string.select_search_en));
            phone.setHint(getResources().getString(R.string.mobileno_en));
            building.setHint(getResources().getString(R.string.pincode_en));
            street.setHint(getResources().getString(R.string.street_en));
            near.setHint(getResources().getString(R.string.near_en));
            comment.setHint(getResources().getString(R.string.comment_en));
        } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            next.setText(getResources().getString(R.string.next_ar));
            autoCompleteArea.setHint(getResources().getString(R.string.select_search_ar));
            phone.setHint(getResources().getString(R.string.mobileno_ar));
            building.setHint(getResources().getString(R.string.pincode_ar));
            street.setHint(getResources().getString(R.string.street_ar));
            near.setHint(getResources().getString(R.string.near_ar));
            comment.setHint(getResources().getString(R.string.comment_ar));
        }
        alert = builder.create();
        autoCompleteArea.setThreshold(0);
        AreaArrayAdapter areaadap = new AreaArrayAdapter(SummaryActivity.this, R.layout.textview, arealist);
        autoCompleteArea.setAdapter(areaadap);
        phone.setText(sp.getString("phone", ""));
        street.setText(sp.getString("street", ""));
        building.setText(sp.getString("building", ""));
        near.setText(sp.getString("near", ""));
        comment.setText(sp.getString("comment", ""));
        String arid = sp.getString("area_id", "");

        for (int i = 0; i < arealist.size(); i++) {
            if (arid.equalsIgnoreCase(arealist.get(i).getId())) {
                autoCompleteArea.setText(arealist.get(i).getName());
                selectedarea = arealist.get(i);
                break;
            }
        }

        autoCompleteArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("before", "before");
                autoCompleteArea.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("change", "change");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("after", "after");
                selectedarea = null;
            }
        });

        autoCompleteArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                }
            }
        });
        autoCompleteArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AreaArrayAdapter adapter = ((AreaArrayAdapter)
                        autoCompleteArea.getAdapter());
                selectedarea = ((Area) adapter
                        .getItem(position));
                Log.e("selection", "selection");
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }
            }
        });

        next.setOnClickListener((v1) -> {
            View view1 = getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
            str_phone = phone.getText().toString();
            str_street = street.getText().toString();
            str_building = building.getText().toString();
            str_near = near.getText().toString();
            str_comments = comment.getText().toString();
            boolean isvalidate = false;
            if (str_phone.equalsIgnoreCase("")) {
                phone.requestFocus();
                // name.setBackgroundResource(R.drawable.bottomline_selector);
                // name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                phone.setBackgroundResource(R.drawable.bottom_line_red);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.mobileno_msg_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.mobileno_msg_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } else if (!str_phone.matches(apath.MobilePattern)) {
                phone.requestFocus();
                phone.setBackgroundResource(R.drawable.bottom_line_red);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.mobileno_valid_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.mobileno_valid_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } else if (selectedarea == null) {
                phone.setBackgroundResource(R.drawable.bottomline_selector);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                autoCompleteArea.setBackgroundResource(R.drawable.bottom_line_red);
                autoCompleteArea.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.selectarea_msg_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.selectarea_msg_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } else if (str_street.equalsIgnoreCase("")) {
                phone.setBackgroundResource(R.drawable.bottomline_selector);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                autoCompleteArea.setBackgroundResource(R.drawable.bottomline_selector);
                autoCompleteArea.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                street.setBackgroundResource(R.drawable.bottom_line_red);
                street.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.street_valid_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.street_valid_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } else if (str_building.equalsIgnoreCase("")) {
                phone.setBackgroundResource(R.drawable.bottomline_selector);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                autoCompleteArea.setBackgroundResource(R.drawable.bottomline_selector);
                autoCompleteArea.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                street.setBackgroundResource(R.drawable.bottomline_selector);
                street.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                building.setBackgroundResource(R.drawable.bottom_line_red);
                building.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.building_valid_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.building_valid_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } /*else if (str_near.equalsIgnoreCase("")) {
                phone.setBackgroundResource(R.drawable.bottomline_selector);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                autoCompleteArea.setBackgroundResource(R.drawable.bottomline_selector);
                autoCompleteArea.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                street.setBackgroundResource(R.drawable.bottomline_selector);
                street.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                building.setBackgroundResource(R.drawable.bottomline_selector);
                building.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                near.setBackgroundResource(R.drawable.bottom_line_red);
                near.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.near_valid_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.near_valid_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            }*/ else {
                isvalidate = true;
            }
            if (isvalidate) {
                phone.setBackgroundResource(R.drawable.bottomline_selector);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                autoCompleteArea.setBackgroundResource(R.drawable.bottomline_selector);
                autoCompleteArea.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                street.setBackgroundResource(R.drawable.bottomline_selector);
                street.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                building.setBackgroundResource(R.drawable.bottomline_selector);
                building.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                near.setBackgroundResource(R.drawable.bottomline_selector);
                near.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                if (selectedarea != null) {
                    DataBaseHelper dbh = new DataBaseHelper(SummaryActivity.this);
                    boolean isinserted = dbh.insertArea(selectedarea.getId(), selectedarea.getName(), selectedarea.getDelivery(), selectedarea.getBranch_id(), apath.getUserid(SummaryActivity.this));
                    deliverycharge = Double.parseDouble(selectedarea.getDelivery());
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("phone", str_phone);
                editor.putString("area_id", selectedarea.getId());
                editor.putString("area_name", selectedarea.getName());
                editor.putString("area_delivery", selectedarea.getDelivery());
                editor.putString("area_branchid", selectedarea.getBranch_id());
                editor.putString("street", str_street);
                editor.putString("building", str_building);
                editor.putString("near", str_near);
                editor.putString("comment", str_comments);
                editor.commit();
                alert.cancel();
                showAddress("area");
                priceUpdate();
            }
        });
        alert.show();

    }


    void showPickup() {
        AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
        View vv = getLayoutInflater().inflate(R.layout.edit_address_branch, null);
        TextView next = vv.findViewById(R.id.next);
        AutoCompleteTextView autoCompletebranch = vv.findViewById(R.id.autoCompletebranch);
        EditText nameofpicker = vv.findViewById(R.id.nameofpicker);
        EditText phone = vv.findViewById(R.id.phonenumber);
        EditText comment = vv.findViewById(R.id.comment);
        builder.setView(vv);
        SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
        phone.setText(sp.getString("phone", ""));
        nameofpicker.setText(sp.getString("picker", ""));
        comment.setText(sp.getString("comment", ""));
        String brid = sp.getString("branch_id", "");

        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            next.setText(getResources().getString(R.string.next_en));
            autoCompletebranch.setHint(getResources().getString(R.string.select_search_br_en));
            phone.setHint(getResources().getString(R.string.mobileno_en));
            nameofpicker.setHint(getResources().getString(R.string.nameofpicker_en));
            comment.setHint(getResources().getString(R.string.comment_en));
        } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            next.setText(getResources().getString(R.string.next_ar));
            autoCompletebranch.setHint(getResources().getString(R.string.select_search_br_ar));
            phone.setHint(getResources().getString(R.string.mobileno_ar));
            nameofpicker.setHint(getResources().getString(R.string.nameofpicker_ar));
            comment.setHint(getResources().getString(R.string.comment_ar));
        }
        alert = builder.create();
        autoCompletebranch.setThreshold(0);
        autoCompletebranch.setAdapter(new BranchArrayAdapter(SummaryActivity.this, R.layout.textview, branchlist));

        for (int i = 0; i < branchlist.size(); i++) {
            if (brid.equalsIgnoreCase(branchlist.get(i).getId())) {
                autoCompletebranch.setText(branchlist.get(i).getName());
                selectedbranch = branchlist.get(i);
                break;
            }
        }
        autoCompletebranch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("before", "before");
                autoCompletebranch.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("change", "change");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("after", "after");
                selectedbranch = null;
            }
        });
        autoCompletebranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BranchArrayAdapter adapter = ((BranchArrayAdapter)
                        autoCompletebranch.getAdapter());
                selectedbranch = ((Branch) adapter
                        .getItem(position));
                Log.e("selection", "selection");
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }
            }
        });

        next.setOnClickListener((v1) -> {
            View view1 = getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
            str_phone = phone.getText().toString();
            str_picker = nameofpicker.getText().toString();
            str_comments = comment.getText().toString();
            boolean isvalidate = false;
            if (str_phone.equalsIgnoreCase("")) {
                phone.requestFocus();
                // name.setBackgroundResource(R.drawable.bottomline_selector);
                // name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                phone.setBackgroundResource(R.drawable.bottom_line_red);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.mobileno_msg_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.mobileno_msg_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } else if (!str_phone.matches(apath.MobilePattern)) {
                phone.requestFocus();
                phone.setBackgroundResource(R.drawable.bottom_line_red);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.mobileno_valid_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.mobileno_valid_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } else if (selectedbranch == null) {
                phone.setBackgroundResource(R.drawable.bottomline_selector);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                autoCompletebranch.setBackgroundResource(R.drawable.bottom_line_red);
                autoCompletebranch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.selectbr_msg_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.selectbr_msg_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } else if (str_picker.equalsIgnoreCase("")) {
                phone.setBackgroundResource(R.drawable.bottomline_selector);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                autoCompletebranch.setBackgroundResource(R.drawable.bottomline_selector);
                autoCompletebranch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                nameofpicker.setBackgroundResource(R.drawable.bottom_line_red);
                nameofpicker.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
                if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    Snackbar.make(mainlay, getString(R.string.nameofpicker_valid_en), Snackbar.LENGTH_LONG).show();
                } else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    Snackbar.make(mainlay, getString(R.string.nameofpicker_valid_ar), Snackbar.LENGTH_LONG).show();
                }
                isvalidate = false;
            } else {
                isvalidate = true;
            }
            if (isvalidate) {
                phone.setBackgroundResource(R.drawable.bottomline_selector);
                phone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                autoCompletebranch.setBackgroundResource(R.drawable.bottomline_selector);
                autoCompletebranch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                nameofpicker.setBackgroundResource(R.drawable.bottomline_selector);
                nameofpicker.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                if (selectedbranch != null) {
                    DataBaseHelper dbh = new DataBaseHelper(SummaryActivity.this);
                    boolean isinserted = dbh.insertBranch(selectedbranch, apath.getUserid(SummaryActivity.this));
                }
                // SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("phone", str_phone);
                editor.putString("branch_id", selectedbranch.getId());
                editor.putString("branch_name", selectedbranch.getName());
                editor.putString("branch_open", selectedbranch.getOpen());
                editor.putString("branch_close", selectedbranch.getClose());
                editor.putString("branch_address", selectedbranch.getAddress());
                editor.putString("branch_phone", selectedbranch.getPhone());
                editor.putString("picker", str_picker);
                editor.putString("comment", str_comments);
                editor.putString("branch_open_lang", selectedbranch.getOpen_lang());
                editor.putString("branch_close_lang", selectedbranch.getClose_lang());
                editor.commit();
                alert.cancel();
                showAddress("branch");
            }
        });
        alert.show();

    }

    void priceUpdate() {
        DecimalFormat df2 = new DecimalFormat("#.##");
        deliveryvalue.setText(df2.format(deliverycharge) + " JD");
        //double taxpr = (subtprice + offerdis + extraaddJD + deliverycharge) * taxpriceJD;
        double taxpr = (subtprice + offerdis + extraaddJD) * taxpriceJD;
        taxvalue.setText(df2.format(taxpr) + " JD");
        tax_calculated = taxpr;
        double totaljd = subtprice + offerdis + taxpr + deliverycharge + extraaddJD;
        totalpricevalue.setText(df2.format(totaljd) + " JD");
        subpricevalue.setText(df2.format(subtprice) + " JD");
        if (offerdis < 0) {
            discountlay.setVisibility(View.VISIBLE);
            discountvalue.setText(df2.format(offerdis) + " JD");
        } else {
            discountlay.setVisibility(View.GONE);
        }
        totalJD = totaljd;
    }

    void showAddress(String type) {
        SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
        String address = "";
        addresstext.setText("");
        if (type.equalsIgnoreCase("area")) {
            address = sp.getString("picker", "");
            if (!sp.getString("phone", "").equalsIgnoreCase("")) {
                address = address + ", " + sp.getString("phone", "");
                String next = "<font color='" + getResources().getColor(R.color.colorPrimary) + "'>" + address + "</font> <br>";
                address = next;
            }
            if (!sp.getString("area_name", "").equalsIgnoreCase(""))
                address = address + sp.getString("area_name", "");
            if (!sp.getString("street", "").equalsIgnoreCase(""))
                address = address + ", " + sp.getString("street", "");
            if (!sp.getString("building", "").equalsIgnoreCase(""))
                address = address + ", " + sp.getString("building", "");
            if (!sp.getString("near", "").equalsIgnoreCase(""))
                address = address + ", " + sp.getString("near", "");
            /*if (!sp.getString("phone", "").equalsIgnoreCase(""))
                address = address + ", " + sp.getString("phone", "");*/
            if (!sp.getString("comment", "").equalsIgnoreCase(""))
                address = address + ", " + sp.getString("comment", "");
        }
        if (type.equalsIgnoreCase("branch")) {
            address = sp.getString("picker", "");
            if (!sp.getString("phone", "").equalsIgnoreCase(""))
                address = address + ", " + sp.getString("phone", "");
            String next = "<font color='" + getResources().getColor(R.color.colorPrimary) + "'>" + address + "</font>";
            address = next;
            address = address + ", " + sp.getString("comment", "") + "<br>";
            address = address + sp.getString("branch_name", "") + ", ";
            address = address + sp.getString("branch_address", "") + ", ";
            address = address + sp.getString("branch_open_lang", "") + " - " + sp.getString("branch_close_lang", "") + ", " + sp.getString("branch_phone", "");

        }
        addresstext.setText(Html.fromHtml(address));
//        if (!sp.getString("phone", "").equalsIgnoreCase(""))
//            addresstext.setText(address);
//        else
//            addresstext.setText("");
    }

    void getDiscount() {
        try {
            usecode.setClickable(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(SummaryActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.DISCOUNT), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        Log.e("response", response + "  coupon ");
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            offercode.setEnabled(false);
                            usecode.setTextColor(getResources().getColor(R.color.gray));
                            JSONArray jarr = jobj.optJSONArray("data");
                            if (jarr.length() > 0) {
                                JSONObject jo = jarr.optJSONObject(0);
                                Double temp = Double.parseDouble(jo.optString("fee"));
                                offerdis = -(((subtprice + extraaddJD) * temp));
                                priceUpdate();
                            }

                        } else {
                            usecode.setClickable(true);
                            offercode.setEnabled(true);

                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    alertDialog.cancel();
                    usecode.setClickable(true);
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
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("action", "check_coupon_code");
                    params.put("coupon_code", offercode.getText().toString());
                    params.put("lang", apath.getLang(SummaryActivity.this));
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
            Singleton.getIntance(SummaryActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void getBranchByID(String branchid, String formattedDate, String later) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(SummaryActivity.this);
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
                                showAlert(formattedDate, later);
                            } else {
                                Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                            }

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(SummaryActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(SummaryActivity.this));
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
            Singleton.getIntance(SummaryActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
