package com.example.scandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * UsersArrayAdapter is an ArrayAdapter implementation used in the app for displaying
 * a list of users
 */
public class UsersArrayAdapter extends ArrayAdapter<Tuple<User, Bitmap>> {

    private OnProfileImageClickListener onProfileImageClickListener;
    /**
     * Constructs a new UsersArrayAdapter
     *
     * @param context context where the adapter is being used
     * @param users list of users and their profile pictures to display
     */
    public UsersArrayAdapter(Context context, ArrayList<Tuple<User, Bitmap>> users, OnProfileImageClickListener onProfileImageClickListener) {
        super(context,0, users);
        this.onProfileImageClickListener = onProfileImageClickListener;
    }

    /**
     * Gets a View that displays the data of a specific user in the data set
     *
     * @param position position of the item within the adapter's data set
     * @param convertView old view to reuse (if possible)
     * @param parent parent that this view will eventually be attached to
     * @return a View that corresponds to the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_list_content, parent, false);
        }
        else {
            view = convertView;
        }

        TextView userNameText = view.findViewById(R.id.user_list_content_name);
        ImageView userProfilePicture = view.findViewById(R.id.user_content_profile_picture);

        Tuple<User, Bitmap> tuple = getItem(position);
        assert tuple != null;
        String userName = tuple.first.getUserName();
        userNameText.setText(userName);
        userProfilePicture.setImageBitmap(tuple.second);

        userProfilePicture.setOnClickListener(v ->
                onProfileImageClickListener.onProfileImageClicked(tuple.first, tuple.second));

        return view;
    }
    public interface OnProfileImageClickListener {
        void onProfileImageClicked(User user, Bitmap bitmap);
    }
}
