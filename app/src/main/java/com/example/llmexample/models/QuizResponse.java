package com.example.llmexample.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class QuizResponse implements Parcelable {
    private List<Question> quiz;

    // Parcelable implementation
    protected QuizResponse(Parcel in) {
        quiz = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<QuizResponse> CREATOR = new Creator<QuizResponse>() {
        @Override
        public QuizResponse createFromParcel(Parcel in) {
            return new QuizResponse(in);
        }

        @Override
        public QuizResponse[] newArray(int size) {
            return new QuizResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(quiz);
    }

    // Getters and setters
    public List<Question> getQuiz() {
        System.out.println(quiz);
        return quiz;
    }

    public void setQuiz(List<Question> quiz) {
        this.quiz = quiz;
    }
}