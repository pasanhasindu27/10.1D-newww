package com.example.llmexample.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.llmexample.models.Topic;

import java.util.List;

@Dao
public interface TopicDao {
    @Insert
    void insert(Topic topic);

    @Query("UPDATE topics SET selected = :selected WHERE id = :id")
    void setSelected(int id, boolean selected);

    @Query("SELECT * FROM topics")
    List<Topic> getAllTopics();
}
