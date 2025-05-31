package com.example.llmexample.activities;

import android.os.Bundle;
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

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.HistoryListener {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "learning_app_db")
                .allowMainThreadQueries() // For simplicity, in production use AsyncTask or coroutines
                .fallbackToDestructiveMigration()
                .build();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load history data
        loadHistoryData();
    }

    private void loadHistoryData() {
        String username = SharedPreferencesHelper.getUsername(this);
        List<QuizHistory> historyItems = db.quizHistoryDao().getUserHistory(username);
        
        adapter = new HistoryAdapter(historyItems, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onHistoryItemClicked(QuizHistory historyItem) {
        // Show details or navigate to detail view
        // For now, just show a toast with the score
        String message = "Quiz on " + historyItem.topic + ": " +
                historyItem.score + "/" + historyItem.totalQuestions;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}