package com.afundacion.fp.coruna.dashboards.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.recyclerviews.QuestionsClickListener;
import com.afundacion.fp.coruna.dashboards.recyclerviews.QuestionsRecyclerAdapter;
import com.afundacion.fp.coruna.dashboards.server.AuthManager;
import com.afundacion.fp.coruna.dashboards.server.Server;
import com.afundacion.fp.coruna.dashboards.server.dtos.QuestionDto;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 3🐧
public class ProfileActivity extends AppCompatActivity {
    private Button logoutButton; // 1🔒.Componente de interfaz
    private Context context = this; // Contexto de la Activity

    //8🌷
    private RecyclerView recyclerUserQuestions;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private List<QuestionDto> userQuestions = new ArrayList<>();
    private QuestionsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

//8🌷
        queue = Volley.newRequestQueue(this);
        recyclerUserQuestions = findViewById(R.id.recyclerUserQuestions);
        progressBar = findViewById(R.id.progressBarProfile);

        recyclerUserQuestions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionsRecyclerAdapter(userQuestions);
        recyclerUserQuestions.setAdapter(adapter);


        // 1🔒
        logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Borramos el token
                AuthManager.getInstance(context).logout();

                // Volvemos al Login
                Intent intent = new Intent(context, LoginOrRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        //.
        loadUserQuestions();
    }

//8🌷
    private void loadUserQuestions() {

        setLoading(true);

        String token = AuthManager.getInstance(context).getSessionToken();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.BASE_URL + "/api/v2/dashboards/2",
                null,

                // Si la respuesta es correcta
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        // Quitamos el modo carga
                        setLoading(false);

                        try {
                            // Lista donde guardaremos las preguntas convertidas
                            List<QuestionDto> questionsList = new ArrayList<>();

                            // Obtenemos el array JSON llamado "questions"
                            JSONArray jsonQuestions = jsonObject.getJSONArray("questions");

                            // Recorremos todas las preguntas recibidas
                            for (int i = 0; i < jsonQuestions.length(); i++) {

                                // Obtenemos cada objeto JSON individual
                                JSONObject jsonQuestion = jsonQuestions.getJSONObject(i);

                                // Convertimos JSON a objeto QuestionDto
                                QuestionDto question = new QuestionDto(jsonQuestion);

                                // Añadimos a la lista
                                questionsList.add(question);
                            }
                            // Actualizamos el RecyclerView con las preguntas
                            refreshQuestionsRecyclerView(questionsList);

                        } catch (JSONException e) {
                            // Si ocurre un error al parsear el JSON
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                // Si ocurre un error en la petición (por ejemplo sin internet)
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        setLoading(false);
                        Toast.makeText(context, "Error: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Añadimos la petición a la cola para que se ejecute
        queue.add(request);
    }

    //8🌷
    private void refreshQuestionsRecyclerView(List<QuestionDto> questionsList) {

        if (questionsList != null && !questionsList.isEmpty()) {

            Collections.sort(questionsList, new Comparator<QuestionDto>() {
                @Override
                public int compare(QuestionDto q1, QuestionDto q2) {
                    return q1.getTitle().compareToIgnoreCase(q2.getTitle()); // (A-Z)
                    //          return q2.getTitle().compareToIgnoreCase(q1.getTitle());    (Z-A)
                }
            });
        }

        // Guardamos lista original
        this.userQuestions.clear();
        this.userQuestions.addAll(questionsList);

        // Configuramos el listener de click
        adapter.setClickListener(new QuestionsClickListener() {
            @Override
            public void onQuestionClicked(QuestionDto question) {
                Intent intent = new Intent(context, AnswersActivity.class);
                intent.putExtra("QUESTION_ID", question.getId());
                context.startActivity(intent);
            }
        });

        recyclerUserQuestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerUserQuestions.setAdapter(adapter);
    }

    //8🌷
    private void setLoading(boolean loading) {

        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerUserQuestions.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            recyclerUserQuestions.setVisibility(View.VISIBLE);
        }
    }
}