package com.diploma.aerodent.ui.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.diploma.aerodent.data.local.entity.User;
import com.diploma.aerodent.data.local.model.DentalSpecialty;
import com.diploma.aerodent.data.local.model.UserRole;
import com.diploma.aerodent.R;
import com.diploma.aerodent.data.repository.UserRepository;
import com.diploma.aerodent.util.SessionManager;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    public enum AuthState {
        LOADING,
        NEEDS_SETUP,
        NEEDS_LOGIN,
        LOGGED_IN
    }

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final MutableLiveData<Boolean> actionComplete = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();
    private final ExecutorService executorService;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        sessionManager = new SessionManager(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    public LiveData<Boolean> getActionComplete() { return actionComplete; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getLoginSuccess() { return loginSuccess; }
    public LiveData<AuthState> getAuthState() { return authState; }

    public void checkAuthStatus() {
        executorService.execute(() -> {
            int userCount = userRepository.getUserCount();
            if (userCount == 0) {
                authState.postValue(AuthState.NEEDS_SETUP);
            } else if (!sessionManager.isLoggedIn()) {
                authState.postValue(AuthState.NEEDS_LOGIN);
            } else {
                authState.postValue(AuthState.LOGGED_IN);
            }
        });
    }

    public void verifyPin(User user, String enteredPin) {
        if (user.getPin() != null && user.getPin().equals(enteredPin)) {
            sessionManager.loginUser(user.getId(), user.getRole().name(), user.getFullName());
            loginSuccess.setValue(true);
        } else {
            errorMessage.setValue(getApplication().getString(R.string.error_invalid_pin));
            loginSuccess.setValue(false);
        }
    }

    public void createInitialAdmin(String fullName, String pin, String pinConfirm, String uin, DentalSpecialty specialty, String rzi) {
        if (fullName.isEmpty() || pin.isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_required_fields));
            return;
        }

        if (!pin.equals(pinConfirm)) {
            errorMessage.setValue(getApplication().getString(R.string.setup_admin_error_pin_match));
            return;
        }

        if (pin.length() < 4) {
            errorMessage.setValue(getApplication().getString(R.string.error_pin_min_length));
            return;
        }

        if (rzi.isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_rzi_required));
            return;
        }

        executorService.execute(() -> {
            try {
                if (userRepository.getUserCount() > 0) {
                    errorMessage.postValue(getApplication().getString(R.string.error_admin_exists));
                    return;
                }

                User user = new User();
                String newUserId = UUID.randomUUID().toString();
                user.setId(newUserId);
                user.setFullName(fullName);
                user.setRole(UserRole.ADMIN);
                user.setPin(pin);
                user.setUin(uin);
                user.setSpecialty(specialty);

                userRepository.insertSync(user);
                
                sessionManager.saveRziCode(rzi);
                sessionManager.loginUser(newUserId, UserRole.ADMIN.name(), fullName);

                actionComplete.postValue(true);
            } catch (Exception e) {
                errorMessage.postValue(getApplication().getString(R.string.error_save_failed));
            }
        });
    }
}
