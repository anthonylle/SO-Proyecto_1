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
    public static Integer messageIDCounter;
    
    private Controller(){
        //configs = new Configuration();
        procesos = new ArrayList<Proceso>();
        mailboxes = new ArrayList<MailBox>();
        requests = new ArrayList<Request>();
        systemMailBox = new ArrayList<Mensaje>();
        messageIDCounter = 0;
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
        
            if(isThereAPendingMessageOnSystemMailBox(destinationID, sourceID)){ // Si al solicitar, ya hab�a un msj 
                Mensaje message = getMessageFromSystemMailBox(sourceID, destinationID);
                System.out.println("Was the received message null? " + String.valueOf(message == null));
                if(configs.getSend().equals(Sync_Send.BLOCKING)){ // Hay q desbloquear el sender y quitar el sending
                    removeRequest(message, Request_Action.ACCUSEMENT);
                    sender.setBlocking(false);
                }
                receiver.setReceiving(false);
                receiver.getRecordHistory().add(new MessageRecord(Record_Message_Action.RECEIVED, message));
                
                //removeRequest(message, Request_Action.ACCUSEMENT);
                // Hay que remover el request si es que hay
                    
            }
            else{ // No hay un msj pendiente por recibir, entonces hay que hacer el request
                requests.add(new Request(sourceID, destinationID, Request_Action.RECEIVE));
                receiver.setReceiving(Boolean.TRUE);
                if(configs.getReceive().equals(Sync_Receive.BLOCKING))
                    receiver.setBlocking(true);
                
            
            }
        }else{ // Indirect Addressing
            if(isThereAPendingMessageOnMailBox(destinationID, sourceID)){
                System.out.println("");
                
                Mensaje message = getMessageFromMailBox(sourceID, destinationID);
                
                if(configs.getSend().equals(Sync_Send.BLOCKING)){ // Hay q desbloquear el sender y quitar el sending
                    //getProcess(message.getSourceID()).setBlocking(false);    // ESTO HAY QUE REVISARLO *****
                    removeRequest(message, Request_Action.ACCUSEMENT);
                    sender.setBlocking(false);
                }
                
                receiver.setReceiving(false);
                receiver.getRecordHistory().add(new MessageRecord(Record_Message_Action.RECEIVED, message));
                //JOptionPane.showMessageDialog(null, "Rec" + receiver.getRecordHistory().get(0).toString(), "Titulo", 1);
                //JOptionPane.showMessageDialog(null, "Ahora hay un record (con msj pendiente): " + receiver.getRecordHistory().toString(), "Titulo", 1);
                
            }
            else{ // No hay un msj pendiente por recibir, entonces hay que hacer un request
                requests.add(new Request(sourceID, destinationID, Request_Action.RECEIVE));
                receiver.setReceiving(Boolean.TRUE);
                if(configs.getReceive().equals(Sync_Receive.BLOCKING))
                    receiver.setBlocking(true);
                System.out.println("");
            }        
        }System.out.println("Requests now: " + requests.size());
        System.out.println("Receiver buffer size: " + String.valueOf(receiver.getRecordHistory().size()));
    }
    
    private boolean isThereAPendingMessageOnMailBox(String destinationID, String sourceID){
        if(isThereAnAccusementRequest(destinationID, sourceID))
            return true;
        
         if(getMailBox(destinationID).getBufferMensajes().size()> 0)
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
    
    private Mensaje getMessageFromSystemMailBox(String sourceID, String destinationID){
        Mensaje pickedMessage = null;
        if(systemMailBox.size() > 0){
            for(Mensaje message: systemMailBox){
                if(message.getSourceID().equals(destinationID) && message.getDestinationID().equals(sourceID)){
                    pickedMessage = message;
                }
            }
            systemMailBox.remove(pickedMessage);
        }
        return pickedMessage;
    }
    
    
    private boolean isThereAnAccusementRequest(String sourceID, String destinationID){
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
        if(isThereAnAccusementRequest(destinationID, sourceID)){
            System.out.println("There's a accusement request for this receive request");
            return true;
        }
        
        for(Mensaje mensaje: systemMailBox)
            if(mensaje.getDestinationID().equals(sourceID) && mensaje.getSourceID().equals(destinationID)){
                //System.out.println("The requested message already exists in the system mailbox");
                return true;
            }
        
        return false;
    }
    
    private void removeRequest(Mensaje mensaje, Request_Action action){
        if(requests.size() > 0){
            Request toBeDeleted = null;
            boolean directAddressing = (Addressing.EXPLICIT.equals(configs.getAddressing()) || Addressing.IMPLICIT.equals(configs.getAddressing()));
            for(Request request: requests){
                if(directAddressing){
                    if(request.getSourceID().equals(mensaje.getDestinationID()) && request.getDestinationID().equals(mensaje.getSourceID()) && request.getAction() == action){
                        System.out.println("Request to be deleted sourceID: "+ request.getSourceID() + " destinationID: "+ request.getDestinationID() + " type: " + request.getAction().toString());
                        toBeDeleted = request;//requests.remove(request);
                    }
                    if(action == Request_Action.ACCUSEMENT){
                        if(request.getSourceID().equals(mensaje.getSourceID()) && request.getDestinationID().equals(mensaje.getDestinationID()) && request.getAction() == action){
                            System.out.println("Request to be deleted sourceID: "+ request.getSourceID() + " destinationID: "+ request.getDestinationID() + " type: " + request.getAction().toString());
                            toBeDeleted = request;//requests.remove(request);
                        }       
                    }
                }
                else{
                    if(request.getSourceID().equals(mensaje.getDestinationID()) && request.getAction().equals(action)){
                        System.out.println("Request to be deleted sourceID: "+ request.getSourceID() + " destinationID: "+ request.getDestinationID() + " type: " + request.getAction().toString());
                        toBeDeleted = request;//requests.remove(request);
                    }
                }
            }
            if(toBeDeleted != null){
                requests.remove(toBeDeleted);
                System.out.println("Request summary");
                for(Request req: requests)
                    System.out.println("Req SOURCEID: " + req.getSourceID() + " DESTINATIONID: " + req.getDestinationID() + " TYPE: " +req.getAction().toString());
            }
        }
    }
    
    public void sendMessage(Mensaje mensaje){ // Abarca tanto addressing direct como indirect
        
        getProcess(mensaje.getSourceID()).getRecordHistory().add(new MessageRecord(Record_Message_Action.SENT, mensaje));
        
        if(messageWasExpected(mensaje)){
            JOptionPane.showMessageDialog(null, "This message was expected", "Information", 1);
            
            Proceso procesoDestino = getProcess(getRequestFromSource(mensaje.getDestinationID()).getSourceID());
            procesoDestino.getRecordHistory().add(new MessageRecord(Record_Message_Action.RECEIVED, mensaje));
            
            if(configs.receive.equals(Sync_Receive.BLOCKING)){
                procesoDestino.setBlocking(false);
            }
            
            removeRequest(mensaje, Request_Action.RECEIVE);   
        }else{  // El msj por enviar no estaba siendo esperado
            if (Addressing.EXPLICIT.equals(configs.getAddressing()) || Addressing.IMPLICIT.equals(configs.getAddressing())){
                systemMailBox.add(mensaje);
                System.out.println("Mensaje de: " + systemMailBox.get(systemMailBox.indexOf(mensaje)).getSourceID() + " de para: "+ systemMailBox.get(systemMailBox.indexOf(mensaje)).getDestinationID());
            }
            else{ // Addressing indirecto
                System.out.println("Message sent to MailBox: " + getMailBox(mensaje.getDestinationID()).getIdMailBox());
                getMailBox(mensaje.getDestinationID()).addMessage(mensaje); 
            }
            if(configs.send.equals(Sync_Send.BLOCKING)){ // Si al enviar hay send BLOCKING hay que bloquear el proceso y hacer un ACCUSEMENT request
                String sourceID = mensaje.sourceID;
                Proceso toBlock = getProcess(sourceID);
                toBlock.setBlocking(true);
                // VERIFICAR QUE ESTE ORDEN EST� CORRECTO
                requests.add(new Request(sourceID, mensaje.getDestinationID(), Request_Action.ACCUSEMENT));
            }
        } System.out.println("Requests now: " + requests.size());
    }   // </ sendMessage(...)>
    
    
    private Request getRequestFromSource(String destinationID){
        for(Request request: requests){
            if(request.getSourceID().equals(destinationID)){
                return request;
            }
        }
        return null;
    }
    
    private boolean messageWasExpected(Mensaje message){
        if (requests.isEmpty())
            return false;
        else{
            // Si viene de Addressing directo
            if (Addressing.EXPLICIT.equals(configs.getAddressing()) || Addressing.IMPLICIT.equals(configs.getAddressing())){
                for(Request request: requests){
                    if(request.getAction().equals(Request_Action.RECEIVE) && request.getSourceID().equals(message.getDestinationID()) && request.getDestinationID().equals(message.getSourceID()))
                        return true;
                }
            }else{ //Si viene de addressing indirecto
                for(Request request: requests){
                    if(request.getAction().equals(Request_Action.RECEIVE) && request.getSourceID().equals(message.getDestinationID()))
                        return true;
                }
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
