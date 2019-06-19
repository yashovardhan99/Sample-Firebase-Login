package com.yashovardhan99.firebaselogin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.yashovardhan99.firebaselogin.databinding.FragmentdialogChangepasswordBinding;

/**
 * Created by Yashovardhan99 on 19/6/19 as a part of Sample-Firebase-Login.
 */
public class ChangePasswordDialogFragment extends DialogFragment {
    private FragmentdialogChangepasswordBinding binding;
    private ChangePasswordListener listener;

    ChangePasswordDialogFragment(ChangePasswordListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmentdialog_changepassword, container, false);
        binding.changePasswordButton.setOnClickListener(this::sendPasswords);
        binding.cancelAction.setOnClickListener(v -> listener.onDismissed());
        return binding.getRoot();
    }

    private void sendPasswords(View v) {
        binding.newPasswordInputLayout.setError(null);
        binding.currentPasswordInputLayout.setError(null);
        String current = binding.currentPasswordEditText.getText().toString();
        String newPassword = binding.newPasswordEditText.getText().toString();
        if (current.length() == 0) {
            binding.currentPasswordInputLayout.setError("Cannot be empty");
        } else if (newPassword.length() == 0) {
            binding.newPasswordInputLayout.setError("Cannot be empty");
        } else {
            listener.onChangePassword(
                    binding.currentPasswordEditText.getText().toString(),
                    binding.newPasswordEditText.getText().toString());
            dismiss();
        }
    }

    interface ChangePasswordListener {
        void onChangePassword(String current, String newPassword);

        void onDismissed();
    }
}
