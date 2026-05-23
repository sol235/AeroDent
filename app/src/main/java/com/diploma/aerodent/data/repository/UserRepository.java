package com.diploma.aerodent.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.UserDao;
import com.diploma.aerodent.data.local.entity.User;

import java.util.List;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public LiveData<List<User>> getActiveUsers() {
        return userDao.getActiveUsers();
    }

    public LiveData<User> getUserById(String id) {
        return userDao.getUserById(id);
    }
    
    public User getUserByIdSync(String id) {
        return userDao.getUserByIdSync(id);
    }

    public LiveData<Integer> getUserCountLiveData() {
        return userDao.getUserCountLiveData();
    }
    
    public int getUserCount() {
        return userDao.getUserCount();
    }

    public void insert(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insert(user));
    }

    public void insertSync(User user) {
        userDao.insert(user);
    }

    public void update(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.delete(user));
    }
}
