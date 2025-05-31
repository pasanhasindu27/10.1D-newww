package com.example.llmexample.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.llmexample.R;
import com.example.llmexample.models.QuizHistory;

import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final List<QuizHistory> historyItems;
    private final HistoryListener listener;

    public interface HistoryListener {
        void onHistoryItemClicked(QuizHistory historyItem);
    }

    public HistoryAdapter(List<QuizHistory> historyItems, HistoryListener listener) {
        this.historyItems = historyItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizHistory history = historyItems.get(position);
        
        holder.topicText.setText(history.topic);
        holder.scoreText.setText("Score: " + history.score + "/" + history.totalQuestions);
        
        // Format date
        String date = DateFormat.format("MMM dd, yyyy", new Date(history.date)).toString();
        holder.dateText.setText(date);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHistoryItemClicked(history);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView topicText, scoreText, dateText;
        public ImageView viewDetailsButton;

        public ViewHolder(View itemView) {
            super(itemView);
            topicText = itemView.findViewById(R.id.historyTopicText);
            scoreText = itemView.findViewById(R.id.historyScoreText);
            dateText = itemView.findViewById(R.id.historyDateText);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
        }
    }
}
