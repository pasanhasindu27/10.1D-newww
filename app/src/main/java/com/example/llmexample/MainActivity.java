package com.example.llmexample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.llmexample.activities.ProfileActivity;
import com.example.llmexample.activities.ResultsActivity;
import com.example.llmexample.adapters.QuizAdapter;
import com.example.llmexample.models.Question;
import com.example.llmexample.models.QuizResponse;
import com.example.llmexample.utilities.SharedPreferencesHelper;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements QuizAdapter.QuizListener {

    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private LinearLayout loadingContainer, errorContainer;
    private List<Question> quizQuestions = new ArrayList<>();
    private Button btnRetry;
    private Button btnProfile; // Add profile button

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        loadingContainer = findViewById(R.id.loadingContainer);
        errorContainer = findViewById(R.id.errorContainer);
        btnProfile = findViewById(R.id.btnProfile); // Initialize profile button

        // Initialize adapter with listener
        adapter = new QuizAdapter(quizQuestions, this); // 'this' implements QuizListener
        recyclerView.setAdapter(adapter);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get topic from SharedPreferences
        String topic = SharedPreferencesHelper.getTopic(this);
        String url = "https://dc6c-2407-c00-e006-d4bd-f04c-dcb1-417a-132d.ngrok-free.app/getQuiz?topic=" + topic;

        errorContainer = findViewById(R.id.errorContainer);
        btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> fetchQuiz(url));
        
        // Set click listener for profile button
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Initial fetch
        fetchQuiz(url);
    }

    private void fetchQuiz(String url) {
        loadingContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingContainer.setVisibility(View.GONE);
                    try {
                        Gson gson = new Gson();
                        QuizResponse quizResponse = gson.fromJson(response.toString(), QuizResponse.class);
                        quizQuestions.clear();
                        quizQuestions.addAll(quizResponse.getQuiz());
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        showError("Error parsing data: " + e.getMessage());
                    }
                },
                error -> {
                    loadingContainer.setVisibility(View.GONE);
                    showError("Network error: " + error.getMessage());
                });

        queue.add(request);
    }

    private void showError(String message) {
        errorContainer.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    // Implement the QuizListener interface
    @Override
    public void onQuizCompleted(int score) {
        // Handle quiz completion
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
    }

}