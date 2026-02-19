package com.afundacion.fp.coruna.dashboards.server.dtos;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardDto {

    private int id;
    private String title;
    private String description;
// 4🤖 private int questionsCount;
//10🐩 private int time;

    public DashboardDto(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.title = jsonObject.getString("title");
        this.description = jsonObject.getString("description");
// 4🤖  this.questionsCount = jsonObject.optInt("questionsCount", 0);
//10🐩  this.time = jsonObject.getInt("time");

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

// 4🤖 public int getQuestionsCount() { return questionsCount; }
//10🐩 public int getTime() { return time; }
}