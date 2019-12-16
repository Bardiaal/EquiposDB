package org.izv.teams.model.data;


public class Equipo {
    private long id, aforo;
    private String nombre, ciudad, estadio;

    public long getId() {
        return id;
    }

    public Equipo setId(long id) {
        this.id = id;
        return this;
    }

    public long getAforo() {
        return aforo;
    }

    public Equipo setAforo(long aforo) {
        this.aforo = aforo;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public Equipo setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getCiudad() {
        return ciudad;
    }

    public Equipo setCiudad(String ciudad) {
        this.ciudad = ciudad;
        return this;
    }

    public String getEstadio() {
        return estadio;
    }

    public Equipo setEstadio(String estadio) {
        this.estadio = estadio;
        return this;
    }


    @Override
    public String toString() {
        return "Equipo{" +
                "id=" + id +
                ", aforo=" + aforo +
                ", nombre='" + nombre + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", estadio='" + estadio + '\'' +
                '}';
    }
}
