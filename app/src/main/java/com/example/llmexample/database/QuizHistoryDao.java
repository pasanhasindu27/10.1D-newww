package com.example.llmexample.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.llmexample.models.QuizHistory;

import java.util.List;

@Dao
public interface QuizHistoryDao {
    @Insert
    void insert(QuizHistory quizHistory);
    
    @Query("SELECT * FROM quiz_history WHERE username = :username ORDER BY date DESC")
    List<QuizHistory> getUserHistory(String username);
    
    @Query("SELECT COUNT(*) FROM quiz_history WHERE username = :username")
    int getCompletedQuizCount(String username);
    
    @Query("SELECT SUM(score) FROM quiz_history WHERE username = :username")
    int getTotalScore(String username);
    
    @Query("SELECT SUM(total_questions) FROM quiz_history WHERE username = :username")
    int getTotalQuestions(String username);
}