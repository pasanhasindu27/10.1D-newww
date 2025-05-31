package com.example.llmexample.services;

import com.example.llmexample.models.QuizResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("getQuiz")
    Call<QuizResponse> getQuiz(@Query("topic") String topic);
    @GET("generate-tasks")
    Call<QuizResponse> generateTasks(
            @Query("topics") List<String> topics,
            @Query("count") int questionCount
    );

    @GET("generate-new-task")
    Call<QuizResponse> generateNewTask(
            @Header("Authorization") String token,
            @Query("topics") List<String> topics
    );
}