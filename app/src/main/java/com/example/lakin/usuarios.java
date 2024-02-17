package com.example.lakin;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;


import com.example.lakin.adapter.UserAdapter;
import com.example.lakin.modelo.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class usuarios extends AppCompatActivity {

    RecyclerView mRecycler; // RecyclerView para mostrar la lista de usuarios
    UserAdapter mAdapter; // Adaptador para el RecyclerView
    FirebaseFirestore mFirestore; // Instancia de Firestore para acceder a la base de datos
    ImageView btnAtras; // Botón para volver atrás

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_view); // Establece la vista desde user_view.xml

        String rol = getIntent().getStringExtra("userRole");

        // Configuración del botón para volver atrás
        btnAtras = findViewById(R.id.btnAtrasUser);
        btnAtras.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de volver atrás
            Intent intent = new Intent(usuarios.this, MainActivity.class);
            intent.putExtra("userRole", rol);
            startActivity(intent); // Inicia la actividad principal (MainActivity)
            finish(); // Finaliza la actividad actual
        });

        // Inicialización de Firestore y RecyclerView
        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.UserList);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Consulta Firestore para obtener la lista de usuarios
        Query query = mFirestore.collection("Usuarios");

        // Configuración del adaptador para el RecyclerView
        FirestoreRecyclerOptions<UserModel> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query, UserModel.class).build();
        mAdapter = new UserAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged(); // Notifica al adaptador sobre los cambios en los datos
        mRecycler.setAdapter(mAdapter); // Establece el adaptador en el RecyclerView

        // Botón para agregar un nuevo usuario
        Button btnAñadirUser = findViewById(R.id.btnAñadirUser);
        btnAñadirUser.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de agregar usuario
            Intent intent = new Intent(usuarios.this, addusers.class);
            intent.putExtra("userRole", rol);
            startActivity(intent); // Inicia la actividad para agregar usuario (addusers)
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening(); // Inicia la escucha del adaptador
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening(); // Detiene la escucha del adaptador
    }
}
