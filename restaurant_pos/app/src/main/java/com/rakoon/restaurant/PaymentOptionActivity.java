package com.rakoon.restaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rakoon.restaurant.datasource.DataBaseHelper;
import com.rakoon.restaurant.model.Area;
import com.rakoon.restaurant.model.Branch;
import com.rakoon.restaurant.model.Cart;
import com.rakoon.restaurant.model.ExtraStuff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PaymentOptionActivity extends AppCompatActivity {
    AllPath apath;
    Unbinder unbinder;
    @BindView(R.id.totalprice)
    TextView totalprice;
    @BindView(R.id.totalpricevalue)
    TextView totalpricevalue;
    @BindView(R.id.paymentoptions)
    TextView paymentoptions;
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    @BindView(R.id.cashondelivery)
    RadioButton cashondelivery;
    @BindView(R.id.cashonpickup)
    RadioButton cashonpickup;
    @BindView(R.id.visamastercard)
    RadioButton visamastercard;
    @BindView(R.id.paypal)
    RadioButton paypal;
    @BindView(R.id.ordernow)
    TextView ordernow;
    @BindView(R.id.mainlay)
    ConstraintLayout mainlay;

    double taxpriceJD = 0.16, deliverycharge = 0.0, extraaddJD = 0.0, subtprice = 0.0, offerdis = 0.0, totalJD = 0.0;
    AlertDialog alertDialog;
    private static String  live_client_id="AQZ15K8vdtVgfe_VHZdVeI-KpQsXIp6gakPXH1cW7kvMiYnB8JP6nKtm3HINHc5iZ5YVtsbOt0LXJj-f";
    private static String sandbox_client_id="AYwXG4QsE2naYm0_Zkbx_HoUVx3Xfr_OGR17yTc8vRBsu5RB1YRoz3f93fpHbwEG1tWO92C-9zrEb604";
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(live_client_id);

    ArrayList<Cart> cartlist;
    ArrayList<ExtraStuff> extralist;
    String txnid = "";
    Branch baa = null;
    Area aa = null;
    JSONObject jobj;
    String address, offercode, ordertimingstatus, later_date;


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
            if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                intent.putExtra("message", "");
            } else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
        setContentView(R.layout.activity_payment_option);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        jobj = new JSONObject();
        getSupportActionBar().setTitle("Payment Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.paymentoption_en));
            totalprice.setText(getResources().getString(R.string.totalprice_en));
            paymentoptions.setText(getResources().getString(R.string.paymentoption_en));
            cashondelivery.setText(getResources().getString(R.string.cashondelivery_en));
            cashonpickup.setText(getResources().getString(R.string.cashonpickup_en));
            visamastercard.setText(getResources().getString(R.string.visamastercard_en));
            paypal.setText(getResources().getString(R.string.paypal_en));
            ordernow.setText(getResources().getString(R.string.ordernow_en));

        } else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.paymentoption_ar));
            totalprice.setText(getResources().getString(R.string.totalprice_ar));
            paymentoptions.setText(getResources().getString(R.string.paymentoption_ar));
            cashondelivery.setText(getResources().getString(R.string.cashondelivery_ar));
            cashonpickup.setText(getResources().getString(R.string.cashonpickup_ar));
            visamastercard.setText(getResources().getString(R.string.visamastercard_ar));
            paypal.setText(getResources().getString(R.string.paypal_ar));
            ordernow.setText(getResources().getString(R.string.ordernow_ar));
        }
        cashondelivery.setVisibility(View.GONE);
        cashonpickup.setVisibility(View.GONE);
        paypal.setVisibility(View.GONE);
        visamastercard.setVisibility(View.GONE);
        DataBaseHelper dbh = new DataBaseHelper(PaymentOptionActivity.this);
        aa = dbh.getArea(apath.getUserid(PaymentOptionActivity.this));
        String cate_id = dbh.getcategoryid(apath.getUserid(PaymentOptionActivity.this));
        if (aa != null) {
            cashonpickup.setVisibility(View.GONE);
            cashondelivery.setVisibility(View.VISIBLE);
            branchDetail(aa.getBranch_id(), "delivery");
        } else {
            baa = dbh.getBranch(apath.getUserid(PaymentOptionActivity.this));
            if (baa != null) {
                cashonpickup.setVisibility(View.VISIBLE);
                cashondelivery.setVisibility(View.GONE);
                branchDetail(baa.getId(), "pickup");
            } else {
                cashonpickup.setVisibility(View.VISIBLE);
                cashondelivery.setVisibility(View.VISIBLE);
            }
        }
        address = getIntent().getStringExtra("address");
        offercode = getIntent().getStringExtra("offercode");
        ordertimingstatus = getIntent().getStringExtra("ordertimingstatus");
        later_date = getIntent().getStringExtra("later_date");
        taxpriceJD = getIntent().getDoubleExtra("taxpriceJD", 0.0);
        deliverycharge = getIntent().getDoubleExtra("deliverycharge", 0.0);
        extraaddJD = getIntent().getDoubleExtra("extraaddJD", 0.0);
        subtprice = getIntent().getDoubleExtra("subtprice", 0.0);
        offerdis = getIntent().getDoubleExtra("offerdis", 0.0);
        totalJD = getIntent().getDoubleExtra("totalJD", 0.0);
        DecimalFormat df2 = new DecimalFormat("#.##");
        totalpricevalue.setText(df2.format(totalJD) + " JD");

        ordernow.setOnClickListener((v) -> {
            try {
                aa = dbh.getArea(apath.getUserid(PaymentOptionActivity.this));
                if (aa != null) {
                    jobj.put("area_id", aa.getId());
                    jobj.put("type", "delivery");
                    jobj.put("branch_id", aa.getBranch_id());
                } else {
                    baa = dbh.getBranch(apath.getUserid(PaymentOptionActivity.this));
                    if (baa != null) {
                        jobj.put("area_id", "");
                        jobj.put("type", "pickup");
                        jobj.put("branch_id", baa.getId());
                    }
                }
            } catch (Exception e) {
                Log.e("Excep...", e.toString());
            }
            int id = radiogroup.getCheckedRadioButtonId();
            switch (id) {
                case R.id.cashondelivery:
                    try {
                        jobj.put("payment_mode", "COD");
                        jobj.put("transaction_id", "");
                    } catch (Exception e) {
                        Log.e("Excp COD", e.toString());
                    }
                    bookOrder(jobj);
                    break;
                case R.id.cashonpickup:
                    try {
                        jobj.put("payment_mode", "COP");
                        jobj.put("transaction_id", "");
                    } catch (Exception e) {
                        Log.e("Excp COP", e.toString());
                    }
                    bookOrder(jobj);
                    break;
                case R.id.visamastercard:
                  /*  Intent i = new Intent(PaymentOptionActivity.this, CardPaymentActivity.class);
                    i.putExtra("amount", df2.format(totalJD));
                    startActivityForResult(i, 1);*/
                  if (apath.isInternetOn(PaymentOptionActivity.this)) {
                      Intent i = new Intent(PaymentOptionActivity.this, PaymentCardActivity.class);
                      i.putExtra("amount", df2.format(totalJD));
                      startActivityForResult(i, 1);
                  }
                  else
                  {
                      String msg = "", str_action = "";
                      if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                          msg = getResources().getString(R.string.internet_message_en);
                          str_action = getResources().getString(R.string.enable_en);
                      } else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
                    // Toast.makeText(PaymentOptionActivity.this, "Visa/Master Card", Toast.LENGTH_LONG).show();
                    break;
                case R.id.paypal:
                    //  Toast.makeText(PaymentOptionActivity.this, "Paypal", Toast.LENGTH_LONG).show();
                    onBuyPressed();
                    break;
            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        cartlist = new ArrayList<>();
        extralist = new ArrayList<>();
        cartlist = dbh.getCartlist(apath.getUserid(PaymentOptionActivity.this));
        extralist = dbh.getextraStuff();
       /* for (int i = 0; i < extralist.size(); i++) {
            Cart c = new Cart();
            ExtraStuff ex = extralist.get(i);
            c.setId(ex.getId());
            c.setQuantity(ex.getQuantity());
            c.setName(ex.getName());
            c.setPhoto(ex.getPhoto());
            c.setPrice(ex.getPrice());
            c.setModification("extra");
            cartlist.add(c);
        }*/

        try {
            jobj.put("address", address);
            jobj.put("offercode", offercode);
            jobj.put("order_type", ordertimingstatus);
            jobj.put("later_date", later_date);
            jobj.put("taxpriceJD", taxpriceJD);
            jobj.put("deliverycharge", deliverycharge);
            jobj.put("extra_price", extraaddJD);
            jobj.put("subtotal", subtprice);
            jobj.put("offerdis", df2.format(offerdis));
            jobj.put("total", df2.format(totalJD));
            jobj.put("email", apath.getEmail(PaymentOptionActivity.this));
            jobj.put("userid", apath.getUserid(PaymentOptionActivity.this));
            JSONArray jarr = new JSONArray();
            for (int i = 0; i < cartlist.size(); i++) {
                Cart c = cartlist.get(i);
                JSONObject jo = new JSONObject();
                jo.put("id", c.getId());
                //  jo.put("cat_id", c.getCat_id());
                //  jo.put("cat_name", c.getCat_name());
                //  jo.put("name", c.getName());
                jo.put("price", c.getPrice());
                //  jo.put("tax", c.getTax());
                //  jo.put("photo", c.getPhoto());
                //  jo.put("modification", c.getModification());
                //  jo.put("description", c.getDescription());
                //  jo.put("userid", c.getUserid());
                jo.put("quantity", c.getQuantity());
                jo.put("required_id", c.getRequired_id());
                // jo.put("required_name", c.getRequired_name());
                jo.put("required_price", c.getRequired_price());
                jo.put("optional_id", c.getOptional_id());
                // jo.put("optional_name", c.getOptional_name());
                jo.put("optional_price", c.getOptional_price());
                jarr.put(jo);
            }
            JSONArray jarrextra = new JSONArray();
            for (int i = 0; i < extralist.size(); i++) {
                ExtraStuff c = extralist.get(i);
                JSONObject jo = new JSONObject();
                jo.put("id", c.getId());
                // jo.put("name", c.getName());
                // jo.put("photo", c.getPhoto());
                jo.put("price", c.getPrice());
                jo.put("quantity", c.getQuantity());
                jarrextra.put(jo);
            }
            jobj.put("Extrastuff", jarrextra);
            jobj.put("items", jarr);
        } catch (Exception e) {

        }

    }

    public void onBuyPressed() {
        // totalAmount="1000";
        PayPalPayment payment = new PayPalPayment(new BigDecimal(totalJD), "USD", "Order Amount :-", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("paymentExample", confirm.toJSONObject().toString(4));
                        JSONObject json = new JSONObject(confirm.toJSONObject().toString(4));
                        JSONObject json1 = new JSONObject(json.getJSONObject("response").toString());
                        txnid = json1.getString("id");
                        //  bookItems();
                        try {
                            jobj.put("payment_mode", "PayPal");
                            jobj.put("transaction_id", txnid);
                            bookOrder(jobj);
                        } catch (Exception e) {
                            Log.e("Excp COP", e.toString());
                        }
                        //  bookOrder(jobj);
                        // TODO: send 'confirm' to your server for verification.
                        // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                        // for more details.
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred:", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(PaymentOptionActivity.this,"The user cancelled",Toast.LENGTH_LONG).show();
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(PaymentOptionActivity.this,"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.",Toast.LENGTH_LONG).show();
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String msg = data.getStringExtra("strmsg");
                String TransactionID = data.getStringExtra("TransactionID");
                String response = data.getStringExtra("response");
                txnid = TransactionID;//data.getStringExtra("tid");
               // Toast.makeText(PaymentOptionActivity.this, msg, Toast.LENGTH_LONG).show();
                try {
                    jobj.put("payment_mode", "Card");
                    jobj.put("transaction_id", txnid);
                    if (response != null && !response.equalsIgnoreCase(""))
                    {
                        JSONObject jb= new JSONObject(response);
                        if (jb.optString("status").equalsIgnoreCase("0"))
                        {
                            bookOrder(jobj);
                        }
                    }
                      //  bookOrder(jobj);
                } catch (Exception e) {
                    Log.e("Excp COP", e.toString());
                }

//                pid = "3";
//                bookItems();
            }
        }
    }

    void bookOrder(JSONObject jobj) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentOptionActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(PaymentOptionActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.BOOKORDER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        Log.e("response", response + "  book order ");
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                           // Log.e("response", response + "  book order ");
                            DataBaseHelper dbh = new DataBaseHelper(PaymentOptionActivity.this);
                            boolean cleared = dbh.clearCart(apath.getUserid(PaymentOptionActivity.this));
                            AlertDialog alert;
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(PaymentOptionActivity.this);
                            View vv = getLayoutInflater().inflate(R.layout.order_success_popup, null);
                            TextView ok = vv.findViewById(R.id.ok);
                            TextView message = vv.findViewById(R.id.message);
                            if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                                message.setText(getResources().getString(R.string.order_confirm_msg_en));
                                ok.setText(getResources().getString(R.string.ok_en));
                            } else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                                message.setText(getResources().getString(R.string.order_confirm_msg_ar));
                                ok.setText(getResources().getString(R.string.ok_ar));
                            }

                            builder1.setView(vv);
                            alert = builder1.create();
                            alert.setCancelable(false);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.cancel();
                                    Intent intent = new Intent(PaymentOptionActivity.this, DashBoardActivity2.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            alert.show();

                           /* Snackbar snackbar = Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            snackbar.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    Intent intent = new Intent(PaymentOptionActivity.this, DashBoardActivity2.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                }
                            });
*/
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(PaymentOptionActivity.this));
                    params.put("action", "submit_order");
                    params.put("data", jobj.toString());
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
            Singleton.getIntance(PaymentOptionActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getBranchinfoByID(String branchid, String type) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentOptionActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(PaymentOptionActivity.this);
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
                                JSONArray arrJson = job.getJSONArray("payment_type");
                                String[] payment_type = new String[arrJson.length()];
                                for (int j = 0; j < arrJson.length(); j++) {
                                    payment_type[j] = arrJson.getString(j);
                                    Log.e("string arra ", payment_type[j] + " value");
                                }
                                handleOPaymentmode(payment_type, type);
                            } else {
                                Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                            }

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(PaymentOptionActivity.this));
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
            Singleton.getIntance(PaymentOptionActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void branchDetail(String branchid, String type) {
        if (apath.isInternetOn(PaymentOptionActivity.this)) {
            getBranchinfoByID(branchid, type);
        } else {
            String msg = "", str_action = "";
            if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(PaymentOptionActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            });
            snackbar.show();
        }

    }

    void handleOPaymentmode(String[] arr, String type) {
        Log.e("type", type);
        cashondelivery.setVisibility(View.GONE);
        cashonpickup.setVisibility(View.GONE);
        visamastercard.setVisibility(View.GONE);
        paypal.setVisibility(View.GONE);
        if (arr.length == 0)
            ordernow.setVisibility(View.GONE);
        boolean b=false;
        for (int i = 0; i < arr.length; i++) {
            String arrval = arr[i];
            Log.e("val", arrval);
            /* cashDelivery
                cashPickUp
visa
paypal*/
            if (arrval.equalsIgnoreCase("cashDelivery") && type.equalsIgnoreCase("delivery")) {
                cashondelivery.setVisibility(View.VISIBLE);
                cashondelivery.setChecked(true);
                b=true;
            }
            if (arrval.equalsIgnoreCase("cashPickUp") && type.equalsIgnoreCase("pickup")) {
                cashonpickup.setVisibility(View.VISIBLE);
                cashonpickup.setChecked(true);
                b=true;
            }
            if (arrval.equalsIgnoreCase("visa")) {
                visamastercard.setVisibility(View.VISIBLE);
                if (!b) {
                    visamastercard.setChecked(true);
                    b=true;
                }

            }
            if (arrval.equalsIgnoreCase("paypal")) {
                paypal.setVisibility(View.VISIBLE);
                if (!b) {
                    paypal.setChecked(true);
                    b=true;
                }
            }

        }
    }
}
