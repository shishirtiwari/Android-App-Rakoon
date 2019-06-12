package com.rakoon.restaurant;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.rakoon.restaurant.view.CustomTextWatcher;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    String str_email = "", str_password = "";
    EditText email, password;
    LinearLayout mainlay;
    TextView signin, forgotpassword;
    AllPath apath;
    private long back_pressed;
    TextView join, account;
    AlertDialog alertDialog;

    @Override
    public void onBackPressed() {
        if (back_pressed + getResources().getInteger(R.integer.delay) > System.currentTimeMillis()) {
            finish();
        } else {
            String msgg = "";
            if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                msgg = getString(R.string.backactivity_en);
            else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                msgg = getString(R.string.backactivity_ar);


            Toast.makeText(getBaseContext(), msgg,
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.emailid);
        password = findViewById(R.id.password);
        mainlay = findViewById(R.id.mainlay);
        signin = findViewById(R.id.signin);
        forgotpassword = findViewById(R.id.forgotpassword);
        join = findViewById(R.id.join);
        account = findViewById(R.id.account);

      //  password.addTextChangedListener(new AsteriskPasswordTransformationMethod());

        //  password.setTransformationMethod(new PasswordTransformationMethod());
//        email.setText("surendra@itsabacus.com");
//        password.setText("mRQqNAkBniHA");

        apath = new AllPath();
        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle(getString(R.string.login_en));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            email.setHint(getString(R.string.email_mobile_en));
            password.setHint(getString(R.string.password_en));
            signin.setText(getString(R.string.login_en));
            forgotpassword.setText(getString(R.string.forgot_en));
            account.setText(getString(R.string.donothaveaccount_en));
            join.setText(" " + getString(R.string.joinnow_en));

        } else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle(getString(R.string.login_ar));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            email.setHint(getString(R.string.email_mobile_ar));
            password.setHint(getString(R.string.password_ar));
            signin.setText(getString(R.string.login_ar));
            forgotpassword.setText(getString(R.string.forgot_ar));
            account.setText(getString(R.string.donothaveaccount_ar));
            join.setText(" " + getString(R.string.joinnow_ar));

        }

//        email.setText("sunil1212@gmail.com");
//        password.setText("acs123");

        email.addTextChangedListener(new CustomTextWatcher(email));
        password.addTextChangedListener(new CustomTextWatcher(password));
        // password.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        mainlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                apath.hideKeyboard(LoginActivity.this);
                return false;
            }
        });

        isStoragePermissionGranted();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginValidation() && isStoragePermissionGranted()) {
                    if (apath.isInternetOn(LoginActivity.this)) {
                        loginUser();
                    } else {
                        String msg = "", str_action = "";
                        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                            msg = getResources().getString(R.string.internet_message_en);
                            str_action = getResources().getString(R.string.enable_en);
                        } else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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

            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (forgotValidation()) {
                    if (apath.isInternetOn(LoginActivity.this)) {
                        forgotUser();
                    } else {
                        String msg = "", str_action = "";
                        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                            msg = getResources().getString(R.string.internet_message_en);
                            str_action = getResources().getString(R.string.enable_en);
                        } else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
            }
        });

        findViewById(R.id.joinnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(in);
                finish();
            }
        });

    }


    boolean loginValidation() {

        str_email = email.getText().toString();
        str_password = password.getText().toString().replaceFirst("\\s++$", "");

        if (str_email.equalsIgnoreCase("")) {
            email.requestFocus();
            email.setBackgroundResource(R.drawable.bottom_line_red);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                Snackbar.make(mainlay, getResources().getString(R.string.email_num_en), Snackbar.LENGTH_LONG).show();
            else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                Snackbar.make(mainlay, getResources().getString(R.string.email_num_ar), Snackbar.LENGTH_LONG).show();

            return false;
        } else if (!(apath.isValidEmail(str_email) || str_email.matches(apath.MobilePattern))) {
            email.requestFocus();
            email.setBackgroundResource(R.drawable.bottom_line_red);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                Snackbar.make(mainlay, getResources().getString(R.string.email_num_correct_en), Snackbar.LENGTH_LONG).show();
            else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                Snackbar.make(mainlay, getResources().getString(R.string.email_num_correct_ar), Snackbar.LENGTH_LONG).show();
            return false;
        } else if (str_password.equalsIgnoreCase("")) {
            password.requestFocus();
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottom_line_red);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                Snackbar.make(mainlay, getResources().getString(R.string.password_valid_en), Snackbar.LENGTH_LONG).show();
            else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                Snackbar.make(mainlay, getResources().getString(R.string.password_valid_ar), Snackbar.LENGTH_LONG).show();

            return false;
        } else {
            apath.hideKeyboard(LoginActivity.this);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottomline_selector);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return true;
        }
    }

    boolean forgotValidation() {

        str_email = email.getText().toString();

        if (str_email.equalsIgnoreCase("")) {
            email.requestFocus();
            email.setBackgroundResource(R.drawable.bottom_line_red);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                Snackbar.make(mainlay, getResources().getString(R.string.email_num_en), Snackbar.LENGTH_LONG).show();
            else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                Snackbar.make(mainlay, getResources().getString(R.string.email_num_ar), Snackbar.LENGTH_LONG).show();

            return false;
        } else if (!(apath.isValidEmail(str_email) || str_email.matches(apath.MobilePattern))) {
            email.requestFocus();
            email.setBackgroundResource(R.drawable.bottom_line_red);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                Snackbar.make(mainlay, getResources().getString(R.string.email_num_correct_en), Snackbar.LENGTH_LONG).show();
            else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                Snackbar.make(mainlay, getResources().getString(R.string.email_num_correct_ar), Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            apath.hideKeyboard(LoginActivity.this);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return true;
        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("permission ", "Permission is granted");
                return true;
            } else {

                Log.e(" revoked ", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e("granted ", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("Permission result", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    void loginUser() {
        try {
            forgotpassword.setEnabled(false);
            signin.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.USER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        Log.e("response", response + "  Login ");
                        JSONObject jobj = new JSONObject(response);

                        if (jobj.optString("status").equalsIgnoreCase("true")) {
                            JSONObject job = jobj.optJSONObject("info");
                            SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("login", true);
                            editor.putString("userid", job.optString("id"));
                            editor.putString("name", job.optString("name"));
                            editor.putString("email", job.optString("email"));
                            editor.putString("phone", job.optString("phone"));
                            editor.putString("city", job.optString("city"));
                            editor.putString("state", job.optString("state"));
                            editor.putString("country", job.optString("country"));
                            editor.putString("street", job.optString("street"));
                            editor.commit();
                            Snackbar snackbar = Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            snackbar.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    forgotpassword.setEnabled(true);
                                    signin.setEnabled(true);
//                                   Intent in=new Intent();
//                                   in.putExtra("status", "true");
//                                   setResult(RESULT_OK, in);
                                    finish();
                                }
                            });
                            if (apath.isInternetOn(LoginActivity.this)) {
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("rakoontoken", 0);
                                Log.e("tokenid", pref.getString("tokenid", ""));
                                updateToken(job.optString("id"),pref.getString("tokenid", ""));
                            }
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                            forgotpassword.setEnabled(true);
                            signin.setEnabled(true);
                        }
                    } catch (Exception e) {
                        forgotpassword.setEnabled(true);
                        signin.setEnabled(true);
                        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    alertDialog.cancel();
                    forgotpassword.setEnabled(true);
                    signin.setEnabled(true);
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
                        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(LoginActivity.this));
                    params.put("action", "user_login");
                    params.put("email", str_email);
                    params.put("password", str_password);
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
            Singleton.getIntance(LoginActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void forgotUser() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.USER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        Log.e("response", response + "  forgot ");
                        JSONObject jobj = new JSONObject(response);
                        //Toast.makeText(LoginActivity.this, "Please check password respective mobile number or email", Toast.LENGTH_LONG).show();
                        if (jobj.optString("state").equalsIgnoreCase("true")) {

                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(LoginActivity.this));
                    params.put("action", "fpassword");
                    params.put("email", str_email);
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
            Singleton.getIntance(LoginActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void updateToken(String user_id, String token)
    {
        try {
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.USER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        Log.e("response", response + "  Login ");
                        JSONObject jobj = new JSONObject(response);

                        if (jobj.optString("status").equalsIgnoreCase("true")) {

                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        Log.e("res data token", new String(response.data));
                        try {
                            JSONObject jobj = new JSONObject(new String(response.data));
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {
                        if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(LoginActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(LoginActivity.this));
                    params.put("action", "user_token");
                    params.put("user_id", user_id);
                    params.put("device_token", token);
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
            Singleton.getIntance(LoginActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

