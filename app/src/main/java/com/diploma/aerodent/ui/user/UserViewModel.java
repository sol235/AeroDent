package com.diploma.aerodent.ui.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;

import com.diploma.aerodent.data.local.entity.User;
import com.diploma.aerodent.data.local.model.DentalSpecialty;
import com.diploma.aerodent.data.local.model.UserRole;
import com.diploma.aerodent.R;
import com.diploma.aerodent.data.repository.UserRepository;
import com.diploma.aerodent.util.SessionManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    private final LiveData<List<User>> allUsers;
    private final LiveData<List<User>> activeUsers;
    private final MediatorLiveData<List<User>> visibleUsers = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> actionComplete = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application, UserRepository userRepository) {
        super(application);
        this.userRepository = userRepository;
        sessionManager = new SessionManager(application);
        allUsers = userRepository.getAllUsers();
        activeUsers = userRepository.getActiveUsers();
        
        visibleUsers.addSource(allUsers, users -> updateVisibleUsers());
        visibleUsers.addSource(currentUser, user -> updateVisibleUsers());
        
        loadCurrentUser();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<List<User>> getAllUsers() { return allUsers; }
    public LiveData<List<User>> getActiveUsers() { return activeUsers; }
    
    public LiveData<List<User>> getVisibleUsers() { return visibleUsers; }
    
    private void updateVisibleUsers() {
        List<User> users = allUsers.getValue();
        User user = currentUser.getValue();
        
        if (users == null) return;
        
        if (user != null && user.getRole() == UserRole.DENTIST) {
            java.util.List<User> filtered = new java.util.ArrayList<>();
            for (User u : users) {
                if (u.getRole() == UserRole.ASSISTANT) {
                    filtered.add(u);
                }
            }
            visibleUsers.setValue(filtered);
        } else {
            visibleUsers.setValue(users);
        }
    }
    public LiveData<Boolean> getActionComplete() { return actionComplete; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    
    public void resetActionComplete() {
        actionComplete.setValue(false);
    }
    
    public void resetErrorMessage() {
        errorMessage.setValue(null);
    }

    public LiveData<User> getUserById(String id) {
        return userRepository.getUserById(id);
    }

    public void loadCurrentUser() {
        String userId = sessionManager.getLoggedInUserId();
        
        if (userId != null) {
            executorService.execute(() -> {
                User user = userRepository.getUserByIdSync(userId);
                currentUser.postValue(user);
            });
        }
    }

    public String getSessionRziCode() {
        return sessionManager.getRziCode();
    }

    public boolean canEditPin(boolean isEditMode, String editUserId) {
        boolean isSelfEdit = isEditMode && editUserId != null && editUserId.equals(sessionManager.getLoggedInUserId());
        return !(isEditMode && !isSelfEdit && "DENTIST".equals(sessionManager.getLoggedInUserRole()));
    }

    public boolean canEditRole(boolean isEditMode, String editUserId) {
        return !(isEditMode && editUserId != null && editUserId.equals(sessionManager.getLoggedInUserId()));
    }

    public boolean isDentist() {
        return "DENTIST".equals(sessionManager.getLoggedInUserRole());
    }

    public boolean canManageUsers(@NonNull User user) {
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.DENTIST;
    }

    public boolean canViewAdvancedSettings(@NonNull User user) {
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.DENTIST;
    }

    private boolean validateUserInput(String fullName, UserRole role, String pin, String pinConfirm, String uin, DentalSpecialty specialty, String rzi) {
        if (fullName.isEmpty() || pin.isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_required_fields));
            return false;
        }

        if (!pin.equals(pinConfirm)) {
            errorMessage.setValue(getApplication().getString(R.string.setup_admin_error_pin_match));
            return false;
        }

        if (pin.length() < 4) {
            errorMessage.setValue(getApplication().getString(R.string.error_pin_min_length));
            return false;
        }

        if (role == UserRole.DENTIST && (uin.isEmpty() || specialty == null)) {
            errorMessage.setValue(getApplication().getString(R.string.error_dentist_uin_specialty));
            return false;
        }

        if (role == UserRole.ADMIN && rzi.isEmpty()) {
            errorMessage.setValue(getApplication().getString(R.string.error_rzi_required));
            return false;
        }
        return true;
    }

    public void saveUser(String userId, String fullName, UserRole role, String pin, String pinConfirm, String uin, DentalSpecialty specialty, String rzi, boolean isActive) {
        if (!validateUserInput(fullName, role, pin, pinConfirm, uin, specialty, rzi)) {
            return;
        }

        executorService.execute(() -> {
            try {
                boolean isNew = (userId == null);
                User user;
                if (isNew) {
                    user = new User();
                    user.setId(UUID.randomUUID().toString());
                } else {
                    user = userRepository.getUserByIdSync(userId);
                    if (user == null) {
                        errorMessage.postValue(getApplication().getString(R.string.error_user_not_found));
                        return;
                    }
                }

                user.setFullName(fullName);
                user.setRole(role);
                user.setPin(pin);
                user.setActive(isActive);
                
                if (role == UserRole.DENTIST || role == UserRole.ADMIN) {
                    user.setUin(uin);
                    user.setSpecialty(specialty);
                } else {
                    user.setUin(null);
                    user.setSpecialty(null);
                }

                if (isNew) {
                    userRepository.insert(user);
                } else {
                    userRepository.update(user);
                    if (role == UserRole.ADMIN) {
                        sessionManager.saveRziCode(rzi);
                    }
                    if (userId.equals(sessionManager.getLoggedInUserId())) {
                        loadCurrentUser();
                    }
                }

                actionComplete.postValue(true);
            } catch (Exception e) {
                errorMessage.postValue(getApplication().getString(R.string.error_save_failed));
            }
        });
    }
    public void logout() {
        sessionManager.logoutUser();
        currentUser.postValue(null);
    }
}
