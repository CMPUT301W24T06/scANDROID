package com.example.scandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseEventsFragment extends Fragment implements onClickListener, CreatedEventsArrayAdapter.OnProfileImageClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boolean isAdmin;
    private final DBAccessor database = new DBAccessor();
    int currentPage = 0;
    int pageSize = 5;
    ArrayList<Tuple<Event, Bitmap>> allEvents;
    ArrayAdapter<Tuple<Event, Bitmap>> allEventsAdapter;
    ListView allEventsList;
    Button prevButton, nextButton;
    int listSize = 0;
    public BrowseEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allEventsList = view.findViewById(R.id.browse_event_list);
        createInitialPage(this::switchPage);

        allEventsList.setOnItemClickListener((parent, view1, position, id) -> {
            String eventID = allEventsAdapter.getItem(position).first.getEventID();
            Intent viewEventIntent = new Intent(view1.getContext(), EventInfoActivity.class);
            viewEventIntent.putExtra("eventID", eventID);
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
        
        database.accessUser(new DeviceIDRetriever(requireContext()).getDeviceId(), user -> {
            isAdmin = user.getHasAdminPermissions();
            if (isAdmin){
                allEventsList.setOnItemLongClickListener((parent, view12, position, id) -> {
                    DialogFragment eventInspectPrompt = new AdminInspectEventFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("eventID", allEventsAdapter.getItem(position).first.getEventID());
                    eventInspectPrompt.setArguments(bundle);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(android.R.id.content, eventInspectPrompt);
                    transaction.commit();
                    return true;
                });
            }
        });

    }

    //OpenAI, 2024, ChatGPT, How to split list into different pages that can be switched between
    public void switchPage(){
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, listSize);
        List<Tuple<Event, Bitmap>> subList = allEvents.subList(start, end);
        ArrayList<Tuple<Event, Bitmap>> currentPageList = new ArrayList<>(subList);
        allEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseEventsFragment.this);
        allEventsList.setAdapter(allEventsAdapter);
    }

    public void createInitialPage(Runnable callback){
        allEvents = new ArrayList<>();
        database.getAllEventReferences(List -> {
            listSize = List.size();
            for (String eventID : List) {
                database.accessEvent(eventID, event -> database.accessEventPoster(event.getEventID(), new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        allEvents.add(new Tuple<>(event, bitmap));
                        if (allEvents.size() == List.size()) {
                            List<Tuple<Event, Bitmap>> subList = allEvents.subList(0, 5);
                            ArrayList<Tuple<Event, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseEventsFragment.this);
                            allEventsList.setAdapter(allEventsAdapter);
                            nextButton.setVisibility(View.VISIBLE);
                            prevButton.setVisibility(View.VISIBLE);
                            callback.run();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        //https://stackoverflow.com/questions/68836461/get-bitmap-from-drawable-resource-android
                        Bitmap newEventPoster = BitmapFactory.decodeResource(getResources(), R.drawable.add_poster_icon);
                        database.storeEventPoster(eventID, newEventPoster);
                        allEvents.add(new Tuple<>(event, newEventPoster));
                        if (allEvents.size() == List.size()) {
                            List<Tuple<Event, Bitmap>> subList = allEvents.subList(0, 5);
                            ArrayList<Tuple<Event, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allEventsAdapter = new CreatedEventsArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseEventsFragment.this);
                            allEventsList.setAdapter(allEventsAdapter);
                            nextButton.setVisibility(View.VISIBLE);
                            prevButton.setVisibility(View.VISIBLE);
                            callback.run();
                        }
                    }
                }));
            }
        });
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseEventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseEventsFragment newInstance(String param1, String param2) {
        BrowseEventsFragment fragment = new BrowseEventsFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.browse_events_fragment, container, false);
    }


    @Override
    public void onClick() {

    }

    @Override
    public void onProfileImageClicked(Event event, Bitmap bitmap) {
        DialogFragment imageInspectPrompt = new AdminInspectImageFragment(bitmap, BrowseEventsFragment.this);
        Bundle bundle = new Bundle();
        bundle.putString("eventID", event.getEventID());
        imageInspectPrompt.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, imageInspectPrompt);
        transaction.commit();
    }
}