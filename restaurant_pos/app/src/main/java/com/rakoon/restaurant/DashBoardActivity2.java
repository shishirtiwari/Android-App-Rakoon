package com.rakoon.restaurant;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.rakoon.restaurant.adapter.CustomExpandableListAdapter;
import com.rakoon.restaurant.datasource.ExpandableListDataSource;
import com.rakoon.restaurant.fragment.DashBoardFragment;
import com.rakoon.restaurant.navigation.FragmentNavigation2;
import com.rakoon.restaurant.navigation.NavigationManager;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoardActivity2 extends AppCompatActivity implements DashBoardFragment.OnItemSelectedListener {
    private String[] items, orders;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandableListView;
    private NavigationManager mNavigationManager;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    ActionBarDrawerToggle mDrawerToggle;
    private Map<String, List<String>> mExpandableListData;
    String appname = "POS";
    private ExpandableListAdapter mExpandableListAdapter;
    private List<String> mExpandableListTitle;
    private long back_pressed;
    ImageView userimage;
    TextView username;
    AllPath apath;
    TextView dashboard, current, future, history, setting, notification, terms, privacy, logoout, changelanguage, name, email, login;
    LinearLayout nameemaillay;
    RadioGroup radioGroup;
    LinearLayout logoutlay;

    String currentVersion, latestVersion;
    @Override
    public void onBackPressed() {
        if (back_pressed + getResources().getInteger(R.integer.delay) > System.currentTimeMillis()) {
            finish();
        } else {
            String msgg = "";
            if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.english)))
                msgg = getString(R.string.backmessage_en);
            else if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.arabic)))
                msgg = getString(R.string.backmessage_ar);


            Toast.makeText(getBaseContext(), msgg,
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        apath = new AllPath();
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mExpandableListView = findViewById(R.id.navList);
        setupToolbar();


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();

        mNavigationManager = FragmentNavigation2.obtain(this);

        initItems();

        LayoutInflater inflater = getLayoutInflater();

        View listHeaderView = inflater.inflate(R.layout.nav_header, null, false);
        dashboard = listHeaderView.findViewById(R.id.dashboard);
        current = listHeaderView.findViewById(R.id.current);
        future = listHeaderView.findViewById(R.id.future);
        history = listHeaderView.findViewById(R.id.orders);
        setting = listHeaderView.findViewById(R.id.setting);
        notification = listHeaderView.findViewById(R.id.notification);
        terms = listHeaderView.findViewById(R.id.terms);
        privacy = listHeaderView.findViewById(R.id.privacy);
        changelanguage = listHeaderView.findViewById(R.id.changelanguage);
        name = listHeaderView.findViewById(R.id.name);
        email = listHeaderView.findViewById(R.id.email);
        login = listHeaderView.findViewById(R.id.login);
        nameemaillay = listHeaderView.findViewById(R.id.nameemaillay);
        radioGroup = listHeaderView.findViewById(R.id.radiogroup);

        View listfooterview = inflater.inflate(R.layout.nav_footer, null, false);
        logoutlay = listfooterview.findViewById(R.id.logoutlay);
        logoout = listfooterview.findViewById(R.id.logout);
        if (apath.isUserLogedin(DashBoardActivity2.this)) {
            nameemaillay.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            logoutlay.setVisibility(View.VISIBLE);
            name.setText(apath.getName(DashBoardActivity2.this));
            email.setText(apath.getEmail(DashBoardActivity2.this));
        } else {
            nameemaillay.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            logoutlay.setVisibility(View.GONE);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent in = new Intent(DashBoardActivity2.this, LoginActivity.class);
                startActivity(in);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(i);
                String language = checkedRadioButton.getText().toString();
                if (language.equalsIgnoreCase(getResources().getString(R.string.english))) {
                    SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("lang", getResources().getString(R.string.english));
                    editor.commit();
                    //   restartActivity(DashBoardActivity2.this);
                    dashboard.setText(getString(R.string.dashboard_en));
                    current.setText(getString(R.string.currentorder_en));
                    future.setText(getString(R.string.futureorders_en));
                    history.setText(getString(R.string.ordershistory_en));
                    setting.setText(getString(R.string.settings_en));
                    notification.setText(getString(R.string.notifications_en));
                    terms.setText(getString(R.string.term_en));
                    privacy.setText(getString(R.string.privacy_en));
                    logoout.setText(getString(R.string.logout_en));
                    changelanguage.setText(getString(R.string.changelanguage_en));
                    login.setText(getResources().getString(R.string.login_en) + "/" + getResources().getString(R.string.joinnow_en));
                    radioGroup.check(R.id.english);

                    if (apath.isInternetOn(DashBoardActivity2.this)) {
                        if (appname.equalsIgnoreCase(getString(R.string.dashboard_en)) || appname.equalsIgnoreCase(getString(R.string.dashboard_ar))) {
                            appname = dashboard.getText().toString();
                            mNavigationManager.showFragmentdashboard(appname);
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else if (appname.equalsIgnoreCase(getString(R.string.currentorder_en)) || appname.equalsIgnoreCase(getString(R.string.currentorder_ar))) {
                            appname = current.getText().toString();
                            mNavigationManager.showFragmentcurrent(appname);
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            appname = dashboard.getText().toString();
                            mNavigationManager.showFragmentdashboard(appname);
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }

                    } else {
                        if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.english)))
                            Toast.makeText(DashBoardActivity2.this, getResources().getString(R.string.internet_message_en), Toast.LENGTH_LONG).show();
                        else if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Toast.makeText(DashBoardActivity2.this, getResources().getString(R.string.internet_message_ar), Toast.LENGTH_LONG).show();
                    }

                }
                if (language.equalsIgnoreCase(getResources().getString(R.string.arabic))) {
                    SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("lang", getResources().getString(R.string.arabic));
                    editor.commit();
                    //  restartActivity(DashBoardActivity2.this);
                    dashboard.setText(getString(R.string.dashboard_ar));
                    current.setText(getString(R.string.currentorder_ar));
                    future.setText(getString(R.string.futureorders_ar));
                    history.setText(getString(R.string.ordershistory_ar));
                    setting.setText(getString(R.string.settings_ar));
                    notification.setText(getString(R.string.notifications_ar));
                    terms.setText(getString(R.string.term_ar));
                    privacy.setText(getString(R.string.privacy_ar));
                    logoout.setText(getString(R.string.logout_ar));
                    changelanguage.setText(getString(R.string.changelanguage_ar));
                    login.setText(getResources().getString(R.string.login_ar) + "/" + getResources().getString(R.string.joinnow_ar));
                    radioGroup.check(R.id.arabic);

                    if (apath.isInternetOn(DashBoardActivity2.this)) {
                        appname = dashboard.getText().toString();
                        mNavigationManager.showFragmentdashboard(appname);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.english)))
                            Toast.makeText(DashBoardActivity2.this, getResources().getString(R.string.internet_message_en), Toast.LENGTH_LONG).show();
                        else if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Toast.makeText(DashBoardActivity2.this, getResources().getString(R.string.internet_message_ar), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.english))) {
            dashboard.setText(getString(R.string.dashboard_en));
            current.setText(getString(R.string.currentorder_en));
            future.setText(getString(R.string.futureorders_en));
            history.setText(getString(R.string.ordershistory_en));
            setting.setText(getString(R.string.settings_en));
            notification.setText(getString(R.string.notifications_en));
            terms.setText(getString(R.string.term_en));
            privacy.setText(getString(R.string.privacy_en));
            logoout.setText(getString(R.string.logout_en));
            changelanguage.setText(getString(R.string.changelanguage_en));
            login.setText(getResources().getString(R.string.login_en) + "/" + getResources().getString(R.string.joinnow_en));
            radioGroup.check(R.id.english);
        } else if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.arabic))) {
            dashboard.setText(getString(R.string.dashboard_ar));
            current.setText(getString(R.string.currentorder_ar));
            future.setText(getString(R.string.futureorders_ar));
            history.setText(getString(R.string.ordershistory_ar));
            setting.setText(getString(R.string.settings_ar));
            notification.setText(getString(R.string.notifications_ar));
            terms.setText(getString(R.string.term_ar));
            privacy.setText(getString(R.string.privacy_ar));
            logoout.setText(getString(R.string.logout_ar));
            changelanguage.setText(getString(R.string.changelanguage_ar));
            login.setText(getResources().getString(R.string.login_ar) + "/" + getResources().getString(R.string.joinnow_ar));
            radioGroup.check(R.id.arabic);
        }

        username = listHeaderView.findViewById(R.id.username);
        userimage = listHeaderView.findViewById(R.id.userimage);

        dashboard.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             if (apath.isInternetOn(DashBoardActivity2.this)) {
                                                 appname = dashboard.getText().toString();
                                                 mNavigationManager.showFragmentdashboard(appname);
                                                 mDrawerLayout.closeDrawer(GravityCompat.START);
                                             } else {
                                                 if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.english)))
                                                     Toast.makeText(DashBoardActivity2.this, getResources().getString(R.string.internet_message_en), Toast.LENGTH_LONG).show();
                                                 else if (apath.getLanguage(DashBoardActivity2.this).equalsIgnoreCase(getString(R.string.arabic)))
                                                     Toast.makeText(DashBoardActivity2.this, getResources().getString(R.string.internet_message_ar), Toast.LENGTH_LONG).show();
                                             }

                                         }
                                     }
        );

        current.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (apath.isUserLogedin(DashBoardActivity2.this)) {
                                               appname = current.getText().toString();
                                               mNavigationManager.showFragmentcurrent(appname);
                                               mDrawerLayout.closeDrawer(GravityCompat.START);
                                           } else {
                                               mDrawerLayout.closeDrawer(GravityCompat.START);
                                               Intent in = new Intent(DashBoardActivity2.this, LoginActivity.class);
                                               startActivity(in);
                                           }
                                       }
                                   }
        );
        future.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          if (apath.isUserLogedin(DashBoardActivity2.this)) {
                                              appname = future.getText().toString();
                                              mNavigationManager.showFragmentfuture(appname);
                                              mDrawerLayout.closeDrawer(GravityCompat.START);
                                          } else {
                                              mDrawerLayout.closeDrawer(GravityCompat.START);
                                              Intent in = new Intent(DashBoardActivity2.this, LoginActivity.class);
                                              startActivity(in);
                                          }
                                      }
                                  }
        );
        history.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (apath.isUserLogedin(DashBoardActivity2.this)) {
                                               appname = history.getText().toString();
                                               mNavigationManager.showFragmenthistory(appname);
                                               mDrawerLayout.closeDrawer(GravityCompat.START);
                                           } else {
                                               mDrawerLayout.closeDrawer(GravityCompat.START);
                                               Intent in = new Intent(DashBoardActivity2.this, LoginActivity.class);
                                               startActivity(in);
                                           }
                                       }
                                   }
        );
        setting.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (apath.isUserLogedin(DashBoardActivity2.this)) {
                                               appname = setting.getText().toString();
                                               mNavigationManager.showFragmentsetting(appname);
                                               mDrawerLayout.closeDrawer(GravityCompat.START);
                                           } else {
                                               mDrawerLayout.closeDrawer(GravityCompat.START);
                                               Intent in = new Intent(DashBoardActivity2.this, LoginActivity.class);
                                               startActivity(in);
                                           }
                                       }
                                   }
        );
        notification.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (apath.isUserLogedin(DashBoardActivity2.this)) {
                                                    appname = notification.getText().toString();
                                                    mNavigationManager.showFragmentnotification(appname);
                                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                                    getSupportActionBar().setTitle(appname);
                                                } else {
                                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                                    Intent in = new Intent(DashBoardActivity2.this, LoginActivity.class);
                                                    startActivity(in);
                                                }
                                            }
                                        }
        );

        terms.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                    mDrawerLayout.closeDrawer(GravityCompat.START);
                                                    Intent in = new Intent(DashBoardActivity2.this, TermsAndConditionActivity.class);
                                                    startActivity(in);
                                            }
                                        }
        );
        privacy.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         mDrawerLayout.closeDrawer(GravityCompat.START);
                                         Intent in = new Intent(DashBoardActivity2.this, PrivacyActivity.class);
                                         startActivity(in);
                                     }
                                 }
        );


        mExpandableListView.addHeaderView(listHeaderView);

        logoout.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
                                           SharedPreferences.Editor editor = sp.edit();
                                           editor.putBoolean("login", false);
                                           editor.commit();
                                           SharedPreferences preferences = getSharedPreferences("address", 0);
                                           SharedPreferences.Editor editor1 = preferences.edit();
                                           editor1.putString("comment", "");
                                           editor1.putString("street", "");
                                           editor1.putString("building", "");
                                           editor1.putString("near", "");
                                           editor1.commit();
                                           Intent in = new Intent(DashBoardActivity2.this, SliderActivity.class);
                                           in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                           in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                           startActivity(in);
                                           finish();
                                       }
                                   }
        );

        mExpandableListView.addFooterView(listfooterview);
        mExpandableListData = ExpandableListDataSource.getData(this);
        mExpandableListTitle = new
                ArrayList(mExpandableListData.keySet()
        );

        addDrawerItems();
        setupDrawer();
        if (savedInstanceState == null) {
            selectFirstItemAsDefault();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (apath.isInternetOn(DashBoardActivity2.this)) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("rakoontoken", 0);
            Log.e("tokenid", pref.getString("tokenid", ""));
            updateToken(apath.getUserid(this), pref.getString("tokenid", ""));
        }

        getCurrentVersion();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.

        mDrawerToggle.syncState();
    }

    private void addDrawerItems() {
        mExpandableListAdapter = new CustomExpandableListAdapter(this, mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                getSupportActionBar().setTitle(mExpandableListTitle.get(groupPosition).toString());
            }
        });

        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                getSupportActionBar().setTitle(appname);
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // String selectedItem = ((List) (mExpandableListData.get(mExpandableListTitle.get(groupPosition)))).get(childPosition).toString();
                //getSupportActionBar().setTitle(selectedItem);

         /*if (orders[childPosition].equalsIgnoreCase(getString(R.string.datetiming))) {
                    appname = orders[childPosition];
                    mNavigationManager.showFragmentCompleted(appname);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else */
                {
                    appname = getString(R.string.app_name);
                    mNavigationManager.showFragmentCompleted(appname);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }


                //  mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void initItems() {
        items = getResources().getStringArray(R.array.option);
        orders = getResources().getStringArray(R.array.settings);
        //  mlm = getResources().getStringArray(R.array.mlm);
    }

    private void selectFirstItemAsDefault() {
        if (mNavigationManager != null) {
            String appname = dashboard.getText().toString();// "Task First";
            mNavigationManager.showFragmentdashboard(appname);
            getSupportActionBar().setTitle(appname);
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(appname);
                invalidateOptionsMenu();
                if (apath.isUserLogedin(DashBoardActivity2.this)) {
                    nameemaillay.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                    logoutlay.setVisibility(View.VISIBLE);
                    name.setText(apath.getName(DashBoardActivity2.this));
                    email.setText(apath.getEmail(DashBoardActivity2.this));
                } else {
                    nameemaillay.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    logoutlay.setVisibility(View.GONE);
                }
                // creates call to onPrepareOptionsMenu()
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );

            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(appname);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                //http://images.fitpregnancy.mdpcdn.com/sites/fitpregnancy.com/files/styles/width_360/public/11-baby-bundled-in-blue-white-fuzzy-hood-shutterstock_120651157.jpg
                //  new ImageLoader(DashBoardActivity.this).DisplayImage("http://images.fitpregnancy.mdpcdn.com/sites/fitpregnancy.com/files/styles/width_360/public/11-baby-bundled-in-blue-white-fuzzy-hood-shutterstock_120651157.jpg", profileicon);
            }
        };
        //  mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onRssItemSelected(String link) {
        if (link.equalsIgnoreCase("setting")) {
            if (apath.isUserLogedin(DashBoardActivity2.this)) {
                appname = setting.getText().toString();
                mNavigationManager.showFragmentsetting(appname);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent in = new Intent(DashBoardActivity2.this, LoginActivity.class);
                startActivity(in);
            }
        } else
            Toast.makeText(DashBoardActivity2.this, link, Toast.LENGTH_LONG).show();
    }

    public static void restartActivity(Activity act) {

        Intent intent = new Intent();
        intent.setClass(act, act.getClass());
        act.startActivity(intent);
        act.finish();

    }

    void updateToken(String user_id, String token) {
        try {
            RequestQueue queue = Volley.newRequestQueue(DashBoardActivity2.this);
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

                        }
                    } catch (Exception e) {

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
                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(DashBoardActivity2.this));
                    params.put("action", "user_token");
                    params.put("user_id", user_id);
                    params.put("device_token", token);
                    Log.e("params dashboard", params.toString());

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
            Singleton.getIntance(DashBoardActivity2.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getCurrentVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        new GetLatestVersion().execute();

    }

    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect("http://play.google.com/store/apps/details?id=" + getPackageName()).get();
                latestVersion = doc.getElementsByClass("htlgb").get(6).text();

            } catch (Exception e) {
                e.printStackTrace();

            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                        showUpdateDialog();
                    }
                }
            } else
                super.onPostExecute(jsonObject);
        }
    }

    private void showUpdateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardActivity2.this);
        builder.setTitle(getResources().getString(R.string.update_title));
        builder.setMessage(getResources().getString(R.string.update_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rateUs();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.reminder_later), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }

    private void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
}
