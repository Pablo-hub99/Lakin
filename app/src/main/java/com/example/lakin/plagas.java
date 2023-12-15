package com.example.lakin;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;


import com.example.lakin.adapter.PlagasAdapter;
import com.example.lakin.modelo.PlagasModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class plagas extends AppCompatActivity {

    RecyclerView mRecycler; // RecyclerView para mostrar la lista de plagas
    PlagasAdapter mAdapter; // Adaptador para el RecyclerView
    FirebaseFirestore mFirestore; // Instancia de Firestore para acceder a la base de datos
    ImageView btnAtras; // Botón para volver atrás

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plagas_view); // Establece la vista desde plagas_view.xml

        // Configuración del botón para volver atrás
        btnAtras = findViewById(R.id.btnAtrasPlagas);
        btnAtras.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de volver atrás
            Intent intent = new Intent(plagas.this, MainActivity.class);
            startActivity(intent); // Inicia la actividad principal (MainActivity)
            finish(); // Finaliza la actividad actual
        });

        // Inicialización de Firestore y RecyclerView
        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.PlagasList);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Consulta Firestore para obtener la lista de plagas
        Query query = mFirestore.collection("Plagas");

        // Configuración del adaptador para el RecyclerView
        FirestoreRecyclerOptions<PlagasModel> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<PlagasModel>().setQuery(query, PlagasModel.class).build();
        mAdapter = new PlagasAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged(); // Notifica al adaptador sobre los cambios en los datos
        mRecycler.setAdapter(mAdapter); // Establece el adaptador en el RecyclerView

        // Botón para agregar una nueva plaga
        Button btnAñadirPlaga = findViewById(R.id.btnAñadirPlaga);
        btnAñadirPlaga.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el botón de agregar plaga
            Intent intent = new Intent(plagas.this, addplagas.class);
            startActivity(intent); // Inicia la actividad para agregar plaga (addplagas)
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
