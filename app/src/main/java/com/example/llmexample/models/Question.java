package com.example.llmexample.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Question implements Parcelable {

    @SerializedName("question")
    private String question;

    @SerializedName("options")
    private List<String> options;

    @SerializedName("correct_answer")
    private String correct_answer;

    private boolean answered;

    // Constructor for manual creation
    public Question(String question, List<String> options, String correct_answer) {
        this.question = question;
        this.options = options;
        this.correct_answer = correct_answer;
    }

    // Empty constructor for Gson
    public Question() {}

    // Parcelable constructor
    protected Question(Parcel in) {
        question = in.readString();
        options = in.createStringArrayList();
        correct_answer = in.readString();
        answered = in.readByte() != 0;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringList(options);
        dest.writeString(correct_answer);
        dest.writeByte((byte) (answered ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correct_answer;
    }

    public void setCorrectAnswer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}