package com.example.lakin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText txtUserRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Establece la vista desde activity_main.xml

        // Recibir el nombre de usuario del Intent
        String userName = getIntent().getStringExtra("userName");

        // Obtener el rol del usuario desde la actividad de inicio de sesión


        // Vinculación de elementos de la interfaz con las variables correspondientes
        ImageView btnUsuario = findViewById(R.id.btnUsuario); // Botón para acceder a la sección de usuarios
        ImageView btnPlagas = findViewById(R.id.btnplagas); // Botón para acceder a la sección de plagas
        ImageView btnplanilla = findViewById(R.id.btnplanilla); // Botón para acceder a la sección de Planilla
        ImageView btngrafica = findViewById(R.id.btngraficas);
        Button btnCerrar = findViewById(R.id.btnCerrarSesion); // Botón para cerrar sesión
        txtUserRol = findViewById(R.id.rol);


        String rol = getIntent().getStringExtra("userRole");
        txtUserRol.setText(rol);

        // Configuración de listeners para los botones

        btnUsuario.setOnClickListener(v -> {
            if ("Admin".equals(txtUserRol.getText().toString())) {

                // Acciones a realizar cuando se hace clic en el botón de usuarios
                Intent intent = new Intent(MainActivity.this, usuarios.class);
                startActivity(intent); // Inicia la actividad de usuarios
            } else {
                Toast.makeText(getApplicationContext(), "Solo los administradores pueden acceder.", Toast.LENGTH_SHORT).show();
            }
        });


        btnPlagas.setOnClickListener(v -> {
            if("Admin".equals(txtUserRol.getText().toString())) {
                // Acciones a realizar cuando se hace clic en el botón de plagas
                Intent intent = new Intent(MainActivity.this, plagas.class);
                startActivity(intent); // Inicia la actividad de plagas
            }else{
                Toast.makeText(getApplicationContext(), "Solo los administradores pueden acceder.", Toast.LENGTH_SHORT).show();
            }
        });

        btnplanilla.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de planilla
            Intent intent = new Intent(MainActivity.this, Planilla.class);
            intent.putExtra("userRole", rol);
            intent.putExtra("userName", userName);
            startActivity(intent); // Inicia la actividad de Planilla
        });

        btngrafica.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de planilla
            Intent intent = new Intent(MainActivity.this, grafica.class);
            startActivity(intent); // Inicia la actividad de
        });

        btnCerrar.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de cerrar sesión
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent); // Inicia la actividad de inicio de sesión (Login)
        });
    }
}
