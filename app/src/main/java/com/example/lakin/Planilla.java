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
import com.example.lakin.modelo.PlanillaDataModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.common.returnsreceiver.qual.This;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private EditText editTextFecha;

    private String name ;
    private String lote ;
    private String finca ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planilla_view);

        txtUserName = findViewById(R.id.editTextUser);
        editTextLote = findViewById(R.id.editTextLote);
        editTextFinca = findViewById(R.id.editTextFinca);
        editTextFecha = findViewById(R.id.editTextFecha);


        // Obtener el nombre de usuario del Intent

        if( getIntent().getStringExtra("userLote") != null){
            editTextLote.setText(getIntent().getStringExtra("userLote"));
            editTextFinca.setText(getIntent().getStringExtra("userFinca"));
        }

        // Mostrar el nombre de usuario en el EditText
        if (Global.Name != null) {
            txtUserName.setText(Global.Name);
            txtUserName.setFocusable(false);
            txtUserName.setClickable(false);
        }

        // Obtener y mostrar la fecha actual en el EditText de la fecha
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        editTextFecha.setText(currentDate);
        editTextFecha.setFocusable(false);
        editTextFecha.setClickable(false);

        // Configurar el botón de guardar local
        findViewById(R.id.btnGuardarlocal).setOnClickListener(v -> guardarEnLocal());

        //Configurar boton de subir datos
        findViewById(R.id.btnGuardarBd).setOnClickListener(v -> subirDatosAFirestore());

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
        // Aquí puedes definir el comportamiento cuando se hace clic en un informe
        informesAdapter = new InformesAdapter(obtenerInformesDesdeJson(), this, this::mostrarDetallesInforme);
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
    private void mostrarDetallesInforme(InformeModel informe) {
        // Obtener el ID del informe seleccionado
        String idInforme = informe.getId();

        // Obtener los datos del informe seleccionado en el JSON
        String jsonExistente = obtenerJsonExistente();
        try {
            JSONObject jsonObject = new JSONObject(jsonExistente);
            if (jsonObject.has("registros")) {
                JSONArray jsonArrayRegistros = jsonObject.getJSONArray("registros");
                for (int i = 0; i < jsonArrayRegistros.length(); i++) {
                    JSONObject informeObject = jsonArrayRegistros.getJSONObject(i);
                    String id = informeObject.optString("id");
                    if (id.equals(idInforme)) {
                        // Se encontró el informe correspondiente
                        String usuario = informeObject.optString("usuario");
                        String fechaHora = informeObject.optString("fechaHora");
                        String lote = informeObject.optString("lote");
                        String finca = informeObject.optString("finca");
                        // Actualizar los EditText con los datos del informe
                        txtUserName.setText(usuario);
                        editTextFecha.setText(fechaHora);
                        editTextLote.setText(lote);
                        editTextFinca.setText(finca);
                        // Además, puedes cargar los datos de las plagas relacionadas con este informe si es necesario
                        // Recuperar el array de plagas seleccionadas
                        JSONArray jsonArrayPlagas = informeObject.optJSONArray("plagasSeleccionadas");
                        if (jsonArrayPlagas != null) {
                            List<String> plagas = new ArrayList<>();
                            for (int j = 0; j < jsonArrayPlagas.length(); j++) {
                                JSONObject plagaObject = jsonArrayPlagas.getJSONObject(j);
                                String nombrePlaga = plagaObject.optString("nombre");
                                plagas.add(nombrePlaga);
                            }

                        }
                        // Salir del bucle una vez que se encuentre el informe correspondiente
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        name = txtUserName.getText().toString();
        lote = editTextLote.getText().toString().trim();
        finca = editTextFinca.getText().toString().trim();

        // Verificar si los campos están completos
        if (lote.isEmpty() || finca.isEmpty()) {
            Toast.makeText(Planilla.this, "Debe completar el lote y la finca", Toast.LENGTH_SHORT).show();
            return;
        }
        if (planillaAdapter.getPlagasSeleccionadas().isEmpty()) {
            Toast.makeText(Planilla.this, "Debe seleccionar al menos una plaga", Toast.LENGTH_SHORT).show();
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
            nuevoRegistro.put("usuario", name);
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
            Toast.makeText(Planilla.this, "Información guardada correctamente.", Toast.LENGTH_LONG).show();
            // Notificar al adaptador que los datos han cambiado
            finish();
            Intent intent = new Intent(Planilla.this, Planilla.class);
            intent.putExtra("userName", name);
            intent.putExtra("userLote", lote);
            intent.putExtra("userFinca", finca);
            startActivity(intent);

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

    // Método para subir los datos del JSON a Firestore
    private void subirDatosAFirestore() {
        name = txtUserName.getText().toString();
        lote = editTextLote.getText().toString().trim();
        finca = editTextFinca.getText().toString().trim();
        // Obtener el JSON almacenado localmente
        String jsonExistente = obtenerJsonExistente();


        try {
            // Convertir el JSON a JSONObject
            JSONObject jsonObject = new JSONObject(jsonExistente);

            // Verificar si el JSON contiene la clave "registros"
            if (jsonObject.has("registros")) {
                // Obtener el array de registros
                JSONArray jsonArrayRegistros = jsonObject.getJSONArray("registros");

                // Iterar sobre cada registro en el array
                for (int i = 0; i < jsonArrayRegistros.length(); i++) {
                    // Obtener el objeto JSON del registro actual
                    JSONObject registro = jsonArrayRegistros.getJSONObject(i);

                    // Crear una instancia de PlanillaData y asignar los valores del JSON
                    PlanillaDataModel planillaData = new PlanillaDataModel();
                    planillaData.setUsuario(registro.optString("usuario"));
                    planillaData.setLote(registro.optString("lote"));
                    planillaData.setFinca(registro.optString("finca"));
                    planillaData.setFechaHora(registro.optString("fechaHora"));

                    // Crear un array JSON para los nombres de las plagas
                    JSONArray jsonArrayPlagas = registro.optJSONArray("plagasSeleccionadas");
                    List<String> nombresPlagas = new ArrayList<>();
                    if (jsonArrayPlagas != null) {
                        for (int j = 0; j < jsonArrayPlagas.length(); j++) {
                            JSONObject plaga = jsonArrayPlagas.getJSONObject(j);
                            nombresPlagas.add(plaga.optString("nombre"));
                        }
                    }
                    planillaData.setPlagasSeleccionadas(nombresPlagas);

                    // Subir los datos a Firestore
                    db.collection("Planilla").document()
                            .set(planillaData)
                            .addOnSuccessListener(aVoid -> {
                                // Éxito al subir los datos a Firestore
                                Toast.makeText(Planilla.this, "Datos subidos correctamente", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Error al subir los datos a Firestore
                                Toast.makeText(Planilla.this, "Error al subir datos " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }

                // Una vez que se han subido todos los registros, puedes limpiar el JSON almacenado localmente si es necesario
                limpiarJsonLocal();
                finish();
                Intent intent = new Intent(Planilla.this, Planilla.class);
                intent.putExtra("userName", name);
                intent.putExtra("userLote", lote);
                intent.putExtra("userFinca", finca);
                startActivity(intent);

            } else {
                Toast.makeText(Planilla.this, "El JSON no contiene la clave 'registros'", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(Planilla.this, "Error al parsear el JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Método para limpiar el JSON almacenado localmente después de subir los datos a Firestore
    private void limpiarJsonLocal() {
        // Limpiar el JSON almacenado localmente
        SharedPreferences sharedPreferences = getSharedPreferences("local_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("json_data");
        editor.apply();
    }
}

