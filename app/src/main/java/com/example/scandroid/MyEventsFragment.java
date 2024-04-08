package com.example.scandroid;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEventsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userID;
    private String userType;
    ArrayAdapter<Tuple<Event, Bitmap>> myEventsAdapter;
    private DBAccessor database;
    private ArrayList<String>  myEventIDs = new ArrayList<>();
    private ArrayList<String> myRealEventIDs;
    private ArrayList<Tuple<Event, Bitmap>> allMyEvents;
    private ListView myEventsList;
    private TextView loadingTextView;

    /**
     * Default constructor for the MyEventsFragment.
     */
    public MyEventsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = new DBAccessor();
        //if (getArguments() != null) {
            //userID = getArguments().getString(ARG_PARAM1);
            //userType = getArguments().getString(ARG_PARAM2);
       //}
        myEventsList = view.findViewById(R.id.my_events_list);
        loadingTextView = view.findViewById(R.id.loading_my_events_text);

        myRealEventIDs = new ArrayList<>();
        allMyEvents = new ArrayList<>();
        //change from here
        new DBAccessor().accessUser(userID, user -> {
            if (Objects.equals(userType, "organizer")) {
                myEventIDs = user.getEventsOrganized();
            } else if (Objects.equals(userType, "attendee")){
                ArrayList<String> attendingEvents;
                attendingEvents = user.getEventsAttending();
                myEventIDs.addAll(attendingEvents);
                ArrayList<String> signedUpEvents = user.getEventsSignedUp();
                for (String eventID : signedUpEvents){
                    if (!myEventIDs.contains(eventID)){
                        myEventIDs.add(eventID);
                    }
                }
            }
            if (myEventIDs == null || !myEventIDs.isEmpty()){
                makeEventList(user);
            } else {
                loadingTextView.setText("No events");
            }

        });

       myEventsList.setOnItemClickListener((parent, view1, position, id) -> {
            //String eventID = myEventsAdapter.getItem(position);
            Event event = myEventsAdapter.getItem(position).first;
            if (Objects.equals(userType, "organizer")) {

                // Source: https://stackoverflow.com/a/24610673/20869063 Stack Overflow. Answered by Ahmad, Nisar. Downloaded 2024-03-07
                Intent i = new Intent(getActivity(), EditEventActivity.class);
                i.putExtra("event", (Serializable) event);
                startActivity(i);

            } else {
                Intent viewEventIntent = new Intent(view1.getContext(), EventInfoActivity.class);
                viewEventIntent.putExtra("event", event);
                startActivity(viewEventIntent);
            }
        });
    }

    /**
     * Create a list of events
     * @param user The user where the list of the events came from
     */
    public void makeEventList(User user){
        List<String> eventsToRemove = new ArrayList<>();
        myRealEventIDs = new ArrayList<>();
        allMyEvents = new ArrayList<>();
        for (String anEvent : myEventIDs) {
            database.accessEvent(anEvent, event -> {
                if (event == null) {
                    if (Objects.equals(userType, "organizer")) {
                        NoticeFragment removalNoticeFragment = new NoticeFragment("Your event was removed by our admins");
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.add(android.R.id.content, removalNoticeFragment);
                        transaction.commit();
                    }
                    eventsToRemove.add(anEvent);
                } else {
                    myRealEventIDs.add(anEvent);
                }

                if (myRealEventIDs.size() + eventsToRemove.size() == myEventIDs.size()) {
                    // Remove the events that need to be removed
                    if (Objects.equals(userType, "organizer")){
                        myEventIDs.removeAll(eventsToRemove);
                    } else {
                        ArrayList<String> attendingEvents = user.getEventsAttending();
                        ArrayList<String> signedUpEvents = user.getEventsSignedUp();
                        for (String eventID : eventsToRemove){
                            if (attendingEvents.contains(eventID)){
                                attendingEvents.remove(eventID);
                            } else {
                                signedUpEvents.remove(eventID);
                            }
                        }
                    }
                    database.storeUser(user);
                    //OpenAI, 2024, ChatGPT, How to sort list of objects by Calendar
                    Comparator<Tuple<Event, Bitmap>> dateComparator = (tuple1, tuple2) -> {
                        Calendar date1 = tuple1.first.getEventDate();
                        Calendar date2 = tuple2.first.getEventDate();

                        // Compare dates
                        if (date1.before(date2)) {
                            return -1;
                        } else if (date1.after(date2)) {
                            return 1;
                        } else {
                            return 0;
                        }
                    };
                    //loop through events here and get their posters to add to tuple
                    for (String eventID : myEventIDs){
                        database.accessEvent(eventID, event1 -> database.accessEventPoster(eventID, new BitmapCallback() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap) {
                                allMyEvents.add(new Tuple<>(event1, bitmap));
                                if (allMyEvents.size() == myEventIDs.size()){
                                    allMyEvents.sort(dateComparator);
                                    if (isAdded()) {
                                        myEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), allMyEvents);
                                    }
                                    myEventsList.setAdapter(myEventsAdapter);
                                    loadingTextView.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e) {
                                allMyEvents.add(new Tuple<>(event, null));
                                if (allMyEvents.size() == myEventIDs.size()) {
                                    allMyEvents.sort(dateComparator);
                                    myEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), allMyEvents);
                                    myEventsList.setAdapter(myEventsAdapter);
                                    loadingTextView.setVisibility(View.INVISIBLE);
                                }
                            }
                        }));
                    }
                }
            });
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyEventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyEventsFragment newInstance(String param1, String param2) {
        MyEventsFragment fragment = new MyEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(ARG_PARAM1);
            userType = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_events_fragment, container, false);
    }
}