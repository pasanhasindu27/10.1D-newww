package com.example.llmexample.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.llmexample.R;
import com.example.llmexample.models.Topic;
import java.util.List;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {

    private List<Topic> topics;
    private final OnTopicSelectedListener listener;

    public interface OnTopicSelectedListener {
        void onTopicSelected(Topic topic, boolean selected);
    }

    public TopicsAdapter(List<Topic> topics, OnTopicSelectedListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic topic = topics.get(position);

        // Remove previous listener to prevent recycling issues
        holder.cbTopic.setOnCheckedChangeListener(null);

        // Set current state
        holder.cbTopic.setChecked(topic.selected);
        holder.tvTopicName.setText(topic.name);

        // Add new listener
        holder.cbTopic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            topic.selected = isChecked;
            if (listener != null) {
                listener.onTopicSelected(topic, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void updateTopics(List<Topic> newTopics) {
        topics = newTopics;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbTopic;
        public TextView tvTopicName;

        public ViewHolder(View itemView) {
            super(itemView);
            cbTopic = itemView.findViewById(R.id.cbTopic);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
        }
    }
}
