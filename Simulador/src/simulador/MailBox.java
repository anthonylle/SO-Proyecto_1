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
}
