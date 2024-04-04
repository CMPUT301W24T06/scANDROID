package com.example.scandroid;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class LimitAttendeesFragment extends DialogFragment {
    interface AddLimitDialogListener{
        void setEventCapacity(int eventCapacity);

    }
    interface RemoveLimitDialogListener{
        void removeEventCapacity();

    }
    private AddLimitDialogListener addLimitListener;
    private RemoveLimitDialogListener removeLimitListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AddLimitDialogListener){
            addLimitListener = (AddLimitDialogListener) context;
        }
        else if (context instanceof  RemoveLimitDialogListener){
            removeLimitListener = (RemoveLimitDialogListener) context;
        }
        else{
            throw new RuntimeException(context + " must implement AddLimitDialog Listener and RemoveLimitDialogListener");
        }
    }
}
