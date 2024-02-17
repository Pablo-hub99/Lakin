package com.example.lakin;


import android.app.Activity;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class addplagas extends AppCompatActivity {

    // Declaración de elementos de la interfaz y Firebase




    private static final int SELECT_PHOTO = 1;
    private ImageView imageView;
    ImageView photo_Plaga, btnAtras;
    Button btn_add_photo, btn_delete_photo, btnsave;
    StorageReference storageReference;
    String storage_path = "Plagas/*";
    String Ruta_Foto;

    // Constantes para el manejo de selección de imágenes
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;

    private Uri image_url; // URL de la imagen seleccionada
    String photo = "photo"; // Cadena para identificar la foto
    String idd; // ID de la plaga
    ProgressDialog progressDialog;

    EditText name, DateStart, DateEnd, Description; // Campos de entrada para datos de la plaga
    private FirebaseFirestore mFirestore;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);


                String folderName = "Plagas";
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                String imageName = "tu_id_de_imagen.jpg"; // Reemplaza esto con el ID de tu imagen
                StorageReference imageRef = storageRef.child(folderName + "/" + imageName);

                UploadTask uploadTask = imageRef.putFile(selectedImage);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Manejar cualquier error
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // La imagen se ha cargado exitosamente
                    }
                });
                // Aquí termina el código para subir la imagen a Firebase

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addplagas_view); // Establece la vista desde addplagas_view.xml

        // Configuración del botón para volver atrás
        btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> {
            Intent intent = new Intent(addplagas.this, plagas.class);
            startActivity(intent);
            finish();
        });

        // Obtiene el ID de la plaga si se pasó desde otra actividad
        String id = getIntent().getStringExtra("id_Plagas");
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Vinculación de elementos de la interfaz con las variables correspondientes
        name = findViewById(R.id.editTextPlagueName);
        DateStart = findViewById(R.id.editTextStartDate);
        DateEnd = findViewById(R.id.editTextEndDate);
        Description = findViewById(R.id.editTextDescription);
        btn_delete_photo = findViewById(R.id.btn_remove_photo);
        btnsave = findViewById(R.id.btnSave);
        Button btnPhoto = findViewById(R.id.btn_photo);
        imageView = findViewById(R.id.plaga_photo);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });





        // Comprueba si se está creando una nueva plaga o actualizando una existente
        if (id == null || id.equals("")) { // Nueva plaga
            btnsave.setOnClickListener(v -> {
                // Obtiene los datos ingresados
                String namePlaga = name.getText().toString().trim();
                String StartPlaga = DateStart.getText().toString().trim();
                String EndPlaga = DateEnd.getText().toString().trim();
                String DessPlaga = Description.getText().toString().trim();

                // Verifica si algún campo está vacío
                if (namePlaga.isEmpty() || StartPlaga.isEmpty() || EndPlaga.isEmpty() || DessPlaga.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese los datos", Toast.LENGTH_SHORT).show();
                } else {
                    // Guarda la nueva plaga en Firestore
                    postPlaga(namePlaga, StartPlaga, EndPlaga, DessPlaga);
                }
            });

        } else { // Actualización de plaga existente
            idd = id;
            btnsave.setText("Actualizar");
            getPlaga(id); // Obtiene los datos de la plaga existente

            btnsave.setOnClickListener(view -> {
                // Obtiene los datos ingresados para actualizar
                String namePlaga = name.getText().toString().trim();
                String StartPlaga = DateStart.getText().toString().trim();
                String EndPlaga = DateEnd.getText().toString().trim();
                String DessPlaga = Description.getText().toString().trim();

                // Verifica si algún campo está vacío
                if (namePlaga.isEmpty() || StartPlaga.isEmpty() || EndPlaga.isEmpty() || DessPlaga.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese los datos", Toast.LENGTH_SHORT).show();
                } else {
                    // Actualiza la plaga en Firestore
                    updatePlaga(namePlaga, StartPlaga, EndPlaga, DessPlaga, id);
                }
            });
        }
    }

    // Método para actualizar los datos de una plaga existente en Firestore
    private void updatePlaga(String namePlaga, String StartPlaga, String EndPlaga, String DessPlaga, String id) {
        // Crea un mapa con los datos actualizados
        Map<String, Object> map = new HashMap<>();
        map.put("Descripcion", DessPlaga);
        map.put("Fecha_Fin", EndPlaga);
        map.put("Fecha_Inicio", StartPlaga);
        map.put("nombre", namePlaga);

        // Actualiza los datos en Firestore
        mFirestore.collection("Plagas").document(id).update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Plaga editada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(addplagas.this, plagas.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al editar la plaga", Toast.LENGTH_SHORT).show());
    }

    // Método para agregar una nueva plaga en Firestore
    private void postPlaga(String namePlaga, String StartPlaga, String EndPlaga, String DessPlaga) {
        Map<String, Object> map = new HashMap<>();
        map.put("Descripcion", DessPlaga);
        map.put("Fecha_Fin", EndPlaga);
        map.put("Fecha_Inicio", StartPlaga);
        map.put("nombre", namePlaga);

        // Agrega los datos a Firestore
        mFirestore.collection("Plagas").add(map)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Plaga creada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(addplagas.this, plagas.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al ingresar la plaga", Toast.LENGTH_SHORT).show());
    }

    // Método para obtener los datos de una plaga existente desde Firestore y mostrarlos en la interfaz
    private void getPlaga(String id) {
        mFirestore.collection("Plagas").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String namePlaga = documentSnapshot.getString("nombre");
                    String StartPlaga = documentSnapshot.getString("Fecha_Inicio");
                    String EndPlaga = documentSnapshot.getString("Fecha_Fin");
                    String DessPlaga = documentSnapshot.getString("Descripcion");
                    String photoPLaga = documentSnapshot.getString("photo");

                    // Muestra los datos en la interfaz
                    name.setText(namePlaga);
                    DateStart.setText(StartPlaga);
                    DateEnd.setText(EndPlaga);
                    Description.setText(DessPlaga);

                    // Intenta cargar la foto si existe
                    try {
                        if (!photoPLaga.equals("")) {
                            // Carga la foto en el ImageView usando Picasso
                            Toast toast = Toast.makeText(getApplicationContext(), "Cargando foto", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 200);
                            toast.show();
                            Picasso.get()
                                    .load(photoPLaga)
                                    .resize(700, 700)
                                    .into(photo_Plaga);
                        }
                    } catch (Exception e) {
                        Log.v("Error", "e: " + e);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error al obtener los datos de la plaga", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }



}










































       /* btn_add_photo.setOnClickListener(view -> uploadPhotoView());

        btn_delete_photo.setOnClickListener(view -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("photo", "");
            mFirestore.collection("Plagas").document(idd).update(map);
            Toast.makeText(addplagas.this, "Foto eliminada", Toast.LENGTH_SHORT).show();

        });
*/
 /*    private void uploadPhotoView() {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");

            startActivityForResult(i,COD_SEL_IMAGE);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == COD_SEL_IMAGE) {
                image_url = data.getData();
                // Muestra la imagen seleccionada en el ImageView
                Picasso.get().load(image_url).resize(700, 700).into(photo_Plaga);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subirPhoto(Uri image_url) {
        String rute_storage_photo = storage_path + "" + photo + "" + idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            if (uriTask.isSuccessful()){
                uriTask.addOnSuccessListener(uri -> {
                    String download_uri = uri.toString();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("photo", download_uri);
                    mFirestore.collection("Plagas").document(idd).update(map);
                    Toast.makeText(addplagas.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(addplagas.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
            }
        });
    }
    */