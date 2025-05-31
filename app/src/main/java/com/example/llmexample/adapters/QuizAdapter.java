package com.example.llmexample.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.llmexample.R;
import com.example.llmexample.models.Question;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    private final List<Question> questions;
    private final QuizListener listener;
    private int score = 0;
    // Add a map to store selected answers
    private final java.util.Map<Integer, String> selectedAnswers = new java.util.HashMap<>();

    public interface QuizListener {
        void onQuizCompleted(int score);
    }

    public QuizAdapter(List<Question> questions, QuizListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = questions.get(position);

        // Set question text
        holder.questionText.setText(question.getQuestion());

        // Set options
        holder.optionA.setText(question.getOptions().get(0));
        holder.optionB.setText(question.getOptions().get(1));
        holder.optionC.setText(question.getOptions().get(2));
        holder.optionD.setText(question.getOptions().get(3));

        // Set up radio button listeners
        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selected = "";
            if (checkedId == holder.optionA.getId()) {
                selected = "A";
            } else if (checkedId == holder.optionB.getId()) {
                selected = "B";
            } else if (checkedId == holder.optionC.getId()) {
                selected = "C";
            } else if (checkedId == holder.optionD.getId()) {
                selected = "D";
            }
            
            // Store the selected answer
            selectedAnswers.put(position, selected);
            question.setAnswered(true);
        });

        // Restore selected answer if any
        if (selectedAnswers.containsKey(position)) {
            String selected = selectedAnswers.get(position);
            if ("A".equals(selected)) {
                holder.optionA.setChecked(true);
            } else if ("B".equals(selected)) {
                holder.optionB.setChecked(true);
            } else if ("C".equals(selected)) {
                holder.optionC.setChecked(true);
            } else if ("D".equals(selected)) {
                holder.optionD.setChecked(true);
            }
        } else {
            holder.radioGroup.clearCheck();
        }
    }

    private void highlightCorrectAnswer(ViewHolder holder, String correctAnswer) {
        int colorGreen = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
        int colorDefault = ContextCompat.getColor(holder.itemView.getContext(), R.color.black);

        // Reset all options
        holder.optionA.setTextColor(colorDefault);
        holder.optionB.setTextColor(colorDefault);
        holder.optionC.setTextColor(colorDefault);
        holder.optionD.setTextColor(colorDefault);

        // Highlight correct answer
        switch (correctAnswer.toUpperCase()) {
            case "A":
                holder.optionA.setTextColor(colorGreen);
                break;
            case "B":
                holder.optionB.setTextColor(colorGreen);
                break;
            case "C":
                holder.optionC.setTextColor(colorGreen);
                break;
            case "D":
                holder.optionD.setTextColor(colorGreen);
                break;
        }
    }

    private boolean isAllQuestionsAnswered() {
        for (Question question : questions) {
            if (!question.isAnswered()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView questionText;
        public RadioGroup radioGroup;
        public RadioButton optionA, optionB, optionC, optionD;

        public ViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            optionA = itemView.findViewById(R.id.optionA);
            optionB = itemView.findViewById(R.id.optionB);
            optionC = itemView.findViewById(R.id.optionC);
            optionD = itemView.findViewById(R.id.optionD);
        }
    }

    // Add method to get a question at a specific position
    public Question getQuestion(int position) {
        if (position >= 0 && position < questions.size()) {
            return questions.get(position);
        }
        return null;
    }

    // Add method to get selected answer for a specific position
    public String getSelectedAnswer(int position) {
        return selectedAnswers.get(position);
    }
}