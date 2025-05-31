package com.example.llmexample.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Add this import

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.llmexample.R;
import com.example.llmexample.adapters.HistoryAdapter;
import com.example.llmexample.database.AppDatabase;
import com.example.llmexample.models.QuizHistory; // Make sure this import is present
import com.example.llmexample.utilities.SharedPreferencesHelper;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    
    private Button viewProfileButton;
    
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
    
        int correctAnswers = getIntent().getIntExtra("CORRECT_ANSWERS", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        String topic = getIntent().getStringExtra("TOPIC");
        int incorrectAnswers = totalQuestions - correctAnswers;
        
        // Calculate percentage
        float percentage = (totalQuestions > 0) ? (correctAnswers * 100.0f / totalQuestions) : 0;
    
        TextView resultsText = findViewById(R.id.resultsText);
        resultsText.setText(String.format("You scored %d/%d (%.1f%%)", correctAnswers, totalQuestions, percentage));
        
        TextView detailedResultsText = findViewById(R.id.detailedResultsText);
        detailedResultsText.setText(String.format("Topic: %s\nTotal Questions: %d\nCorrect Answers: %d\nIncorrect Answers: %d\nPercentage: %.1f%%",
            topic, totalQuestions, correctAnswers, incorrectAnswers, percentage));
        
        viewProfileButton = findViewById(R.id.viewProfileButton);
        
        viewProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    
        // Save quiz history to database
        saveQuizHistory(correctAnswers, totalQuestions, topic);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void saveQuizHistory(int correctAnswers, int totalQuestions, String topic) {
        // Get current username
        String username = SharedPreferencesHelper.getUsername(this);
        
        // Create history entry
        QuizHistory history = new QuizHistory(username, topic, correctAnswers, totalQuestions);
        
        // Save to database
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "learning_app_db")
                .allowMainThreadQueries() // For simplicity, in production use AsyncTask or coroutines
                .fallbackToDestructiveMigration()
                .build();
        
        db.quizHistoryDao().insert(history);
    }
}
