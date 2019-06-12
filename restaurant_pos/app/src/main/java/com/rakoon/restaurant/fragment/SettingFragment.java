package com.rakoon.restaurant.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rakoon.restaurant.AllPath;
import com.rakoon.restaurant.R;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    private static final String KEY_MOVIE_TITLE = "key_title";
    TabLayout tabLayout;
    ViewPager viewPager;
    AllPath apath;

    private int[] navLabels_en = {
            R.string.change_profile_en,
            R.string.change_password_en,
    };
    private int[] navLabels_ar = {
            R.string.change_profile_ar,
            R.string.change_password_ar,
    };


    public SettingFragment() {
        // Required empty public constructor
    }


    public static SettingFragment newInstance(String movieTitle) {
        SettingFragment fragmentAction = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_TITLE, movieTitle);
        fragmentAction.setArguments(args);

        return fragmentAction;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vv = inflater.inflate(R.layout.frag_setting, container, false);
        apath = new AllPath();

        initFragment(vv);
        return vv;
    }

    void initFragment(View view) {

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //setupTabIcons();
        viewPager.setOffscreenPageLimit(0);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            LinearLayout tab = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.nav_tab, null);
            TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);
            // set the label text by getting the actual string value by its id
            // by getting the actual resource value `getResources().getString(string_id)`
            if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.english))) {
                tab_label.setText(getResources().getString(navLabels_en[i]));
            } else if (apath.getLanguage(getActivity()).equalsIgnoreCase(getString(R.string.arabic))) {
                tab_label.setText(getResources().getString(navLabels_ar[i]));
            }
            // set the home to be active at first
            tab_label.setTextColor(getResources().getColor(R.color.gray));
            if (i == 0) {
                tab_label.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            // finally publish this custom view to navigation tab
            tabLayout.getTabAt(i).setCustomView(tab);
        }
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        // 1. get the custom View you've added
                        View tabView = tab.getCustomView();
                        // get inflated children Views the icon and the label by their id
                        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                        // change the label color, by getting the color resource value
                        tab_label.setTextColor(getResources().getColor(R.color.colorPrimary));
                        // change the image Resource
                        // i defined all icons in an array ordered in order of tabs appearances
                        // call tab.getPosition() to get active tab index.

                        //  Toast.makeText(getActivity(),tab.getPosition()+" pos ",Toast.LENGTH_LONG).show();
                    }

                    // do as the above the opposite way to reset tab when state is changed
                    // as it not the active one any more
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        View tabView = tab.getCustomView();
                        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);

                        // back to the black color
                        tab_label.setTextColor(getResources().getColor(R.color.gray));
                        // and the icon resouce to the old black image
                        // also via array that holds the icon resources in order
                        // and get the one of this tab's position

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );

    }

    private void setupViewPager(ViewPager viewPager) {
        //  ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ChangeProfileFragment(), getResources().getString(R.string.change_profile_en));
        adapter.addFragment(new ChangePasswordFragment(), getResources().getString(R.string.change_password_en));
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
