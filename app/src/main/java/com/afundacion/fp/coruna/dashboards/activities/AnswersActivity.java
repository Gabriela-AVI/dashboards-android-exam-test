package com.afundacion.fp.coruna.dashboards.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.server.Server;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

// 5🐸
public class AnswersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_answers);
        TextView texto = findViewById(R.id.texto);

        int id = getIntent().getIntExtra("QUESTION_ID", -1);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.BASE_URL + "/api/v2/questions/" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            int numAnswers = jsonObject.getJSONArray("answers").length();
                            texto.setText("Respuestas: " + numAnswers);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        throw new RuntimeException(volleyError);
                    }
                });
    }
}