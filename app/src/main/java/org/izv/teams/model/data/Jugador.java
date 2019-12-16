package org.izv.teams.model.data;


public class Jugador {
    private long id, idequipo;
    private String nombre, apellidos;

    public long getId() {
        return id;
    }

    public Jugador setId(long id) {
        this.id = id;
        return this;
    }

    public long getIdequipo() {
        return idequipo;
    }

    public Jugador setIdequipo(long idequipo) {
        this.idequipo = idequipo;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public Jugador setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getApellidos() {
        return apellidos;
    }

    public Jugador setApellidos(String apellidos) {
        this.apellidos = apellidos;
        return this;
    }





}
