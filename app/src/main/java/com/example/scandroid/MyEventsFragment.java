package com.example.scandroid;


import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;

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
    ArrayAdapter<String> myEventsAdapter;
    private DBAccessor database;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);
        database = new DBAccessor();
        if (getArguments() != null) {
            userID = getArguments().getString(ARG_PARAM1);
            userType = getArguments().getString(ARG_PARAM2);
        }
        ListView myEventsList = view.findViewById(R.id.my_events_list);
        new DBAccessor().accessUser(userID, user -> {
            if (Objects.equals(userType, "organizer")) {
                ArrayList<String> myEvents = user.getEventsOrganized();
                myEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), myEvents, userID);
                myEventsList.setAdapter(myEventsAdapter);
            }
            else if (Objects.equals(userType, "attendee")){
                ArrayList<String> myEvents = user.getEventsAttending();
                myEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), myEvents, userID);
                myEventsList.setAdapter(myEventsAdapter);

                myEventsList.setOnItemClickListener((parent, view, position, id) -> {
                    String event = myEvents.get(position);
                    database.accessEvent(event, event1 -> {
                        // Source: https://stackoverflow.com/a/24610673/20869063 Stack Overflow. Answered by Ahmad, Nisar. Downloaded 2024-03-07
                        Intent i = new Intent(getActivity(), EditEventActivity.class);
                        i.putExtra("event", (Serializable) event1);
                        startActivity(i);
                    });
                });
            }
        });

        myEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Objects.equals(userType, "organizer")){
                    String eventID = myEventsAdapter.getItem(position);
                    Intent editEventIntent = new Intent(view.getContext(), CreateEventActivity.class);
                    editEventIntent.putExtra("eventID", eventID);
                    startActivity(editEventIntent);
                } else {
                    String eventID = myEventsAdapter.getItem(position);
                    Intent viewEventIntent = new Intent(view.getContext(), EventInfoActivity.class);
                    viewEventIntent.putExtra("eventID", eventID);
                    startActivity(viewEventIntent);
                }
                //Retrieve the new information about the lists
                new DBAccessor().accessUser(userID, user -> {
                    if (Objects.equals(userType, "organizer")) {
                        ArrayList<String> myEvents = user.getEventsOrganized();
                        myEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), myEvents, userID);
                        myEventsList.setAdapter(myEventsAdapter);
                    }
                    else if (Objects.equals(userType, "attendee")){
                        ArrayList<String> myEvents = user.getEventsAttending();
                        myEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), myEvents, userID);
                        myEventsList.setAdapter(myEventsAdapter);
                    }
                    //Update the adapter
                    myEventsAdapter.notifyDataSetChanged();
                });
            }
        });
    }


    public MyEventsFragment() {
        // Required empty public constructor
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