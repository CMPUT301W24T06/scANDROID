package com.example.scandroid;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomepageActivityPageAdapter extends FragmentStateAdapter {
    String userID;
    public HomepageActivityPageAdapter(@NonNull FragmentActivity fragmentActivity, String userID) {
        super(fragmentActivity);
        this.userID = userID;
    }

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

    @Override
    public int getItemCount() {
        return 2;
    }
}
