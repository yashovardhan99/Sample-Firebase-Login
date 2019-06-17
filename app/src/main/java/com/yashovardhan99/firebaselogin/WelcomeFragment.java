package com.yashovardhan99.firebaselogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Yashovardhan99 on 28/4/19 as a part of Sample-Firebase-Login.
 */
public class WelcomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().show();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
