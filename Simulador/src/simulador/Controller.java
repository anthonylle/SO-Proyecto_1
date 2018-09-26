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

/**
 *
 * @author aleandro
 */
public class Controller {
    static Controller instance = null;
    Configuration configs;
    
    private Controller(){
        //configs = new Configuration();
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
    
    
    
}
