/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulador;

import java.util.ArrayList;

/**
 *
 * @author FranM
 */
public class MailBox {
    String idMailBox;
    Boolean privacidad;
    ArrayList<Proceso> suscritos = new ArrayList<Proceso>();

    public MailBox(String idMailBox, Boolean privacidad) {
        this.idMailBox = idMailBox;
        this.privacidad = privacidad;
    }

    public String getIdMailBox() {
        return idMailBox;
    }

    public Boolean getPrivacidad() {
        return privacidad;
    }

    public ArrayList<Proceso> getSuscritos() {
        return suscritos;
    }

    public void setIdMailBox(String idMailBox) {
        this.idMailBox = idMailBox;
    }

    public void setPrivacidad(Boolean privacidad) {
        this.privacidad = privacidad;
    }

    public void setSuscritos(ArrayList<Proceso> suscritos) {
        this.suscritos = suscritos;
    }

    @Override
    public String toString() {
        return "MailBox{" + "idMailBox=" + idMailBox + ", privacidad=" + privacidad + ", suscritos=" + suscritos + '}';
    }
}
