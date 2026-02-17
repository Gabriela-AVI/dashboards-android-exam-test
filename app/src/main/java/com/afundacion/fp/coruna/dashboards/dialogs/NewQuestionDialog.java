package com.afundacion.fp.coruna.dashboards.dialogs;

// IMPORTACIONES necesarias

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.server.AuthManager;
import com.afundacion.fp.coruna.dashboards.server.Server;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Esta clase representa un diálogo personalizado
 * para crear una nueva pregunta dentro de un dashboard.
 *
 * No es una Activity.
 * Es una clase auxiliar que construye un AlertDialog.
 */
public class NewQuestionDialog {

    private Context context; // Contexto desde donde se crea el diálogo
    private RequestQueue queue; // Cola de peticiones HTTP
    private EditText editTextNewQuestionTitle; // Campo del formulario
    private EditText editTextNewQuestionDescription; // Campo del formulario
    private int dashboardId; // ID del dashboard donde se va a crear la pregunta
    private NewQuestionDialogListener listener; // Listener para avisar a la Activity cuando empieza/termina la petición

    /*
     * Constructor
     * Recibe:
     * - Context
     * - ID del dashboard
     * - Listener para comunicar eventos
     */
    public NewQuestionDialog(Context context, int dashboardId, NewQuestionDialogListener listener) {
        this.context = context;
        this.dashboardId = dashboardId;
        this.listener = listener;

        // Inicializamos Volley
        this.queue = Volley.newRequestQueue(context);
    }

    /*
     * METODO --> Construye el AlertDialog
     */
    public AlertDialog buildDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflamos el layout personalizado del diálogo
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_new_question, null, false);

        // Conectamos los EditText del layout
        editTextNewQuestionTitle = view.findViewById(R.id.editTextNewQuestionTitle);
        editTextNewQuestionDescription = view.findViewById(R.id.editTextNewQuestionDescription);

        builder.setView(view);

        // Botón positivo (Preguntar)
        builder.setPositiveButton("Preguntar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Validamos que no estén vacíos
                if (!isTitleOrDescriptionEmpty()) {
                    try {
                        sendCreateQuestion();
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return builder.create();
    }

    /*
     * ENVÍA PETICIÓN POST para crear la pregunta
     */
    private void sendCreateQuestion() throws JSONException {

        // Creamos JSON con los datos de la pregunta
        JSONObject requestBody = new JSONObject();
        requestBody.put("title", editTextNewQuestionTitle.getText().toString());
        requestBody.put("description", editTextNewQuestionDescription.getText().toString());

        // Avisamos a la Activity que empieza la petición
        listener.onCreateQuestionRequestHasBeenSent();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.BASE_URL + "/api/v2/dashboards/" + dashboardId + "/questions",
                requestBody,

                // RESPUESTA CORRECTA
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        // Avisamos que terminó correctamente
                        listener.onCreateQuestionRequestHasFinished();
                    }
                },

                // ERROR
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(context, "Error: " + volleyError, Toast.LENGTH_SHORT).show();

                        // Aunque falle, avisamos que terminó
                        listener.onCreateQuestionRequestHasFinished();
                    }
                }
        ) {
            /*
             * Sobrescribimos getHeaders()
             * para añadir el token de sesión
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();

                // Añadimos el token guardado en AuthManager
                headers.put("Session-Token",
                        AuthManager.getInstance(context).getSessionToken());

                return headers;
            }
        };
        // Añadimos petición a la cola
        queue.add(request);
    }

    /*
     * METODO --> Valida que título y descripción no estén vacíos
     */
    private boolean isTitleOrDescriptionEmpty() {

        if (editTextNewQuestionTitle.getText().toString().isEmpty()
                || editTextNewQuestionDescription.getText().toString().isEmpty()) {
            Toast.makeText(context, "Introduce título y pregunta", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}