package com.example.welcomscreen;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<ContactCard> contactCard;
    private int itemsPerPage = 1; // Bilang ng card bawat pahina
    private int currentPage = 0; // Kasalukuyang pahina

    public ContactAdapter(List<ContactCard> contactCard) {
        this.contactCard = contactCard;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardview_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        List<ContactCard> currentPageItems = getCurrentPageItems();
        final int adapterPosition = holder.getAdapterPosition(); // Get the adapter position
        final ContactCard contact = currentPageItems.get(position);

        // Bind data to views in CardView layout
        holder.textViewName.setText(contact.getName());
        holder.textViewPhoneNumber.setText(contact.getPhoneNumber());
        holder.textViewEmail.setText(contact.getEmail());
        holder.textViewAddress.setText(contact.getAddress());

        // Set onClickListener for editing the contact
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle editing the contact here
                // You can open a dialog or activity for editing
                // For simplicity, let's assume you open an activity for editing
                Intent intent = new Intent(v.getContext(), EditContactActivity.class);
                intent.putExtra("contactIndex", adapterPosition);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getCurrentPageItems().size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPhoneNumber;
        TextView textViewEmail;
        TextView textViewAddress;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView1);
            textViewPhoneNumber = itemView.findViewById(R.id.textView2);
            textViewEmail = itemView.findViewById(R.id.textView3);
            textViewAddress = itemView.findViewById(R.id.textView4);
        }
    }

    // Pagkuha ng list ng card para sa kasalukuyang pahina
    private List<ContactCard> getCurrentPageItems() {
        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, contactCard.size());
        return contactCard.subList(start, end);
    }

    // Method para mag-refresh ng RecyclerView
    public void refresh() {
        notifyDataSetChanged();
    }

    // Method para pumunta sa susunod na pahina
    public void nextPage() {
        if ((currentPage + 1) * itemsPerPage < contactCard.size()) {
            currentPage++;
            refresh();
        }
    }

    // Method para pumunta sa nakaraang pahina
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            refresh();
        }
    }

    // Method para i-edit ang contact
    public void editContact(int index, ContactCard newContact) {
        contactCard.set(index, newContact);
        refresh();
    }
}