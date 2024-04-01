package com.example.scandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * BrowseUsersFragment handles the list of all users in BrowseActivity
 * A simple {@link Fragment} subclass.
 */
public class BrowseUsersFragment extends Fragment implements onClickListener, UsersArrayAdapter.OnProfileImageClickListener {
    ArrayList<Tuple<User, Bitmap>> allUsers;
    ArrayAdapter<Tuple<User, Bitmap>> allUserAdapter;
    private final DBAccessor database = new DBAccessor();
    boolean isAdmin;
    int currentPage = 0;
    int pageSize = 5;
    ListView allUsersList;
    int listSize = 0;
    Button prevButton, nextButton;
    TextView loadingTextView;
    androidx.appcompat.widget.SearchView searchUsersView;

    /**
     * Default constructor for BrowseUsersFragment
     */
    public BrowseUsersFragment() {
        // required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allUsersList = view.findViewById(R.id.browse_users_list);
        createInitialPage(this::switchPage);
        loadingTextView = view.findViewById(R.id.loading_browse_users_text);
        prevButton = view.findViewById(R.id.browse_users_previous_button);
        nextButton = view.findViewById(R.id.browse_users_next_button);
        searchUsersView = view.findViewById(R.id.users_search);
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
                    DialogFragment userInspectPrompt = new AdminInspectUserFragment(BrowseUsersFragment.this);
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
        searchUsersView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    /**
     * For switching pages within the list of all users
     */
    //OpenAI, 2024, ChatGPT, How to split list into different pages that can be switched between
    public void switchPage(){
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, listSize);
        List<Tuple<User, Bitmap>> subList = allUsers.subList(start, end);
        ArrayList<Tuple<User, Bitmap>> currentPageList = new ArrayList<>(subList);
        allUserAdapter = new UsersArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseUsersFragment.this);
        allUsersList.setAdapter(allUserAdapter);
    }

    /**
     * Creates the initial list of all users in app and then sets the first page of users
     * @param callback Synchronize creation of list before setting and switching page
     */
    //OpenAI, 2024, ChatGPT, How to create list before switching page
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
                            List<Tuple<User, Bitmap>> subList = allUsers.subList(0, pageSize);
                            ArrayList<Tuple<User, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allUserAdapter = new UsersArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseUsersFragment.this);
                            allUsersList.setAdapter(allUserAdapter);
                            nextButton.setVisibility(View.VISIBLE);
                            prevButton.setVisibility(View.VISIBLE);
                            loadingTextView.setVisibility(View.INVISIBLE);
                            callback.run();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e) {
                        Bitmap newProfilePicture = new ProfilePictureGenerator().generatePictureBitmap(user.getUserName());
                        database.storeUserProfileImage(userID, newProfilePicture);
                        allUsers.add(new Tuple<>(user, newProfilePicture));
                        if (allUsers.size() == List.size()) {
                            List<Tuple<User, Bitmap>> subList = allUsers.subList(0, pageSize);
                            ArrayList<Tuple<User, Bitmap>> currentPageList = new ArrayList<>(subList);
                            allUserAdapter = new UsersArrayAdapter(requireContext(), currentPageList, getActivity().getSupportFragmentManager(), BrowseUsersFragment.this);
                            allUsersList.setAdapter(allUserAdapter);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.browse_users_fragment, container, false);
    }

    /**
     * Alert BrowseUsersFragment that a remove button has been pressed in order
     * to update list.
     */
    @Override
    public void onClick() {
        createInitialPage(this::switchPage);
    }

    /**
     * Alerts BrowseUsersFragment that a user's profile picture has been clicked on so that it knows to start
     * an popup showing the picture in an AdminInspectImageFragment
     * @param user The user associated with the profile picture that was clicked on
     * @param bitmap The bitmap of the user profile image
     */
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
    public void searchUsersList(String text){
        ArrayList<Tuple<User, Bitmap>> usersResults = new ArrayList<>();
        for (Tuple<User, Bitmap> userData: allUsers){
            User user = userData.first;
            String userName = user.getUserName();
            if (userName.toLowerCase().contains(text.toLowerCase())){
                usersResults.add(userData);
            }
        }
        if (allUserAdapter != null) {
            allUserAdapter.clear(); // Clear existing data from the adapter
            allUserAdapter.addAll(usersResults); // Add filtered results to the adapter
            allUserAdapter.notifyDataSetChanged(); // Notify adapter of changes
        }
    }
}