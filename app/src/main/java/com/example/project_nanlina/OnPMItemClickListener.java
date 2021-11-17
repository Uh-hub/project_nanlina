package com.example.project_nanlina;

import android.view.View;

import com.example.project_nanlina.parking.PMListAdapter;

public interface OnPMItemClickListener {
    public void onItemClick(PMListAdapter.ViewHolder holder, View view, int position);
}
