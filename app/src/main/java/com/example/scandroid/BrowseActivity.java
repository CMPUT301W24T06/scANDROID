package com.example.scandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

/**
 * BrowseActivity is activity for browsing all users and events in the app
 * It extends AppCompatActivity in order to be compatible with
 * older versions of Android as well as use modern Android features.
 */
public class BrowseActivity extends AppCompatActivity {
    TabLayout browseTabs;
    ViewPager2 browsePager;
    BrowseActivityPageAdapter browseActivityPageAdapter;
    BottomNavigationView navigationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_activity);

        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setSelectedItemId(R.id.browse_button);
        browseTabs = findViewById(R.id.browse_tabs);
        browsePager = findViewById(R.id.browse_pager);
        browseActivityPageAdapter = new BrowseActivityPageAdapter(this);
        browsePager.setAdapter(browseActivityPageAdapter);

        browseTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                browsePager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        browsePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(browseTabs.getTabAt(position)).select();
            }
        });

        navigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_button:
                        startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
                        return true;
                    case R.id.qr_button:
                        startActivity(new Intent(getApplicationContext(), QRScannerActivity.class));
                        return true;
                    case R.id.browse_button:
                        return true;
                    case R.id.notification_button:
                        startActivity(new Intent(getApplicationContext(), EventViewAnnouncementsActivity.class));
                        return true;

                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //browseActivityPageAdapter = new BrowseActivityPageAdapter(this);
        //browsePager.setAdapter(browseActivityPageAdapter);
    }
}