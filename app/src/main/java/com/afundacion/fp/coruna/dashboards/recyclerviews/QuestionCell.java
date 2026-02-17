package com.afundacion.fp.coruna.dashboards.recyclerviews;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;

/*
 * Esta clase representa una celda individual
 * dentro del RecyclerView de preguntas.
 *
 * Es un ViewHolder.
 *
 * Su función es:
 * - Guardar las referencias a los TextView
 * - Representar visualmente una pregunta
 * - Permitir que el Adapter le pase los datos
 */
public class QuestionCell extends RecyclerView.ViewHolder {

    private TextView titleView; // TextView que muestra el título de la pregunta
    private TextView contentView; // TextView que muestra el contenido/descripción
    private TextView authorView; // TextView que muestra el autor

    public QuestionCell(@NonNull View itemView) {
        super(itemView);
        // Conectamos cada vista del layout con su variable
        titleView = itemView.findViewById(R.id.questionCellTitle);
        contentView = itemView.findViewById(R.id.questionCellContent);
        authorView = itemView.findViewById(R.id.questionCellAuthor);
    }

    /*
     * METODO --> permite al Adapter asignar los datos
     * de la pregunta a esta celda.
     */
    public void setInfo(String title, String content, String author) {

        titleView.setText(title);// Mostramos título
        contentView.setText(content);// Mostramos contenid
        authorView.setText(author);// Mostramos autor
    }
}