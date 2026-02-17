package com.afundacion.fp.coruna.dashboards.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.server.AuthManager;
import com.afundacion.fp.coruna.dashboards.server.Server;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Esta Activity permite:
 * - Iniciar sesión
 * - Registrar usuario
 *
 * Si ya hay sesión guardada → salta directamente a la pantalla principal.
 */
public class LoginOrRegisterActivity extends AppCompatActivity {

    private Context context = this;
    // Componentes UI
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button loginButton;
    private Button registerButton;
    private RequestQueue queue; // Cola de peticiones HTTP

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si el usuario ya está logueado → no mostramos esta pantalla
        if (AuthManager.getInstance(this).isLoggedIn()) {
            goToMainActivity();
            return;
        }

        // Cargamos layout
        setContentView(R.layout.activity_login_or_register);

        // Conectamos vistas
        usernameEditText = findViewById(R.id.editTextLoginUsername);
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        progressBar = findViewById(R.id.progressBarLogin);
        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);

        // Inicializamos Volley
        queue = Volley.newRequestQueue(this);

        // BOTÓN LOGIN
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Comprobamos que no estén vacíos
                if (!isUsernameOrPasswordEmpty()) {
                    try {
                        sendLogin();
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // BOTÓN REGISTER
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUsernameOrPasswordEmpty()) {
                    try {
                        sendRegister();
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /*
     * ENVÍA PETICIÓN LOGIN
     */
    private void sendLogin() throws JSONException {

        // Creamos JSON con username y password
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", usernameEditText.getText().toString());
        requestBody.put("password", passwordEditText.getText().toString());

        // Desactivamos botones y mostramos ProgressBar
        setUserInteractionEnabled(false);

        // Petición POST al endpoint de sesiones
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.BASE_URL + "/api/v2/sessions",
                requestBody,

                // RESPUESTA CORRECTA
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        setUserInteractionEnabled(true);

                        try {
                            // Extraemos el token de sesión
                            String token = jsonObject.getString("session_token");

                            // Guardamos token en AuthManager
                            AuthManager.getInstance(context).saveSessionToken(token);

                            // Ir a pantalla principal
                            goToMainActivity();

                        } catch (JSONException e) {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                // ERROR
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        setUserInteractionEnabled(true);

                        String message = "Error de conexión";

                        if (volleyError.networkResponse != null) {
                            switch (volleyError.networkResponse.statusCode) {

                                case 404:
                                    message = "Ese usuario no existe";
                                    break;

                                case 401:
                                    message = "Contraseña incorrecta";
                                    break;

                                default:
                                    message = "Ha habido un problema con el servidor.";
                                    break;
                            }
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Añadimos petición a la cola
        queue.add(request);
    }

    /*
     * ENVÍA PETICIÓN REGISTER
     */
    private void sendRegister() throws JSONException {

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", usernameEditText.getText().toString());
        requestBody.put("password", passwordEditText.getText().toString());

        setUserInteractionEnabled(false);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.BASE_URL + "/api/v2/users",
                requestBody,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        setUserInteractionEnabled(true);
                        Toast.makeText(context, "¡Cuenta creada! Ya puedes iniciar sesión", Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        setUserInteractionEnabled(true);

                        String message = "Error de conexión";

                        if (volleyError.networkResponse != null) {
                            switch (volleyError.networkResponse.statusCode) {

                                case 409:
                                    message = "Ya existe un usuario con ese nombre";
                                    break;

                                default:
                                    message = "Problema con el servidor";
                                    break;
                            }
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(request);
    }

    /*
     * METODO --> Comprueba si usuario o contraseña están vacíos
     */
    private boolean isUsernameOrPasswordEmpty() {

        if (usernameEditText.getText().toString().isEmpty()
                || passwordEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /*
     * METODO --> Activa o desactiva botones + ProgressBar
     */
    private void setUserInteractionEnabled(boolean enabled) {

        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);

        if (enabled) {
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /*
     * METODO --> Abre la pantalla principal y cierra esta Activity
     */
    private void goToMainActivity() {

        Intent intent = new Intent(context, DashboardsListActivity.class);
        context.startActivity(intent);
        // Cerramos LoginActivity para que no se pueda volver atrás
        finish();
    }
}