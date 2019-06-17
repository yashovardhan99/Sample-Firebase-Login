package com.yashovardhan99.firebaselogin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.yashovardhan99.firebaselogin.databinding.FragmentPreloginBinding;

/**
 * Created by Yashovardhan99 on 28/4/19 as a part of Sample-Firebase-Login.
 */
public class PreLoginFragment extends Fragment {

    private final String TAG = "PreLoginFragment";
    private FragmentPreloginBinding binding;
    private CircularProgressDrawable drawable;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prelogin, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = Navigation.findNavController(getActivity(), R.id.nav_host);
        binding.loginButton.setOnClickListener(v -> navController.navigate(R.id.action_preLoginFragment_to_loginFragment));
        binding.signupButton.setOnClickListener(v -> navController.navigate(R.id.action_preLoginFragment_to_signupFragment));
        binding.anonymousButton.setOnClickListener(v -> anonymousSignIn());
    }

    private void anonymousSignIn() {
        showProgress();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "anonymousSignIn: Success");
                FancyToast.makeText(getContext(), "Welcome Anonymous",
                        FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                navController.navigate(PreLoginFragmentDirections.actionPreLoginFragmentToWelcomeFragment());
            } else {
                hideProgress();
                Log.d(TAG, "anonymousSignIn: Error" + task.getException());
                FancyToast.makeText(getContext(), "We couldn't get you in. Please check your internet connection",
                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        });
    }

    private void showProgress() {
        drawable = new CircularProgressDrawable(getContext());
        drawable.setStyle(CircularProgressDrawable.LARGE);
        drawable.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        int size = (int) (drawable.getCenterRadius() + drawable.getStrokeWidth()) * 2;
        drawable.setBounds(0, 0, size, size);
        binding.anonymousButton.setText("Connecting");
        binding.anonymousButton.setCompoundDrawables(null, null, drawable, null);
        drawable.start();

        Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                binding.anonymousButton.invalidate();
            }

            @Override
            public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

            }

            @Override
            public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

            }
        };
        drawable.setCallback(callback);
    }

    private void hideProgress() {
        if (drawable != null)
            drawable.stop();
        drawable = null;
        binding.anonymousButton.setText(R.string.continue_without_signing_in);
        binding.anonymousButton.setCompoundDrawables(null, null, null, null);
    }
}