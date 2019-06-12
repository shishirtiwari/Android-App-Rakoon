package com.rakoon.restaurant.fragment;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.rakoon.restaurant.AllPath;
import com.rakoon.restaurant.R;
import com.rakoon.restaurant.Singleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeProfileFragment extends Fragment {

    private static final String KEY_MOVIE_TITLE = "key_title";
    EditText name, emailid, mobileno, city, state, street, buildingno;
    String str_name, str_emailid, str_mobileno, str_city, str_state, str_street, str_building, str_country;
    TextView updateprofile;
    AllPath apath;
    ConstraintLayout mainlay;
    AlertDialog alertDialog;

    public ChangeProfileFragment() {
        // Required empty public constructor
    }

    public static ChangeProfileFragment newInstance(String movieTitle) {
        ChangeProfileFragment fragmentAction = new ChangeProfileFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_TITLE, movieTitle);
        fragmentAction.setArguments(args);

        return fragmentAction;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.change_profile_frag, container, false);
        apath = new AllPath();

        mainlay = vv.findViewById(R.id.mainlay);
        name = vv.findViewById(R.id.name);
        mobileno = vv.findViewById(R.id.mobileno);
        city = vv.findViewById(R.id.city);
        state = vv.findViewById(R.id.state);
        street = vv.findViewById(R.id.street);

        emailid = vv.findViewById(R.id.emailid);
        buildingno = vv.findViewById(R.id.pincode);
        updateprofile = vv.findViewById(R.id.updateprofile);

        if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
            name.setHint(getString(R.string.username_en));
            emailid.setHint(getString(R.string.email_en));
            mobileno.setHint(getString(R.string.mobileno_en));
            city.setHint(getString(R.string.city_en));
            state.setHint(getString(R.string.state_en));
            street.setHint(getString(R.string.street_en));
            buildingno.setHint(getString(R.string.pincode_en));
            updateprofile.setText(getString(R.string.change_profile_en));
        } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
            name.setHint(getString(R.string.username_ar));
            emailid.setHint(getString(R.string.email_ar));
            mobileno.setHint(getString(R.string.mobileno_ar));
            city.setHint(getString(R.string.city_ar));
            state.setHint(getString(R.string.state_ar));
            // country.setHint(getString(R.string.country_ar));
            street.setHint(getString(R.string.street_ar));
            buildingno.setHint(getString(R.string.pincode_ar));
            updateprofile.setText(getString(R.string.change_profile_ar));

        }

        if (apath.isInternetOn(getActivity())) {
            getUserInfo();
        } else {
            String msg = "";
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                msg = getResources().getString(R.string.internet_message_ar);
            }
            Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();

        }

        updateprofile.setOnClickListener((v) -> {
            if (userValidation()) {
                if (apath.isInternetOn(getActivity())) {
                    updateUser();
                } else {
                    String msg = "";
                    if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                        msg = getResources().getString(R.string.internet_message_en);
                    } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                        msg = getResources().getString(R.string.internet_message_ar);
                    }
                    Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
                }
            }
        });

        return vv;
    }

    boolean userValidation() {
        str_emailid = emailid.getText().toString();
        str_building = buildingno.getText().toString();
        str_city = city.getText().toString();
        str_state = state.getText().toString();
        str_street = street.getText().toString();
        str_name = name.getText().toString();
        str_mobileno = mobileno.getText().toString();

        if (str_name.equalsIgnoreCase("")) {
            name.requestFocus();
            name.setBackgroundResource(R.drawable.bottom_line_red);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.name_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.name_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_mobileno.equalsIgnoreCase("")) {
            mobileno.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottom_line_red);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_msg_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_msg_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (!str_mobileno.matches(apath.MobilePattern)) {
            mobileno.requestFocus();
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottom_line_red);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_valid_ar), Snackbar.LENGTH_LONG).show();
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
            state.setBackgroundResource(R.drawable.bottom_line_red);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.state_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
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
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.city_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.city_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_street.equalsIgnoreCase("")) {
            street.requestFocus();
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
            street.setBackgroundResource(R.drawable.bottom_line_red);
            street.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.street_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.street_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else {
            apath.hideKeyboard(getActivity());
            emailid.setBackgroundResource(R.drawable.bottomline_selector);
            emailid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            name.setBackgroundResource(R.drawable.bottomline_selector);
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            buildingno.setBackgroundResource(R.drawable.bottomline_selector);
            buildingno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            city.setBackgroundResource(R.drawable.bottomline_selector);
            city.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            state.setBackgroundResource(R.drawable.bottomline_selector);
            state.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            street.setBackgroundResource(R.drawable.bottomline_selector);
            street.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            return true;
        }
    }

    void updateUser() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                            SharedPreferences sp1 = getActivity().getSharedPreferences("address", getActivity().MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sp1.edit();
                            editor1.putString("phone", str_mobileno);
                            editor1.putString("street", str_street);
                            editor1.putString("building", str_building);
                            editor1.commit();

                            SharedPreferences sp = getActivity().getSharedPreferences("userdata", getActivity().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("name", str_name);
                            editor.putString("email", str_emailid);
                            editor.putString("phone", str_mobileno);
                            editor.putString("city", str_city);
                            editor.putString("state", str_state);
                            editor.putString("street", str_street);
                            editor.commit();

                            Snackbar snackbar = Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            snackbar.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {

                                }
                            });
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(getActivity()));
                    params.put("action", "update_user");
                    params.put("user_id", apath.getUserid(getActivity()));
                    params.put("email", str_emailid);
                    params.put("name", str_name);
                    params.put("phone", str_mobileno);
                    params.put("city", str_city);
                    params.put("state", str_state);
                    params.put("street", str_street);
                    params.put("pincode", str_building);
                    params.put("country", str_country);
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
            Singleton.getIntance(getActivity()).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void getUserInfo() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.USER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        Log.e("response", response + "  user info ");
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("true")) {
                            JSONObject jo = jobj.optJSONObject("data");
                            str_name = jo.optString("name");
                            str_emailid = jo.optString("email");
                            str_mobileno = jo.optString("phone");
                            str_city = jo.optString("city");
                            str_state = jo.optString("state");
                            str_street = jo.optString("street");
                            str_building = jo.optString("pincode");
                            str_country = jo.optString("country");

                            name.setText(str_name);
                            emailid.setText(str_emailid);
                            mobileno.setText(str_mobileno);
                            state.setText(str_state);
                            city.setText(str_city);
                            buildingno.setText(str_building);
                            street.setText(str_street);
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic)))
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
                        if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(getActivity()));
                    params.put("action", "user_info");
                    params.put("user_id", apath.getUserid(getActivity()));
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
            Singleton.getIntance(getActivity()).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
