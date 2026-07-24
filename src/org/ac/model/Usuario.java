package org.ac.model;

//POJO: Nombre, atributos de clase, constructores, metodos(get y set) otros...
//encapssulación, herencia, polimorfismo, abstractión
public class Usuario {
    //id,nombre, rol
    private int id;
    private String username;
    private String rol;

    public Usuario() {
    }

    public Usuario(int id, String username, String rol) {
        this.id = id;
        this.username = username;
        this.rol = rol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    
           
}
