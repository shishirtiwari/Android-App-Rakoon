package com.rakoon.restaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edesign.stspayment.Payment.PaymentRequest;
import com.edesign.stspayment.Payment.PaymentResponse;
import com.edesign.stspayment.Payment.PaymentService;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CardPaymentActivity extends AppCompatActivity {
    @BindView(R.id.atitle)
    TextView atitle;
    @BindView(R.id.amount)
    TextView amount_txt;
    @BindView(R.id.dtitle)
    TextView dtitle;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.ttitle)
    TextView ttitle;
    @BindView(R.id.tnumber)
    TextView tnumber;
    @BindView(R.id.nametitle)
    TextView nametitle;
    @BindView(R.id.numbertitle)
    TextView numbertitle;
    @BindView(R.id.mtitle)
    TextView mtitle;
    @BindView(R.id.ytitle)
    TextView ytitle;
    @BindView(R.id.cvvtitle)
    TextView cvvtitle;
    @BindView(R.id.paynow)
    TextView paynow;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.number)
    EditText number;
    @BindView(R.id.month)
    EditText month;
    @BindView(R.id.year)
    EditText year;
    @BindView(R.id.cvv)
    EditText cvv;

    @BindView(R.id.mainlay)
    RelativeLayout mainlay;

    Unbinder unbinder;
    AllPath apath;
    ProgressDialog progressDialog;

    public static String amount = "";
    String ip = "";
    Integer[] imageArray = {R.drawable.vis, R.drawable.mastercard, R.drawable.discover, R.drawable.americanexpress};
    AlertDialog alertDialog;

    @Override
    protected void onDestroy() {
        if (progressDialog != null)
            progressDialog.cancel();
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = getIntent();
            if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                intent.putExtra("message", "");
            } else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
        setContentView(R.layout.activity_card_payment);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        getSupportActionBar().setTitle("Card Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.cardpayment_en));
            atitle.setText(getResources().getString(R.string.amount_en));
            dtitle.setText(getResources().getString(R.string.date_en));
            ttitle.setText(getResources().getString(R.string.transaction_en));
            nametitle.setText(getResources().getString(R.string.nameofcard_en));
            numbertitle.setText(getResources().getString(R.string.card_number_en));
            mtitle.setText(getResources().getString(R.string.expire_month_en));
            ytitle.setText(getResources().getString(R.string.expire_year_en));
            cvvtitle.setText(getResources().getString(R.string.cvv_en));
            paynow.setText(getResources().getString(R.string.paynow_en));
            name.setHint(getResources().getString(R.string.nameofcard_en));
            number.setHint(getResources().getString(R.string.card_number_en));
            month.setHint(getResources().getString(R.string.mm_en));
            year.setHint(getResources().getString(R.string.yy_en));
            cvv.setHint(getResources().getString(R.string.cvv_en));

        } else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.cardpayment_ar));
            atitle.setText(getResources().getString(R.string.amount_ar));
            dtitle.setText(getResources().getString(R.string.date_ar));
            ttitle.setText(getResources().getString(R.string.transaction_ar));
            nametitle.setText(getResources().getString(R.string.nameofcard_ar));
            numbertitle.setText(getResources().getString(R.string.card_number_ar));
            mtitle.setText(getResources().getString(R.string.expire_month_ar));
            ytitle.setText(getResources().getString(R.string.expire_year_ar));
            cvvtitle.setText(getResources().getString(R.string.cvv_ar));
            paynow.setText(getResources().getString(R.string.paynow_ar));
            name.setHint(getResources().getString(R.string.nameofcard_ar));
            number.setHint(getResources().getString(R.string.card_number_ar));
            month.setHint(getResources().getString(R.string.mm_ar));
            year.setHint(getResources().getString(R.string.yy_ar));
            cvv.setHint(getResources().getString(R.string.cvv_ar));
        }
        String dat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date.setText(dat);

        //Log.e("random123:-- ",String.valueOf(generateRandom()));
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ccNum = s.toString();

                if (ccNum.length() >= 2) {
                    for (int i = 0; i < listOfPattern().size(); i++) {
                        if (ccNum.substring(0, 2).matches(listOfPattern().get(i))) {
                            number.setCompoundDrawablesWithIntrinsicBounds(0, 0, imageArray[i], 0);
                            // cardtype = String.valueOf(i);
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!number.getText().toString().equalsIgnoreCase("")) {
                    for (int i = 0; i < listOfPattern().size(); i++) {
                        if (number.getText().toString().matches(listOfPattern().get(i))) {
                            number.setCompoundDrawablesWithIntrinsicBounds(0, 0, imageArray[i], 0);

                            // cardtype = String.valueOf(i);
                        }
                    }
                } else {
                    number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card, 0);
                }
            }
        });

        if (getIntent() != null) {
            amount = getIntent().getStringExtra("amount");
            amount_txt.setText(amount + " JD");
            tnumber.setText(String.valueOf(generateRandom()));
        }

        ip = getLocalIpAddress();
        Log.d("ExternalIP", ip);

        amount_txt.setText("1000 JD");
        amount = "1";
        name.setText("omar");
        number.setText("4012001037141112");
        month.setText("01");
        cvv.setText("684");
        year.setText("22");

        paynow.setOnClickListener((v) -> {
            //  submit();
            if (apath.isInternetOn(CardPaymentActivity.this)) {
                sendPayment();
            } else {
                String msg = "", str_action = "";
                if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    msg = getResources().getString(R.string.internet_message_en);
                    str_action = getResources().getString(R.string.enable_en);
                } else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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

    }

    public static long generateRandom() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // first not 0 digit
        sb.append(random.nextInt(9) + 1);

        // rest of 11 digits
        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }

        return Long.valueOf(sb.toString()).longValue();
    }

    public static ArrayList<String> listOfPattern() {
        ArrayList<String> listOfPattern = new ArrayList<String>();

        String ptVisa = "^4[0-9]JD";

        listOfPattern.add(ptVisa);

        String ptMasterCard = "^5[1-5]JD";

        listOfPattern.add(ptMasterCard);

        String ptDiscover = "^6(?:011|5[0-9]{2})JD";

        listOfPattern.add(ptDiscover);

        String ptAmeExp = "^3[47]JD";

        listOfPattern.add(ptAmeExp);

        return listOfPattern;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("hjkhjkllhkjh", "***** IP=" + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("jhkjhkjkjh", ex.toString());
        }
        return null;
    }

    PaymentRequest request;
    PaymentService service;

    public void submit() {
        if (name.getText().toString().isEmpty()) {
            if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getResources().getString(R.string.cardname_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getResources().getString(R.string.cardname_ar), Snackbar.LENGTH_LONG).show();
            }
        } else if (number.getText().toString().isEmpty()) {
            if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getResources().getString(R.string.cardnumber_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getResources().getString(R.string.cardnumber_ar), Snackbar.LENGTH_LONG).show();
            }
        } else if (month.getText().toString().isEmpty()) {
            if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getResources().getString(R.string.expirymonth_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getResources().getString(R.string.expirymonth_ar), Snackbar.LENGTH_LONG).show();
            }
        } else if (year.getText().toString().isEmpty()) {
            if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getResources().getString(R.string.expiryyear_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getResources().getString(R.string.expiryyear_ar), Snackbar.LENGTH_LONG).show();
            }
        } else if (cvv.getText().toString().isEmpty()) {
            if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getResources().getString(R.string.cvvnumber_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getResources().getString(R.string.cvvnumber_ar), Snackbar.LENGTH_LONG).show();
            }
        } else {

            progressDialog = new ProgressDialog(CardPaymentActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("please wait processing payment request...");
            progressDialog.show();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    progressDialog.dismiss();

                }
                //   catch (Exception e)
                //    {
                //      Log.e("ok123", e.getMessage());
                //  }

            }, 4000);
            //   Double d=Double.valueOf(amount)*1000;
            //  Integer i=d.intValue();
            //  amount=String.valueOf(i);
            request = new PaymentRequest();

            request.setPaymentType("smartRoute");
            request.isLogging(true);
            request.setPaymentAuthenticationToken("AuthenticationToken", "NGRkNzRjMDMyYzM5NjJlYWRiZTk2MTFi");
            /*request.add("MessageID", "9");
            request.add("TransactionID", tnumber.getText().toString());
            request.add("MerchantID", "2000000021");
            request.add("Amount", amount);
            request.add("PaymentMethod", "1");

            request.add("CardHolderName", name.getText().toString());
            request.add("CardNumber", number.getText().toString());
            request.add("ExpiryDateMonth", month.getText().toString());
            request.add("ExpiryDateYear", year.getText().toString());
            request.add("SecurityCode", cvv.getText().toString());
            request.add("CurrencyISOCode", "400");

            request.add("ClientIPaddress", ip);
            request.add("PaymentDescription", "LunchBill719");
            request.add("Quantity", "1");
            request.add("ItemID", "310");
            request.add("Channel", "0");*/


            request.add("MessageID", "2");
            request.add("TransactionID", tnumber.getText().toString());
            request.add("MerchantID", "2000000021");
            request.add("Amount", amount);
            request.add("PaymentMethod", "1");

            request.add("CardHolderName", name.getText().toString());
            request.add("CardNumber", number.getText().toString());
            request.add("ExpiryDateMonth", month.getText().toString());
            request.add("ExpiryDateYear", year.getText().toString());
            request.add("SecurityCode", cvv.getText().toString());
            request.add("CurrencyISOCode", "400");

            request.add("ClientIPaddress", ip);
            request.add("PaymentDescription", "LunchBill719");
            request.add("Quantity", "1");
            request.add("ItemID", "310");
            request.add("Channel", "0");

            service = new PaymentService(this);


            //Log.d("ipaddress",ip);
            //Toast.makeText(getApplicationContext(),"ipaddress"+ip,Toast.LENGTH_SHORT).show();
            // new Loaddata().execute();
            //new  AsyncTask<>().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            //  try
            // {
            PaymentResponse response = service.process(request);

            Log.e("responce123yaz", response.getResponse().toString());
            Log.e("execode", response.get("execCode") + " ;;;  " + response.toString());

            if (response.getResponse() != null && response.get("execCode").equalsIgnoreCase("0")) {
                Log.e("getResponse", "1");
                if (response.getResponse().containsKey("Response.StatusCode") && response.getResponse().get("Response.StatusCode").toString().equalsIgnoreCase("00000")) {
                    Log.e("getResponse", "2");
                    if (response.getResponse().containsKey("Response.GatewayStatusCode") && response.getResponse().get("Response.GatewayStatusCode").toString().equalsIgnoreCase("0000")) {
                        Log.e("getResponse", "3");
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("message", response.getResponse().get("Response.GatewayStatusDescription").toString());
                        returnIntent.putExtra("tid", response.getResponse().get("Response.TransactionID").toString());
                        setResult(Activity.RESULT_OK, returnIntent);
                        /*Intent intent = new Intent(getApplicationContext(), OrderPaymentStatus.class);
                        intent.putExtra(ConstVariable.PaymentStatusID, response.getResponse().get("Response.StatusCode").toString());
                        intent.putExtra(ConstVariable.PaymentStatus, response.getResponse().get("Response.StatusDescription").toString());
                        intent.putExtra(ConstVariable.TransactionID, response.getResponse().get("Response.TransactionID").toString());
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        intent.putExtra(ConstVariable.TransactionDate, currentDateTimeString);
                        intent.putExtra(ConstVariable.PRICE, response.getResponse().get("Response.Amount").toString());
                        startActivity(intent);
                        Utils.toastTxt("Payment process successfully", CardPayment.this);
                        finish();*/
                        Toast.makeText(CardPaymentActivity.this, "success", Toast.LENGTH_LONG).show();
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        AlertDialog.Builder builder = new AlertDialog.Builder(CardPaymentActivity.this);
                        builder.setTitle("Payment process successfully");
                        builder.setMessage("Payment status ID :- " + response.getResponse().get("Response.StatusCode").toString() + "\n" +
                                "Transaction ID:- " + response.getResponse().get("Response.TransactionID").toString() + "\n" +
                                "Payment Status:- " + response.getResponse().get("Response.StatusDescription").toString() + "\n" +
                                "Amount:- " + response.getResponse().get("Response.Amount").toString() + "\n" +
                                "Transaction Date:-" + currentDateTimeString);
                        builder.setCancelable(false);
                        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.show();

                    }
                } else {
                    Log.e("getResponse", "4");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("message", response.getResponse().get("Response.StatusDescription").toString());
                    returnIntent.putExtra("tid", tnumber.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                /*    Intent intent = new Intent(getApplicationContext(), OrderPaymentStatus.class);
                    intent.putExtra(ConstVariable.PaymentStatusID, response.getResponse().get("Response.StatusCode").toString());
                    intent.putExtra(ConstVariable.PaymentStatus, response.getResponse().get("Response.StatusDescription").toString());
                    intent.putExtra(ConstVariable.TransactionID, transId.getText().toString());
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    intent.putExtra(ConstVariable.TransactionDate, currentDateTimeString);
                    intent.putExtra(ConstVariable.PRICE, amount.toString());
                    startActivity(intent);
                    Utils.toastTxt("Payment process failed, try again later", CardPayment.this);
                    finish();*/
                    Toast.makeText(CardPaymentActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    AlertDialog.Builder builder = new AlertDialog.Builder(CardPaymentActivity.this);
                    builder.setTitle("Payment process failed, try again later");
                    builder.setMessage("Payment status ID :- " + response.getResponse().get("Response.StatusCode").toString() + "\n" +
                            "Transaction ID:- " + tnumber.getText().toString() + "\n" +
                            "Payment Status:- " + response.getResponse().get("Response.StatusDescription").toString() + "\n" +
                            "Amount:- " + amount.toString() + "\n" +
                            "Transaction Date:-" + currentDateTimeString);
                    builder.setCancelable(false);
                    builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                }
            }

        }
    }

    void sendPayment() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(CardPaymentActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(CardPaymentActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.PAYMENT), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        Log.e("response ppp", response + "  branch detail ");
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {


                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(CardPaymentActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                  //  params.put("lang", apath.getLang(CardPaymentActivity.this));
                    params.put("amount", "4.5");
                    params.put("CardNumber", "5271045423029111");
                    params.put("CardHolderName", "rakoon");
                    params.put("SecurityCode", "684");
                    params.put("ExpiryDateYear", "22");
                    params.put("ExpiryDateMonth", "01");

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
            Singleton.getIntance(CardPaymentActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
