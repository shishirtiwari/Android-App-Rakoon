package com.rakoon.restaurant.navigation;

/**
 * @author msahakyan
 */

public interface NavigationManager {

    void showFragmentCompleted(String title);

    void showFragmentRecent(String title);

    void showFragmentdashboard(String title);

    void showFragmentcurrent(String title);

    void showFragmentfuture(String title);

    void showFragmenthistory(String title);

    void showFragmentsetting(String title);

    void showFragmentnotification(String title);


}
