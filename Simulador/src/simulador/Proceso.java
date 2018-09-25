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

    public Proceso(String name, Integer priority) {
        this.idProceso = name;
        this.colaMensajes = new ArrayList<Mensaje>();
    }

    public String getIdProceso() {
        return idProceso;
    }

    @Override
    public String toString() {
        return "Process{" + "name=" + idProceso + '}';
    }
}
