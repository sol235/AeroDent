package com.diploma.aerodent.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diploma.aerodent.data.local.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> getUserById(String id);

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserByIdSync(String id);

    @Query("SELECT * FROM users ORDER BY fullName ASC")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();
    
    @Query("SELECT COUNT(*) FROM users")
    LiveData<Integer> getUserCountLiveData();
}
