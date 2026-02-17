package com.afundacion.fp.coruna.dashboards.recyclerviews;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;

/*
 * Esta clase representa una "celda" individual
 * dentro del RecyclerView de dashboards.
 *
 * Es un ViewHolder.
 *
 * Su función es:
 * - Guardar referencias a las vistas del item
 * - Evitar hacer findViewById continuamente
 * - Representar una fila visual
 */
public class DashboardCell extends RecyclerView.ViewHolder {

    private TextView titleView;   // Referencia al TextView que muestra el título del dashboard

    /*
     * Recibe la vista completa del item (itemView),
     * que corresponde al layout XML de una celda.
     */
    public DashboardCell(@NonNull View itemView) {
        super(itemView);

        // Conectamos el TextView del layout con el código
        titleView = itemView.findViewById(R.id.dashboardCellTitle);
    }

    /*
     * METODO --> establecer el título del dashboard en la celda.
     *
     * El Adapter llamará a este metodo en onBindViewHolder()
     * para mostrar los datos correctos.
     */
    public void setTitle(String title) {
        this.titleView.setText(title);
    }
}