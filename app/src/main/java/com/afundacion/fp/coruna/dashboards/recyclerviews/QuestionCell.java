package com.afundacion.fp.coruna.dashboards.recyclerviews;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;

public class QuestionCell extends RecyclerView.ViewHolder {
    private TextView titleView;
    private TextView contentView;
    private TextView authorView;
    public QuestionCell(@NonNull View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.questionCellTitle);
        contentView = itemView.findViewById(R.id.questionCellContent);
        authorView = itemView.findViewById(R.id.questionCellAuthor);
    }

    public void setInfo(String title, String content, String author) {
        titleView.setText(title);
        contentView.setText(content);
        authorView.setText(author);
    }
}
