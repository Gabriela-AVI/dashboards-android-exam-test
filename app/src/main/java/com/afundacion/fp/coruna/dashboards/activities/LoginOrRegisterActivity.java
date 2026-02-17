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

public class LoginOrRegisterActivity extends AppCompatActivity {
    private Context context = this;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button loginButton;
    private Button registerButton;
    private RequestQueue queue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthManager.getInstance(this).isLoggedIn()) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login_or_register);
        usernameEditText = findViewById(R.id.editTextLoginUsername);
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        progressBar = findViewById(R.id.progressBarLogin);
        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);
        queue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUsernameOrPasswordEmpty()) {
                    try {
                        sendLogin();
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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

    private void sendLogin() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", usernameEditText.getText().toString());
        requestBody.put("password", passwordEditText.getText().toString());
        setUserInteractionEnabled(false);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.BASE_URL + "/api/v2/sessions",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        setUserInteractionEnabled(true);
                        try {
                            String token = jsonObject.getString("session_token");
                            AuthManager.getInstance(context).saveSessionToken(token);
                            goToMainActivity();
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
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
                                    message = "Ha habido un problema con el servidor. Inténtalo de nuevo más tarde";
                                    break;
                            }
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    }
                }
        );
        queue.add(request);
    }

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
                                    message = "Ha habido un problema con el servidor. Inténtalo de nuevo más tarde";
                                    break;
                            }
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(request);
    }

    private boolean isUsernameOrPasswordEmpty() {
        if ((usernameEditText.getText().toString().isEmpty()) || (passwordEditText.getText().toString().isEmpty())) {
            Toast.makeText(this, "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void setUserInteractionEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);
        if (enabled) {
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(context, DashboardsListActivity.class);
        context.startActivity(intent);
        finish();
    }
}
