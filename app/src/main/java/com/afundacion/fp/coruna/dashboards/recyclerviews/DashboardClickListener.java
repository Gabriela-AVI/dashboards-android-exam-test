package com.afundacion.fp.coruna.dashboards.recyclerviews;

import com.afundacion.fp.coruna.dashboards.server.dtos.DashboardDto;

/*
 * Esta interfaz define un contrato de comunicación
 * entre el Adapter (DashboardsRecyclerAdapter)
 * y la Activity (DashboardsListActivity).
 *
 * Sirve para detectar cuándo el usuario pulsa
 * sobre un dashboard en el RecyclerView.
 *
 * El Adapter NO abre Activities directamente.
 * Solo notifica el evento a través de esta interfaz.
 */
public interface DashboardClickListener {

    /*
     * Cuando el usuario hace click en una celda del RecyclerView.
     * Recibe como parámetro el DashboardDto correspondiente a la celda pulsada.
     */
    void onDashboardClicked(DashboardDto dashboard);
}