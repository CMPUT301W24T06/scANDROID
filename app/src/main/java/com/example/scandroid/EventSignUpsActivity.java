package com.example.scandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;

/**
 * Activity to display attendees signed up for an event
 */
public class EventSignUpsActivity extends AppCompatActivity implements UsersArrayAdapter.OnProfileImageClickListener{
    ArrayAdapter<Tuple<User, Bitmap>> signUpsListAdapter;
    ArrayList<Tuple<User, Bitmap>> allSignUps = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_attendees_activity);
        Button backButton = findViewById(R.id.event_attendees_list_back_arrow);
        ListView eventSignUpsList = findViewById(R.id.event_attendees_list);
        TextView totalSignUpCount = findViewById(R.id.event_attendees_list_total_attendee_count);
        androidx.appcompat.widget.SearchView searchSignUpsView = findViewById(R.id.attendee_search);

        Event event = (Event)getIntent().getSerializableExtra("event");
        assert event != null;
        ArrayList<String> signUpsDataList = event.getEventSignUps();

        DBAccessor database = new DBAccessor();
        if(signUpsDataList != null) {
            int listSize = signUpsDataList.size();
            totalSignUpCount.setText("Total SignUps: " + signUpsDataList.size());

            for (String userID: signUpsDataList){
                database.accessUser(userID, new UserCallback() {
                    @Override
                    public void onUserRetrieved(User user) {
                        database.accessUserProfileImage(userID, new BitmapCallback() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap) {
                                allSignUps.add(new Tuple<>(user, bitmap));
                                if (allSignUps.size() == listSize){
                                    signUpsListAdapter = new UsersArrayAdapter(
                                            EventSignUpsActivity.this, allSignUps, EventSignUpsActivity.this);
                                    eventSignUpsList.setAdapter(signUpsListAdapter);
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e) {
                                allSignUps.add(new Tuple<>(user, null));
                                if (allSignUps.size() == listSize){
                                    signUpsListAdapter = new UsersArrayAdapter(
                                            EventSignUpsActivity.this, allSignUps, EventSignUpsActivity.this);
                                    eventSignUpsList.setAdapter(signUpsListAdapter);
                                }
                            }
                        });
                    }
                });
            }


            eventSignUpsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent profileInfoIntent = new Intent(EventSignUpsActivity.this, ProfileInfoActivity.class);
                    profileInfoIntent.putExtra("userID", signUpsListAdapter.getItem(position).first.getUserID());
                    startActivity(profileInfoIntent);
                }
            });

        } else {
            totalSignUpCount.setText("Total SignUps: 0");
        }
        // handle search functionality
        searchSignUpsView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsersList(newText);
                return true;
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Search for specific users signed up to event that match keyword
     * @param text Keyword entered by the user
     */
    // Credit: https://www.youtube.com/watch?v=DWIGAkYkpg8
    public void searchUsersList(String text){
        ArrayList<Tuple<User, Bitmap>> usersResults = new ArrayList<>();
        for (Tuple<User, Bitmap> userData: allSignUps){
            User user = userData.first;
            String userName = user.getUserName();
            if (userName.toLowerCase().contains(text.toLowerCase())){
                usersResults.add(userData);
            }
        }
        if (signUpsListAdapter != null) {
            signUpsListAdapter.clear(); // Clear existing data from the adapter
            signUpsListAdapter.addAll(usersResults); // Add filtered results to the adapter
            signUpsListAdapter.notifyDataSetChanged(); // Notify adapter of changes
        }
    }
    @Override
    public void onProfileImageClicked(User user, Bitmap bitmap) {

    }
}
