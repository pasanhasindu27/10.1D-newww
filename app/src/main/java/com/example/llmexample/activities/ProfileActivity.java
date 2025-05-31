package com.example.llmexample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.llmexample.R;
import com.example.llmexample.database.AppDatabase;
import com.example.llmexample.database.QuizHistoryDao;
import com.example.llmexample.utilities.SharedPreferencesHelper;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameText, txtTotalQuestions, txtCorrectAnswers, txtIncorrectAnswers, txtNotification;
    private ImageView profileImageView;
    private ImageButton btnBack;
    private Button shareButton, viewHistoryButton, updateButton;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "learning_app_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // Initialize views
        usernameText = findViewById(R.id.usernameText);
        profileImageView = findViewById(R.id.profileImageView);
        txtTotalQuestions = findViewById(R.id.txtTotalQuestions);
        txtCorrectAnswers = findViewById(R.id.txtCorrectAnswers);
        txtIncorrectAnswers = findViewById(R.id.txtIncorrectAnswers);
        txtNotification = findViewById(R.id.txtNotification);
        btnBack = findViewById(R.id.btnBack);
        shareButton = findViewById(R.id.shareButton);
        viewHistoryButton = findViewById(R.id.viewHistoryButton);
        updateButton = findViewById(R.id.updateButton);

        // Set username
        String username = SharedPreferencesHelper.getUsername(this);
        usernameText.setText(username);

        // Load user stats
        loadUserStats(username);

        // Set up back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Set up share button
        shareButton.setOnClickListener(v -> showShareDialog());

        // Set up view history button
        viewHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Set up upgrade button (previously update button)
        updateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpgradeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void loadUserStats(String username) {
        QuizHistoryDao historyDao = db.quizHistoryDao();
        
        // Get total questions
        int totalQuestions = historyDao.getTotalQuestions(username);
        txtTotalQuestions.setText(String.valueOf(totalQuestions));
        
        // Get total score (correct answers)
        int totalScore = historyDao.getTotalScore(username);
        txtCorrectAnswers.setText(String.valueOf(totalScore));
        
        // Calculate incorrect answers
        int incorrectAnswers = totalQuestions - totalScore;
        txtIncorrectAnswers.setText(String.valueOf(incorrectAnswers));
        
        // Set notification text
        txtNotification.setText("Display any important notifications here");
    }

    private void showShareDialog() {
        ShareProfileDialog dialog = new ShareProfileDialog();
        dialog.show(getSupportFragmentManager(), "ShareProfileDialog");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}