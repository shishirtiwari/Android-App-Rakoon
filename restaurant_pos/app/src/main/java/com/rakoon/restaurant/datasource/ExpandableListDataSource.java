package com.rakoon.restaurant.datasource;

import android.content.Context;


import com.rakoon.restaurant.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by msahakyan on 22/10/15.
 */
public class ExpandableListDataSource {

    /**
     * Returns fake data of films
     *
     * @param context
     * @return
     */
    public static Map<String, List<String>> getData(Context context) {
        Map<String, List<String>> expandableListData = new TreeMap<>(Collections.reverseOrder());

        List<String> filmGenres = Arrays.asList(context.getResources().getStringArray(R.array.option));

        List<String> tranfermoney = Arrays.asList(context.getResources().getStringArray(R.array.settings));
       // List<String> mlm = Arrays.asList(context.getResources().getStringArray(R.array.mlm));
       /* List<String> contacts = Arrays.asList(context.getResources().getStringArray(R.array.contact));
        List<String> setup = Arrays.asList(context.getResources().getStringArray(R.array.setup));*/


      //  expandableListData.put(filmGenres.get(0), tranfermoney);
//        expandableListData.put(filmGenres.get(1), mlm);
        /*expandableListData.put(filmGenres.get(1), contacts);
        expandableListData.put(filmGenres.get(2), setup);*/


        return expandableListData;
    }
}
