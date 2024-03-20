package com.example.scandroid;

import android.content.Intent;
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
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseUsersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String userID;
    ArrayAdapter<String> allUsersAdapter;
    private final DBAccessor database = new DBAccessor();

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
        //boolean isAdmin = false;
        boolean isAdmin = true;// Setting to true now for testing
        if (getArguments() != null) {
            userID = getArguments().getString(ARG_PARAM1);
            //Some way to tell if user is admin or not. user.isAdmin()?
            //isAdmin = true;

        }
        ListView allUsersList = view.findViewById(R.id.browse_users_list);
        database.getAllUserReferences(List -> {
            allUsersAdapter = new UsersArrayAdapter(requireContext(), List);
            allUsersList.setAdapter(allUsersAdapter);
        });

        allUsersList.setOnItemClickListener((parent, view1, position, id) -> {
            String userID = allUsersAdapter.getItem(position);
            Intent viewUserIntent = new Intent(view1.getContext(), ProfileInfoActivity.class);
            viewUserIntent.putExtra("userID", userID);
            startActivity(viewUserIntent);

            //Retrieve the new information about the lists
            database.getAllUserReferences(List -> {
                allUsersAdapter = new UsersArrayAdapter(requireContext(), List);
                allUsersList.setAdapter(allUsersAdapter);
            });
            //Update the adapter
            allUsersAdapter.notifyDataSetChanged();
        });
        if (isAdmin){
            allUsersList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    DialogFragment userInspectPrompt = new AdminInspectUserFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", allUsersAdapter.getItem(position));
                    userInspectPrompt.setArguments(bundle);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(android.R.id.content, userInspectPrompt);
                    transaction.commit();
                    return true;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.browse_users_fragment, container, false);
    }
}