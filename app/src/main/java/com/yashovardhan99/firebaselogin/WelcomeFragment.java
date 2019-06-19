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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.yashovardhan99.firebaselogin.databinding.FragmentWelcomeBinding;

/**
 * Created by Yashovardhan99 on 28/4/19 as a part of Sample-Firebase-Login.
 */
public class WelcomeFragment extends Fragment implements ChangePasswordDialogFragment.ChangePasswordListener {
    private final String TAG = "WelcomeFragment";
    private FragmentWelcomeBinding binding;
    private NavController navController;
    private FirebaseAuth mAuth;
    private CircularProgressDrawable drawable;
    private DialogFragment changePasswordFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        binding.signoutButton.setOnClickListener(v -> signOut());
        binding.changePasswordButton.setOnClickListener(v -> changePassword());
        binding.deleteButton.setOnClickListener(v -> deleteUser());
        mAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(getActivity(), R.id.nav_host);

        if (mAuth.getCurrentUser() == null) {
            signOut();
        }
        String name = mAuth.getCurrentUser().getDisplayName();
        String email = mAuth.getCurrentUser().getEmail();
        if (mAuth.getCurrentUser().isAnonymous()) {
            name = "Anonymous User";
            email = mAuth.getCurrentUser().getUid();
            binding.changePasswordButton.setVisibility(View.INVISIBLE);
            binding.deleteButton.setVisibility(View.INVISIBLE);
        }
        binding.setUser(new User(name, email));
        return binding.getRoot();
    }

    private void deleteUser() {
    }

    private void changePassword() {
        changePasswordFragment = new ChangePasswordDialogFragment(this);
        changePasswordFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
        changePasswordFragment.show(getFragmentManager(), "dialog");
        showChangePasswordProgress();
    }

    private void signOut() {
        showSignOutProgress();
        if (mAuth.getCurrentUser().isAnonymous()) {
            mAuth.getCurrentUser().delete().addOnCompleteListener(this::onDeleteUser);
        } else {
            mAuth.signOut();
            navController.navigate(R.id.action_welcomeFragment_to_preLoginFragment);
        }
    }

    private void onDeleteUser(Task<Void> task) {
        if (task.isSuccessful()) {
            Log.d(TAG, "onDeleteUser: Deleted");
            navController.navigate(R.id.action_welcomeFragment_to_preLoginFragment);
        } else {
            Log.d(TAG, "onDeleteUser: ERROR : " + task.getException());
        }
    }

    @Override
    public void onChangePassword(String current, String newPassword) {
        if (mAuth.getCurrentUser().isAnonymous()) {
            FancyToast.makeText(getContext(), "You are not signed in with a proper account", FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false).show();
            hideChangePasswordProgress();
            return;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), current);
        mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "onChangePassword: Successfully reauthenticated");
                mAuth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(task2 -> onPasswordChangeListener(task2));
            } else {
                hideChangePasswordProgress();
                Log.d(TAG, "onChangePassword: Error : " + task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                    FancyToast.makeText(getContext(), "Your user account may be disabled or deleted. Please contact support",
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    signOut();
                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    FancyToast.makeText(getContext(), "Wrong password!", FancyToast.LENGTH_LONG,
                            FancyToast.ERROR, false).show();
                } else if (task.getException() instanceof FirebaseNetworkException) {
                    FancyToast.makeText(getContext(), "No Internet", FancyToast.LENGTH_LONG,
                            FancyToast.ERROR, false).show();
                } else
                    FancyToast.makeText(getContext(), "Something went wrong. Please try again later.", FancyToast.LENGTH_LONG,
                            FancyToast.ERROR, false).show();
            }
        });
    }

    @Override
    public void onDismissed() {
        hideChangePasswordProgress();
        changePasswordFragment.dismiss();
    }

    private void onPasswordChangeListener(Task<Void> task2) {
        hideChangePasswordProgress();
        if (task2.isSuccessful()) {
            Log.d(TAG, "onPasswordChangeListener: Password changed");
            FancyToast.makeText(getContext(), "Password Successfully changed", FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS, false).show();
        } else {
            Log.d(TAG, "onPasswordChangeListener: Error : " + task2.getException());
            if (task2.getException() instanceof FirebaseNetworkException) {
                FancyToast.makeText(getContext(), "No Internet", FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false).show();
            } else if (task2.getException() instanceof FirebaseAuthWeakPasswordException) {
                FancyToast.makeText(getContext(), "Your password is too small. Please try again", FancyToast.LENGTH_LONG,
                        FancyToast.WARNING, false).show();
            } else if (task2.getException() instanceof FirebaseAuthInvalidUserException) {
                FancyToast.makeText(getContext(), "Your account may be disabled or deleted. Please contact support",
                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                signOut();
            } else if (task2.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                FancyToast.makeText(getContext(), "There was some problem authenticating you. Please try again",
                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            } else
                FancyToast.makeText(getContext(), "Something went wrong. Please try again",
                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        }
    }

    private void showChangePasswordProgress() {
        drawable = new CircularProgressDrawable(getContext());
        drawable.setStyle(CircularProgressDrawable.LARGE);
        drawable.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), 0);
        int size = (int) (drawable.getCenterRadius() + drawable.getStrokeWidth()) * 2;
        drawable.setBounds(0, 0, size, size);
        binding.changePasswordButton.setText("Changing");
        binding.changePasswordButton.setCompoundDrawables(null, null, drawable, null);
        drawable.start();

        Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                binding.changePasswordButton.invalidate();
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

    private void hideChangePasswordProgress() {
        if (drawable != null)
            drawable.stop();
        drawable = null;
        binding.changePasswordButton.setText(R.string.change_password);
        binding.changePasswordButton.setCompoundDrawables(null, null, null, null);
    }

    private void showSignOutProgress() {
        drawable = new CircularProgressDrawable(getContext());
        drawable.setStyle(CircularProgressDrawable.LARGE);
        drawable.setColorSchemeColors(Color.WHITE);
        int size = (int) (drawable.getCenterRadius() + drawable.getStrokeWidth()) * 2;
        drawable.setBounds(0, 0, size, size);
        binding.signoutButton.setText("Signing out");
        binding.signoutButton.setCompoundDrawables(null, null, drawable, null);
        drawable.start();

        Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                binding.signoutButton.invalidate();
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

    private void hideSignOutProgress() {
        if (drawable != null)
            drawable.stop();
        drawable = null;
        binding.signoutButton.setText(R.string.sign_out);
        binding.signoutButton.setCompoundDrawables(null, null, null, null);
    }

    private void showDeleteProgress() {
        drawable = new CircularProgressDrawable(getContext());
        drawable.setStyle(CircularProgressDrawable.LARGE);
        drawable.setColorSchemeColors(Color.WHITE);
        int size = (int) (drawable.getCenterRadius() + drawable.getStrokeWidth()) * 2;
        drawable.setBounds(0, 0, size, size);
        binding.deleteButton.setText("Deleting");
        binding.deleteButton.setCompoundDrawables(null, null, drawable, null);
        drawable.start();

        Drawable.Callback callback = new Drawable.Callback() {
            @Override
            public void invalidateDrawable(@NonNull Drawable who) {
                binding.deleteButton.invalidate();
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

    private void hideDeleteProgress() {
        if (drawable != null)
            drawable.stop();
        drawable = null;
        binding.deleteButton.setText(R.string.delete_account);
        binding.deleteButton.setCompoundDrawables(null, null, null, null);
    }
}
