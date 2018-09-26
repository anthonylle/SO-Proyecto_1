/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulador;

/**
 *
 * @author FranM
 */
public class Mensaje {
    String idMensaje;
    String tipo;
    String largo;
    Integer prioridad;
    String texto;   //Definir si imagenes y videos se manejan solo con url.

    public Mensaje(String idMensaje, String tipo, String largo, Integer prioridad, String texto) {
        this.idMensaje = idMensaje;
        this.tipo = tipo;
        this.largo = largo;
        this.prioridad = prioridad;
        this.texto = texto;
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLargo() {
        return largo;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public String getTexto() {
        return texto;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setLargo(String largo) {
        this.largo = largo;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return "Mensaje{" + "idMensaje=" + idMensaje + ", tipo=" + tipo + ", largo=" + largo + ", prioridad=" + prioridad + ", texto=" + texto + '}';
    }
    
}
