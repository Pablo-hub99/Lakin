package com.example.lakin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lakin.adapter.InformesAdapter;
import com.example.lakin.adapter.PlanillaAdapter;
import com.example.lakin.modelo.InformeModel;
import com.example.lakin.modelo.PlagasModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Planilla extends AppCompatActivity {

    private FirebaseFirestore db;
    private PlanillaAdapter planillaAdapter;
    private InformesAdapter informesAdapter;
    private RecyclerView recyclerView;
    private RecyclerView informesRecyclerView;
    private ImageView btnAtrasPlanilla;
    private EditText txtUserName;
    private EditText editTextLote;
    private EditText editTextFinca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planilla_view);

        txtUserName = findViewById(R.id.editTextUser);
        editTextLote = findViewById(R.id.editTextLote);
        editTextFinca = findViewById(R.id.editTextFinca);

        // Obtener el nombre de usuario del Intent
        String userName = getIntent().getStringExtra("userName");

        // Mostrar el nombre de usuario en el EditText
        if (userName != null) {
            txtUserName.setText(userName);
            txtUserName.setFocusable(false);
            txtUserName.setClickable(false);
        }

        // Obtener y mostrar la fecha actual en el EditText de la fecha
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        EditText editTextFecha = findViewById(R.id.editTextFecha);
        editTextFecha.setText(currentDate);
        editTextFecha.setFocusable(false);
        editTextFecha.setClickable(false);

        // Configurar el botón de guardar local
        findViewById(R.id.btnGuardarlocal).setOnClickListener(v -> guardarEnLocal());

        // Configuración del botón para volver atrás
        btnAtrasPlanilla = findViewById(R.id.btnAtrasPlanilla);
        btnAtrasPlanilla.setOnClickListener(v -> {
            Intent intent = new Intent(Planilla.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar el RecyclerView para los informes
        informesRecyclerView = findViewById(R.id.InformesList);
        informesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener los informes del JSON y configurar el adaptador InformesAdapter
        informesAdapter = new InformesAdapter(obtenerInformesDesdeJson(), this, informe -> {
            // Aquí puedes definir el comportamiento cuando se hace clic en un informe
            Toast.makeText(Planilla.this, "Clic en el informe: " + informe.getId(), Toast.LENGTH_SHORT).show();
        });
        informesRecyclerView.setAdapter(informesAdapter);

        // Configurar el RecyclerView para las plagas
        recyclerView = findViewById(R.id.PlanillaList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear las opciones de FirestoreRecyclerOptions para las plagas
        FirestoreRecyclerOptions<PlagasModel> options = new FirestoreRecyclerOptions.Builder<PlagasModel>()
                .setQuery(db.collection("Plagas"), PlagasModel.class)
                .build();

        // Crear una instancia del adaptador PlanillaAdapter
        planillaAdapter = new PlanillaAdapter(options, this);

        // Configurar el adaptador en el RecyclerView
        recyclerView.setAdapter(planillaAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Iniciar la escucha del adaptador
        planillaAdapter.startListening();


    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detener la escucha del adaptador
        planillaAdapter.stopListening();

    }

    // Método para obtener los informes desde el JSON almacenado localmente
    private List<InformeModel> obtenerInformesDesdeJson() {
        List<InformeModel> informesList = new ArrayList<>();
        String jsonExistente = obtenerJsonExistente();

        try {
            JSONObject jsonObject = new JSONObject(jsonExistente);
            if (jsonObject.has("registros")) {
                JSONArray jsonArrayRegistros = jsonObject.getJSONArray("registros");
                for (int i = 0; i < jsonArrayRegistros.length(); i++) {
                    JSONObject informeObject = jsonArrayRegistros.getJSONObject(i);
                    String id = informeObject.optString("id");
                    String usuario = informeObject.optString("usuario");
                    String fechaHora = informeObject.optString("fechaHora");
                    informesList.add(new InformeModel(id, usuario, fechaHora));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return informesList;
    }
    // Método para guardar la información localmente en formato JSON
    private void guardarEnLocal() {
        String userName = txtUserName.getText().toString();
        String lote = editTextLote.getText().toString().trim();
        String finca = editTextFinca.getText().toString().trim();

        // Verificar si los campos están completos
        if (lote.isEmpty() || finca.isEmpty()) {
            Toast.makeText(Planilla.this, "Debe completar el lote y la finca", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String fechaHora = dateFormat.format(calendar.getTime());

        // Generar un ID único para este informe
        String idInforme = UUID.randomUUID().toString();

        // Obtener el JSON existente
        String jsonExistente = obtenerJsonExistente();

        try {
            JSONObject jsonObjectNuevo;

            // Si el JSON existente está vacío, crear un nuevo objeto JSON
            if (jsonExistente.isEmpty()) {
                jsonObjectNuevo = new JSONObject();
            } else {
                // Convertir el JSON existente a JSONObject
                jsonObjectNuevo = new JSONObject(jsonExistente);
            }

            // Crear un nuevo objeto JSON para los datos actuales
            JSONObject nuevoRegistro = new JSONObject();
            nuevoRegistro.put("id", idInforme); // Agregar el ID único
            nuevoRegistro.put("usuario", userName);
            nuevoRegistro.put("lote", lote);
            nuevoRegistro.put("finca", finca);
            nuevoRegistro.put("fechaHora", fechaHora);

            // Obtener las plagas seleccionadas del adaptador
            List<PlagasModel> plagasSeleccionadas = planillaAdapter.getPlagasSeleccionadas();

            // Crear un array JSON para las nuevas plagas seleccionadas
            JSONArray jsonArrayNuevasPlagas = new JSONArray();
            for (PlagasModel plaga : plagasSeleccionadas) {
                JSONObject jsonPlaga = new JSONObject();
                jsonPlaga.put("nombre", plaga.getNombre());
                // Agregar otras propiedades de la plaga según sea necesario
                jsonArrayNuevasPlagas.put(jsonPlaga);
            }

            // Agregar el array de plagas seleccionadas al nuevo registro
            nuevoRegistro.put("plagasSeleccionadas", jsonArrayNuevasPlagas);

            // Agregar el nuevo registro al JSON existente
            agregarNuevoRegistro(jsonObjectNuevo, nuevoRegistro);

            // Convertir el objeto JSON a cadena
            String jsonStr = jsonObjectNuevo.toString();

            // Guardar el JSON en SharedPreferences
            guardarJsonEnSharedPreferences(jsonStr);

            // Notificar al usuario que la información se ha guardado localmente
            Toast.makeText(Planilla.this, "JSON guardado localmente:\n" + jsonStr, Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(Planilla.this, "Error al guardar la información localmente", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para agregar un nuevo registro al JSON existente
    private void agregarNuevoRegistro(JSONObject jsonExistente, JSONObject nuevoRegistro) throws JSONException {
        // Verificar si hay registros existentes en el JSON
        if (jsonExistente.has("registros")) {
            // Obtener el array de registros existentes
            JSONArray jsonArrayRegistros = jsonExistente.getJSONArray("registros");
            // Agregar el nuevo registro al array de registros
            jsonArrayRegistros.put(nuevoRegistro);
        } else {
            // Crear un nuevo array de registros y agregar el nuevo registro
            JSONArray jsonArrayRegistros = new JSONArray();
            jsonArrayRegistros.put(nuevoRegistro);
            // Agregar el array de registros al JSON existente
            jsonExistente.put("registros", jsonArrayRegistros);
        }
    }


    // Método para obtener el JSON existente, si existe
    private String obtenerJsonExistente() {
        SharedPreferences sharedPreferences = getSharedPreferences("local_data", MODE_PRIVATE);
        return sharedPreferences.getString("json_data", "");
    }

    // Método para guardar el JSON en SharedPreferences
    private void guardarJsonEnSharedPreferences(String json) {
        SharedPreferences sharedPreferences = getSharedPreferences("local_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("json_data", json);
        editor.apply();
    }
}
