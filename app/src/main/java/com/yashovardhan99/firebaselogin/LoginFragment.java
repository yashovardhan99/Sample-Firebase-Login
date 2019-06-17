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

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.yashovardhan99.firebaselogin.databinding.FragmentLoginBinding;

/**
 * Created by Yashovardhan99 on 28/4/19 as a part of Sample-Firebase-Login.
 */
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private final String TAG = "LoginFragment";
    private CircularProgressDrawable drawable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        binding.loginButton.setOnClickListener(this::login);
        return binding.getRoot();
    }

    private void login(View view) {
        showProgress();
        String email = binding.emailEdittext.getText().toString();
        String password = binding.passwordEdittext.getText().toString();
        if (!validate(email, password)) {
            hideProgress();
            return;
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onLogin(task.getResult());
            } else {
                onLoginFailed(task.getException());
                hideProgress();
            }
        });
    }

    private void showProgress() {
        drawable = new CircularProgressDrawable(getContext());
        drawable.setStyle(CircularProgressDrawable.LARGE);
        drawable.setColorSchemeColors(Color.WHITE);
        int size = (int) (drawable.getCenterRadius() + drawable.getStrokeWidth()) * 2;
        drawable.setBounds(0, 0, size, size);
        binding.loginButton.setText("Logging In");
        binding.loginButton.setCompoundDrawables(null, null, drawable, null);
        drawable.start();

        Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                binding.loginButton.invalidate();
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
        binding.loginButton.setText(R.string.login);
        binding.loginButton.setCompoundDrawables(null, null, null, null);
    }

    private boolean validate(String email, String password) {
        if (email != null && email.length() >= 8 && email.contains("@")) {
            binding.emailInputlayout.setError(null);
            if (password != null && password.length() >= 8) {
                binding.passwordInputlayout.setError(null);
                return true;
            } else {
                binding.passwordInputlayout.setError("Password too short");
            }
        } else
            binding.emailInputlayout.setError("Invalid Email");
        return false;
    }

    private void onLogin(AuthResult result) {
        FirebaseUser user = result.getUser();
        Log.d(TAG, "onLogin: Successfully logged in");
        FancyToast.makeText(getContext(), "Logged In!", FancyToast.LENGTH_LONG,
                FancyToast.SUCCESS, false).show();
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
        navController.navigate(
                LoginFragmentDirections.actionLoginFragmentToWelcomeFragment());
    }

    private void onLoginFailed(Exception exception) {
        Log.d(TAG, "onLoginFailed: " + exception);
        if (exception instanceof FirebaseAuthException) {
            if (exception instanceof FirebaseAuthEmailException) {
                FancyToast.makeText(getContext(), "Invalid Email or Password", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthWebException) {
                FancyToast.makeText(getContext(), "No internet", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                FancyToast.makeText(getContext(), "Invalid Email or Password",
                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthInvalidUserException) {
                if (((FirebaseAuthInvalidUserException) exception).getErrorCode().equals("ERROR_USER_DISABLED")) {
                    FancyToast.makeText(getContext(), "Your account may be disabled. Please contact support",
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                } else {
                    FancyToast.makeText(getContext(), "Invalid Email or Password", FancyToast.LENGTH_LONG,
                            FancyToast.ERROR, false).show();
                }
            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                FancyToast.makeText(getContext(), "There is some issue with your account. Please contact support", FancyToast.LENGTH_LONG,
                        FancyToast.CONFUSING, false).show();
            } else
                FancyToast.makeText(getContext(), "Something went wrong creating your account", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
        } else {
            FancyToast.makeText(getContext(), "Something went wrong signing you up", FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false).show();
        }
    }


}
