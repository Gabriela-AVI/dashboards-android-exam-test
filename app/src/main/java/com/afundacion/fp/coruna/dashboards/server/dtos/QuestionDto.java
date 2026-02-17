package com.afundacion.fp.coruna.dashboards.server.dtos;

import org.json.JSONException;
import org.json.JSONObject;

public class QuestionDto {

    private int id;
    private String title;
    private String description;
    private String author;

    public QuestionDto(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("question_id");
        this.title = jsonObject.getString("title");
        this.description = jsonObject.getString("description");
        this.author = jsonObject.getString("author");
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() { return author; }
}
