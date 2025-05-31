package com.example.llmexample.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "topics")
public class Topic {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public boolean selected;

    // Required no-arg constructor for Room
    public Topic() {
    }

    // Constructor for manual instantiation
    public Topic(String name) {
        this.name = name;
        this.selected = false; // Default to unselected
    }
}
