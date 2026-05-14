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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    public LiveData<List<Appointment>> getAppointmentsForCurrentMonth() {
        return Transformations.switchMap(currentMonth, month -> {
            Calendar start = (Calendar) month.clone();
            start.set(Calendar.DAY_OF_MONTH, 1);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            
            Calendar end = (Calendar) month.clone();
            end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);

            return appointmentRepository.getAppointmentsBetweenDates(start.getTime(), end.getTime());
        });
    }

    public LiveData<List<Patient>> getAllPatients() {
        return patientRepository.getAllPatients();
    }
}
