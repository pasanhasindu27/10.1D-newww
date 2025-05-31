package com.example.llmexample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.llmexample.R;
import com.example.llmexample.adapters.TopicsAdapter;
import com.example.llmexample.database.AppDatabase;
import com.example.llmexample.models.Topic;
import com.example.llmexample.models.User;
import com.example.llmexample.utilities.PasswordHelper;
import com.example.llmexample.utilities.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity {
    private EditText etEmail, etUsername, etPassword, etConfirmPassword, etPhone;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        db = Room.databaseBuilder(this, AppDatabase.class, "learning-db").build();

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        Button btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> validateAndRegister());
    }

    private void validateAndRegister() {
        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String phone = etPhone.getText().toString();

        if (validateInputs(email, username, password, confirmPassword, phone)) {
            new Thread(() -> {
                try {
                    String hashedPassword = PasswordHelper.hashPassword(password);
                    User newUser = new User();
                    newUser.email = email;
                    newUser.username = username;
                    newUser.passwordHash = hashedPassword;
                    newUser.phone = phone;

                    db.userDao().insert(newUser);

                    runOnUiThread(() -> {
                        navigateToTopicSelection(email);
                    });
                } catch (Exception e) {
                    showError("Registration failed: " + e.getMessage());
                }
            }).start();
        }
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean validateInputs(String email, String username, String password,
                                   String confirmPassword, String phone) {
        // Validation logic here
        return true;
    }

    private void navigateToTopicSelection(String email) {
        Intent intent = new Intent(this, TopicSelectionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Add here
        finish();
    }
}
