package com.diploma.aerodent.ui.user;

import com.diploma.aerodent.AeroDentApplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import com.diploma.aerodent.R;
import com.diploma.aerodent.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserManagementFragment extends Fragment {

    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        userViewModel = new ViewModelProvider(requireActivity(), app.getViewModelFactory()).get(UserViewModel.class);

        RecyclerView recyclerUsers = view.findViewById(R.id.recycler_users);
        UserCardAdapter adapter = new UserCardAdapter(user -> openUserForm(user.getId()));
        recyclerUsers.setAdapter(adapter);

        userViewModel.getVisibleUsers().observe(getViewLifecycleOwner(), adapter::setUsers);

        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && !userViewModel.canManageUsers(user)) {
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
        });

        view.findViewById(R.id.fab_add_user).setOnClickListener(v -> openUserForm(null));
    }

    private void openUserForm(@Nullable String userId) {
        Bundle args = new Bundle();
        args.putBoolean("is_edit_mode", userId != null);
        if (userId != null) {
            args.putString("user_id", userId);
        }
        UserFormFragment fragment = new UserFormFragment();
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
