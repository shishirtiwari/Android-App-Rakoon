package com.rakoon.restaurant.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
import com.rakoon.restaurant.OrderDetailActivity;
import com.rakoon.restaurant.R;
import com.rakoon.restaurant.Singleton;
import com.rakoon.restaurant.model.Order;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragment extends Fragment {

    private static final String KEY_MOVIE_TITLE = "key_title";
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.mainlay)
    ConstraintLayout mainlay;
    @BindView(R.id.nodatafound)
    TextView nodatafound;
    @BindView(R.id.search)
    EditText search;

    AllPath apath;
    ArrayList<Order> list;
    RecyclerViewAdapter adapter;
    AlertDialog alertDialog;
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    String searchtext = "";

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static HistoryFragment newInstance(String movieTitle) {
        HistoryFragment fragmentAction = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_TITLE, movieTitle);
        fragmentAction.setArguments(args);

        return fragmentAction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.frag_current, null);
        unbinder = ButterKnife.bind(this, vv);

        apath = new AllPath();
        list = new ArrayList<>();
        Handler handler = new Handler();
        int width = recyclerView.getWidth();   //  for always 1 column in grid
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), width);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        viewHandler();

        if (apath.isInternetOn(getActivity())) {
            getCurrentOrder();
        } else {
            String msg = "", str_action = "";
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
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

        Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    ArrayList<Order> templist = new ArrayList<>();
                    searchtext = searchtext.toLowerCase();
                    for (int i = 0; i < list.size(); i++) {
                        Order ex = list.get(i);
                        if (ex.getOrder_id().toLowerCase().contains(searchtext) || ex.getOrder_title().toLowerCase().contains(searchtext)
                                || ex.getTotal_price().toLowerCase().contains(searchtext) || ex.getDate().toLowerCase().contains(searchtext)) {
                            templist.add(ex);
                        }
                    }
                    if (templist.size() > 0) {
                        nodatafound.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        nodatafound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    adapter = new RecyclerViewAdapter(getActivity(), templist);
                    recyclerView.setAdapter(adapter);
                    // Toast.makeText(ExistingActivity.this,"Typing stop",Toast.LENGTH_LONG).show();
                }
            }
        };
        search.addTextChangedListener(new TextWatcher() {
                                          @Override
                                          public void beforeTextChanged(CharSequence s, int start, int count,
                                                                        int after) {
                                          }

                                          @Override
                                          public void onTextChanged(final CharSequence s, int start, int before,
                                                                    int count) {
                                              //You need to remove this to run only once
                                              handler.removeCallbacks(input_finish_checker);

                                          }

                                          @Override
                                          public void afterTextChanged(final Editable s) {
                                              //avoid triggering event when text is empty
                                              if (s.length() > 0) {
                                                  searchtext = s.toString();
                                                  last_text_edit = System.currentTimeMillis();
                                                  handler.postDelayed(input_finish_checker, delay);
                                              } else {
                                                  adapter = new RecyclerViewAdapter(getActivity(), list);
                                                  recyclerView.setAdapter(adapter);
                                                  viewHandler();
                                              }
                                          }
                                      }

        );

        return vv;
    }

    void viewHandler() {
        if (list.size() > 0) {
            nodatafound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
        } else {
            nodatafound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        Context mContext;
        ArrayList<Order> exlist;

        public RecyclerViewAdapter(Context mContext, ArrayList<Order> exlist) {
            this.mContext = mContext;
            this.exlist = exlist;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView orderid, orderidvalue, ordertitle, ordertitlevalue, date, datevalue, totalprice, totalpricevalue;
            Order item;

            public ViewHolder(View v) {
                super(v);
                orderid = v.findViewById(R.id.orderid);
                orderidvalue = v.findViewById(R.id.orderidvalue);
                ordertitle = v.findViewById(R.id.ordertitle);
                ordertitlevalue = v.findViewById(R.id.ordertitlevalue);
                date = v.findViewById(R.id.date);
                datevalue = v.findViewById(R.id.datevalue);
                totalprice = v.findViewById(R.id.totalprice);
                totalpricevalue = v.findViewById(R.id.totalpricevalue);

                if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                    orderid.setText(getResources().getString(R.string.order_id_en));
                    ordertitle.setText(getResources().getString(R.string.order_title_en));
                    date.setText(getResources().getString(R.string.date_en));
                    totalprice.setText(getResources().getString(R.string.totalprice_en));
                } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                    orderid.setText(getResources().getString(R.string.order_id_ar));
                    ordertitle.setText(getResources().getString(R.string.order_title_ar));
                    date.setText(getResources().getString(R.string.date_ar));
                    totalprice.setText(getResources().getString(R.string.totalprice_ar));
                }


            }

            public void setData(Order item) {
                this.item = item;
                orderidvalue.setText(item.getOrder_id());
                ordertitlevalue.setText(item.getOrder_title());
                datevalue.setText(item.getDate());
                totalpricevalue.setText(item.getTotal_price()+" JD");

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.order_adap, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            Order c = exlist.get(position);
            Vholder.setData(c);
            Vholder.itemView.setOnClickListener((v)->{
                Intent in=new Intent(getActivity(),OrderDetailActivity.class);
                in.putExtra("order",c);
                startActivity(in);
            });
        }

        @Override
        public int getItemCount() {

            return exlist.size();
        }
    }

    void getCurrentOrder() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.BOOKORDER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        Log.e("response", response + "  current order ");
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            Log.e("response", response + "  forgot ");
                            JSONArray jar = jobj.optJSONArray("data");
                            JSONArray jarr = jar.optJSONArray(0);
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = jarr.optJSONObject(i);
                                list.add(new Order(job.optString("order_id"), job.optString("order_title"),
                                        job.optString("date"), job.optString("total_price")));
                            }

                            adapter = new RecyclerViewAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                            viewHandler();
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
                    params.put("action", "order_history");
                    params.put("user_id", apath.getUserid(getActivity()));

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
            Singleton.getIntance(getActivity()).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
