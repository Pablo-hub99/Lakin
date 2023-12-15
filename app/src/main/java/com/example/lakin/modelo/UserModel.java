package com.example.lakin.modelo;


public class UserModel {
    // Propiedades de un usuario
    String apellido, contraseña, correo, nombre, rol;

    // Constructor vacío requerido para Firebase
    public UserModel() {}

    // Constructor personalizado para inicializar propiedades
    public UserModel(String apellido, String contraseña, String correo, String nombre, String rol) {
        this.apellido = apellido;
        this.contraseña = contraseña;
        this.correo = correo;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Getter y Setter para la propiedad apellido
    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    // Getter y Setter para la propiedad contraseña
    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    // Getter y Setter para la propiedad correo
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    // Getter y Setter para la propiedad nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getter y Setter para la propiedad rol
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
