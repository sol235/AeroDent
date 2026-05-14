package com.diploma.aerodent.ui.calendar;

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
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.ui.appointments.AddAppointmentFragment;

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
    
    private SimpleDateFormat selectedDateFormat = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

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
        recyclerAppointments.setAdapter(appointmentAdapter);
    }

    private void setupObservers() {
        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            try {
                calendarView.setDate(date);
            } catch (Exception e) {
                android.util.Log.e("CalendarFragment", "Failed to set date", e);
            }
        });

        viewModel.getAppointmentsForSelectedDate().observe(getViewLifecycleOwner(), appointments -> {
            appointmentAdapter.setAppointments(appointments);
            Calendar selectedDate = viewModel.getSelectedDate().getValue();
            if (selectedDate != null) {
                updateSelectedDateHeader(selectedDate, appointments.size());
            }
        });

        viewModel.getAppointmentsForCurrentMonth().observe(getViewLifecycleOwner(), appointments -> {
            List<CalendarDay> calendarDays = new ArrayList<>();
            List<String> addedDays = new ArrayList<>(); //  prevent duplicate icons

            for (Appointment appt : appointments) {
                if (appt.getDateTime() == null) continue;
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(appt.getDateTime());
                
                // Normalize time to 00:00:00
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                
                String dayKey = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
                if (!addedDays.contains(dayKey)) {
                    CalendarDay calendarDay = new CalendarDay((Calendar) cal.clone());
                    calendarDay.setImageResource(R.drawable.dot_circle);
                    calendarDays.add(calendarDay);
                    addedDays.add(dayKey);
                }
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
}
