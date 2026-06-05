package com.diploma.aerodent.ui.home;

import com.diploma.aerodent.AeroDentApplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.ui.appointments.AppointmentDetailFragment;
import com.diploma.aerodent.ui.patients.PatientsFragment;
import com.diploma.aerodent.ui.calendar.CalendarFragment;
import com.diploma.aerodent.ui.payment.PaymentViewModel;
import com.diploma.aerodent.ui.payment.PaymentDetailsFragment;
import com.diploma.aerodent.ui.user.UserViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private PaymentViewModel paymentViewModel;

    private TextView textTotalPatients, textTodaysAppts, textUnpaidAccounts, textAppointmentsLeft;
    private RecyclerView recyclerSchedule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ViewModel
        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        homeViewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(HomeViewModel.class);
        paymentViewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(PaymentViewModel.class);

        // Bind UI Elements
        textTotalPatients = root.findViewById(R.id.text_total_patients);
        textTodaysAppts = root.findViewById(R.id.text_todays_appointments);
        textUnpaidAccounts = root.findViewById(R.id.text_unpaid_accounts_count);
        textAppointmentsLeft = root.findViewById(R.id.text_appointments_left);
        recyclerSchedule = root.findViewById(R.id.recycler_home_schedule);

        UserViewModel userViewModel = new ViewModelProvider(requireActivity(), app.getViewModelFactory()).get(UserViewModel.class);
        userViewModel.loadCurrentUser();
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                TextView textUserName = root.findViewById(R.id.text_user_name);
                TextView textUserRole = root.findViewById(R.id.text_user_role);
                if (textUserName != null) textUserName.setText(user.getFullName());
                if (textUserRole != null) textUserRole.setText(getString(user.getRole().getDisplayName()));
            }
        });

        // Observe Data
        homeViewModel.getTotalPatientsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) textTotalPatients.setText(String.valueOf(count));
        });

        homeViewModel.getTodaysAppointmentsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) textTodaysAppts.setText(String.valueOf(count));
        });

        paymentViewModel.getPendingAppointmentsLiveData().observe(getViewLifecycleOwner(), pendingPayments -> {
            if (pendingPayments != null) {
                int unpaidAccountsCount = paymentViewModel.getUnpaidAccountsCount(pendingPayments);
                textUnpaidAccounts.setText(String.valueOf(unpaidAccountsCount));
            }
        });

        homeViewModel.getAppointmentsLeftCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                textAppointmentsLeft.setText(String.valueOf(count));
            }
        });

        // Schedule RecyclerView Setup
        HomeAppointmentAdapter scheduleAdapter = new HomeAppointmentAdapter();
        scheduleAdapter.setOnAppointmentClickListener(appointment -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, AppointmentDetailFragment.newInstance(appointment.getId()))
                    .addToBackStack(null)
                    .commit();
        });
        recyclerSchedule.setAdapter(scheduleAdapter);

        homeViewModel.getTodaysAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) {
                scheduleAdapter.setAppointments(appointments);
            }
        });

        homeViewModel.getAllPatients().observe(getViewLifecycleOwner(), patients -> {
            if (patients != null) {
                scheduleAdapter.setPatients(patients);
            }
        });

        // Quick Actions
        root.findViewById(R.id.card_total_patients).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new PatientsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        root.findViewById(R.id.card_todays_appointments).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new CalendarFragment())
                    .addToBackStack(null)
                    .commit();
        });

        root.findViewById(R.id.card_unpaid_accounts).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new PaymentDetailsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        root.findViewById(R.id.card_appointments_left).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new CalendarFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textTotalPatients = null;
        textTodaysAppts = null;
        textUnpaidAccounts = null;
        textAppointmentsLeft = null;
        recyclerSchedule = null;
    }
}
