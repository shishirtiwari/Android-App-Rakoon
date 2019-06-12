package com.rakoon.restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Admin on 12/12/2017.
 */

public class AllPath {
    String passwordvalidstin = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*?&]{6,}";
    public String MobilePattern = "[0-9]{10}";
    String PinCodePattern = "[0-9]{5,10}";
    public static String dformat = "yyyy-MM-dd HH:mm:ss";
    // public String MAIN_URL = "http://43.229.227.26:81/buildmapper/Api/AndroidAppApi/";
    // public String MAIN_URL = "http://salesquid.itsabacus.org/api/";
    public String MAIN_URL = "http://www.uptorko.com/rakoon/wp-content/themes/atork_admin_panel/api/";
    public String successurl = "http://uptorko.com/wp-content/themes/atork_admin_panel/api/paymentRespone.php";
    public String failureurl = MAIN_URL + "failureurl";

    public static final String CHANNEL_ID = "Rakoon Restaurant";
    public static final String CHANNEL_NAME = "Rakoon Restaurant POS";
    public static final String CHANNEL_DESC = "Rakoon Restaurant POS desc";


    public String BRANCH_CATEGORY = "branch_category.php";
    public String DASHBOARD = "menus.php";
    public String AREA = "area.php";
    public String ALL_BRANCHES = "all_branches.php";
    public String RESERVATION = "reservation.php";
    public String DISCOUNT = "discount.php";
    public String WELCOMEIMAGES = "settings.php";
    public String USER = "users.php";
    public String CATEGORYITEM = "category_item.php";
    public String EXTRA_STUFF = "extra_stuff.php";

    public String ITEM = "item.php";
    public String BOOKORDER = "orders.php";
    public String PAYMENT = "payment.php";
    public String TERMS = "terms_conditions.php";
    public String PRIVACY = "privacy.php";

    public String getMethodUrl(String methodName) {
        String url = "";
        url = MAIN_URL + methodName;
        return url;
    }

    public boolean isInternetOn(Context ctx) {
        ConnectivityManager connManager1 = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
        NetworkInfo mMobile = connManager1.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mMobile.isConnected()) {
            return true;
        }

        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }

        return false;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public String getLanguage(Activity activity) {
        String lang = "";
        SharedPreferences sp = activity.getSharedPreferences("userdata", activity.MODE_PRIVATE);
        lang = sp.getString("lang", "");
        return lang;
    }

    public String getUserid(Activity activity) {
        String userid = "";
        SharedPreferences sp = activity.getSharedPreferences("userdata", activity.MODE_PRIVATE);
        userid = sp.getString("userid", "");
        return userid;
    }

    public String getName(Activity activity) {
        String name = "";
        SharedPreferences sp = activity.getSharedPreferences("userdata", activity.MODE_PRIVATE);
        name = sp.getString("name", "");
        return name;
    }

    public String getEmail(Activity activity) {
        String email = "";
        SharedPreferences sp = activity.getSharedPreferences("userdata", activity.MODE_PRIVATE);
        email = sp.getString("email", "");
        return email;
    }

    public String getLang(Activity activity) {
        String lang = "";
        SharedPreferences sp = activity.getSharedPreferences("userdata", activity.MODE_PRIVATE);
        if (sp.getString("lang", "").equalsIgnoreCase(activity.getResources().getString(R.string.english)))
            lang = "en";
        else if (sp.getString("lang", "").equalsIgnoreCase(activity.getResources().getString(R.string.arabic)))
            lang = "ar";
        return lang;
    }

    public boolean isUserLogedin(Activity activity) {
        boolean login = false;
        SharedPreferences sp = activity.getSharedPreferences("userdata", activity.MODE_PRIVATE);
        login = sp.getBoolean("login", false);
        return login;
    }

    public void startCallActivity(Context activity) {
        SharedPreferences sp = activity.getSharedPreferences("userdata", activity.MODE_PRIVATE);
        String phonenum = sp.getString("phone_num", "");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phonenum));
        activity.startActivity(intent);
    }

    public void startshareActivity(Activity activity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=com.rakoon.restaurant");
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

}
