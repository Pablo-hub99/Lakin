package com.example.lakin;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Login extends AppCompatActivity {

    // Declaración de variables
    Button btn_login; // Botón de inicio de sesión
    EditText email, password; // Campos de entrada para correo y contraseña
    FirebaseAuth mAuth; // Instancia de Firebase Authentication

    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        // Inicializar FirebaseFirestore
        mFirestore = FirebaseFirestore.getInstance();

        // Obtener la fecha actual
        Date currentDate = new Date();

        // Obtener las plagas que están activas en la fecha actual
        mFirestore.collection("Plagas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Iterar sobre los documentos de las plagas encontradas
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Obtener la fecha de inicio y fin de la plaga
                        String startDateStr = documentSnapshot.getString("Fecha_Inicio");
                        String endDateStr = documentSnapshot.getString("Fecha_Fin");

                        // Parsear las fechas al formato adecuado
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                        try {
                            Date startDate = dateFormat.parse(startDateStr);
                            Date endDate = dateFormat.parse(endDateStr);

                            // Verificar si la fecha actual está dentro del rango de la plaga
                            if (startDate != null && endDate != null &&
                                    currentDate.compareTo(startDate) >= 0 && currentDate.compareTo(endDate) <= 0) {
                                // Obtener la descripción de la plaga
                                String descripcionPlaga = documentSnapshot.getString("Descripcion");

                                // Mostrar la descripción de la plaga al usuario
                                Toast.makeText(Login.this, "Descripción de la plaga: " + descripcionPlaga, Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            // Manejar errores de análisis de fecha
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar errores
                    Toast.makeText(Login.this, "Error al obtener las plagas", Toast.LENGTH_SHORT).show();
                });

        mAuth = FirebaseAuth.getInstance(); // Inicializa la instancia de Firebase Authentication

        // Vincula los elementos de la interfaz con las variables correspondientes
        email = findViewById(R.id.correo); // Obtiene el campo de correo electrónico
        password = findViewById(R.id.contrasena); // Obtiene el campo de contraseña
        btn_login = findViewById(R.id.btn_ingresar); // Obtiene el botón de inicio de sesión

        // Configura un listener para el botón de inicio de sesión
        btn_login.setOnClickListener(view -> {
            // Obtiene el correo y contraseña ingresados
            String emailUser = email.getText().toString().trim();
            String passUser = password.getText().toString().trim();

            // Verifica si el correo y contraseña están vacíos
            if (emailUser.isEmpty() || passUser.isEmpty()){
                // Muestra un mensaje si ambos campos están vacíos
                Toast.makeText(Login.this, "Ingresar los datos", Toast.LENGTH_SHORT).show();
            } else {
                // Llama al método para iniciar sesión
                loginUser(emailUser, passUser);
            }
        });
    }

    // Método para iniciar sesión con Firebase Authentication
    private void loginUser(String emailUser, String passUser) {
        // Intenta iniciar sesión con el correo y contraseña proporcionados
        mAuth.signInWithEmailAndPassword(emailUser, passUser)
                .addOnCompleteListener(task -> {
                    // Verifica si la autenticación fue exitosa
                    if (task.isSuccessful()){
                        // Obtiene el usuario actual
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                        if (currentUser != null ) {
                            // Obtiene el ID del usuario
                            String userId = currentUser.getUid();

                            // Realiza una consulta en Firestore para obtener el documento del usuario
                            FirebaseFirestore.getInstance().collection("Usuarios")
                                    .document(userId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        // Verifica si el documento existe y contiene el campo "rol"
                                        if (documentSnapshot.exists() && documentSnapshot.contains("rol")) {
                                            // Obtiene el rol del usuario
                                            String userRole = documentSnapshot.getString("rol");

                                            // Finaliza esta actividad y abre la MainActivity
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            Global.Name = currentUser.getDisplayName();
                                            Global.Rol = userRole;
                                            intent.putExtra("userName", currentUser.getDisplayName());
                                            intent.putExtra("userRole", userRole); // Pasar el rol del usuario como extra
                                            startActivity(intent);
                                            finish();
                                            // Muestra un mensaje de bienvenida
                                            Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // El documento no existe o no contiene el campo "rol"
                                            Toast.makeText(Login.this, "El usuario no tiene un rol definido", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error al obtener el documento del usuario
                                        Toast.makeText(Login.this, "Error al obtener información del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Muestra un mensaje de error si la autenticación falla
                        Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                })
                // Agrega un listener para manejar el fallo en la autenticación
                .addOnFailureListener(e -> Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show());
    }


}
