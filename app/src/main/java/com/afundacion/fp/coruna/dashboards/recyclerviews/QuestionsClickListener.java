package com.afundacion.fp.coruna.dashboards.recyclerviews;

import com.afundacion.fp.coruna.dashboards.server.dtos.QuestionDto;

//5🐸
public interface QuestionsClickListener {
    void onQuestionClicked(QuestionDto question);
}
