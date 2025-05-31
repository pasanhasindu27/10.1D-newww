package com.example.llmexample.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.llmexample.R;
import com.example.llmexample.adapters.QuizAdapter;
import com.example.llmexample.models.Question;
import com.example.llmexample.models.QuizResponse;
import com.example.llmexample.services.ApiClient;
import com.example.llmexample.services.ApiInterface;
import androidx.room.Room;
import com.example.llmexample.database.AppDatabase;
import com.example.llmexample.models.QuizHistory;
import com.example.llmexample.utilities.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity implements QuizAdapter.QuizListener {

    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private Button btnSubmitQuiz; // Add this line


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize the submit button
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);
        btnSubmitQuiz.setOnClickListener(v -> {
            if (adapter != null) {
                // Calculate score - you may need to implement a method in your adapter to calculate this
                int score = calculateScore();
                onQuizCompleted(score);
            }
        });
        
        String topic = getIntent().getStringExtra("TOPIC");
        Log.d("TOPIC_DEBUG", "Received topic: " + topic); // Add this

        if (topic == null || topic.trim().isEmpty()) {
            Log.e("API_ERROR", "Null or empty topic received");
            showFallbackQuestions();
            return;
        }

        fetchQuiz(topic);
    }

    private void fetchQuiz(String topics) {
        if (topics == null || topics.isEmpty()) {
            Log.e("API_ERROR", "Invalid topics parameter");
            showFallbackQuestions();
            return;
        }

        Log.d("API_DEBUG", "Fetching quiz for topics: " + topics);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<QuizResponse> call = apiInterface.getQuiz(topics);

        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Question> apiQuestions = response.body().getQuiz();
                    Log.d("API_DEBUG", "Fetched question for topics: " + apiQuestions);
                    if (apiQuestions != null && !apiQuestions.isEmpty()) {
                        Log.d("API_SUCCESS", "Received " + apiQuestions.size() + " questions");
                        setupRecyclerView(apiQuestions);
                    } else {
                        Log.e("API_ERROR", "Received empty question list");
                        showFallbackQuestions();
                    }
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                    showFallbackQuestions();
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
                showFallbackQuestions();
            }
        });
    }

    private void setupRecyclerView(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            Log.e("ERROR", "No questions to display!");
            return;
        }

        recyclerView = findViewById(R.id.quizRecyclerView);
        if (recyclerView == null) {
            Log.e("ERROR", "RecyclerView not found!");
            return;
        }

        adapter = new QuizAdapter(questions, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Log.d("DEBUG", "Adapter updated with " + questions.size() + " items");
    }

    private void showFallbackQuestions() {
        // Fallback to hardcoded questions if API fails
        List<Question> testQuestions = new ArrayList<>();
        testQuestions.add(new Question(
                "What is 2+2?",
                Arrays.asList("4", "5", "6", "7"),
                "A"
        ));
        setupRecyclerView(testQuestions);
        Toast.makeText(this, "Using sample questions", Toast.LENGTH_SHORT).show();
    }



//private void fetchQuiz(String topics) {
//    // Use test data directly (bypass API)
//    List<Question> testQuestions = new ArrayList<>();
//    testQuestions.add(new Question(
//            "What is 2+2?",
//            Arrays.asList("4", "5", "6", "7"),
//            "A"
//    ));
//
//    Log.d("DEBUG", "Test questions count: " + testQuestions.size()); // Should log 1
//    setupRecyclerView(testQuestions);
//}

//private void setupRecyclerView(List<Question> questions) {
//    if (questions == null || questions.isEmpty()) {
//        Log.e("ERROR", "No questions to display!");
//        return;
//    }
//
//    recyclerView = findViewById(R.id.quizRecyclerView);
//    if (recyclerView == null) {
//        Log.e("ERROR", "RecyclerView not found!");
//        return;
//    }
//
//    adapter = new QuizAdapter(questions, this);
//    recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    recyclerView.setAdapter(adapter);
//    Log.d("DEBUG", "Adapter updated with " + questions.size() + " items");
//}

    @Override
    public void onQuizCompleted(int score) {
        // First save the quiz result
        saveQuizResult(score);
        
        // Then get the quiz details and navigate
        String topic = getIntent().getStringExtra("TOPIC");
        int totalQuestions = adapter.getItemCount();
        
        // Navigate to Results Activity
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("CORRECT_ANSWERS", score);
        intent.putExtra("TOTAL_QUESTIONS", totalQuestions);
        intent.putExtra("TOPIC", topic);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void saveQuizResult(int score) {
        String topic = getIntent().getStringExtra("TOPIC");
        int totalQuestions = adapter.getItemCount();
        
        // Create history entry and save to database
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "learning_app_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        
        String username = SharedPreferencesHelper.getUsername(this);
        QuizHistory history = new QuizHistory(username, topic, score, totalQuestions);
        db.quizHistoryDao().insert(history);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // Add this method to calculate the score
    private int calculateScore() {
        // If adapter is null, return 0
        if (adapter == null) return 0;
        
        int score = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            Question question = adapter.getQuestion(i);
            String selectedAnswer = adapter.getSelectedAnswer(i);
            if (selectedAnswer != null && checkAnswer(selectedAnswer, question.getCorrectAnswer())) {
                score++;
            }
        }
        return score;
    }

    // Add method to check individual answers
    private boolean checkAnswer(String selectedAnswer, String correctAnswer) {
        // Remove the "*ANS:* " prefix from the correct answer
        String formattedCorrectAnswer = correctAnswer.replace("*ANS:* ", "").trim();
        return selectedAnswer.equals(formattedCorrectAnswer);
    }

    // Remove the duplicate onQuizCompleted method
}