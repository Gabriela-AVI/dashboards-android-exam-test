package com.afundacion.fp.coruna.dashboards.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.server.dtos.DashboardDto;

import java.util.List;

/*
 * Este Adapter conecta:
 * - La lista de datos (DashboardDto)
 * - Con el RecyclerView
 *
 * Es el intermediario entre:
 * DATOS → UI
 */
public class DashboardsRecyclerAdapter extends RecyclerView.Adapter<DashboardCell> {

    private List<DashboardDto> dashboards; // Lista de dashboards que vamos a mostrar
    private DashboardClickListener clickListener;  // Listener para detectar cuando se pulsa una celda

    public DashboardsRecyclerAdapter(List<DashboardDto> dashboards) {
        this.dashboards = dashboards;
    }

    //Permite asignar el listener desde fuera (normalmente desde la Activity)
    public void setClickListener(DashboardClickListener listener) {
        this.clickListener = listener;
    }

    /*
     * Cuando el RecyclerView necesita crear una nueva celda.
     * Aquí inflamos el layout XML de cada fila.
     */
    @NonNull
    @Override
    public DashboardCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflamos el layout dashboard_cell.xml
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.dashboard_cell, parent, false);

        // Creamos el ViewHolder (DashboardCell)
        return new DashboardCell(view);
    }

    /*
     * Rellenar los datos de cada celda.
     * Aquí conectamos los datos con la vista.
     */
    @Override
    public void onBindViewHolder(@NonNull DashboardCell holder, int position) {

        // Obtenemos el dashboard correspondiente a esta posición
        DashboardDto dashboard = dashboards.get(position);

        // Asignamos el título al TextView de la celda
        holder.setTitle(dashboard.getTitle());

        // Configuramos el click de la fila
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Si existe listener, notificamos qué dashboard se ha pulsado
                if (clickListener != null) {
                    clickListener.onDashboardClicked(dashboard);
                }
            }
        });
    }

    //Devuelve el número total de elementos que tiene la lista.
    @Override
    public int getItemCount() {
        return dashboards.size();
    }
}