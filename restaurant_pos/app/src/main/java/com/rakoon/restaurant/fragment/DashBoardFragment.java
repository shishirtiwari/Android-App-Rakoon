package com.rakoon.restaurant.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import com.rakoon.restaurant.AllPath;
import com.rakoon.restaurant.CartActivity;
import com.rakoon.restaurant.DeliveryLocationActivity;
import com.rakoon.restaurant.DiscountActivity;
import com.rakoon.restaurant.LoginActivity;
import com.rakoon.restaurant.PickupBranchActivity;
import com.rakoon.restaurant.R;
import com.rakoon.restaurant.ReservationActivity;
import com.rakoon.restaurant.Singleton;
import com.rakoon.restaurant.datasource.DataBaseHelper;
import com.rakoon.restaurant.model.Cart;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DashBoardFragment extends Fragment {
    @BindView(R.id.deliveryimg)
    ImageView deliveryimg;
    @BindView(R.id.pickupimg)
    ImageView pickupimg;
    @BindView(R.id.reservationimg)
    ImageView reservationimg;
    @BindView(R.id.discountimg)
    ImageView discountimg;

    @BindView(R.id.deliverytext)
    TextView deliverytext;
    @BindView(R.id.pickuptext)
    TextView pickuptext;
    @BindView(R.id.reservationtext)
    TextView reservationtext;
    @BindView(R.id.discounttext)
    TextView discounttext;
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
    @BindView(R.id.whatisplan)
    TextView whatisplan;

    Unbinder unbinder;
    AlertDialog alertDialog;
    AllPath apath;
    TextView tv = null;
    FrameLayout cartclick;

    private static final String KEY_MOVIE_TITLE = "key_title";
    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onRssItemSelected(String link);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showCartCount();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public DashBoardFragment() {
        // Required empty public constructor
    }

    public static DashBoardFragment newInstance(String movieTitle) {
        DashBoardFragment fragmentAction = new DashBoardFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_TITLE, movieTitle);
        fragmentAction.setArguments(args);

        return fragmentAction;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.bridge_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(false);

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
                if (apath.isUserLogedin(getActivity())) {
                    Intent in = new Intent(getActivity(), CartActivity.class);
                    startActivity(in);
                } else {
                    Intent in = new Intent(getActivity(), LoginActivity.class);
                    in.putExtra("message", "rquired");
                    startActivity(in);
                }
            }
        });

        showCartCount();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                if (apath.isUserLogedin(getActivity())) {
                    Intent in = new Intent(getActivity(), CartActivity.class);
                    startActivity(in);
                } else {
                    Intent in = new Intent(getActivity(), LoginActivity.class);
                    in.putExtra("message", "rquired");
                    startActivity(in);
                }
                //  Toast.makeText(getActivity(), "cart clicked ", Toast.LENGTH_LONG).show();
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.frag_dashboard, null);
        unbinder = ButterKnife.bind(this, vv);

        apath = new AllPath();

        if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
            whatisplan.setText(getString(R.string.whatisplan_en));
        } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
            whatisplan.setText(getString(R.string.whatisplan_ar));
        }

        if (apath.isInternetOn(getActivity()))
            getDashboard();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                msg = getResources().getString(R.string.internet_message_ar);
                str_action = getResources().getString(R.string.enable_ar);
            }

            Toast.makeText(getActivity(), msg,
                    Toast.LENGTH_LONG).show();
        }
        deliveryimg.setOnClickListener((v) -> {
            onMenuClick(0, deliverytext.getText().toString());
        });
        deliverytext.setOnClickListener((v) -> {
            onMenuClick(0, deliverytext.getText().toString());
        });

        pickupimg.setOnClickListener((v) -> {
            onMenuClick(1, pickuptext.getText().toString());
        });
        pickuptext.setOnClickListener((v) -> {
            onMenuClick(1, pickuptext.getText().toString());
        });

        reservationimg.setOnClickListener((v) -> {
            onMenuClick(2, reservationtext.getText().toString());
        });
        reservationtext.setOnClickListener((v) -> {
            onMenuClick(2, reservationtext.getText().toString());
        });

        discountimg.setOnClickListener((v) -> {
            onMenuClick(3, discounttext.getText().toString());
        });
        discounttext.setOnClickListener((v) -> {
            onMenuClick(3, discounttext.getText().toString());
        });
        calllay.setOnClickListener((v) ->
        {
            apath.startCallActivity(getActivity());
        });
        adduserlay.setOnClickListener((v) ->
        {
            apath.startshareActivity(getActivity());
        });
        settinglay.setOnClickListener((v) ->
        {
            listener.onRssItemSelected("setting");
        });
        homelay.setOnClickListener((v) ->
        {

        });

        return vv;
    }

    void getDashboard() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.DASHBOARD), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            Log.e("response", response + "  das ");
                            JSONArray jarr = jobj.optJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = jarr.optJSONObject(i);
                                if (i == 0) {
                                    deliverytext.setTag(job.opt("id"));
                                    deliverytext.setText(job.optString("name"));
                                    ImageRequest request = new ImageRequest(job.optString("photo"),
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    deliveryimg.setImageBitmap(bitmap);
                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                public void onErrorResponse(VolleyError error) {
                                                    deliveryimg.setImageResource(R.drawable.ic_launcher_foreground);
                                                }
                                            });
                                    Singleton.getIntance(getActivity()).addToRequestQueue(request);
                                }
                                if (i == 1) {
                                    pickuptext.setTag(job.opt("id"));
                                    pickuptext.setText(job.optString("name"));
                                    ImageRequest request = new ImageRequest(job.optString("photo"),
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    pickupimg.setImageBitmap(bitmap);
                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                public void onErrorResponse(VolleyError error) {
                                                    pickupimg.setImageResource(R.drawable.ic_launcher_foreground);
                                                }
                                            });
                                    Singleton.getIntance(getActivity()).addToRequestQueue(request);
                                }
                                if (i == 2) {
                                    reservationtext.setTag(job.opt("id"));
                                    reservationtext.setText(job.optString("name"));
                                    ImageRequest request = new ImageRequest(job.optString("photo"),
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    reservationimg.setImageBitmap(bitmap);
                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                public void onErrorResponse(VolleyError error) {
                                                    reservationimg.setImageResource(R.drawable.ic_launcher_foreground);
                                                }
                                            });
                                    Singleton.getIntance(getActivity()).addToRequestQueue(request);
                                }
                                if (i == 3) {
                                    discounttext.setTag(job.opt("id"));
                                    discounttext.setText(job.optString("name"));
                                    ImageRequest request = new ImageRequest(job.optString("photo"),
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    discountimg.setImageBitmap(bitmap);
                                                }
                                            }, 0, 0, null,
                                            new Response.ErrorListener() {
                                                public void onErrorResponse(VolleyError error) {
                                                    discountimg.setImageResource(R.drawable.ic_launcher_foreground);
                                                }
                                            });
                                    Singleton.getIntance(getActivity()).addToRequestQueue(request);
                                }
                            }

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

        /*    sr.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            queue.add(sr);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void onMenuClick(int position, String message) {
        switch (position) {
            case 0:
                Intent indelivery = new Intent(getActivity(), DeliveryLocationActivity.class);
                indelivery.putExtra("message", message);
                startActivity(indelivery);
                break;
            case 1:
                Intent inpickup = new Intent(getActivity(), PickupBranchActivity.class);
                inpickup.putExtra("message", message);
                startActivity(inpickup);
                break;
            case 2:
                if (apath.isUserLogedin(getActivity())) {
                    if (apath.isInternetOn(getActivity())) {
                        Intent inreserv = new Intent(getActivity(), ReservationActivity.class);
                        inreserv.putExtra("message", message);
                        inreserv.putExtra("reservation", reservationtext.getText().toString());
                        startActivity(inreserv);
                    } else {
                        String msg = "";
                        if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                            msg = getResources().getString(R.string.internet_message_en);
                        } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                            msg = getResources().getString(R.string.internet_message_ar);
                        }
                        Toast.makeText(getActivity(), msg,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Intent in = new Intent(getActivity(), LoginActivity.class);
                    in.putExtra("message", "rquired");
                    startActivity(in);
                }
                break;
            case 3:
                Intent indiscount = new Intent(getActivity(), DiscountActivity.class);
                indiscount.putExtra("message", message);
                startActivity(indiscount);
                break;

            default:
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_LONG).show();
        }
    }


    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.count_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    void showCartCount() {
        DataBaseHelper dbh = new DataBaseHelper(getActivity());
        ArrayList<Cart> cartlist = dbh.getCartlist(apath.getUserid(getActivity()));
        if (cartlist != null) {
            if (tv != null) {
                tv.setText(cartlist.size() + "");
            }
        }
    }

}
