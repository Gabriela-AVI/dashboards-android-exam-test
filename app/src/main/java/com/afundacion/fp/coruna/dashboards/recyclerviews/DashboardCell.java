package com.afundacion.fp.coruna.dashboards.recyclerviews;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;

public class DashboardCell extends RecyclerView.ViewHolder {
    private TextView titleView;
    public DashboardCell(@NonNull View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.dashboardCellTitle);
    }

    public void setTitle(String title) {
        this.titleView.setText(title);
    }
}
