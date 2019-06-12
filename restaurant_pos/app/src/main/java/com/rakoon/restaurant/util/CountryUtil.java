package com.rakoon.restaurant.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.rakoon.restaurant.model.Country;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CountryUtil {

    public static String AssetJSONFile(String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    public ArrayList<Country> getcountryList(Context ctx) {
        ArrayList<Country> clist = new ArrayList<>();
        try {
            String jsonLocation = AssetJSONFile("country.json", ctx);
            JSONArray jarr = new JSONArray(jsonLocation);
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.optJSONObject(i);
                clist.add(new Country(jobj.optString("name"), jobj.optString("dial_code"), jobj.optString("code")));
            }
        } catch (Exception e) {

        }
        return clist;
    }
}
