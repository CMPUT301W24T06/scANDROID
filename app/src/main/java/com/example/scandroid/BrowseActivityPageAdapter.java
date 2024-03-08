package com.example.scandroid;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * BrowseActivityPageAdapter class is responsible for managing the BrowseEventsFragment and
 * BrowseUsersFragment displayed in BrowseActivity's ViewPager2 (tabs).
 * It extends FragmentStateAdapter to provide the necessary adapter functionality
 * for handling fragment transactions and lifecycle events.
 */
public class BrowseActivityPageAdapter extends FragmentStateAdapter {

    /**
     * Constructs a new BrowseActivityPageAdapter instance.
     * @param fragmentActivity the FragmentActivity hosting the adapter.
     */
    public BrowseActivityPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * There are 2 tabs in the ViewPager2, so it returns the fragment associated
     * with a specific position in the ViewPager2.
     * @param position position of the fragment in the ViewPager2 (tabs).
     * @return corresponding Fragment (BrowseUsersFragment or BrowseEventsFragment)
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new BrowseUsersFragment();
            case 1:
                return new BrowseEventsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
