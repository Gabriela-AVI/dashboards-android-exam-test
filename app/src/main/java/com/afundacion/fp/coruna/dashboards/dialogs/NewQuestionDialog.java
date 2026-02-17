package com.afundacion.fp.coruna.dashboards.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class NewQuestionDialog {
    private Context context;
    private RequestQueue queue;
    private EditText editTextNewQuestionTitle;
    private EditText editTextNewQuestionDescription;
    private int dashboardId;
    private NewQuestionDialogListener listener;

    public NewQuestionDialog(Context context, int dashboardId, NewQuestionDialogListener listener) {
        this.context = context;
        this.dashboardId = dashboardId;
        this.listener = listener;
        this.queue = Volley.newRequestQueue(context);
    }

    public AlertDialog buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_new_question, null, false);
        editTextNewQuestionTitle = view.findViewById(R.id.editTextNewQuestionTitle);
        editTextNewQuestionDescription = view.findViewById(R.id.editTextNewQuestionDescription);
        builder.setView(view);
        builder.setPositiveButton("Preguntar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void sendCreateQuestion() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("title", editTextNewQuestionTitle.getText().toString());
        requestBody.put("description", editTextNewQuestionDescription.getText().toString());
        listener.onCreateQuestionRequestHasBeenSent();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.BASE_URL + "/api/v2/dashboards/" + dashboardId + "/questions",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        listener.onCreateQuestionRequestHasFinished();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context, "Error: " + volleyError, Toast.LENGTH_SHORT).show();
                        listener.onCreateQuestionRequestHasFinished();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Session-Token", AuthManager.getInstance(context).getSessionToken());
                return headers;
            }
        };
        queue.add(request);
    }

    private boolean isTitleOrDescriptionEmpty() {
        if ((editTextNewQuestionTitle.getText().toString().isEmpty()) || (editTextNewQuestionDescription.getText().toString().isEmpty())) {
            Toast.makeText(context, "Introduce título y pregunta", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
