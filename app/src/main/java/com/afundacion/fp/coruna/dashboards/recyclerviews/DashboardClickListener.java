package com.afundacion.fp.coruna.dashboards.recyclerviews;


import com.afundacion.fp.coruna.dashboards.server.dtos.DashboardDto;

public interface DashboardClickListener {

    void onDashboardClicked(DashboardDto dashboard);
}
