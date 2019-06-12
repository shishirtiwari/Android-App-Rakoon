package com.rakoon.restaurant.navigation;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.rakoon.restaurant.BuildConfig;
import com.rakoon.restaurant.DashBoardActivity2;
import com.rakoon.restaurant.R;
import com.rakoon.restaurant.fragment.CurrentFragment;
import com.rakoon.restaurant.fragment.DashBoardFragment;
import com.rakoon.restaurant.fragment.FutureFragment;
import com.rakoon.restaurant.fragment.HistoryFragment;
import com.rakoon.restaurant.fragment.NotificationFragment;
import com.rakoon.restaurant.fragment.RecentFragment;
import com.rakoon.restaurant.fragment.SettingFragment;


public class FragmentNavigation2 implements NavigationManager {

    private static FragmentNavigation2 sInstance;

    private FragmentManager mFragmentManager;
    private DashBoardActivity2 mActivity;

    public static FragmentNavigation2 obtain(DashBoardActivity2 activity) {
        if (sInstance == null) {
            sInstance = new FragmentNavigation2();
        }
        sInstance.configure(activity);
        return sInstance;
    }

    private void configure(DashBoardActivity2 activity) {
        mActivity = activity;
        mFragmentManager = mActivity.getSupportFragmentManager();
    }

    @Override
    public void showFragmentCompleted(String title) {
        showFragment(RecentFragment.newInstance(title), false);
    }

    @Override
    public void showFragmentRecent(String title) {
        showFragment(RecentFragment.newInstance(title), false);
    }

    @Override
    public void showFragmentdashboard(String title) {
        showFragment(DashBoardFragment.newInstance(title), false);
    }

    @Override
    public void showFragmentcurrent(String title) {
        showFragment(CurrentFragment.newInstance(title), false);
    }

    @Override
    public void showFragmentfuture(String title) {
        showFragment(FutureFragment.newInstance(title), false);
    }

    @Override
    public void showFragmenthistory(String title) {
        showFragment(HistoryFragment.newInstance(title), false);
    }

    @Override
    public void showFragmentsetting(String title) {
        showFragment(SettingFragment.newInstance(title), false);
    }

    @Override
    public void showFragmentnotification(String title) {
        showFragment(NotificationFragment.newInstance(title), false);
    }


    private void showFragment(Fragment fragment, boolean allowStateLoss) {
        FragmentManager fm = mFragmentManager;

        @SuppressLint("CommitTransaction")
        FragmentTransaction ft = fm.beginTransaction()
                .replace(R.id.content_frame, fragment);

        ft.addToBackStack(null);

        if (allowStateLoss || !BuildConfig.DEBUG) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }

        fm.executePendingTransactions();
    }
}
