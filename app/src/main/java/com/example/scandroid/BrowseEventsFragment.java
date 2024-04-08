package com.example.scandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * BrowseEventsFragment handles the list of all events in BrowseActivity
 * A simple {@link Fragment} subclass.
 */
public class BrowseEventsFragment extends Fragment implements onClickListener, CreatedEventsArrayAdapter.OnEventPosterClickListener{
    boolean isAdmin;
    private final DBAccessor database = new DBAccessor();
    int currentPage = 0;
    int pageSize = 5;
    ArrayList<Tuple<Event, Bitmap>> allEvents;
    ArrayAdapter<Tuple<Event, Bitmap>> allEventsAdapter;
    ListView allEventsList;
    Button prevButton, nextButton;
    int listSize = 0;
    TextView loadingTextView;
    androidx.appcompat.widget.SearchView searchEventsView;

    /**
     * Default constructor for BrowseEventsFragment
     */
    public BrowseEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allEventsList = view.findViewById(R.id.browse_event_list);
        //OpenAI, 2024, ChatGPT, How to create list before switching page
        loadingTextView = view.findViewById(R.id.loading_browse_events_text);
        searchEventsView = view.findViewById(R.id.events_search);
        searchEventsView.clearFocus();
        createInitialPage(this::switchPage);

        //Listener for opening activity showing event page when clicking on an event in list
        allEventsList.setOnItemClickListener((parent, view1, position, id) -> {
            Event event = allEventsAdapter.getItem(position).first;
            Intent viewEventIntent = new Intent(view1.getContext(), EventInfoActivity.class);
            viewEventIntent.putExtra("event", event);
            startActivity(viewEventIntent);
        });

        prevButton = view.findViewById(R.id.browse_event_previous_button);
        nextButton = view.findViewById(R.id.browse_event_next_button);
        prevButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                switchPage();
            }
        });
        nextButton.setOnClickListener(v -> {
            if ((currentPage+1) * pageSize <= listSize){
                currentPage++;
                switchPage();
            }
        });

        //Allow admins to long click/hold on events in list
        database.accessUser(new DeviceIDRetriever(requireContext()).getDeviceId(), user -> {
            isAdmin = user.getHasAdminPermissions();
            if (isAdmin){
                allEventsList.setOnItemLongClickListener((parent, view12, position, id) -> {
                    DialogFragment eventInspectPrompt = new AdminInspectEventFragment(BrowseEventsFragment.this);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event", allEventsAdapter.getItem(position).first);
                    eventInspectPrompt.setArguments(bundle);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(android.R.id.content, eventInspectPrompt);
                    transaction.commit();
                    return true;
                });
            }
        });
        searchEventsView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchEventsList(newText);
                return true;
            }
        });
    }

    /**
     * For switching pages within the list of all events
     */
    //OpenAI, 2024, ChatGPT, How to split list into different pages that can be switched between
    public void switchPage(){
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, listSize);
        List<Tuple<Event, Bitmap>> subList = allEvents.subList(start, end);
        ArrayList<Tuple<Event, Bitmap>> currentPageList = new ArrayList<>(subList);
        allEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), currentPageList, BrowseEventsFragment.this);
        allEventsList.setAdapter(allEventsAdapter);
    }

    /**
     * Creates the initial list of all events in app and then sets the first page of events
     * @param callback Synchronize creation of list before setting and switching page
     */
    //OpenAI, 2024, ChatGPT, How to create list before switching page
    public void createInitialPage(Runnable callback){
        allEvents = new ArrayList<>();
        database.getAllEventReferences(List -> {
            listSize = List.size();
            for (String eventID : List) {
                database.accessEvent(eventID, event -> database.accessEventPoster(event.getEventID(), new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        allEvents.add(new Tuple<>(event, bitmap));
                        if (allEvents.size() == List.size()) { //Looped through all event IDs and retrieved all event objects
                            if (allEvents.size() < 5) {
                                ArrayList<Tuple<Event, Bitmap>> currentPageList = new ArrayList<>(allEvents);
                                allEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), currentPageList, BrowseEventsFragment.this);
                                allEventsList.setAdapter(allEventsAdapter);
                                loadingTextView.setVisibility(View.INVISIBLE);
                                callback.run();
                            } else {
                                List<Tuple<Event, Bitmap>> subList = allEvents.subList(0, pageSize);
                                ArrayList<Tuple<Event, Bitmap>> currentPageList = new ArrayList<>(subList);
                                allEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), currentPageList, BrowseEventsFragment.this);
                                allEventsList.setAdapter(allEventsAdapter);
                                nextButton.setVisibility(View.VISIBLE);
                                prevButton.setVisibility(View.VISIBLE);
                                loadingTextView.setVisibility(View.INVISIBLE);
                                callback.run();
                            }
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        //https://stackoverflow.com/questions/68836461/get-bitmap-from-drawable-resource-android
                        //Give default event poster if Event does not already have a poster
                        Bitmap newEventPoster = BitmapFactory.decodeResource(getResources(), R.drawable.add_poster_icon);
                        database.storeEventPoster(eventID, newEventPoster);
                        allEvents.add(new Tuple<>(event, newEventPoster));
                        if (allEvents.size() == List.size()) { //Looped through all event IDs and retrieved all event objects
                            List<Tuple<Event, Bitmap>> subList = allEvents.subList(0, pageSize);
                            ArrayList<Tuple<Event, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), currentPageList, BrowseEventsFragment.this);
                            allEventsList.setAdapter(allEventsAdapter);
                            nextButton.setVisibility(View.VISIBLE);
                            prevButton.setVisibility(View.VISIBLE);
                            loadingTextView.setVisibility(View.INVISIBLE);
                            callback.run();
                        }
                    }
                }));
            }
        });
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.browse_events_fragment, container, false);
    }

    /**
     * Alert BrowseEventsFragment that a remove button has been pressed so that it knows
     * to update list.
     */
    @Override
    public void onClick() {
        createInitialPage(this::switchPage);
    }

    /**
     * Alerts BrowseEventsFragment that an event poster has been clicked on so that it knows to start
     * an popup showing the event poster in an AdminInspectImageFragment
     * @param event The event associated with the poster that was clicked on
     * @param bitmap The bitmap of the event poster
     */
    @Override
    public void onEventPosterClicked(Event event, Bitmap bitmap) {
        DialogFragment imageInspectPrompt = new AdminInspectImageFragment(bitmap, BrowseEventsFragment.this);
        Bundle bundle = new Bundle();
        bundle.putString("eventID", event.getEventID());
        imageInspectPrompt.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, imageInspectPrompt);
        transaction.commit();
    }

    /**
     * Search for specific events that match keyword
     * @param text Keyword entered by the user
     */
    // Credit: https://www.youtube.com/watch?v=DWIGAkYkpg8
    public void searchEventsList(String text){
        ArrayList<Tuple<Event, Bitmap>> eventsResults = new ArrayList<>();
        for (Tuple<Event, Bitmap> eventData: allEvents){
            Event event = eventData.first;
            String eventName = event.getEventName();
            if (eventName.toLowerCase().contains(text.toLowerCase())){
                eventsResults.add(eventData);
            }
        }
        if (allEventsAdapter != null) {
            allEventsAdapter.clear(); // Clear existing data from the adapter
            allEventsAdapter.addAll(eventsResults); // Add filtered results to the adapter
            allEventsAdapter.notifyDataSetChanged(); // Notify adapter of changes
        }
    }
}
