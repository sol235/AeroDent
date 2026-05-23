package com.diploma.aerodent.ui.user;

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

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        RecyclerView recyclerUsers = view.findViewById(R.id.recycler_users);
        UserCardAdapter adapter = new UserCardAdapter(user -> openUserForm(user.getId()));
        recyclerUsers.setAdapter(adapter);

        userViewModel.getVisibleUsers().observe(getViewLifecycleOwner(), adapter::setUsers);

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
