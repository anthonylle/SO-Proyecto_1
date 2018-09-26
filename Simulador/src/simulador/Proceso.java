/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulador;

import java.util.ArrayList;
import simulador.Mensaje;

/**
 *
 * @author FranM
 */
public class Proceso {
    String idProceso;    
    Boolean sending;
    Boolean receiving;
    Boolean blocking;
    ArrayList<Mensaje> colaMensajes = new ArrayList<Mensaje>();

    public Proceso(String idProceso, Boolean sending, Boolean receiving, Boolean blocking) {
        this.idProceso = idProceso;
        this.sending = sending;
        this.receiving = receiving;
        this.blocking = blocking;
    }

    public String getIdProceso() {
        return idProceso;
    }

    public Boolean getSending() {
        return sending;
    }

    public Boolean getReceiving() {
        return receiving;
    }

    public Boolean getBlocking() {
        return blocking;
    }

    public ArrayList<Mensaje> getColaMensajes() {
        return colaMensajes;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }

    public void setSending(Boolean sending) {
        this.sending = sending;
    }

    public void setReceiving(Boolean receiving) {
        this.receiving = receiving;
    }

    public void setBlocking(Boolean blocking) {
        this.blocking = blocking;
    }

    public void setColaMensajes(ArrayList<Mensaje> colaMensajes) {
        this.colaMensajes = colaMensajes;
    }

    @Override
    public String toString() {
        return "Proceso{" + "idProceso=" + idProceso + ", sending=" + sending + ", receiving=" + receiving + ", blocking=" + blocking + ", colaMensajes=" + colaMensajes + '}';
    }    
}
