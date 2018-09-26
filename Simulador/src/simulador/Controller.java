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
    
    
    public void sendMessage(Mensaje mensaje){
        // If Addressing Direct then 
        systemMailBox.add(mensaje);
        // Else send to specified MailBox
            // Falta
        
        
        getProcess(mensaje.getSourceID()).getRecordHistory().add(new MessageRecord(Record_Message_Action.SENT, mensaje));
        
        // --------------------------------------------------------------------- hacer todas las validaciones posibles
        if(messageWasExpected(mensaje)){
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
    
    public Mensaje receiveDirectMessage(String sourceID){
        return null;
    }
    
    private Request getRequestFromSource(String destinationID){
        for(Request request: requests){
            if(request.getDestinationID().equals(destinationID)){
                return request;
            }
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
