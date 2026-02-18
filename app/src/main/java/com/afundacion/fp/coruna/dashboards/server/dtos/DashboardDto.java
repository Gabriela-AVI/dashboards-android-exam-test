package com.afundacion.fp.coruna.dashboards.server.dtos;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardDto {

    private int id;
    private String title;
    private String description;
// 4🤖 private int questionsCount;

    public DashboardDto(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.title = jsonObject.getString("title");
        this.description = jsonObject.getString("description");
// 4🤖  this.questionsCount = jsonObject.optInt("questionsCount", 0);

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

// 4🤖 public int getQuestionsCount() {return questionsCount;}
}