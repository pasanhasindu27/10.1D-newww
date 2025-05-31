package com.example.llmexample.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import com.example.llmexample.R;
import com.example.llmexample.database.AppDatabase;
import com.example.llmexample.database.QuizHistoryDao;
import com.example.llmexample.utilities.SharedPreferencesHelper;

public class ShareProfileDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_share_profile, container, false);
        
        Button shareButton = view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> shareProfile());
        
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void shareProfile() {
        // Get user stats
        String username = SharedPreferencesHelper.getUsername(requireContext());
        
        AppDatabase db = Room.databaseBuilder(requireContext(),
                AppDatabase.class, "learning_app_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        
        QuizHistoryDao historyDao = db.quizHistoryDao();
        int totalScore = historyDao.getTotalScore(username);
        int quizzesCompleted = historyDao.getCompletedQuizCount(username);
        int totalQuestions = historyDao.getTotalQuestions(username);
        int accuracy = totalQuestions > 0 ? (totalScore * 100) / totalQuestions : 0;
        
        // Create share content
        String shareContent = "Check out my progress on Personalized Learning!\n\n" +
                "Username: " + username + "\n" +
                "Total Score: " + totalScore + "\n" +
                "Quizzes Completed: " + quizzesCompleted + "\n" +
                "Accuracy: " + accuracy + "%";
        
        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Learning Profile");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        
        startActivity(Intent.createChooser(shareIntent, "Share via"));
        dismiss();
    }
}