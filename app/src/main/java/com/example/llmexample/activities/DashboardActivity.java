package com.example.llmexample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.llmexample.R;
import com.example.llmexample.adapters.InterestsAdapter;
import com.example.llmexample.adapters.QuizAdapter;
import com.example.llmexample.models.QuizResponse;
import com.example.llmexample.utilities.SharedPreferencesHelper;
import java.util.Arrays;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private RecyclerView tasksRecyclerView;
    private QuizAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        tasksRecyclerView = findViewById(R.id.rvTasks);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get username from SharedPreferences
        String username = SharedPreferencesHelper.getUsername(this);
        TextView greeting = findViewById(R.id.greetingText);
        greeting.setText("Welcome, " + username);
        
        // Add profile button click listener
        findViewById(R.id.profileButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        // Add history button click listener
        findViewById(R.id.historyButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        // Add upgrade button click listener
        findViewById(R.id.upgradeButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, UpgradeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Load tasks
        QuizResponse tasks = getIntent().getParcelableExtra("TASKS");
        if (tasks != null) {
            displayTasks(tasks);
        } else {
            // If no tasks available, generate new ones
            generateNewTasks();
        }
    }

    private void displayTasks(QuizResponse response) {
        if (response == null || response.getQuiz() == null) {
            generateNewTasks();
            return;
        }
        
        adapter = new QuizAdapter(response.getQuiz(), new QuizAdapter.QuizListener() {
            @Override
            public void onQuizCompleted(int score) {
                navigateToResults(score);
                generateNewTasks();
            }
        });
        tasksRecyclerView.setAdapter(adapter);
    }

    // java/com/example/llmexample/activities/DashboardActivity.java
    private void generateNewTasks() {
        String interests = SharedPreferencesHelper.getInterests(this);
        if (interests.isEmpty()) {
            interests = "General"; // Default topic
        }

        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("TOPIC", interests);
        startActivity(intent);
    }

    private void navigateToResults(int score) {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Add here
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}