package com.example.llmexample.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.llmexample.models.User;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUser(String username);
}
