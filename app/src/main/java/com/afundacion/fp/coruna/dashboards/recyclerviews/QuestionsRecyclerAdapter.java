package com.afundacion.fp.coruna.dashboards.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.server.dtos.QuestionDto;

import java.util.List;

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionCell> {

    private List<QuestionDto> questions;

    public QuestionsRecyclerAdapter(List<QuestionDto> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_cell, parent, false);
        return new QuestionCell(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionCell holder, int position) {
        QuestionDto question = questions.get(position);
        holder.setInfo(question.getTitle(), question.getDescription(), question.getAuthor());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
