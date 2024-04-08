package com.example.scandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;


/**
 * Code for the Limit Attendees Fragment that is called upon clicking a button within the creation of an event.
 * @author Moyo Dawodu
 */
public class LimitAttendeesFragment extends DialogFragment {
    public LimitAttendeesFragment(){
    }

    /**
     * onCreate method overridden to customize behaviour.
     * 
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return a View object.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_or_edit_attendee_limit_fragment, container, false);

        AppCompatButton cancel = view.findViewById(R.id.cancel_button_limit_attendees);
        AppCompatButton remove = view.findViewById(R.id.remove_button_limit_attendees);
        AppCompatButton confirm = view. findViewById(R.id.confirm_button_limit_attendees);
        EditText attendeeLimEdit = view.findViewById(R.id.enter_limit_editText);

        cancel.setOnClickListener(v -> {
            dismiss();
        });

        confirm.setOnClickListener(v ->{
            CreateEventActivity activity = (CreateEventActivity) getActivity();
            String entered = attendeeLimEdit.getText().toString();
            if(entered.equals("")){
                entered = "0";
            }
            int lim = Integer.parseInt(entered);
            Bundle limitValue = new Bundle();
            limitValue.putInt("attendeeLimit", lim);
            if (activity != null){
                activity.onDataReceived(limitValue);
                getParentFragmentManager().setFragmentResult("requestKey", limitValue);
            }
            dismiss();
        });

        remove.setOnClickListener(v ->{
            CreateEventActivity activity = (CreateEventActivity) getActivity();
            Bundle newLimit = new Bundle();
            newLimit.putInt("attendeeLimit", 0);
            if (activity != null){
                activity.onDataReceived(newLimit);
                getParentFragmentManager().setFragmentResult("requestKey", newLimit);

            }

            dismiss();
        });

        return view;
    }


}
