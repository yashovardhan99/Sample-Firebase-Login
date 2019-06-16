package com.yashovardhan99.firebaselogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.yashovardhan99.firebaselogin.databinding.FragmentLoginBinding;

/**
 * Created by Yashovardhan99 on 28/4/19 as a part of Sample-Firebase-Login.
 */
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        return binding.getRoot();
    }
}
