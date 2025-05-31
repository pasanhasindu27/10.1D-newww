package com.example.llmexample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.example.llmexample.R;
import com.example.llmexample.adapters.TopicsAdapter;
import com.example.llmexample.database.AppDatabase;
import com.example.llmexample.models.Topic;
import com.example.llmexample.utilities.SharedPreferencesHelper;
import java.util.ArrayList;
import java.util.List;

public class TopicSelectionActivity extends AppCompatActivity implements TopicsAdapter.OnTopicSelectedListener {
    private RecyclerView recyclerView;
    private TopicsAdapter adapter;
    private List<Topic> allTopics = new ArrayList<>();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_selection);

        initializeDatabase();
        initializeTopics();
        setupRecyclerView();
        setupContinueButton();
    }

    private void initializeDatabase() {
        db = Room.databaseBuilder(this, AppDatabase.class, "learning-db")
                .fallbackToDestructiveMigration()
                .build();
    }

    private void initializeTopics() {
        String[] topics = {"Mathematics", "Physics", "Computer Science", "History", "Biology"};
        for (String topicName : topics) {
            allTopics.add(new Topic(topicName));
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.topicsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new TopicsAdapter(allTopics, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupContinueButton() {
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> validateAndProceed());
    }

    private void validateAndProceed() {
        List<Topic> selectedTopics = getSelectedTopics();
        if (selectedTopics.isEmpty()) {
            showSelectionError();
            return;
        }
        saveSelectedTopics(selectedTopics);
    }

    private List<Topic> getSelectedTopics() {
        List<Topic> selected = new ArrayList<>();
        for (Topic topic : allTopics) {
            if (topic.selected) selected.add(topic);
        }
        return selected;
    }

    private void showSelectionError() {
        Toast.makeText(this, "Please select at least one topic", Toast.LENGTH_SHORT).show();
    }

    private void saveSelectedTopics(List<Topic> selectedTopics) {
        new Thread(() -> {
            // Save to database
            for (Topic topic : selectedTopics) {
                db.topicDao().insert(topic);
            }

            // Save to preferences
            String interests = formatTopicsString(selectedTopics);
            SharedPreferencesHelper.saveInterests(this, interests);

            // Navigate with validated data
            runOnUiThread(() -> navigateToQuiz(interests));
        }).start();
    }

    private String formatTopicsString(List<Topic> topics) {
        StringBuilder sb = new StringBuilder();
        for (Topic topic : topics) {
            if (sb.length() > 0) sb.append(",");
            sb.append(topic.name.trim());
        }
        return sb.toString();
    }

    private void navigateToQuiz(String topics) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("TOPIC", topics);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    public void onTopicSelected(Topic topic, boolean selected) {
        topic.selected = selected;
    }
}