package com.example.project_nanlina;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PMAdapter extends RecyclerView.Adapter<PMAdapter.ViewHolder>
        implements OnPMItemClickListener {

    ArrayList<PM> items = new ArrayList<PM>();

    OnPMItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.pm_item, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PM item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(PM item) {
        items.add(item);
    }

    public void setItems(ArrayList<PM> items) {
        this.items = items;
    }

    public PM getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, PM item) {
        items.set(position, item);
    }

    public void setOnItemClickListener(OnPMItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textAddress;
        Button buttonNumber;

        public ViewHolder(View itemView, final OnPMItemClickListener listener) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textAddress = itemView.findViewById(R.id.textAddress);
            buttonNumber = itemView.findViewById(R.id.buttonNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(PM item) {
            textName.setText(item.getName());
            textAddress.setText(item.getAddress());
            buttonNumber.setText(item.getNumber());
        }

    }
}
