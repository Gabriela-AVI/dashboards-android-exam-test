package com.afundacion.fp.coruna.dashboards.server.dtos;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardDto {

    private int id;
    private String title;
    private String description;

    public DashboardDto(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.title = jsonObject.getString("title");
        this.description = jsonObject.getString("description");
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
}