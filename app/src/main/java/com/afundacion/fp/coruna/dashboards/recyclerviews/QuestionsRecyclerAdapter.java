package com.afundacion.fp.coruna.dashboards.recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.activities.AnswersActivity;
import com.afundacion.fp.coruna.dashboards.server.dtos.QuestionDto;

import java.util.List;

/*
 * Este Adapter conecta:
 * - La lista de preguntas (QuestionDto)
 * - Con el RecyclerView
 *
 * Es el encargado de:
 * 1. Crear las celdas (ViewHolder)
 * 2. Asignar los datos a cada celda
 * 3. Indicar cuántos elementos hay
 */
public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionCell> {

    private List<QuestionDto> questions;  // Lista de preguntas que se van a mostrar
    private Context context; // Guardamos el contexto de la Activity
    private QuestionsClickListener clickListener; //5🐸


    public QuestionsRecyclerAdapter(List<QuestionDto> questions) {
        this.questions = questions;
    }

    //5🐸
    public void setClickListener(QuestionsClickListener listener) {
        this.clickListener = listener;
    }

    /*
     * Se ejecuta cuando el RecyclerView necesita crear una nueva celda.
     * Aquí inflamos el layout XML de cada pregunta.
     */
    @NonNull
    @Override
    public QuestionCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Convertimos el XML question_cell en una vista real
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.question_cell, parent, false);

        // Creamos el ViewHolder (QuestionCell)
        return new QuestionCell(view);
    }

    /*
     * Se ejecuta para rellenar los datos de cada celda.
     * Aquí conectamos los datos con la vista.
     */
    @Override
    public void onBindViewHolder(@NonNull QuestionCell holder, int position) {

        // Obtenemos la pregunta correspondiente a esta posición
        QuestionDto question = questions.get(position);

        // Asignamos los datos a la celda
        holder.setInfo(
                question.getTitle(),
                question.getDescription(),
                question.getAuthor()
        );

        //5🐸
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (clickListener != null) {
                    clickListener.onQuestionClicked(question);
                }
            }
        });
    }

    /*
     * Devuelve el número total de preguntas.
     * El RecyclerView lo usa para saber cuántas filas dibujar.
     */
    @Override
    public int getItemCount() {
        return questions.size();
    }
}