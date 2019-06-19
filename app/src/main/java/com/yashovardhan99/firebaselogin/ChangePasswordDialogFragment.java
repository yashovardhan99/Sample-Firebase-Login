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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmentdialog_changepassword, container, false);
        binding.changePasswordButton.setOnClickListener(v -> listener.onChangePassword(
                binding.currentPasswordEditText.getText().toString(),
                binding.newPasswordEditText.getText().toString()));
        binding.cancelAction.setOnClickListener(v -> dismiss());
        return binding.getRoot();
    }

    interface ChangePasswordListener {
        void onChangePassword(String current, String newPassword);
    }
}
