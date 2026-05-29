package com.diploma.aerodent.ui.settings;

import com.diploma.aerodent.AeroDentApplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.diploma.aerodent.R;
import com.diploma.aerodent.ui.user.UserViewModel;
import com.diploma.aerodent.ui.user.UserManagementFragment;
import com.diploma.aerodent.ui.user.UserFormFragment;
import com.diploma.aerodent.ui.user.LoginFragment;
import com.diploma.aerodent.ui.payment.PaymentDetailsFragment;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View cardUserManagement = view.findViewById(R.id.card_user_management);
        View cardUserData = view.findViewById(R.id.card_user_data);
        View cardPayments = view.findViewById(R.id.card_payments);
        View cardReports = view.findViewById(R.id.card_reports);

        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        UserViewModel userViewModel = new androidx.lifecycle.ViewModelProvider(requireActivity(),
                app.getViewModelFactory()).get(UserViewModel.class);
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                android.widget.TextView textUserName = view.findViewById(R.id.text_user_name);
                android.widget.TextView textUserRole = view.findViewById(R.id.text_user_role);
                if (textUserName != null)
                    textUserName.setText(user.getFullName());
                if (textUserRole != null)
                    textUserRole.setText(getString(user.getRole().getDisplayName()));

                // Role Based Access Control with ViewModel
                boolean canManage = userViewModel.canManageUsers(user);
                boolean canViewAdvanced = userViewModel.canViewAdvancedSettings(user);

                if (cardUserManagement != null)
                    cardUserManagement.setVisibility(canManage ? View.VISIBLE : View.GONE);
                cardUserData.setVisibility(View.VISIBLE);
                cardPayments.setVisibility(View.VISIBLE);
                cardReports.setVisibility(canViewAdvanced ? View.VISIBLE : View.GONE);

                cardUserData.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putBoolean("is_edit_mode", true);
                    args.putString("user_id", user.getId());

                    UserFormFragment fragment = new UserFormFragment();
                    fragment.setArguments(args);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
                });
            }
        });

        if (cardUserManagement != null) {
            cardUserManagement.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new UserManagementFragment()).addToBackStack(null).commit();
            });
        }

        cardPayments.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new PaymentDetailsFragment()).addToBackStack(null).commit();
        });

        cardReports.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new ExportFragment()).addToBackStack(null).commit();
        });

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            userViewModel.logout();
            requireActivity().getSupportFragmentManager().popBackStack(null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, LoginFragment.newInstance(false)).commit();
        });
    }
}
