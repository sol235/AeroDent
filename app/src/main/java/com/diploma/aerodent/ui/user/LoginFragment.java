package com.diploma.aerodent.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.ui.home.HomeFragment;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.diploma.aerodent.data.local.entity.User;

public class LoginFragment extends Fragment {

    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        RecyclerView recyclerUsers = view.findViewById(R.id.recycler_users);
        UserCardAdapter adapter = new UserCardAdapter(this::showPinDialog);
        recyclerUsers.setAdapter(adapter);

        userViewModel.getActiveUsers().observe(getViewLifecycleOwner(), adapter::setUsers);

        authViewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                navigateToHome();
            }
        });

        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPinDialog(User user) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pin_input, null);
        TextInputEditText pinEditText = dialogView.findViewById(R.id.pinEditText);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.Theme_AeroDent_AlertDialog)
                .setTitle(getString(R.string.login_for_user, user.getFullName())).setView(dialogView)
                .setPositiveButton(R.string.login_button, (d, which) -> attemptLogin(user, pinEditText))
                .setNegativeButton(R.string.cancel, null).create();

        pinEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin(user, pinEditText);
                dialog.dismiss();
                return true;
            }
            return false;
        });

        dialog.setOnShowListener(d -> {
            pinEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(pinEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        dialog.show();
    }

    private void attemptLogin(User user, TextInputEditText pinEditText) {
        String pin = String.valueOf(pinEditText.getText());
        authViewModel.verifyPin(user, pin);
    }

    private void navigateToHome() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new HomeFragment()).commit();

        View bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }
}
