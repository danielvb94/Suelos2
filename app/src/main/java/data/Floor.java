package data;

/**
 * Created by danie on 03/12/2016.
 */

public class Floor {

    private Float lat;
    private Float lon;
    private String fecha;
    private String tipo;
    private String path;
    private int id;

    public Floor(Float lat, Float lon, String fecha, String tipo, String path, int id) {
        this.lat = lat;
        this.lon = lon;
        this.fecha = fecha;
        this.tipo = tipo;
        this.path = path;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }



}
