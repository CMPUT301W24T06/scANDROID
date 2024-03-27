package com.example.scandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseUsersFragment extends Fragment implements onClickListener, UsersArrayAdapter.OnProfileImageClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<Tuple<User, Bitmap>> allUsers;
    ArrayAdapter<Tuple<User, Bitmap>> allUserAdapter;
    private final DBAccessor database = new DBAccessor();
    boolean isAdmin;
    int currentPage = 0;
    int pageSize = 5;
    ListView allUsersList;
    int listSize = 0;
    Button prevButton, nextButton;

    public BrowseUsersFragment() {
        // required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseUsersFragment newInstance(String param1, String param2) {
        BrowseUsersFragment fragment = new BrowseUsersFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allUsersList = view.findViewById(R.id.browse_users_list);
        createInitialPage(this::switchPage);

        prevButton = view.findViewById(R.id.browse_users_previous_button);
        nextButton = view.findViewById(R.id.browse_users_next_button);
        prevButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                switchPage();
            }
        });
        nextButton.setOnClickListener(v -> {;
            if ((currentPage+1) * pageSize <= listSize){
                currentPage++;
                switchPage();
            }
        });

        allUsersList.setOnItemClickListener((parent, view1, position, id) -> {
            String userID = allUserAdapter.getItem(position).first.getUserID();
            Intent viewUserIntent = new Intent(view1.getContext(), ProfileInfoActivity.class);
            viewUserIntent.putExtra("userID", userID);
            viewUserIntent.putExtra("ifAdmin", isAdmin);
            startActivity(viewUserIntent);
        });

        database.accessUser(new DeviceIDRetriever(requireContext()).getDeviceId(), user -> {
            isAdmin = user.getHasAdminPermissions();
            if (isAdmin){
                allUsersList.setOnItemLongClickListener((parent, view12, position, id) -> {
                    DialogFragment userInspectPrompt = new AdminInspectUserFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", allUserAdapter.getItem(position).first.getUserID());
                    userInspectPrompt.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(android.R.id.content, userInspectPrompt);
                    transaction.commit();
                    return true;
                });
            }
        });

    }
    /**
     * Sets and creates the list of events
     * @param page The current page to be viewed
     * @param pageSize The number of users being displayed per page
     */
    //OpenAI, 2024, ChatGPT, How to split list into different pages that can be switched between
    private void setList(int page, int pageSize) {
        int start = page * pageSize;
        allUsers = new ArrayList<>();
        database.getAllUserReferences(List -> {
            int end = Math.min(start + pageSize, List.size());
            for (String userID : List) {
                database.accessUser(userID, user -> database.accessUserProfileImage(user.getUserID(), new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        allUsers.add(new Tuple<>(user, bitmap));
                        if (allUsers.size() == List.size()) {
                            List<Tuple<User, Bitmap>> subList = allUsers.subList(start, end);
                            ArrayList<Tuple<User, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allUserAdapter = new UsersArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseUsersFragment.this);
                            allUsersList.setAdapter(allUserAdapter);
                            nextButton.setVisibility(View.VISIBLE);
                            prevButton.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        Bitmap newProfilePicture = new ProfilePictureGenerator().generatePictureBitmap(user.getUserName());
                        database.storeUserProfileImage(userID, newProfilePicture);
                        allUsers.add(new Tuple<>(user, newProfilePicture));
                        if (allUsers.size() == List.size()) {
                            List<Tuple<User, Bitmap>> subList = allUsers.subList(start, end);
                            ArrayList<Tuple<User, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allUserAdapter = new UsersArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseUsersFragment.this);
                            allUsersList.setAdapter(allUserAdapter);
                            nextButton.setVisibility(View.VISIBLE);
                            prevButton.setVisibility(View.VISIBLE);
                        }
                    }
                }));
            }
        });
    }


    public void switchPage(){
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, listSize);
        List<Tuple<User, Bitmap>> subList = allUsers.subList(start, end);
        ArrayList<Tuple<User, Bitmap>> currentPageList = new ArrayList<>(subList);
        allUserAdapter = new UsersArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseUsersFragment.this);
        allUsersList.setAdapter(allUserAdapter);
    }

    public void createInitialPage(Runnable callback){
        allUsers = new ArrayList<>();
        database.getAllUserReferences(List -> {
            listSize = List.size();
            for (String userID : List) {
                database.accessUser(userID, user -> database.accessUserProfileImage(user.getUserID(), new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        allUsers.add(new Tuple<>(user, bitmap));
                        if (allUsers.size() == List.size()) {
                            List<Tuple<User, Bitmap>> subList = allUsers.subList(0, 5);
                            ArrayList<Tuple<User, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allUserAdapter = new UsersArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseUsersFragment.this);
                            allUsersList.setAdapter(allUserAdapter);
                            nextButton.setVisibility(View.VISIBLE);
                            prevButton.setVisibility(View.VISIBLE);
                            callback.run();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        Bitmap newProfilePicture = new ProfilePictureGenerator().generatePictureBitmap(user.getUserName());
                        database.storeUserProfileImage(userID, newProfilePicture);
                        allUsers.add(new Tuple<>(user, newProfilePicture));
                        if (allUsers.size() == List.size()) {
                            List<Tuple<User, Bitmap>> subList = allUsers.subList(0, 5);
                            ArrayList<Tuple<User, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allUserAdapter = new UsersArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseUsersFragment.this);
                            allUsersList.setAdapter(allUserAdapter);
                            nextButton.setVisibility(View.VISIBLE);
                            prevButton.setVisibility(View.VISIBLE);
                            callback.run();
                        }
                    }
                }));
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.browse_users_fragment, container, false);
    }

    @Override
    public void onClick() {
        createInitialPage(this::switchPage);
    }

    @Override
    public void onProfileImageClicked(User user, Bitmap bitmap) {
        DialogFragment imageInspectPrompt = new AdminInspectImageFragment(bitmap, BrowseUsersFragment.this);
        Bundle bundle = new Bundle();
        bundle.putString("userID", user.getUserID());
        imageInspectPrompt.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, imageInspectPrompt);
        transaction.commit();
    }
}