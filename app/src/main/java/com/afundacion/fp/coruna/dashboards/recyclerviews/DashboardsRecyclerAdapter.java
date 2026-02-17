package com.afundacion.fp.coruna.dashboards.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.server.dtos.DashboardDto;

import java.util.List;

public class DashboardsRecyclerAdapter extends RecyclerView.Adapter<DashboardCell> {
    private List<DashboardDto> dashboards;
    private DashboardClickListener clickListener;

    public DashboardsRecyclerAdapter(List<DashboardDto> dashboards) {
        this.dashboards = dashboards;
    }

    public void setClickListener(DashboardClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public DashboardCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_cell, parent, false);
        return new DashboardCell(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardCell holder, int position) {
        DashboardDto dashboard = dashboards.get(position);
        holder.setTitle(dashboard.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onDashboardClicked(dashboard);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dashboards.size();
    }
}
