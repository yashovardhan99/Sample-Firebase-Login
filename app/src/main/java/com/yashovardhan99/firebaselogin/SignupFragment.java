package com.yashovardhan99.firebaselogin;

import android.graphics.Color;
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

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.yashovardhan99.firebaselogin.databinding.FragmentSignupBinding;

/**
 * Created by Yashovardhan99 on 28/4/19 as a part of Sample-Firebase-Login.
 */
public class SignupFragment extends Fragment {
    private FragmentSignupBinding binding;
    private final String TAG = "SignUpFragment";
    private CircularProgressDrawable drawable;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        binding.signupButton.setOnClickListener(this::signup);
        ((MainActivity)getActivity()).getSupportActionBar().show();
        return binding.getRoot();
    }

    private void signup(View v) {
        showProgress();
        String name = binding.nameEdittext.getText().toString();
        String email = binding.emailEdittext.getText().toString();
        String password = binding.passwordEdittext.getText().toString();
        String confirmPassword = binding.confirmPasswordEdittext.getText().toString();
        if (!validate(name, email, password, confirmPassword)) {
            hideProgress();
            return;
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authResultTask -> {
            if (authResultTask.isSuccessful())
                onSignUp(authResultTask.getResult(), name);
            else {
                onSignUpFailed(authResultTask.getException());
                hideProgress();
            }
        });
    }

    private void hideProgress() {
        if (drawable != null)
            drawable.stop();
        drawable = null;
        binding.signupButton.setText(R.string.sign_up);
        binding.signupButton.setCompoundDrawables(null, null, null, null);
    }

    private void showProgress() {
        drawable = new CircularProgressDrawable(getContext());
        drawable.setStyle(CircularProgressDrawable.LARGE);
        drawable.setColorSchemeColors(Color.WHITE);
        int size = (int) (drawable.getCenterRadius() + drawable.getStrokeWidth()) * 2;
        drawable.setBounds(0, 0, size, size);
        binding.signupButton.setText("Signing Up");
        binding.signupButton.setCompoundDrawables(null, null, drawable, null);
        drawable.start();

        Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                binding.signupButton.invalidate();
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

    private boolean validate(String name, String email, String password, String confirmPassword) {
        Log.d(TAG, "validate: " + name);
        if (name != null && name.length() >= 4) {
            binding.nameInputelayout.setError(null);
            if (password != null && password.length() >= 8) {
                binding.passwordInputlayout.setError(null);
                if (password.equals(confirmPassword)) {
                    binding.confirmPasswordInputlayout.setError(null);
                    if (email != null && email.length() >= 8 && email.contains("@")) {
                        binding.emailInputlayout.setError(null);
                        return true;
                    } else
                        binding.emailInputlayout.setError("Invalid Email");
                } else
                    binding.confirmPasswordInputlayout.setError("Both passwords must match");
            } else
                binding.passwordInputlayout.setError("Password too short");
        } else
            binding.nameInputelayout.setError("Name too short");
        return false;
    }

    private void onSignUp(AuthResult result, String name) {
        Log.d(TAG, "onSignUp: Success");
        FirebaseUser user = result.getUser();
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(changeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "onSignUp: Successfully updated name");
                FancyToast.makeText(getContext(), "Success!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
                navController.navigate(R.id.welcomeFragment);
            } else {
                Log.d(TAG, "onSignUp: Error updating name. Error - " + task.getException());
                FancyToast.makeText(getContext(), "Something is weird. Your profile is missing your name", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
                navController.navigate(R.id.welcomeFragment);
            }
        });
    }

    private void onSignUpFailed(Exception exception) {
        Log.d(TAG, "onSignUpFailed: " + exception);
        if (exception instanceof FirebaseAuthException) {
            if (exception instanceof FirebaseAuthEmailException) {
                binding.emailInputlayout.setError("Invalid Email");
            } else if (exception instanceof FirebaseAuthWebException) {
                FancyToast.makeText(getContext(), "No internet", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                if (exception instanceof FirebaseAuthWeakPasswordException) {
                    binding.passwordInputlayout.setError("Password too weak");
                } else
                    FancyToast.makeText(getContext(), "Invalid Email",
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthInvalidUserException) {
                FancyToast.makeText(getContext(), "Your account may be disabled. Please contact support",
                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                FancyToast.makeText(getContext(), "Account Already Exists", FancyToast.LENGTH_LONG,
                        FancyToast.WARNING, false).show();
            } else
                FancyToast.makeText(getContext(), "Something went wrong creating your account", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
        } else if (exception instanceof FirebaseNetworkException) {
            FancyToast.makeText(getContext(), "No Internet", FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false).show();
        } else {
            FancyToast.makeText(getContext(), "Something went wrong signing you up", FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false).show();
        }
    }
}
