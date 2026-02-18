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
import com.afundacion.fp.coruna.dashboards.dialogs.NewQuestionDialog;
import com.afundacion.fp.coruna.dashboards.dialogs.NewQuestionDialogListener;
import com.afundacion.fp.coruna.dashboards.recyclerviews.DashboardClickListener;
import com.afundacion.fp.coruna.dashboards.recyclerviews.QuestionsClickListener;
import com.afundacion.fp.coruna.dashboards.recyclerviews.QuestionsRecyclerAdapter;
import com.afundacion.fp.coruna.dashboards.server.Server;
import com.afundacion.fp.coruna.dashboards.server.dtos.DashboardDto;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Esta Activity muestra todas las preguntas de un Dashboard concreto.
 * Recibe el ID del dashboard por Intent.
 * Hace una petición al servidor para obtener sus preguntas.
 * Las muestra en un RecyclerView.
 * Permite añadir nuevas preguntas mediante un diálogo.
 */
public class DashboardQuestionsActivity extends AppCompatActivity {

    public static final String INTENT_DASHBOARD_ID = "D_ID";  // Clave para recibir el ID del dashboard desde otra Activity
    private Context context = this; // Guardamos el contexto de la Activity
    private ProgressBar progressBar; // Componente de la interfaz
    private RecyclerView recyclerQuestions;  // Componente de la interfaz
    private FloatingActionButton addQuestionButton;  // Componente de la interfaz
    private RequestQueue queue; // Cola de peticiones Volley (para peticiones HTTP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargamos el layout XML asociado a esta Activity
        setContentView(R.layout.activity_questions);

        // Inicializamos la cola de peticiones Volley
        queue = Volley.newRequestQueue(this);

        // Conectamos los componentes del XML con el código Java
        progressBar = findViewById(R.id.progressBarDashboardQuestions);
        recyclerQuestions = findViewById(R.id.recyclerQuestions);
        addQuestionButton = findViewById(R.id.buttonAddQuestionDialog);

        // Cuando se pulsa el botón flotante, se abre el diálogo para crear pregunta
        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewQuestionDialog();
            }
        });

        // Nada más iniciar la Activity, cargamos las preguntas
        loadQuestions();
    }

    /*
     * METODO --> carga las preguntas del dashboard desde el servidor
     */
    private void loadQuestions() {

        // Activamos modo carga (muestra ProgressBar y oculta contenido)
        setLoading(true);

        // Recuperamos el ID del dashboard que nos enviaron por Intent
        int dashboardId = getIntent().getIntExtra(INTENT_DASHBOARD_ID, -1);

        // Creamos la petición GET al servidor
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.BASE_URL + "/api/v2/dashboards/" + dashboardId,
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

    /*
     * METODO --> configura el RecyclerView con la lista de preguntas
     */
    private void refreshQuestionsRecyclerView(List<QuestionDto> questions) {

        // 2🆎 
        // Comentario --> getTitle
        // Usuario --> getAuthor
        if (questions != null && !questions.isEmpty()) {

            Collections.sort(questions, new Comparator<QuestionDto>() {
                @Override
                public int compare(QuestionDto q1, QuestionDto q2) {
                    return q1.getTitle().compareToIgnoreCase(q2.getTitle()); // (A-Z)
                //  return q2.getTitle().compareToIgnoreCase(q1.getTitle());    (Z-A)
                }
            });
        }
        //.

        QuestionsRecyclerAdapter adapter = new QuestionsRecyclerAdapter(questions);

        //5🐸
        // Configuramos el listener de click
        adapter.setClickListener(new QuestionsClickListener() {
            @Override
            public void onQuestionClicked(QuestionDto question) {
                Intent intent = new Intent(context, AnswersActivity.class);
                intent.putExtra("QUESTION_ID", question.getId());
                context.startActivity(intent);
            }
        });
        //.
        recyclerQuestions.setAdapter(adapter);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(this));
    }


    /*
     * METODO --> Muestra el diálogo para crear una nueva pregunta
     */
    private void showNewQuestionDialog() {

        // Recuperamos el ID del dashboard actual
        int dashboardId = getIntent().getIntExtra(INTENT_DASHBOARD_ID, -1);

        // Creamos el diálogo pasando:
        // - contexto
        // - id del dashboard
        // - listener para saber cuándo empieza y termina la petición
        NewQuestionDialog dialog = new NewQuestionDialog(context, dashboardId, new NewQuestionDialogListener() {

            // Cuando empieza la petición de crear pregunta
            @Override
            public void onCreateQuestionRequestHasBeenSent() {
                setLoading(true);
            }
            // Cuando termina la petición
            @Override
            public void onCreateQuestionRequestHasFinished() {
                setLoading(false);

                // Volvemos a cargar las preguntas para refrescar la lista
                loadQuestions();
            }
        });
        // Construimos y mostramos el diálogo
        dialog.buildDialog().show();
    }

    /*
     * Activa o desactiva el modo carga
     * Si loading = true:
     *  - Se muestra el ProgressBar
     *  - Se oculta el RecyclerView
     *  - Se oculta el botón flotante
     */
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