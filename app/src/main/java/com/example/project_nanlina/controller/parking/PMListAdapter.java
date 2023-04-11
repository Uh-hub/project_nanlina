package com.example.project_nanlina.controller.parking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_nanlina.R;
import com.example.project_nanlina.model.PMItem;

import java.util.ArrayList;

public class PMListAdapter extends RecyclerView.Adapter<PMListAdapter.ViewHolder> implements OnPMItemClickListener {

    ArrayList<PMItem> items = new ArrayList<PMItem>();
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
        PMItem item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(PMItem item) {
        items.add(item);
    }

    public void setItems(ArrayList<PMItem> items) {
        this.items = items;
    }

    public PMItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, PMItem item) {
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textAddress;
        Button buttonNumber;

        // 숨김 정보
        TextView textPhoto;
        TextView textKickboard;
        TextView textBicycle;

        public ViewHolder(View itemView, final OnPMItemClickListener listener) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textAddress = itemView.findViewById(R.id.textAddress);
            buttonNumber = itemView.findViewById(R.id.buttonNumber);

            textPhoto = itemView.findViewById(R.id.textPhoto);
            textKickboard = itemView.findViewById(R.id.textKickboard);
            textBicycle = itemView.findViewById(R.id.textBicycle);

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

        public void setItem(PMItem item) {
            textName.setText(item.getName());
            textAddress.setText(item.getAddress());
            buttonNumber.setText(Integer.toString(Integer.parseInt(item.getKickboard()) + Integer.parseInt(item.getBicycle())) + "대");

            textPhoto.setText(item.getPhoto());
            textKickboard.setText(item.getKickboard());
            textBicycle.setText(item.getBicycle());
        }

    }
}
