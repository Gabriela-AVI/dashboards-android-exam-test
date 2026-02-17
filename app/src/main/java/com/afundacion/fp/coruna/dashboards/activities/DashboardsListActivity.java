package com.afundacion.fp.coruna.dashboards.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.server.Server;
import com.afundacion.fp.coruna.dashboards.server.dtos.DashboardDto;
import com.afundacion.fp.coruna.dashboards.recyclerviews.DashboardClickListener;
import com.afundacion.fp.coruna.dashboards.recyclerviews.DashboardsRecyclerAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardsListActivity extends AppCompatActivity {
    private Context context = this;
    private ProgressBar progressBar;
    private RecyclerView recyclerDashboards;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboards);
        queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBarDashboards);
        recyclerDashboards = findViewById(R.id.recyclerDashboards);
        loadDashboards();
    }

    private void loadDashboards() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                Server.BASE_URL + "/api/v2/dashboards",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            List<DashboardDto> dashboardsList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                DashboardDto dashboard = new DashboardDto(jsonObject);
                                dashboardsList.add(dashboard);
                            }
                            refreshDashboardsRecyclerView(dashboardsList);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Error: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(request);
    }

    private void refreshDashboardsRecyclerView(List<DashboardDto> dashboards) {
        DashboardsRecyclerAdapter adapter = new DashboardsRecyclerAdapter(dashboards);
        adapter.setClickListener(new DashboardClickListener() {
            @Override
            public void onDashboardClicked(DashboardDto dashboard) {
                Intent intent = new Intent(context, DashboardQuestionsActivity.class);
                intent.putExtra(DashboardQuestionsActivity.INTENT_DASHBOARD_ID, dashboard.getId());
                context.startActivity(intent);
            }
        });
        recyclerDashboards.setAdapter(adapter);
        recyclerDashboards.setLayoutManager(new LinearLayoutManager(this));
    }
}