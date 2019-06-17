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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.yashovardhan99.firebaselogin.databinding.FragmentResetpasswordBinding;

/**
 * Created by Yashovardhan99 on 17/6/19 as a part of Sample-Firebase-Login.
 */
public class ResetPasswordFragment extends Fragment {
    private final String TAG = "ResetPasswordFragment";
    private FragmentResetpasswordBinding binding;
    private CircularProgressDrawable drawable;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_resetpassword, container, false);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host);

        binding.sendPasswordButton.setOnClickListener(v -> {
            if (binding.emailEdittext.getText() != null) {
                showProgress();
                binding.emailInputlayout.setError(null);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.sendPasswordResetEmail(binding.emailEdittext.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        emailSent();
                    else
                        errorSendingEmail(task.getException());
                });
            } else
                binding.emailInputlayout.setError("Email cannot be blank");
        });
        return binding.getRoot();
    }

    private void errorSendingEmail(Exception exception) {
        hideProgress();
        Log.d(TAG, "onLoginFailed: " + exception);
        if (exception instanceof FirebaseAuthException) {
            if (exception instanceof FirebaseAuthEmailException) {
                FancyToast.makeText(getContext(), "Can't send email. Please check your internet", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthWebException) {
                FancyToast.makeText(getContext(), "No internet", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                FancyToast.makeText(getContext(), "Invalid Email address",
                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            } else if (exception instanceof FirebaseAuthInvalidUserException) {
                if (((FirebaseAuthInvalidUserException) exception).getErrorCode().equals("ERROR_USER_DISABLED")) {
                    FancyToast.makeText(getContext(), "Your account may be disabled. Please contact support",
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                } else {
                    FancyToast.makeText(getContext(), "Account does not exist", FancyToast.LENGTH_LONG,
                            FancyToast.ERROR, false).show();
                }
            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                FancyToast.makeText(getContext(), "There is some issue with your account. Please contact support", FancyToast.LENGTH_LONG,
                        FancyToast.CONFUSING, false).show();
            } else
                FancyToast.makeText(getContext(), "Something went wrong sending the email", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
        } else if (exception instanceof FirebaseNetworkException) {
            FancyToast.makeText(getContext(), "No Internet", FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false).show();
        } else {
            FancyToast.makeText(getContext(), "Something went wrong sending the email", FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false).show();
        }
    }

    private void emailSent() {
        FancyToast.makeText(getContext(), "Email sent. Please check your email",
                FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
        navController.navigateUp();
    }

    private void showProgress() {
        drawable = new CircularProgressDrawable(getContext());
        drawable.setStyle(CircularProgressDrawable.LARGE);
        drawable.setColorSchemeColors(Color.WHITE);
        int size = (int) (drawable.getCenterRadius() + drawable.getStrokeWidth()) * 2;
        drawable.setBounds(0, 0, size, size);
        binding.sendPasswordButton.setText("Sending");
        binding.sendPasswordButton.setCompoundDrawables(null, null, drawable, null);
        drawable.start();

        Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                binding.sendPasswordButton.invalidate();
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
        binding.sendPasswordButton.setText(R.string.send_reset_link);
        binding.sendPasswordButton.setCompoundDrawables(null, null, null, null);
    }


}
