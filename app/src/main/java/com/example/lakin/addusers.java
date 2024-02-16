package com.example.lakin;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class addusers extends AppCompatActivity {

    // Declaración de elementos de la interfaz y Firebase
    EditText name, surname, correo, pass, rol; // Campos de entrada para datos del usuario
    Button btnsave; // Botón para guardar o actualizar usuario
    ImageView btnAtras; // Botón para volver atrás
    FirebaseAuth mAuth; // Instancia de Firebase Authentication
    private FirebaseFirestore mFirestore; // Instancia de Firebase Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addusers_view); // Establece la vista desde addusers_view.xml

        // Configuración del botón para volver atrás
        btnAtras = findViewById(R.id.Atras);
        btnAtras.setOnClickListener(v -> {
            Intent intent = new Intent(addusers.this, usuarios.class);
            startActivity(intent);
            finish();
        });

        // Obtiene el ID del usuario si se pasó desde otra actividad
        String id = getIntent().getStringExtra("id_User");

        // Inicialización de Firebase
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Vinculación de elementos de la interfaz con las variables correspondientes
        name = findViewById(R.id.eTNombre);
        surname = findViewById(R.id.eTApellido);
        correo = findViewById(R.id.eTCorreo);
        pass = findViewById(R.id.eTpassword);
        rol = findViewById(R.id.eTRol);
        btnsave = findViewById(R.id.btnGuardar);

        // Comprueba si se está creando un nuevo usuario o actualizando uno existente
        if (id == null || id.equals("")) { // Nuevo usuario
            btnsave.setOnClickListener(v -> {
                // Obtiene los datos ingresados
                String nameUser = name.getText().toString().trim();
                String surnameUser = surname.getText().toString().trim();
                String correoUser = correo.getText().toString().trim();
                String passUser = pass.getText().toString().trim();
                String rolUser = rol.getText().toString().trim();

                // Verifica si algún campo está vacío
                if (nameUser.isEmpty() || surnameUser.isEmpty() || correoUser.isEmpty() || passUser.isEmpty() || rolUser.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese los datos", Toast.LENGTH_SHORT).show();
                } else {
                    // Guarda el nuevo usuario en Firebase
                    postUser(nameUser, surnameUser, correoUser, passUser, rolUser);
                }
            });
        } else { // Actualización de usuario existente
            btnsave.setText("Actualizar");
            getUser(id); // Obtiene los datos del usuario existente

            btnsave.setOnClickListener(view -> {
                // Obtiene los datos ingresados para actualizar
                String nameUser = name.getText().toString().trim();
                String surnameUser = surname.getText().toString().trim();
                String correoUser = correo.getText().toString().trim();
                String passUser = pass.getText().toString().trim();
                String rolUser = rol.getText().toString().trim();

                // Verifica si algún campo está vacío
                if (nameUser.isEmpty() || surnameUser.isEmpty() || correoUser.isEmpty() || passUser.isEmpty() || rolUser.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese los datos", Toast.LENGTH_SHORT).show();
                } else {
                    // Actualiza el usuario en Firebase
                    updateUser(nameUser, surnameUser, correoUser, passUser, rolUser, id);
                }
            });
        }
    }

    // Método para actualizar los datos de un usuario existente en Firestore
    private void updateUser(String nameUser, String surnameUser, String correoUser, String passUser, String rolUser, String id) {
        // Crea un mapa con los datos actualizados
        Map<String, Object> map = new HashMap<>();
        map.put("apellido", surnameUser);
        map.put("contraseña", passUser);
        map.put("correo", correoUser);
        map.put("nombre", nameUser);
        map.put("rol", rolUser);

        // Actualiza los datos en Firestore
        mFirestore.collection("Usuarios").document(id).update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Usuario editado exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(addusers.this, usuarios.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al editar usuario", Toast.LENGTH_SHORT).show());
    }

    // Método para agregar un nuevo usuario en Firebase Authentication y Firestore
    private void postUser(String nameUser, String surnameUser, String correoUser, String passUser, String rolUser) {
        if (!isValidEmail(correoUser)) {
            Toast.makeText(getApplicationContext(), "El correo no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passUser.length() < 6) {
            Toast.makeText(getApplicationContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correoUser, passUser)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Registro exitoso, ahora establece el nombre de usuario
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();

                                // Guarda los detalles del nuevo usuario en Firestore
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("apellido", surnameUser);
                                userData.put("contraseña", passUser);
                                userData.put("correo", correoUser);
                                userData.put("nombre", nameUser);
                                userData.put("rol", rolUser);

                                // Guarda los datos en Firestore
                                FirebaseFirestore.getInstance().collection("Usuarios")
                                        .document(userId)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            // Establece el nombre de usuario
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(nameUser)
                                                    .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            // Nombre de usuario actualizado correctamente
                                                            Toast.makeText(getApplicationContext(), "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(addusers.this, usuarios.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            // No se pudo actualizar el nombre de usuario
                                                            Toast.makeText(getApplicationContext(), "Error al establecer el nombre de usuario", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al ingresar usuario", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error al crear usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            // Manejar la excepción aquí
            Toast.makeText(getApplicationContext(), "Error general: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // Método para obtener la verificacion de que el correo intruducio es valido
    private boolean isValidEmail(String email) {
        // Utiliza una expresión regular para validar el formato del correo electrónico
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    // Método para obtener los datos de un usuario existente desde Firestore y mostrarlos en la interfaz
    private void getUser(String id) {
        mFirestore.collection("Usuarios").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String nameUser = documentSnapshot.getString("nombre");
                    String surnameUser = documentSnapshot.getString("apellido");
                    String correoUser = documentSnapshot.getString("correo");
                    String passUser = documentSnapshot.getString("contraseña");
                    String rolUser = documentSnapshot.getString("rol");

                    // Muestra los datos en la interfaz
                    name.setText(nameUser);
                    surname.setText(surnameUser);
                    correo.setText(correoUser);
                    pass.setText(passUser);
                    rol.setText(rolUser);
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
