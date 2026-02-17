package com.afundacion.fp.coruna.dashboards.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.dialogs.NewQuestionDialog;
import com.afundacion.fp.coruna.dashboards.dialogs.NewQuestionDialogListener;
import com.afundacion.fp.coruna.dashboards.recyclerviews.QuestionsRecyclerAdapter;
import com.afundacion.fp.coruna.dashboards.server.Server;
import com.afundacion.fp.coruna.dashboards.server.dtos.QuestionDto;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DashboardQuestionsActivity extends AppCompatActivity {
    public static final String INTENT_DASHBOARD_ID = "D_ID";
    private Context context = this;
    private ProgressBar progressBar;
    private RecyclerView recyclerQuestions;
    private FloatingActionButton addQuestionButton;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBarDashboardQuestions);
        recyclerQuestions = findViewById(R.id.recyclerQuestions);
        addQuestionButton = findViewById(R.id.buttonAddQuestionDialog);

        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewQuestionDialog();
            }
        });

        loadQuestions();
    }

    private void loadQuestions() {
        setLoading(true);
        int dashboardId = getIntent().getIntExtra(INTENT_DASHBOARD_ID, -1);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.BASE_URL + "/api/v2/dashboards/" + dashboardId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        setLoading(false);
                        try {
                            List<QuestionDto> questionsList = new ArrayList<>();
                            JSONArray jsonQuestions = jsonObject.getJSONArray("questions");
                            for (int i = 0; i < jsonQuestions.length(); i++) {
                                JSONObject jsonQuestion = jsonQuestions.getJSONObject(i);
                                QuestionDto question = new QuestionDto(jsonQuestion);
                                questionsList.add(question);
                            }
                            refreshQuestionsRecyclerView(questionsList);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        setLoading(false);
                        Toast.makeText(context, "Error: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(request);
    }

    private void refreshQuestionsRecyclerView(List<QuestionDto> questions) {
        QuestionsRecyclerAdapter adapter = new QuestionsRecyclerAdapter(questions);
        recyclerQuestions.setAdapter(adapter);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showNewQuestionDialog() {
        int dashboardId = getIntent().getIntExtra(INTENT_DASHBOARD_ID, -1);
        NewQuestionDialog dialog = new NewQuestionDialog(context, dashboardId, new NewQuestionDialogListener() {
            @Override
            public void onCreateQuestionRequestHasBeenSent() {
                setLoading(true);
            }

            @Override
            public void onCreateQuestionRequestHasFinished() {
                setLoading(false);
                loadQuestions();
            }
        });
        dialog.buildDialog().show();
    }

    private void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerQuestions.setVisibility(View.INVISIBLE);
            addQuestionButton.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            recyclerQuestions.setVisibility(View.VISIBLE);
            addQuestionButton.setVisibility(View.VISIBLE);
        }
    }
}