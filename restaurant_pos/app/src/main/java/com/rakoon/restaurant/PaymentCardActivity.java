package com.rakoon.restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
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
import com.rakoon.restaurant.util.DateTimeUtils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PaymentCardActivity extends AppCompatActivity {

    AlertDialog alertDialog;
    WebView webView;
    ProgressBar progressBar;
    Context activity;
    AllPath apath;
    boolean b = true;

    int mId; //Getting from Previous Activity
    private String mBaseURL = "http://www.uptorko.com/rakoon/wp-content/themes/atork_admin_panel/api/payment.php";// "https://secure.payu.in";

    private String userid = "0"; // For Final URL
    private String amount = "0"; // This will create below randomly

    private String mSuccessUrl = new AllPath().successurl;
    private String mFailedUrl = new AllPath().failureurl;

    boolean isFromOrder;
    Handler mHandler = new Handler();
    String TransactionID = "";

    /**
     * @param savedInstanceState
     */
    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getApplicationContext().getSharedPreferences("type", MODE_PRIVATE);
        if (sp.getString("type", "").equalsIgnoreCase("landscape"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        getActionBar().hide();
        apath = new AllPath();
        TransactionID = String.valueOf(generate4DigitRandom()) + String.valueOf(generateRandom());// UUID.randomUUID().toString();
        mSuccessUrl = apath.successurl;
        mFailedUrl = apath.failureurl;

        mBaseURL = apath.getMethodUrl(apath.PAYMENT);//apath.getGameurl();
        // mBaseURL = "https://cognosia.com/Mix_n_Match_Double_Trouble_App";
        userid = apath.getUserid(PaymentCardActivity.this);
        amount = getIntent().getStringExtra("amount");
        Log.e("URL%%%%+++++====", mBaseURL + "   ==  " + userid + "   ==   " + amount);

        setContentView(R.layout.activity_payment);

        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.card);

        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
        activity = getApplicationContext();
       /* LinearLayout gamelay = (LinearLayout) findViewById(R.id.game_launch);

        if (Build.VERSION.SDK_INT <= 10 || (Build.VERSION.SDK_INT >= 14 &&
                ViewConfiguration.get(this).hasPermanentMenuKey())) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 50);
            gamelay.setLayoutParams(layoutParams);
        } else {
            //No menu key
        }*/
        Random rand = new Random();
        String randomString = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);

        /**
         * WebView Client
         */
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(activity, "Oh no! error ", Toast.LENGTH_SHORT).show();
            }

                /* @Override
                public void onReceivedError(WebView view, WebResourceRequest request) {
                   // super.onReceivedError(view, request);

                }*/

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PaymentCardActivity.this);
                builder.setMessage("Error! unable to connect with server, press continue for try again.");
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("url####", url);
                // if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                if (url.contains(mSuccessUrl)) {
                    if (apath.isInternetOn(PaymentCardActivity.this))
                        checkPaymentStatus();
                    else {
                        String msg = "", str_action = "";
                        if (apath.getLanguage(PaymentCardActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                            msg = getResources().getString(R.string.internet_message_en);
                            str_action = getResources().getString(R.string.enable_en);
                        } else if (apath.getLanguage(PaymentCardActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                            msg = getResources().getString(R.string.internet_message_ar);
                            str_action = getResources().getString(R.string.enable_ar);
                        }

                        Snackbar.make(webView, msg, Snackbar.LENGTH_LONG)
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
             /*       view.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    Log.e("HTML", html);
                                    String string = html.substring(html.indexOf("{"), html.lastIndexOf("}") + 1);
                                    Log.e("String", string);
                                    str = string;
                                    displayPopup();
                                    // code here

                                }
                            });*/

                }

      /*          if (url.contains(mSuccessUrl)) {
                    if (b == true) {
                        Toast.makeText(PaymentCardActivity.this, "success", Toast.LENGTH_LONG).show();
                    }

                } else if (url.equals(mFailedUrl)) {
                    if (b == true) {
                        AlertDialog.Builder build = new AlertDialog.Builder(PaymentCardActivity.this);
                        build.setTitle("game failed try again");
                        build.setCancelable(false);
                        build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(PaymentCardActivity.this, "failed", Toast.LENGTH_LONG).show();
//                                Intent in = new Intent(PayUmoneyIntegration.this, MyWalletActivity.class);
//                               in.putExtra("id", mId+"");
                                finish();
                                //                               startActivity(in);
                            }
                        });
                        build.show();
                    }

                }*/
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadWithOverviewMode(false);
        // webView.addJavascriptInterface(new PayUJavaScriptInterface(PayUmoneyIntegration.this), "PayUMoney");

        /**
         * Mapping Compulsory Key Value Pairs[
         */
        Map<String, String> mapParams = new HashMap<>();

        mapParams.put("user_id", userid);
        mapParams.put("amount", amount);
        mapParams.put("TransactionID", TransactionID);
        //  mapParams.put("gameid", 19 + "");
        //  mapParams.put("surl", mSuccessUrl);
        //  mapParams.put("furl", mFailedUrl);

        Log.e("param..", mapParams.toString());
        mBaseURL = mBaseURL + "?amount=" + amount + "&user_id=" + userid + "&TransactionID=" + TransactionID;
        Log.e("mbase url", mBaseURL);
        if (apath.isInternetOn(PaymentCardActivity.this))
            webViewClientPost(webView, mBaseURL, mapParams.entrySet());
        else {
            if (apath.getLanguage(PaymentCardActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                finishScreen("", getResources().getString(R.string.internet_message_en));
            } else if (apath.getLanguage(PaymentCardActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                finishScreen("", getResources().getString(R.string.internet_message_ar));
            }
        }


    }


    /**
     * Posting Data on PayUMoney Site with Form
     *
     * @param webView
     * @param url
     * @param postData
     */
    public void webViewClientPost(WebView webView, String url,
                                  Collection<Map.Entry<String, String>> postData) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));

        for (Map.Entry<String, String> item : postData) {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");

        Log.e("TAG", "webViewClientPost called: " + sb.toString());
        webView.loadData(sb.toString(), "text/html", "utf-8");
    }

    /**
     * Hash Key Calculation
     *
     * @param type
     * @param str
     * @return
     */
    public String hashCal(String type, String str) {
        byte[] hashSequence = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashSequence);
            byte messageDigest[] = algorithm.digest();

            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append("0");
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException NSAE) {
        }
        return hexString.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onPressingBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onPressingBack();
    }

    /**
     * On Pressing Back
     * Giving Alert...
     */
    private void onPressingBack() {

        final Intent intent;


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentCardActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Warning");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to close the payment process?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                b = false;
                webView.clearCache(true);
                webView.canGoForward();
                webView.clearHistory();
                webView.destroy();
                finishScreen("", "Transaction have canceled");
                //  startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public class PayUJavaScriptInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        PayUJavaScriptInterface(Context c) {
            mContext = c;
        }

        public void success(long id, final String paymentId) {
            mHandler.post(new Runnable() {

                public void run() {
                    mHandler = null;

                }
            });
        }
    }

    void finishScreen(String response, String strmsg) {
        Intent intent = getIntent();
        intent.putExtra("strmsg", strmsg);
        intent.putExtra("TransactionID", TransactionID);
        intent.putExtra("response", response);
        setResult(RESULT_OK, intent);
        finish();
    }

    void displayPopup(String response, String strmsg) {
        try {
          /*  AlertDialog.Builder build = new AlertDialog.Builder(PaymentCardActivity.this);
            build.setTitle(strmsg);
            build.setCancelable(false);
            build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishScreen(response, strmsg);
                }
            });
            build.show();*/
            AlertDialog alert;
            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentCardActivity.this);
            View v = getLayoutInflater().inflate(R.layout.payment_alert, null);
            TextView paymentdescription = v.findViewById(R.id.paymentdescription);
            TextView paymentdstatus = v.findViewById(R.id.paymentstatus);
            TextView paymentdate = v.findViewById(R.id.paymentdate);
            TextView paymentamount = v.findViewById(R.id.paymentamount);
            TextView paymentid = v.findViewById(R.id.paymentid);
            JSONObject jobj = new JSONObject(response);
            JSONObject jo = jobj.optJSONObject("fullinfo");
            DecimalFormat df2 = new DecimalFormat("#.##");
            String date = DateTimeUtils.parseDateTime(jobj.optString("time"), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm a");
            //{"status":0,"message":"Transaction was processed successfully","time":"2019-04-18 13:24:15","fullinfo":{"Response_TransactionID":"96087079082890231072"}}
            //  double am = Double.parseDouble(jo.optString("Response_Amount"));
            // am = am / 1000;
           /* paymentdescription.setText(jo.optString("Response_GatewayStatusDescription"));
            paymentdstatus.setText("Status : " + jo.optString("Response_StatusDescription"));
            paymentdate.setText("Date : " + date);
            paymentamount.setText("Amount : " + df2.format(am) + " JD");
            paymentid.setText("Payment ID : " + jo.optString("Response_TransactionID"));*/

            paymentdescription.setText(strmsg);
            paymentdate.setText("Date : " + date);
            paymentamount.setText("Amount : " + amount + " JD");
            paymentid.setText("Payment ID : " + jo.optString("Response_TransactionID"));
            paymentdstatus.setVisibility(View.GONE);

            TextView ok = v.findViewById(R.id.ok);
            builder.setView(v);
            builder.setCancelable(false);
            alert = builder.create();
            alert.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.cancel();
                    finishScreen(response, strmsg);

                }
            });

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

    }

    public String removeBackslashes(final String content) {

        if (content == null)
            return null;

        final StringBuffer buff = new StringBuffer();
        buff.append(content);

        final int len = buff.length();
        for (int i = len - 1; i >= 0; i--)
            if ('\\' == buff.charAt(i))
                buff.deleteCharAt(i);
        return buff.toString();
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

    public static long generate4DigitRandom() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // first not 0 digit
        sb.append(random.nextInt(9) + 1);

        // rest of 11 digits
        for (int i = 0; i < 3; i++) {
            sb.append(random.nextInt(10));
        }

        return Long.valueOf(sb.toString()).longValue();
    }


    void checkPaymentStatus() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentCardActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(PaymentCardActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.BOOKORDER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        Log.e("response", response + "  check payment ");
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("0")) {
                            displayPopup(response, jobj.optString("message"));
                        } else {
                            //  Snackbar.make(webView, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                            displayPopup(response, jobj.optString("message"));
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(PaymentCardActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(webView, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(PaymentCardActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(webView, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

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
                            Snackbar.make(webView, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {
                        if (apath.getLanguage(PaymentCardActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(webView, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(PaymentCardActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(webView, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("action", "checkPayment");
                    params.put("transaction_id", TransactionID);
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
            Singleton.getIntance(PaymentCardActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
