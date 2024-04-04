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
 * EventAttendeesActivity is a class responsible for
 * displaying event attendees and providing search functionality.
 */
public class EventAttendeesActivity extends AppCompatActivity {
    ArrayAdapter<Event.CheckIn> attendeeListAdapter;
    ArrayList<Event.CheckIn> attendeeDataList;
    DBAccessor database = new DBAccessor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_attendees_activity);
        Button backButton = findViewById(R.id.event_attendees_list_back_arrow);
        ListView attendeeList = findViewById(R.id.event_attendees_list);
        TextView totalAttendeeCount = findViewById(R.id.event_attendees_list_total_attendee_count);
        androidx.appcompat.widget.SearchView searchSignUpsView = findViewById(R.id.attendee_search);

        Event event = (Event)getIntent().getSerializableExtra("event");
        assert event != null;
        attendeeDataList = event.getEventAttendeeList();

        if(attendeeDataList != null) {
            attendeeListAdapter = new EventAttendeesArrayAdapter(this, attendeeDataList, event.getEventID());
            attendeeList.setAdapter(attendeeListAdapter);
            totalAttendeeCount.setText("Total Attendees: " + attendeeDataList.size());

            attendeeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent profileInfoIntent = new Intent(EventAttendeesActivity.this, ProfileInfoActivity.class);
                    profileInfoIntent.putExtra("attendee", attendeeListAdapter.getItem(position));
                    profileInfoIntent.putExtra("eventID", event.getEventID());
                    startActivity(profileInfoIntent);
                }
            });

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

        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
                }
        });


    }

    /**
     * Search for specific users attending event that match keyword
     * @param text Keyword entered by the user
     */
    // Credit: https://www.youtube.com/watch?v=DWIGAkYkpg8
    public void searchUsersList(String text){
        ArrayList<Event.CheckIn> usersResults = new ArrayList<>();
        for (Event.CheckIn userData: attendeeDataList){
            database.accessUser(userData.getUserID(), user -> {
                String userName = user.getUserName();
                if (userName.toLowerCase().contains(text.toLowerCase())){
                    usersResults.add(userData);
                }
            });

        }
        if (attendeeListAdapter != null) {
            attendeeListAdapter.clear(); // Clear existing data from the adapter
            attendeeListAdapter.addAll(usersResults); // Add filtered results to the adapter
            attendeeListAdapter.notifyDataSetChanged(); // Notify adapter of changes
        }
    }
}