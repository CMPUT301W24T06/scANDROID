package com.example.scandroid;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * HomepageActivityPageAdapter class is responsible for managing the MyEventsFragment and
 * AttendingEventsFragment displayed in HomepageActivity's ViewPager2 (tabs).
 * It extends FragmentStateAdapter to provide the necessary adapter functionality
 * for handling fragment transactions and lifecycle events.
 */
public class HomepageActivityPageAdapter extends FragmentStateAdapter {
    String userID;

    /**
     * Constructs a new HomepageActivityPageAdapter
     *
     * @param fragmentActivity is the FragmentActivity hosting the adapter
     * @param userID is the ID of the user
     */
    public HomepageActivityPageAdapter(@NonNull FragmentActivity fragmentActivity, String userID) {
        super(fragmentActivity);
        this.userID = userID;
    }

    /**
     * Creates a new fragment based on position.
     * There are 2 tabs in the ViewPager2, so it returns the fragment associated
     * with a specific position in the ViewPager2.
     * @param position is the position of the fragment
     * @return the corresponding Fragment (MyEventsFragment or AttendingEventsFragment)
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                new MyEventsFragment();
                return MyEventsFragment.newInstance(userID,"organizer");
            case 1:
                new MyEventsFragment();
                return MyEventsFragment.newInstance(userID, "attendee");
            default:
                return null;
        }
    }

    /**
     * Returns the total number of fragments managed by the adapter (2 in this case)
     * @return The number of fragments.
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}
