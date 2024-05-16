package com.example.welcomscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> events;

    public EventAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event currentEvent = events.get(position);
        holder.nameTextView.setText(currentEvent.getName());
        holder.speakerTextView.setText(currentEvent.getSpeaker());
        holder.groupTextView.setText(currentEvent.getGroup());
        holder.startTimeTextView.setText(currentEvent.getStartTime());
        holder.endTimeTextView.setText(currentEvent.getEndTime());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView speakerTextView;
        TextView groupTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            speakerTextView = itemView.findViewById(R.id.textViewSpeaker);
            groupTextView = itemView.findViewById(R.id.textViewGroup);
            startTimeTextView = itemView.findViewById(R.id.textViewStartTime);
            endTimeTextView = itemView.findViewById(R.id.textViewEndTime);
        }
    }
}