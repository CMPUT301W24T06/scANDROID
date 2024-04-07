package com.example.scandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class AnnouncementsArrayAdapter extends ArrayAdapter<Event.EventAnnouncement> {
    public AnnouncementsArrayAdapter(Context context, ArrayList<Event.EventAnnouncement> announcementList) {
        super(context, 0, announcementList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.announcement_list_content, parent, false);
        } else {
            view = convertView;
        }
        TextView announcementName = view.findViewById(R.id.announcements_content_name);
        TextView announcementAbout = view.findViewById(R.id.announcements_content_about);
        TextView announcementOrganizer = view.findViewById(R.id.announcements_content_organizer);
        TextView announcementTimeAgo = view.findViewById(R.id.announcements_content_time_ago);

        Event.EventAnnouncement announcement = getItem(position);
        assert announcement != null;
        DBAccessor database = new DBAccessor();
        database.accessUser(announcement.getAnnouncementOrganizerID(), user -> {
            announcementName.setText(announcement.getAnnouncementTitle());
            announcementAbout.setText(announcement.getAnnouncementAbout());
            announcementOrganizer.setText(user.getUserName());
            Time announcementTime = announcement.getAnnouncementTime();
            long currentTimeMillis = System.currentTimeMillis();
            Time currentTime = new Time(currentTimeMillis);
            long timeDifferenceMillis = currentTime.getTime() - announcementTime.getTime();
            long minutes = timeDifferenceMillis / (60 * 1000);
            announcementTimeAgo.setText(minutes + "m ago");
        });
        return view;
    }
}
