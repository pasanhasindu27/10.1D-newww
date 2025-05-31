package com.example.llmexample.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.llmexample.models.User;
import com.example.llmexample.models.Topic;
import com.example.llmexample.models.QuizHistory;

@Database(
        entities = {User.class, Topic.class, QuizHistory.class},
        version = 2,
        exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract TopicDao topicDao();
    public abstract QuizHistoryDao quizHistoryDao();
}
