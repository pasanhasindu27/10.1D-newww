package com.example.llmexample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.llmexample.R;
import com.example.llmexample.database.AppDatabase;
import com.example.llmexample.models.User;
import com.example.llmexample.utilities.SharedPreferencesHelper;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import com.example.llmexample.R;
import com.example.llmexample.database.AppDatabase;
import com.example.llmexample.models.User;
import com.example.llmexample.utilities.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity {
    private AppDatabase db;
    private EditText etUsername, etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
            db = Room.databaseBuilder(this, AppDatabase.class, "learning-db")
                    .fallbackToDestructiveMigration()
                    .build();

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignup = findViewById(R.id.tvSignup);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            new Thread(() -> {
                User user = db.userDao().getUser(username);
                if(user != null && checkPassword(password, user.passwordHash)) {
                    runOnUiThread(() -> startDashboard(user));
                } else {
                    runOnUiThread(() -> showError("Invalid credentials"));
                }
            }).start();
        });


        // Signup text click listener
        tvSignup.setOnClickListener(v -> navigateToSetup());
    }

    private boolean checkPassword(String plainPassword, String storedHash) {
        try {
            // Split stored hash into salt and hash components
            String[] parts = storedHash.split(":");
            if (parts.length != 2) return false;

            byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
            byte[] storedHashBytes = Base64.decode(parts[1], Base64.NO_WRAP);

            KeySpec spec = new PBEKeySpec(
                    plainPassword.toCharArray(),
                    salt,
                    65536,  // Iteration count
                    256     // Key length
            );

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            // Constant-time comparison
            return MessageDigest.isEqual(storedHashBytes, testHash);

        } catch (Exception e) {
            Log.e("LoginActivity", "Password check failed", e);
            return false;
        }
    }
    private void startDashboard(User user) {
        SharedPreferencesHelper.saveUsername(this, user.username);
        SharedPreferencesHelper.saveInterests(this, user.interests);

        Intent intent = new Intent(LoginActivity.this, TopicSelectionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateInputs(username, password)) {
            // For prototype purposes, just save username without password validation
            SharedPreferencesHelper.saveUsername(this, username);

            // Navigate to dashboard
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }

    private boolean validateInputs(String username, String password) {
        if (username.isEmpty()) {
            showError("Username cannot be empty");
            return false;
        }
        if (password.isEmpty()) {
            showError("Password cannot be empty");
            return false;
        }
        return true;
    }

    private void navigateToSetup() {
        startActivity(new Intent(this, SetupActivity.class));
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Auto-fill username if available
        String savedUsername = SharedPreferencesHelper.getUsername(this);
        if (!savedUsername.equals("Student")) {
            etUsername.setText(savedUsername);
        }
    }
}
