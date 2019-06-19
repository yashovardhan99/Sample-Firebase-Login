package com.yashovardhan99.firebaselogin;

import android.os.Bundle;
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

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.yashovardhan99.firebaselogin.databinding.FragmentWelcomeBinding;

/**
 * Created by Yashovardhan99 on 28/4/19 as a part of Sample-Firebase-Login.
 */
public class WelcomeFragment extends Fragment implements ChangePasswordDialogFragment.ChangePasswordListener {
    private final String TAG = "WelcomeFragment"
    private FragmentWelcomeBinding binding;
    private NavController navController;
    private FirebaseAuth mAuth;

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
        }
        binding.setUser(new User(name, email));
        return binding.getRoot();
    }

    private void deleteUser() {
    }

    private void changePassword() {
        DialogFragment dialogFragment = new ChangePasswordDialogFragment();
        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, 0);
        dialogFragment.show(getFragmentManager(), "dialog");
    }

    private void signOut() {
        if (mAuth.getCurrentUser().isAnonymous()) {
            mAuth.getCurrentUser().delete();
        } else
            mAuth.signOut();
        navController.navigate(R.id.action_welcomeFragment_to_preLoginFragment);
    }

    @Override
    public void onChangePassword(String current, String newPassword) {
        if (mAuth.getCurrentUser().isAnonymous()) {
            FancyToast.makeText(getContext(), "You are not signed in with a proper account", FancyToast.LENGTH_LONG,
                    FancyToast.ERROR, false).show();
            return;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), current);
        mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

            }
        });
    }
}
