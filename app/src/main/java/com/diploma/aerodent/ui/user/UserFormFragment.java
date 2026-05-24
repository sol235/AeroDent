package com.diploma.aerodent.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.User;
import com.diploma.aerodent.data.local.model.DentalSpecialty;
import com.diploma.aerodent.data.local.model.UserRole;
import com.diploma.aerodent.ui.home.HomeFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

public class UserFormFragment extends Fragment {

    private UserViewModel viewModel;

    private boolean isEditMode = false;
    private String editUserId = null;

    private MaterialToolbar toolbar;
    private TextInputEditText nameEditText;
    private AutoCompleteTextView roleAutoComplete;
    private TextInputLayout roleInputLayout;
    private TextInputLayout pinInputLayout;
    private TextInputEditText pinEditText;
    private TextInputLayout pinConfirmInputLayout;
    private TextInputEditText pinConfirmEditText;
    private TextInputEditText uinEditText;
    private AutoCompleteTextView specialtyAutoComplete;
    private TextInputLayout uinInputLayout;
    private TextInputLayout specialtyInputLayout;
    private View uinHelperTextView;
    private TextInputEditText rziEditText;
    private TextInputLayout rziInputLayout;
    private View rziHelperTextView;
    private com.google.android.material.materialswitch.MaterialSwitch activeSwitch;
    private Button saveButton;

    private UserRole selectedRole = UserRole.DENTIST;
    private DentalSpecialty selectedSpecialty = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_form, container, false);
        
        if (getArguments() != null) {
            isEditMode = getArguments().getBoolean("is_edit_mode", false);
            editUserId = getArguments().getString("user_id");
        }

        toolbar = view.findViewById(R.id.toolbar);
        nameEditText = view.findViewById(R.id.nameEditText);
        roleAutoComplete = view.findViewById(R.id.roleAutoComplete);
        roleInputLayout = view.findViewById(R.id.roleInputLayout);
        pinInputLayout = view.findViewById(R.id.pinInputLayout);
        pinEditText = view.findViewById(R.id.pinEditText);
        pinConfirmInputLayout = view.findViewById(R.id.pinConfirmInputLayout);
        pinConfirmEditText = view.findViewById(R.id.pinConfirmEditText);
        uinEditText = view.findViewById(R.id.uinEditText);
        specialtyAutoComplete = view.findViewById(R.id.specialtyAutoComplete);
        uinInputLayout = view.findViewById(R.id.uinInputLayout);
        specialtyInputLayout = view.findViewById(R.id.specialtyInputLayout);
        uinHelperTextView = view.findViewById(R.id.uinHelperTextView);
        rziEditText = view.findViewById(R.id.rziEditText);
        rziInputLayout = view.findViewById(R.id.rziInputLayout);
        rziHelperTextView = view.findViewById(R.id.rziHelperTextView);
        activeSwitch = view.findViewById(R.id.activeSwitch);
        saveButton = view.findViewById(R.id.saveButton);

        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        setupSpecialtyDropdown();
        setupRoleDropdown();
        setupInitialMode();
        
        viewModel.getActionComplete().observe(getViewLifecycleOwner(), complete -> {
            if (Boolean.TRUE.equals(complete)) {
                viewModel.resetActionComplete();
                Toast.makeText(getContext(), R.string.user_saved_success, Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        
        if (isEditMode && editUserId != null) {
            viewModel.getUserById(editUserId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    populateUserData(user);
                }
            });
        }
        
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.resetErrorMessage();
            }
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), loggedInUser -> {
            if (loggedInUser != null) {
                boolean isSelf = loggedInUser.getId().equals(editUserId);
                if (!isSelf && !viewModel.canManageUsers(loggedInUser)) {
                    requireActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.nav_home);
                    } else {
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, new HomeFragment())
                                .commit();
                    }
                }
            }
        });

        saveButton.setOnClickListener(v -> saveUser());

        return view;
    }

    private void setupInitialMode() {
        toolbar.setTitle(isEditMode ? R.string.user_edit_title : R.string.user_add_title);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        
        rziInputLayout.setVisibility(View.GONE);
        rziHelperTextView.setVisibility(View.GONE);
        saveButton.setText(isEditMode ? R.string.user_save_changes : R.string.user_save_employee);

        if (!viewModel.canEditPin(isEditMode, editUserId)) {
            pinInputLayout.setVisibility(View.GONE);
            pinConfirmInputLayout.setVisibility(View.GONE);
        }

        if (!isEditMode || !viewModel.canEditRole(isEditMode, editUserId)) {
            activeSwitch.setVisibility(View.GONE);
        }
    }

    private void populateUserData(User user) {
        nameEditText.setText(user.getFullName());
        pinEditText.setText(user.getPin());
        pinConfirmEditText.setText(user.getPin());
        activeSwitch.setChecked(user.isActive());

        selectedRole = user.getRole();
        

        if (selectedRole == UserRole.ADMIN) {
            roleInputLayout.setVisibility(View.GONE);
            rziInputLayout.setVisibility(View.VISIBLE);
            rziHelperTextView.setVisibility(View.VISIBLE);
            
            String rzi = viewModel.getSessionRziCode();
            if (rzi != null) {
                rziEditText.setText(rzi);
            }
            toolbar.setTitle(R.string.user_edit_admin_title);
        } else {
            roleAutoComplete.setText(getString(user.getRole().getDisplayName()), false);
            updateUinSpecialtyVisibility();
            toolbar.setTitle(R.string.user_edit_employee_title);
            
            if (!viewModel.canEditRole(isEditMode, editUserId)) {
                roleInputLayout.setVisibility(View.GONE);
            }
        }

        if (user.getUin() != null) {
            uinEditText.setText(user.getUin());
        }

        if (user.getSpecialty() != null) {
            selectedSpecialty = user.getSpecialty();
            specialtyAutoComplete.setText(getString(user.getSpecialty().getDisplayName()), false);
        }
    }

    private void setupRoleDropdown() {
        List<UserRole> roles;
        if (viewModel.isDentist()) {
            roles = Arrays.asList(UserRole.ASSISTANT);
        } else {
            roles = Arrays.asList(UserRole.DENTIST, UserRole.ASSISTANT);
        }
        
        String[] roleNames = new String[roles.size()];
        for (int i = 0; i < roles.size(); i++) {
            roleNames[i] = getString(roles.get(i).getDisplayName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, roleNames);
        roleAutoComplete.setAdapter(adapter);

        roleAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedRole = roles.get(position);
            updateUinSpecialtyVisibility();
        });
        
        roleAutoComplete.setText(getString(roles.get(0).getDisplayName()), false);
        selectedRole = roles.get(0);
        updateUinSpecialtyVisibility();
    }

    private void updateUinSpecialtyVisibility() {
        if (selectedRole == UserRole.ASSISTANT) {
            uinInputLayout.setVisibility(View.GONE);
            specialtyInputLayout.setVisibility(View.GONE);
            uinHelperTextView.setVisibility(View.GONE);
        } else {
            uinInputLayout.setVisibility(View.VISIBLE);
            specialtyInputLayout.setVisibility(View.VISIBLE);
            uinHelperTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setupSpecialtyDropdown() {
        DentalSpecialty[] specialties = DentalSpecialty.values();
        String[] specialtyNames = new String[specialties.length];
        for (int i = 0; i < specialties.length; i++) {
            specialtyNames[i] = getString(specialties[i].getDisplayName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_custom, specialtyNames);
        specialtyAutoComplete.setAdapter(adapter);

        specialtyAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedSpecialty = specialties[position];
        });
    }

    private void saveUser() {
        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String pin = pinEditText.getText() != null ? pinEditText.getText().toString().trim() : "";
        String pinConfirm = pinConfirmEditText.getText() != null ? pinConfirmEditText.getText().toString().trim() : "";
        String uin = uinEditText.getText() != null ? uinEditText.getText().toString().trim() : "";
        String rzi = rziEditText.getText() != null ? rziEditText.getText().toString().trim() : "";

        if (selectedSpecialty == null && selectedRole != UserRole.ASSISTANT && selectedRole != UserRole.ADMIN) {
            String specialtyName = specialtyAutoComplete.getText() != null ? specialtyAutoComplete.getText().toString().trim() : "";
            for (DentalSpecialty sp : DentalSpecialty.values()) {
                if (getString(sp.getDisplayName()).equals(specialtyName)) {
                    selectedSpecialty = sp;
                    break;
                }
            }
        }

        boolean isActive = isEditMode ? activeSwitch.isChecked() : true;
        viewModel.saveUser(editUserId, name, selectedRole, pin, pinConfirm, uin, selectedSpecialty, rzi, isActive);
    }
}
