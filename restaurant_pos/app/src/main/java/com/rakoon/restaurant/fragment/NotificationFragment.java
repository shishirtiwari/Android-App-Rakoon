package com.rakoon.restaurant.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rakoon.restaurant.R;

public class NotificationFragment extends Fragment {

    private static final String KEY_MOVIE_TITLE = "key_title";

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String movieTitle) {
        NotificationFragment fragmentAction = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_TITLE, movieTitle);
        fragmentAction.setArguments(args);

        return fragmentAction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.frag_notification, null);

        return vv;
    }
}
