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
import Config_Enums.Sync_Receive;
import Config_Enums.Sync_Send;
import java.util.ArrayList;

/**
 *
 * @author aleandro
 */
public class Controller {
    static Controller instance = null;
    Configuration configs;
    ArrayList<Proceso> procesos;
    ArrayList<MailBox> mailboxes;
    
    private Controller(){
        //configs = new Configuration();
        procesos = new ArrayList<Proceso>();
        mailboxes = new ArrayList<MailBox>();
    }
    
    public static Controller getInstance(){
        if(instance == null)
            instance = new Controller();
        return instance;
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
