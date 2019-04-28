package com.yashovardhan99.firebaselogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.yashovardhan99.firebaselogin.databinding.FragmentPreloginBinding;

/**
 * Created by Yashovardhan99 on 28/4/19 as a part of Sample-Firebase-Login.
 */
public class PreLoginFragment extends Fragment {

    private FragmentPreloginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prelogin, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
        binding.loginButton.setOnClickListener(v -> navController.navigate(R.id.action_preLoginFragment_to_loginFragment));
        binding.signupButton.setOnClickListener(v -> navController.navigate(R.id.action_preLoginFragment_to_signupFragment));
    }
}
