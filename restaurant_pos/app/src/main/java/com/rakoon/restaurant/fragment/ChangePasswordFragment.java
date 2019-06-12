package com.rakoon.restaurant.fragment;

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

public class ChangePasswordFragment extends Fragment {

    private static final String KEY_MOVIE_TITLE = "key_title";

    EditText oldpassword, password, confirmpassword;
    String str_oldpassword, str_password, str_confirmpassword;
    TextView updatepassword;
    AllPath apath;
    ConstraintLayout mainlay;
    AlertDialog alertDialog;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String movieTitle) {
        ChangePasswordFragment fragmentAction = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_TITLE, movieTitle);
        fragmentAction.setArguments(args);

        return fragmentAction;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.change_password_frag, container, false);
        apath = new AllPath();
        oldpassword = vv.findViewById(R.id.oldpassword);
        password = vv.findViewById(R.id.password);
        confirmpassword = vv.findViewById(R.id.confirmpassword);
        mainlay = vv.findViewById(R.id.mainlay);
        updatepassword = vv.findViewById(R.id.updatepassword);

//        oldpassword.addTextChangedListener(new AsteriskPasswordTransformationMethod());
//        password.addTextChangedListener(new AsteriskPasswordTransformationMethod());
//        confirmpassword.addTextChangedListener(new AsteriskPasswordTransformationMethod());
        if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
            oldpassword.setHint(getString(R.string.old_password_en));
            password.setHint(getString(R.string.password_en));
            confirmpassword.setHint(getString(R.string.confirmpassword_en));
            updatepassword.setText(getString(R.string.change_password_en));

        } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
            oldpassword.setHint(getString(R.string.old_password_ar));
            password.setHint(getString(R.string.password_ar));
            confirmpassword.setHint(getString(R.string.confirmpassword_ar));
            updatepassword.setText(getString(R.string.change_password_ar));

        }
        updatepassword.setOnClickListener((v) -> {
            if (passwordValidation()) {
                if (apath.isInternetOn(getActivity())) {
                    changePassword();
                } else {
                    String msg = "";
                    if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                        msg = getResources().getString(R.string.internet_message_en);
                    } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                        msg = getResources().getString(R.string.internet_message_ar);
                    }
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });

        return vv;
    }

    boolean passwordValidation() {
        str_oldpassword = oldpassword.getText().toString().replaceFirst("\\s++$", "");
        str_password = password.getText().toString().replaceFirst("\\s++$", "");
        str_confirmpassword = confirmpassword.getText().toString().replaceFirst("\\s++$", "");

        if (str_oldpassword.equalsIgnoreCase("")) {
            oldpassword.requestFocus();
            oldpassword.setBackgroundResource(R.drawable.bottom_line_red);
            oldpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.password_old_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.password_old_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_password.equalsIgnoreCase("")) {
            password.requestFocus();

            oldpassword.setBackgroundResource(R.drawable.bottomline_selector);
            oldpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            password.setBackgroundResource(R.drawable.bottom_line_red);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.password_new_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.password_new_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_password.length() < 6) {
            password.requestFocus();
            oldpassword.setBackgroundResource(R.drawable.bottomline_selector);
            oldpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottom_line_red);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.password_correct_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.password_correct_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (!str_confirmpassword.equalsIgnoreCase(str_password)) {
            confirmpassword.requestFocus();
            oldpassword.setBackgroundResource(R.drawable.bottomline_selector);
            oldpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottomline_selector);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            confirmpassword.setBackgroundResource(R.drawable.bottom_line_red);
            confirmpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.password_match_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.password_match_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else {
            apath.hideKeyboard(getActivity());
            oldpassword.setBackgroundResource(R.drawable.bottomline_selector);
            oldpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            password.setBackgroundResource(R.drawable.bottomline_selector);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            confirmpassword.setBackgroundResource(R.drawable.bottomline_selector);
            confirmpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            return true;
        }
    }

    void changePassword() {
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
                            Snackbar snackbar = Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG);
                            snackbar.show();

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

                    params.put("old_password", str_oldpassword);
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
            Singleton.getIntance(getActivity()).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
