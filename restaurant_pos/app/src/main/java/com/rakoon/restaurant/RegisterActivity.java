package com.rakoon.restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rakoon.restaurant.model.Country;
import com.rakoon.restaurant.util.CountryUtil;
import com.rakoon.restaurant.view.CustomTextWatcher;
import com.rakoon.restaurant.view.PatternEditableBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextView terms;
    Spinner country;
    EditText name, emailid, mobileno, city, state, street, buildingno, password, confirmpassword;
    String str_name, str_emailid, str_mobileno, str_city, str_state, str_country, str_street, str_building, str_password, str_confirmpassword;
    TextView signup, join, account;
    AllPath apath;
    LinearLayout mainlay;
    private long back_pressed;
    String termsofuse = "";
    AlertDialog alertDialog;
    CountryUtil cutil;
    ArrayList<Country> clist;

    @Override
    public void onBackPressed() {
        if (back_pressed + getResources().getInteger(R.integer.delay) > System.currentTimeMillis()) {
            finish();
        } else {
            String msgg = "";
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english)))
                msgg = getString(R.string.backactivity_en);
            else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        apath = new AllPath();
        cutil = new CountryUtil();
        clist = cutil.getcountryList(RegisterActivity.this);
        Log.e("c list size ", clist.size() + " size");
        mainlay = findViewById(R.id.mainlay);
        terms = findViewById(R.id.terms);
        name = findViewById(R.id.name);
        mobileno = findViewById(R.id.mobileno);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        street = findViewById(R.id.street);

        emailid = findViewById(R.id.emailid);
        buildingno = findViewById(R.id.pincode);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        signup = findViewById(R.id.signup);
        join = findViewById(R.id.join);
        account = findViewById(R.id.account);

        name.addTextChangedListener(new CustomTextWatcher(name));
        mobileno.addTextChangedListener(new CustomTextWatcher(mobileno));
        buildingno.addTextChangedListener(new CustomTextWatcher(buildingno));
        emailid.addTextChangedListener(new CustomTextWatcher(emailid));
        password.addTextChangedListener(new CustomTextWatcher(password));
        confirmpassword.addTextChangedListener(new CustomTextWatcher(confirmpassword));
        // password.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        // confirmpassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

//         password.addTextChangedListener(new AsteriskPasswordTransformationMethod());
//         confirmpassword.addTextChangedListener(new AsteriskPasswordTransformationMethod());

        if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle(getString(R.string.register_en));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            name.setHint(getString(R.string.username_en));
            emailid.setHint(getString(R.string.email_en));
            mobileno.setHint(getString(R.string.mobileno_en));
            city.setHint(getString(R.string.city_en));
            state.setHint(getString(R.string.state_en));
            // country.setHint(getString(R.string.country_en));
            street.setHint(getString(R.string.street_en));
            buildingno.setHint(getString(R.string.pincode_en));
            password.setHint(getString(R.string.password_en));
            confirmpassword.setHint(getString(R.string.confirmpassword_en));
            signup.setText(getString(R.string.register_en));
            join.setText(" " + getString(R.string.login_en));
            account.setText(getString(R.string.haveaccount_en));
            terms.setText(getString(R.string.terms_en));
            termsofuse = getResources().getString(R.string.termsofuse_en);

        } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle(getString(R.string.register_ar));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            name.setHint(getString(R.string.username_ar));
            emailid.setHint(getString(R.string.email_ar));
            mobileno.setHint(getString(R.string.mobileno_ar));
            city.setHint(getString(R.string.city_ar));
            state.setHint(getString(R.string.state_ar));
            // country.setHint(getString(R.string.country_ar));
            street.setHint(getString(R.string.street_ar));
            buildingno.setHint(getString(R.string.pincode_ar));
            password.setHint(getString(R.string.password_ar));
            confirmpassword.setHint(getString(R.string.confirmpassword_ar));
            signup.setText(getString(R.string.register_ar));
            join.setText(" " + getString(R.string.login_ar));
            account.setText(getString(R.string.haveaccount_ar));
            terms.setText(getString(R.string.terms_ar));
            termsofuse = getResources().getString(R.string.termsofuse_ar);
            terms.setOnClickListener((v) -> {
               /* Uri uri = Uri.parse("https://marvelapp.com/4h6ii8b"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/
               Intent in=new Intent(RegisterActivity.this,TermsAndConditionActivity.class);
               startActivity(in);
            });
            terms.setVisibility(View.GONE);
        }
        country.setAdapter(new Countryadap());
        for (int i = 0; i < clist.size(); i++) {
            if (clist.get(i).getName().equalsIgnoreCase("Jordan")) {
                country.setSelection(i);
                break;
            }
        }
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Country c = clist.get(position);
                TextView code = view.findViewById(R.id.text1);
                code.setText(c.getCode() + " " + c.getDial_code());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new PatternEditableBuilder().
                addPattern(Pattern.compile(termsofuse), getResources().getColor(R.color.colorAccent),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
//                                Toast.makeText(RegisterActivity.this, "Clicked username: " + text,
//                                        Toast.LENGTH_SHORT).show();
                              /*  Uri uri = Uri.parse("https://marvelapp.com/4h6ii8b"); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);*/
                                Intent in=new Intent(RegisterActivity.this,TermsAndConditionActivity.class);
                                startActivity(in);
                            }
                        }).into(terms);

        mainlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                apath.hideKeyboard(RegisterActivity.this);
                return false;
            }
        });

        findViewById(R.id.signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(in);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userValidation()) {
                    if (apath.isInternetOn(RegisterActivity.this)) {
                        saveUser();
                    } else {
                        String msg = "", str_action = "";
                        if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                            msg = getResources().getString(R.string.internet_message_en);
                            str_action = getResources().getString(R.string.enable_en);
                        } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
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
//                Intent in = new Intent(RegisterActivity.this, DashBoardActivity2.class);
//                startActivity(in);
//                finish();
            }
        });

    }

    class Countryadap extends BaseAdapter {

        @Override
        public int getCount() {
            return clist.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vw = getLayoutInflater().inflate(R.layout.textview, null);
            TextView tvt = vw.findViewById(R.id.text1);
            tvt.setText(clist.get(i).getName() + "(" + clist.get(i).getDial_code() + ")");
            int padding = (int) getResources().getDimension(R.dimen._8sdp);
            int padding5 = (int) getResources().getDimension(R.dimen._4sdp);
            tvt.setPadding(padding5, padding, padding5, padding);
            return vw;
        }
    }

    boolean userValidation() {
        str_emailid = emailid.getText().toString();
        str_building = buildingno.getText().toString();
        str_city = city.getText().toString();
        str_state = state.getText().toString();
        str_country = clist.get(country.getSelectedItemPosition()).getName();
        str_street = street.getText().toString();

        str_password = password.getText().toString().replaceFirst("\\s++$", "");
        str_confirmpassword = confirmpassword.getText().toString().replaceFirst("\\s++$", "");
        str_name = name.getText().toString();
        str_mobileno = mobileno.getText().toString();

        if (str_name.equalsIgnoreCase("")) {
            name.requestFocus();
            name.setBackgroundResource(R.drawable.bottom_line_red);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.name_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.name_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_mobileno.equalsIgnoreCase("")) {
            mobileno.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottom_line_red);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_msg_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_msg_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (!str_mobileno.matches(apath.MobilePattern)) {
            mobileno.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottom_line_red);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } /*else if (!str_emailid.equalsIgnoreCase("")) {
            emailid.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            emailid.setBackgroundResource(R.drawable.bottom_line_red);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.email_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.email_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (!apath.isValidEmail(str_emailid)) {
            emailid.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            emailid.setBackgroundResource(R.drawable.bottom_line_red);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.email_correct_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.email_correct_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } */ else if (str_street.equalsIgnoreCase("")) {
            street.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//            country.setBackgroundResource(R.drawable.bottomline_selector);
//            country.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            street.setBackgroundResource(R.drawable.bottom_line_red);
            street.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.street_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.street_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_state.equalsIgnoreCase("")) {
            state.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            street.setBackgroundResource(R.drawable.bottomline_selector);
            street.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            city.setBackgroundResource(R.drawable.bottomline_selector);
            city.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            state.setBackgroundResource(R.drawable.bottom_line_red);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.state_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.state_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_city.equalsIgnoreCase("")) {
            city.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            state.setBackgroundResource(R.drawable.bottomline_selector);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            city.setBackgroundResource(R.drawable.bottom_line_red);
            city.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.city_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.city_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        }
     /*   else if (str_country.equalsIgnoreCase("")) {
            country.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            city.setBackgroundResource(R.drawable.bottomline_selector);
            city.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            state.setBackgroundResource(R.drawable.bottomline_selector);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            country.setBackgroundResource(R.drawable.bottom_line_red);
            country.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.country_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.country_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        }*/
        else if (str_password.equalsIgnoreCase("")) {
            password.requestFocus();
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            city.setBackgroundResource(R.drawable.bottomline_selector);
            city.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            state.setBackgroundResource(R.drawable.bottomline_selector);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            state.setBackgroundResource(R.drawable.bottomline_selector);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//            country.setBackgroundResource(R.drawable.bottomline_selector);
//            country.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            street.setBackgroundResource(R.drawable.bottomline_selector);
            street.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottom_line_red);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.password_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.password_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_password.length() < 6) {
            password.requestFocus();
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottom_line_red);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.password_correct_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.password_correct_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (!str_confirmpassword.equalsIgnoreCase(str_password)) {
            confirmpassword.requestFocus();
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottomline_selector);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            confirmpassword.setBackgroundResource(R.drawable.bottom_line_red);
            confirmpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.password_match_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.password_match_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else {
            apath.hideKeyboard(RegisterActivity.this);
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            buildingno.setBackgroundResource(R.drawable.bottomline_selector);
            buildingno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottomline_selector);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            confirmpassword.setBackgroundResource(R.drawable.bottomline_selector);
            confirmpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            city.setBackgroundResource(R.drawable.bottomline_selector);
            city.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            state.setBackgroundResource(R.drawable.bottomline_selector);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            state.setBackgroundResource(R.drawable.bottomline_selector);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//            country.setBackgroundResource(R.drawable.bottomline_selector);
//            country.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            street.setBackgroundResource(R.drawable.bottomline_selector);
            street.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            return true;
        }
    }

    void saveUser() {
        try {
            signup.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.USER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        Log.e("response", response + "  register ");
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("true")) {
                            SharedPreferences sp = getSharedPreferences("address", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("phone", str_mobileno);
                            editor.putString("street", str_street);
                            editor.putString("building", str_building);
                            editor.commit();

                            Snackbar snackbar = Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            snackbar.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    Intent in = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(in);
                                    finish();
                                    signup.setEnabled(true);
                                }
                            });
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                            signup.setEnabled(true);
                        }
                    } catch (Exception e) {
                        signup.setEnabled(true);
                        if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    alertDialog.cancel();
                    signup.setEnabled(true);
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
                        if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(RegisterActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(RegisterActivity.this));
                    params.put("action", "new_user");
                    params.put("email", str_emailid);
                    params.put("password", str_password);
                    params.put("name", str_name);
                    params.put("phone", str_mobileno);
                    params.put("city", str_city);
                    params.put("state", str_state);
                    params.put("country", str_country);
                    params.put("street", str_street);
                    params.put("pincode", str_building);
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
//            sr.setRetryPolicy(new DefaultRetryPolicy(
//                    0,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Singleton.getIntance(RegisterActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
