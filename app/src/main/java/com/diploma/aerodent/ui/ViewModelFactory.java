package com.diploma.aerodent.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.diploma.aerodent.data.AppContainer;
import com.diploma.aerodent.ui.appointments.AppointmentViewModel;
import com.diploma.aerodent.ui.calendar.CalendarViewModel;
import com.diploma.aerodent.ui.dentalchart.DentalChartViewModel;
import com.diploma.aerodent.ui.home.HomeViewModel;
import com.diploma.aerodent.ui.patients.PatientDetailViewModel;
import com.diploma.aerodent.ui.patients.PatientViewModel;
import com.diploma.aerodent.ui.payment.PaymentViewModel;
import com.diploma.aerodent.ui.photos.PhotoViewModel;
import com.diploma.aerodent.ui.user.AuthViewModel;
import com.diploma.aerodent.ui.user.UserViewModel;
import com.diploma.aerodent.ui.settings.ExportViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final AppContainer appContainer;

    public ViewModelFactory(Application application, AppContainer appContainer) {
        this.application = application;
        this.appContainer = appContainer;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DentalChartViewModel.class)) {
            return (T) new DentalChartViewModel(
                application,
                appContainer.getToothStatusRepository(),
                appContainer.getPatientRepository(),
                appContainer.getProcedureLogRepository()
            );
        }
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(
                application,
                appContainer.getUserRepository()
            );
        }
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(
                application,
                appContainer.getUserRepository()
            );
        }
        if (modelClass.isAssignableFrom(PhotoViewModel.class)) {
            return (T) new PhotoViewModel(
                application,
                appContainer.getPhotoRepository()
            );
        }
        if (modelClass.isAssignableFrom(PaymentViewModel.class)) {
            return (T) new PaymentViewModel(
                application,
                appContainer.getPaymentRepository()
            );
        }
        if (modelClass.isAssignableFrom(PatientViewModel.class)) {
            return (T) new PatientViewModel(
                application,
                appContainer.getPatientRepository()
            );
        }
        if (modelClass.isAssignableFrom(PatientDetailViewModel.class)) {
            return (T) new PatientDetailViewModel(
                application,
                appContainer.getPatientRepository(),
                appContainer.getAppointmentRepository(),
                appContainer.getProcedureLogRepository(),
                appContainer.getPaymentRepository()
            );
        }
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(
                application,
                appContainer.getPatientRepository(),
                appContainer.getAppointmentRepository(),
                appContainer.getPaymentRepository()
            );
        }
        if (modelClass.isAssignableFrom(CalendarViewModel.class)) {
            return (T) new CalendarViewModel(
                application,
                appContainer.getAppointmentRepository(),
                appContainer.getPatientRepository()
            );
        }
        if (modelClass.isAssignableFrom(AppointmentViewModel.class)) {
            return (T) new AppointmentViewModel(
                application,
                appContainer.getAppointmentRepository(),
                appContainer.getPatientRepository(),
                appContainer.getProcedureLogRepository()
            );
        }
        if (modelClass.isAssignableFrom(ExportViewModel.class)) {
            return (T) new ExportViewModel(
                application,
                appContainer.getPatientRepository(),
                appContainer.getAppointmentRepository(),
                appContainer.getPaymentRepository(),
                appContainer.getUserRepository(),
                appContainer.getToothStatusRepository(),
                appContainer.getProcedureLogRepository()
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class " + modelClass.getName());
    }
}
