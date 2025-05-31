package com.example.llmexample.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_history")
public class QuizHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    @ColumnInfo(name = "username")
    public String username;
    
    @ColumnInfo(name = "topic")
    public String topic;
    
    @ColumnInfo(name = "score")
    public int score;
    
    @ColumnInfo(name = "total_questions")
    public int totalQuestions;
    
    @ColumnInfo(name = "date")
    public long date;
    
    // Constructor
    public QuizHistory(String username, String topic, int score, int totalQuestions) {
        this.username = username;
        this.topic = topic;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.date = System.currentTimeMillis();
    }
}