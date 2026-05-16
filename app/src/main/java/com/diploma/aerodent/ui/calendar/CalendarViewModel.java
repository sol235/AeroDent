package com.diploma.aerodent.ui.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.repository.AppointmentRepository;
import com.diploma.aerodent.data.repository.PatientRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarViewModel extends AndroidViewModel {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    private final MutableLiveData<Calendar> currentMonth = new MutableLiveData<>();
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        appointmentRepository = new AppointmentRepository(application);
        patientRepository = new PatientRepository(application);

        Calendar now = Calendar.getInstance();
        currentMonth.setValue((Calendar) now.clone());
        selectedDate.setValue((Calendar) now.clone());
    }

    public LiveData<Calendar> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Calendar date) {
        selectedDate.setValue(date);
    }

    public void nextMonth() {
        Calendar cal = currentMonth.getValue();
        if (cal != null) {
            Calendar next = (Calendar) cal.clone();
            next.add(Calendar.MONTH, 1);
            currentMonth.setValue(next);
        }
    }

    public void previousMonth() {
        Calendar cal = currentMonth.getValue();
        if (cal != null) {
            Calendar prev = (Calendar) cal.clone();
            prev.add(Calendar.MONTH, -1);
            currentMonth.setValue(prev);
        }
    }

    public LiveData<List<Appointment>> getAppointmentsForSelectedDate() {
        return Transformations.switchMap(selectedDate, date -> {
            Calendar start = (Calendar) date.clone();
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            Calendar end = (Calendar) date.clone();
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            end.set(Calendar.MILLISECOND, 999);

            return appointmentRepository.getAppointmentsBetweenDates(start.getTime(), end.getTime());
        });
    }

    public LiveData<List<Calendar>> getAppointmentDates() {
        return Transformations.map(appointmentRepository.getAllAppointments(), appointments -> {
            Set<String> seenDates = new HashSet<>();
            List<Calendar> appointmentDates = new ArrayList<>();
            for (Appointment appt : appointments) {
                if (appt.getDateTime() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(appt.getDateTime());
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    
                    String key = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
                    if (seenDates.add(key)) {
                        appointmentDates.add(cal);
                    }
                }
            }
            return appointmentDates;
        });
    }

    public LiveData<List<Appointment>> getAllAppointments() {
        return appointmentRepository.getAllAppointments();
    }

    public LiveData<List<Patient>> getAllPatients() {
        return patientRepository.getAllPatients();
    }
}
