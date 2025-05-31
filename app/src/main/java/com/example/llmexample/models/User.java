package com.example.llmexample.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String username;

    public String email;
    public String passwordHash;
    public String phone;
    public String interests; // Comma-separated list of topics
    public int totalScore;
    public int communityRank;
    public String subscriptionLevel; // "free", "starter", "intermediate", "advanced"
}
