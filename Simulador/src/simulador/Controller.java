/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulador;

import Config_Enums.Addressing;
import Config_Enums.Format_Content;
import Config_Enums.Format_Length;
import Config_Enums.MailBox_Discipline;
import Config_Enums.Record_Message_Action;
import Config_Enums.Request_Action;
import Config_Enums.Sync_Receive;
import Config_Enums.Sync_Send;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author aleandro
 */
public class Controller {
    private static Controller instance = null;
    private Configuration configs;
    private ArrayList<Proceso> procesos;
    private ArrayList<MailBox> mailboxes;
    private ArrayList<Mensaje> systemMailBox;
    private ArrayList<Request> requests;
    private static Integer messageIDCounter;
    
    private Controller(){
        //configs = new Configuration();
        procesos = new ArrayList<Proceso>();
        mailboxes = new ArrayList<MailBox>();
        requests = new ArrayList<Request>();
        systemMailBox = new ArrayList<Mensaje>();
    }
    
    public static Controller getInstance(){
        if(instance == null)
            instance = new Controller();
        return instance;
    }
    
    public static Integer getMessageCounter(){
        messageIDCounter++;
        return messageIDCounter;
    }
    
    
    public void receiveMessage(String sourceID, String destinationID){
        Proceso sender = getProcess(destinationID); //NO SE SABE SI SIRVE EN MAILBOX
        Proceso receiver = getProcess(sourceID);
        
        if(Addressing.EXPLICIT.equals(configs.getAddressing()) || Addressing.IMPLICIT.equals(configs.getAddressing())){ // Direct Addressing
            if(isThereAPendingMessageOnSystemMailBox(destinationID, sourceID)){
                Mensaje message = getMessageFromSystemMailBox(sourceID, destinationID);

                if(configs.getSend().equals(Sync_Send.BLOCKING)){ // Hay q desbloquear el sender y quitar el sending
                    sender.setBlocking(false);
                }
                receiver.setReceiving(false);
                receiver.getRecordHistory().add(new MessageRecord(Record_Message_Action.RECEIVED, message));
                    
            }
            else{ // No hay un msj pendiente por recibir, entonces hay que hacer el request
                requests.add(new Request(sourceID, destinationID, Request_Action.RECEIVE));
                receiver.setReceiving(Boolean.TRUE);
                if(configs.getReceive().equals(Sync_Receive.BLOCKING))
                    receiver.setBlocking(true);
                System.out.println(requests.toString());
            
            }
        }else{ // Indirect Addressing
            if(isThereAPendingMessageOnMailBox(destinationID, sourceID)){
                Mensaje message = getMessageFromMailBox(sourceID, destinationID);
                
                if(configs.getSend().equals(Sync_Send.BLOCKING)){ // Hay q desbloquear el sender y quitar el sending
                    getProcess(message.getSourceID()).setBlocking(false);   
                }
                receiver.setReceiving(false);
                receiver.getRecordHistory().add(new MessageRecord(Record_Message_Action.RECEIVED, message));
             
            }
            else{ // No hay un msj pendiente por recibir, entonces hay que hacer un request
                requests.add(new Request(sourceID, destinationID, Request_Action.RECEIVE));
                receiver.setReceiving(Boolean.TRUE);
                if(configs.getReceive().equals(Sync_Receive.BLOCKING))
                    receiver.setBlocking(true);
                System.out.println(requests.toString());
                
            }        
        }
    }
    
    private boolean isThereAPendingMessageOnMailBox(String destinationID, String sourceID){
        if(isThereAnAccusementRequest(destinationID, sourceID))
            return true;
        
         for(Mensaje mensaje: getMailBox(destinationID).getBufferMensajes())
            if(mensaje.getDestinationID().equals(sourceID))
                return true;
        
        return false;
    }
    
    private Mensaje getMessageFromMailBox(String sourceID, String destinationID){
        Mensaje pickedMessage = null;
        for(Mensaje mensaje: getMailBox(destinationID).getBufferMensajes()){
            if(mensaje.getSourceID().equals(destinationID) && mensaje.getDestinationID().equals(sourceID)){
                pickedMessage = mensaje;
                getMailBox(destinationID).getBufferMensajes().remove(mensaje);
            }
        }
        return pickedMessage;
    }
    
    private Mensaje getMessageFromSystemMailBox(String destinationID, String sourceID){
        Mensaje pickedMessage = null;
        for(Mensaje mensaje: systemMailBox){
            if(mensaje.getSourceID().equals(destinationID) && mensaje.getDestinationID().equals(sourceID)){
                pickedMessage = mensaje;
                systemMailBox.remove(mensaje);
            }
        }
        return pickedMessage;
    }
    
    
    private boolean isThereAnAccusementRequest(String destinationID, String sourceID){
        Request req = null;
        for(Request request: requests){
            if(request.getDestinationID().equals(sourceID) && request.getSourceID().equals(destinationID))     
                req = request;
        }
        if(req != null)
            return true;
        return false;
    }
    
    private boolean isThereAPendingMessageOnSystemMailBox(String destinationID, String sourceID){
        if(isThereAnAccusementRequest(destinationID, sourceID))
            return true;
        
        for(Mensaje mensaje: systemMailBox)
            if(mensaje.getDestinationID().equals(sourceID))
                return true;
        
        return false;
    }
    
    public void sendMessage(Mensaje mensaje){
        if (Addressing.EXPLICIT.equals(configs.getAddressing()) || Addressing.IMPLICIT.equals(configs.getAddressing()))
            systemMailBox.add(mensaje);
        else
            getMailBox(mensaje.getDestinationID()).getBufferMensajes().add(mensaje);
        
            
        getProcess(mensaje.getSourceID()).getRecordHistory().add(new MessageRecord(Record_Message_Action.SENT, mensaje));
        
        // --------------------------------------------------------------------- hacer todas las validaciones posibles
        if(messageWasExpected(mensaje)){
            JOptionPane.showMessageDialog(null, "This message was expected", "Information", 1);
            //JOptionPane.showMessageDialog(null, "getRequest (destino): " + getRequestFromSource(mensaje.getDestinationID()).getSourceID(), "Titulo", 1);
            Proceso procesoDestino = getProcess(getRequestFromSource(mensaje.getDestinationID()).getSourceID());
            procesoDestino.getRecordHistory().add(new MessageRecord(Record_Message_Action.RECEIVED, mensaje));
            if(configs.receive.equals(Sync_Receive.BLOCKING)){
                procesoDestino.setBlocking(false);
            }
        }else{
            if(configs.send.equals(Sync_Send.BLOCKING)){
                String sourceID = mensaje.sourceID;
                Proceso toBlock = getProcess(sourceID);
                toBlock.setBlocking(true);
                requests.add(new Request(sourceID, mensaje.getDestinationID(), Request_Action.ACCUSEMENT));
            }
        }
    }
    
    
    private Request getRequestFromSource(String destinationID){
        for(Request request: requests){
            if(request.getSourceID().equals(destinationID)){
                //JOptionPane.showMessageDialog(null, "request: "+  request.toString(), "", 1);
                return request;
            }
        }
        //JOptionPane.showMessageDialog(null, "No se encontró", "Problema con el request", 0);

        return null;
    }
    
    private boolean messageWasExpected(Mensaje message){
        if (requests.isEmpty())
            return false;
        else{
            for(Request request: requests){
                if(request.getAction().equals(Request_Action.RECEIVE) && request.getSourceID().equals(message.getDestinationID()))
                    return true;
            }
            return false;
        }
    }

    
    public MailBox getMailBox(String mailBoxID){
        for(MailBox mail: mailboxes){
            if (mailBoxID.equals(mail.getIdMailBox()))
                return mail;
        }
        return null;
    }
    
    public Proceso getProcess(String processID){
        for(Proceso proceso: procesos){
            if (processID.equals(proceso.getIdProceso()))
                return proceso;
        }
        return null;
    }
    
    public void setConfiguration(Sync_Receive receive, Sync_Send send, Addressing addressing, Format_Content content, Format_Length length, MailBox_Discipline discipline){
        configs = new Configuration(receive, send, addressing, content, length, discipline);
    }
    
    public Configuration getConfiguration(){
        return configs;
    }
    
    public void addProcess(Proceso process){
        procesos.add(process);
    }
    
    public ArrayList<Proceso> getProcesses(){
        return procesos;
    }
    
    public void addMailBox(MailBox mailbox){
        mailboxes.add(mailbox);
    }
    
    public ArrayList<MailBox> getMailBoxes(){
        return mailboxes;
    }
    
    
}
