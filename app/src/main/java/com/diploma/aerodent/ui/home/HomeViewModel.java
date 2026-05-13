package com.diploma.aerodent.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.repository.AppointmentRepository;
import com.diploma.aerodent.data.repository.PatientRepository;
import com.diploma.aerodent.data.repository.PaymentRepository;

public class HomeViewModel extends AndroidViewModel {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;

    private LiveData<Integer> totalPatientsCount;
    private LiveData<Integer> activeTreatmentsCount;
    private LiveData<Integer> appointmentsLeftCount;
    private LiveData<Integer> todaysAppointmentsCount;
    private LiveData<List<Appointment>> todaysAppointments;
    private LiveData<List<Patient>> allPatients;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        patientRepository = new PatientRepository(application);
        appointmentRepository = new AppointmentRepository(application);
        paymentRepository = new PaymentRepository(application);

        totalPatientsCount = patientRepository.getPatientCount();

        // Get todays start and end dates
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        todaysAppointmentsCount = appointmentRepository.getAppointmentCountBetween(startOfDay, endOfDay);

        // Dummy data
        androidx.lifecycle.MutableLiveData<Integer> activeDummy = new androidx.lifecycle.MutableLiveData<>();
        activeDummy.setValue(3);
        activeTreatmentsCount = activeDummy;

        androidx.lifecycle.MutableLiveData<Integer> leftDummy = new androidx.lifecycle.MutableLiveData<>();
        leftDummy.setValue(2);
        appointmentsLeftCount = leftDummy;
        todaysAppointments = appointmentRepository.getAppointmentsBetweenDates(startOfDay, endOfDay);
        allPatients = patientRepository.getAllPatients();
    }

    public LiveData<Integer> getTotalPatientsCount() {
        return totalPatientsCount;
    }

    public LiveData<Integer> getActiveTreatmentsCount() {
        return activeTreatmentsCount;
    }

    public LiveData<Integer> getAppointmentsLeftCount() {
        return appointmentsLeftCount;
    }

    public LiveData<Integer> getTodaysAppointmentsCount() {
        return todaysAppointmentsCount;
    }

    public LiveData<List<Appointment>> getTodaysAppointments() {
        return todaysAppointments;
    }

    public LiveData<List<Patient>> getAllPatients() {
        return allPatients;
    }
}
