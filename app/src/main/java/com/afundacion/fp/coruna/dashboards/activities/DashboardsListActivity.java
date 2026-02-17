package com.afundacion.fp.coruna.dashboards.activities;

// IMPORTACIONES necesarias

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
import com.afundacion.fp.coruna.dashboards.server.AuthManager;
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

/*
 * Esta Activity:
 * - Hace una petición al servidor para obtener todos los dashboards.
 * - Convierte el JSON recibido en objetos DashboardDto.
 * - Los muestra en un RecyclerView.
 * - Cuando se pulsa uno, abre DashboardQuestionsActivity
 *   enviándole el ID del dashboard seleccionado.
 */
public class DashboardsListActivity extends AppCompatActivity {

    private Context context = this; // Contexto de la Activity
    private ProgressBar progressBar; // Componente de la interfaz
    private RecyclerView recyclerDashboards; // Componente de la interfaz
    private Button logoutButton; // 1🔒.Componente de interfaz
    private RequestQueue queue; // Cola de peticiones Volley

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargamos el layout XML
        setContentView(R.layout.activity_dashboards);

        // Inicializamos Volley
        queue = Volley.newRequestQueue(this);

        // Conectamos vistas con el XML
        progressBar = findViewById(R.id.progressBarDashboards);
        recyclerDashboards = findViewById(R.id.recyclerDashboards);
        logoutButton = findViewById(R.id.buttonLogout); // 1🔒

        // Cargamos los dashboards al iniciar la Activity
        loadDashboards();

        // 1🔒
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
    }

    /*
     * METODO --> realiza la petición GET para obtener todos los dashboards
     */
    private void loadDashboards() {

        // Creamos la petición GET al endpoint
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                Server.BASE_URL + "/api/v2/dashboards",
                null,

                // Si la petición tiene éxito
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        // Ocultamos el ProgressBar
                        progressBar.setVisibility(View.INVISIBLE);

                        try {

                            // Lista donde guardaremos los dashboards convertidos
                            List<DashboardDto> dashboardsList = new ArrayList<>();

                            // Recorremos el array JSON
                            for (int i = 0; i < jsonArray.length(); i++) {

                                // Obtenemos cada objeto JSON
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // Convertimos JSON a DashboardDto
                                DashboardDto dashboard = new DashboardDto(jsonObject);

                                // Añadimos a la lista
                                dashboardsList.add(dashboard);
                            }

                            // Actualizamos el RecyclerView
                            refreshDashboardsRecyclerView(dashboardsList);

                        } catch (JSONException e) {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                // Si hay error en la petición
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(context, "Error: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Añadimos la petición a la cola
        queue.add(request);
    }

    /*
     * METODO --> configura el RecyclerView
     */
    private void refreshDashboardsRecyclerView(List<DashboardDto> dashboards) {

        // Creamos el Adapter con la lista de dashboards
        DashboardsRecyclerAdapter adapter = new DashboardsRecyclerAdapter(dashboards);

        // Configuramos el listener de click
        adapter.setClickListener(new DashboardClickListener() {

            @Override
            public void onDashboardClicked(DashboardDto dashboard) {

                // Creamos un Intent para abrir la pantalla de preguntas
                Intent intent = new Intent(context, DashboardQuestionsActivity.class);

                // Enviamos el ID del dashboard seleccionado
                intent.putExtra(
                        DashboardQuestionsActivity.INTENT_DASHBOARD_ID,
                        dashboard.getId()
                );

                // Lanzamos la nueva Activity
                context.startActivity(intent);
            }
        });

        // Asignamos adapter al RecyclerView
        recyclerDashboards.setAdapter(adapter);

        // Indicamos que será una lista vertical
        recyclerDashboards.setLayoutManager(new LinearLayoutManager(this));
    }
}