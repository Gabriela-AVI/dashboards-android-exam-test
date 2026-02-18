package com.afundacion.fp.coruna.dashboards.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.afundacion.fp.coruna.dashboards.R;
import com.afundacion.fp.coruna.dashboards.server.AuthManager;

// 3🐧
public class ProfileActivity extends AppCompatActivity {
    private Button logoutButton; // 1🔒.Componente de interfaz
    private Context context = this; // Contexto de la Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        
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
    }
}