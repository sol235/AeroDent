package com.diploma.aerodent.ui.calendar;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.diploma.aerodent.R;
import com.diploma.aerodent.ui.appointments.AddAppointmentFragment;
import com.diploma.aerodent.ui.appointments.AppointmentDetailFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CalendarViewModel viewModel;
    private CalendarAppointmentAdapter appointmentAdapter;
    private CalendarView calendarView;
    private TextView textSelectedDateHeader;
    
    private final SimpleDateFormat selectedDateFormat = new SimpleDateFormat("EEE, d MMM", new Locale("bg", "BG"));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(CalendarViewModel.class);

        setupViews(root);
        setupObservers();

        return root;
    }

    private void setupViews(View root) {
        calendarView = root.findViewById(R.id.calendarView);
        textSelectedDateHeader = root.findViewById(R.id.text_selected_date_header);

        root.findViewById(R.id.btn_add_appointment).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new AddAppointmentFragment())
                    .addToBackStack(null)
                    .commit();
        });

        calendarView.setOnCalendarDayClickListener(calendarDay -> {
            viewModel.setSelectedDate(calendarDay.getCalendar());
        });

        calendarView.setOnForwardPageChangeListener(() -> viewModel.nextMonth());
        calendarView.setOnPreviousPageChangeListener(() -> viewModel.previousMonth());

        RecyclerView recyclerAppointments = root.findViewById(R.id.recycler_appointments);
        recyclerAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentAdapter = new CalendarAppointmentAdapter();
        appointmentAdapter.setOnAppointmentClickListener(appointment -> {
            AppointmentDetailFragment detailFragment = AppointmentDetailFragment.newInstance(appointment.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerAppointments.setAdapter(appointmentAdapter);
    }

    private void setupObservers() {
        viewModel.getAppointmentsForSelectedDate().observe(getViewLifecycleOwner(), appointments -> {
            appointmentAdapter.setAppointments(appointments);
            Calendar selectedDate = viewModel.getSelectedDate().getValue();
            if (selectedDate != null) {
                updateSelectedDateHeader(selectedDate, appointments.size());
            }
        });

        viewModel.getAppointmentDates().observe(getViewLifecycleOwner(), dates -> {
            List<CalendarDay> calendarDays = new ArrayList<>();
            for (Calendar cal : dates) {
                CalendarDay calendarDay = new CalendarDay(cal);
                calendarDay.setImageResource(R.drawable.dot_circle);
                calendarDays.add(calendarDay);
            }
            calendarView.setCalendarDays(calendarDays);
        });

        viewModel.getAllPatients().observe(getViewLifecycleOwner(), patients -> {
            appointmentAdapter.setPatients(patients);
        });
    }

    private void updateSelectedDateHeader(Calendar date, int count) {
        String dateStr = selectedDateFormat.format(date.getTime()).toUpperCase();
        String appointmentStr = getString(R.string.calendar_appointments_count, count);
        textSelectedDateHeader.setText(dateStr + " - " + appointmentStr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        calendarView = null;
        textSelectedDateHeader = null;
    }
}
